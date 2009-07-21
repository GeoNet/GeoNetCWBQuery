/*
 * VMSServer.java
 *
 * Created on December 15, 2006, 1:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.edge.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.net.ServerSocket;
import java.lang.reflect.*;
import gov.usgs.anss.seed.*;
import gov.usgs.anss.util.*;

/**
 *
 * @author davidketchum
 */
public class VMSServer extends Thread {

    int port;
    ServerSocket d;
    int totmsgs;
    boolean terminate;

    public void terminate() {
        terminate = true;
        interrupt();
    }   // cause the termination to begin

    public int getNumberOfMessages() {
        return totmsgs;
    }

    /** Creates a new instance of VMSServers */
    public VMSServer(int porti) {
        port = porti;
        terminate = false;
        // Register our shutdown thread with the Runtime system.
        Runtime.getRuntime().addShutdownHook(new ShutdownVMSServer());

        start();
    }

    public void run() {
        boolean dbg = false;
        GregorianCalendar now;
        StringBuffer sb = new StringBuffer(10000);

        // OPen up a port to listen for new connections.
        while (true) {
            try {
                //server = s;
                if (terminate) {
                    break;
                }
                Util.prt(Util.asctime() + " VSS: Open Port=" + port);
                d = new ServerSocket(port);
                break;
            } catch (SocketException e) {
                if (e.getMessage().equals("VSS:Address already in use")) {
                    try {
                        Util.prt("VSS: Address in use - try again.");
                        Thread.sleep(2000);
                        continue;
                    } catch (InterruptedException E) {
                        continue;
                    }
                } else {
                    Util.prt("VSS:Error opening TCP listen port =" + port + "-" + e.getMessage());
                    try {
                        Thread.sleep(2000);
                        continue;
                    } catch (InterruptedException E) {
                        continue;
                    }
                }
            } catch (IOException e) {
                Util.prt("VSS:ERror opening socket server=" + e.getMessage());
                try {
                    Thread.sleep(2000);
                    continue;
                } catch (InterruptedException E) {
                    continue;
                }
            }
        }


        while (true) {
            if (terminate) {
                break;
            }
            try {
                Socket s = d.accept();
                Util.prt("VSS: from " + s);
                new VMSServerHandler(s);
            } catch (IOException e) {
                Util.prt("VSS:receive through IO exception");
            }
        }       // end of infinite loop (while(true))
        //Util.prt("Exiting VMSServers run()!! should never happen!****\n");
        Util.prt("VSS:read loop terminated");
    }

    private class ShutdownVMSServer extends Thread {

        public ShutdownVMSServer() {
        }

        /** this is called by the Runtime.shutdown during the shutdown sequence
         *cause all cleanup actions to occur
         */
        public void run() {
            terminate = true;
            interrupt();
            Util.prta("VSS: Shutdown started");
            int nloop = 0;

            Util.prta("VSS:Shutdown Done. Client");

        }
    }

    class VMSServerHandler extends Thread {

        Socket s;

        public VMSServerHandler(Socket ss) {
            s = ss;
            try {
                s.setSendBufferSize(512000);
                //s.setTcpNoDelay(true);
            } catch (SocketException e) {
                Util.prt("VSH: setSendBuffer problem=" + e.getMessage());
            }
            start();
        }

        public void run() {
            boolean dbg = true;
            byte[] line = new byte[1000];
            boolean done = false;
            boolean littleEndian = false;
            int off = 0;
            String argline = "";
            while (!done) {
                try {
                    int l = s.getInputStream().read(line, off, 1000 - off);
                    if (l < 0) {
                        Util.prt("VSH: error getting command line abort");
                        return;
                    }
                    off += l;
                    for (int i = 0; i < off; i++) {
                        if (line[i] == '\n') {
                            argline = new String(line, 0, i);
                            done = true;
                            break;
                        }
                    }
                } catch (IOException e) {
                    Util.prt("VSH: IOexception reading command line- abort.");
                    try {
                        s.close();
                    } catch (IOException e2) {
                    }
                    return;
                }
            }

            // The new line is found, parse the line!
            Util.prta("VSH: Input command line=" + argline);
            String[] args2 = argline.split(" ");
            String[] args = new String[args2.length];

            double duration = 300.;
            int j = 0;
            boolean inquote = false;
            for (int i = 0; i < args2.length; i++) {   // put quoted string back together
                if (args2[i].trim().equals("")) {
                    if (inquote) {
                        args[j] += " ";
                    }
                    continue;
                }
                if (inquote) {
                    if (args2[i].substring(args2[i].length() - 1).equals("'") ||
                            args2[i].substring(args2[i].length() - 1).equals("\"")) {
                        args[j++] += " " + args2[i].substring(0, args2[i].length() - 1);
                        inquote = false;
                    } else {
                        args[j] += " " + args2[i];
                    }
                } else {
                    if (args2[i].substring(0, 1).equals("'") || args2[i].substring(0, 1).equals("\"")) {
                        args[j] = args2[i].substring(1);      // remove the quote
                        inquote = true;
                    } else {
                        args[j++] = args2[i];     // No quote, nor one found, just add it on
                    }
                }
            }
            for (int i = 0; i < j; i++) {
                args2[i] = args[i];
            }
            args = new String[j];
            for (int i = 0; i < j; i++) {
                args[i] = args2[i];
            }
            for (int i = 0; i < args.length; i++) {
                if (args[i].charAt(0) == '\'') {
                    args[i] = args[i].substring(1);
                }
                if (args[i].charAt(args[i].length() - 1) == '\'') {
                    args[i] = args[i].substring(0, args[i].length() - 1);
                }
            }
            String seedname = "";
            StringBuffer sb = new StringBuffer(100);
            String begin = "";
            for (int i = 0; i < args.length; i++) {
                sb.append(args[i] + " ");
                //Util.prt("VSH: arg="+args[i]+"| i="+i);
                if (args[i].equals("-s")) {
                    seedname = args[i + 1];
                } else if (args[i].equals("-b")) {
                    begin = args[i + 1];
                } else if (args[i].equals("-d")) {
                    if (args[i + 1].endsWith("D") || args[i + 1].endsWith("d")) {
                        duration = Double.parseDouble(args[i + 1].substring(0, args[i + 1].length() - 1)) * 86400. - 0.001;
                    } else {
                        duration = Double.parseDouble(args[i + 1]);
                    }
                } else if (args[i].equals("-little")) {
                    littleEndian = true;
                }
            }
            Util.prta("VSH: VMS Query =" + sb.toString());
            ArrayList<ArrayList<MiniSeed>> allBlks = EdgeQueryClient.query(args);

            try {
                OutputStream out = s.getOutputStream();
                for (int ichn = 0; ichn < allBlks.size(); ichn++) {
                    ArrayList<MiniSeed> blks = allBlks.get(ichn);
                    if (blks.size() > 0) {
                        Collections.sort(blks);
                        // Use the span to populate a sac file
                        GregorianCalendar start = new GregorianCalendar();
                        start.setTimeInMillis(Util.stringToDate2(begin).getTime());
                        //GregorianCalendar start = blks.get(0).getGregorianCalendar();

                        // build the zero filled area (either with exact limits or with all blocks)
                        int NODATA = 2127000000;
                        ZeroFilledSpan span = new ZeroFilledSpan(blks, start, duration, NODATA);
                        if (dbg) {
                            Util.prt("ZeroSpan=" + span.toString());
                        }
                        int noval = span.getNMissingData();
                        int[] samples = span.getData();
                        int ipnt = 0;
                        byte[] buf = new byte[span.getNsamp() * 4];
                        ByteBuffer bb = ByteBuffer.wrap(buf);
                        //Util.prt("bborder="+bb.order()+" ByteOrder.LITTLE="+ByteOrder.LITTLE_ENDIAN);
                        if (littleEndian) {
                            bb.order(ByteOrder.LITTLE_ENDIAN);
                            Util.prt("bborder=" + bb.order() + " ByteOrder.LITTLE=" + ByteOrder.LITTLE_ENDIAN);
                        }
                        long sum = 0;
                        for (int i = 0; i <= span.getNsamp(); i++) {
                            if (i == span.getNsamp() || samples[i] == NODATA) {
                                out.write(blks.get(0).getSeedName().getBytes(), 0, 12);
                                GregorianCalendar now = new GregorianCalendar();
                                now.setTimeInMillis(start.getTimeInMillis() +
                                        ((long) (ipnt / blks.get(0).getRate() * 1000. + 0.5)));
                                bb.position(0);
                                bb.putInt(now.get(Calendar.YEAR));              //0
                                bb.putShort((short) now.get(Calendar.DAY_OF_YEAR));       //4
                                bb.putShort((short) now.get(Calendar.HOUR_OF_DAY));    // 6
                                bb.putShort((short) now.get(Calendar.MINUTE));  //8
                                bb.putShort((short) now.get(Calendar.SECOND));  //18
                                bb.putShort((short) now.get(Calendar.MILLISECOND));//12 length=14
                                bb.putInt(i - ipnt);
                                out.write(bb.array(), 0, 18);
                                sum = 0;
                                bb.position(0);
                                for (int k = ipnt; k < i; k++) {
                                    sum += samples[k];
                                    bb.putInt(samples[k]);
                                }
                                out.write(bb.array(), 0, (i - ipnt) * 4);
                                Util.prta("VSH: " + blks.get(0).getSeedName() + " " + now.get(Calendar.YEAR) + " " +
                                        now.get(Calendar.DAY_OF_YEAR) + " " + now.get(Calendar.HOUR_OF_DAY) +
                                        ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND) + "." +
                                        now.get(Calendar.MILLISECOND) + " ns=" + (i - ipnt) + " sum=" + sum);
                                ipnt = i;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                Util.SocketIOErrorPrint(e, "in VMSServerHandler - aborting");
            }
            Util.prta("VSH: VMSServerHandler has exit on s=" + s);
            if (s != null) {
                if (!s.isClosed()) {
                    try {
                        s.close();
                    } catch (IOException e) {
                        Util.prta("VSS: IOError closing socket");
                    }
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Util.prt(Util.asctime());
        Util.setModeGMT();
        Util.setNoInteractive(true);
        Util.prt(Util.asctime());
        //ServerThread server = new ServerThread(AnssPorts.PROCESS_SERVER_PORT, false);

        int port = 7777;
        //try  {
        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-p")) {
                port = Integer.parseInt(args[i + 1]);
            }
            if (args[i].equals("-?") || args[i].indexOf("help") > 0) {
                Util.prt("-p nnnn Set port name to something other than 7984");
                Util.prt("-?            Print this message");
                System.exit(0);
            }
        }

        VMSServer t = new VMSServer(port);

    }
}

  
