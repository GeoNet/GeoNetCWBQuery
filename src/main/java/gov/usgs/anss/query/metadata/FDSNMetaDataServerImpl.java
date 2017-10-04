/*
 * Copyright 2010, Institute of Geological & Nuclear Sciences Ltd or
 * third-party contributors as indicated by the @author tags.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package gov.usgs.anss.query.metadata;

import gov.usgs.anss.query.NSCL;
import gov.usgs.anss.query.QueryProperties;
import gov.usgs.anss.query.cwb.formatter.CWBQueryFormatter;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
/**
 *
 * @author Howard Wu
 */
public class FDSNMetaDataServerImpl implements MetaDataServer {

    protected static final Logger logger = Logger.getLogger(FDSNMetaDataServerImpl.class.getName());

    Document pzDoc;
    XPath pzXpath;
    // This is used to format the query to the meta data server,
    // not the CWB server.  It looks like the format is different
    // so we will format it ourselves here instead of creating
    //  a new instance of options.
    static {
        logger.fine("$Id$");
    }
    
    public String getResponseData(NSCL nscl, DateTime date, String pzunit) {
        HttpURLConnection conn;
        String serviceUrl = QueryProperties.getFDSNStationQueryUrl();

        String postBody = "level=response\n";
        postBody += CWBQueryFormatter.fdsnQueryBody(date, -1.0, nscl.toString());
        try {
            URL url = new URL(serviceUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/xml");
            conn.setDoOutput(true);
            conn.getOutputStream().write(postBody.getBytes());
        } catch (IOException ex) {
            conn = null;
            logger.warning("FDSN service " + serviceUrl + " error:" + ex);
            System.exit(1);
        }


        String line;
        String s = "";
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            s = outputPZ(sb.toString());
            br.close();
        } catch (IOException e) {
            logger.severe("Error reading output from station service:"+ e.getLocalizedMessage());
        }

        if(conn!=null)
            conn.disconnect();

        return s;
    }

    private String outputPZ(String xml) {
        StringBuilder out = new StringBuilder("");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            pzDoc = builder.parse(new InputSource(new StringReader(xml)));
            pzXpath = XPathFactory.newInstance().newXPath();

            String network = pzXpath.compile("/FDSNStationXML/Network/@code").evaluate(pzDoc);
            String station = pzXpath.compile("/FDSNStationXML/Network/Station/@code").evaluate(pzDoc);
            String channel = pzXpath.compile("/FDSNStationXML/Network/Station/Channel/@code").evaluate(pzDoc);
            String location = pzXpath.compile("/FDSNStationXML/Network/Station/Channel/@locationCode").evaluate(pzDoc);

            out.append(String.format("* %-13s%s%s %s%s\n", "CHANNEL(NSCL)", network, station, channel, location));
            out.append(String.format("* %-13s%s\n", "NETWORK", network));
            out.append(String.format("* %-13s%s\n", "STATION", station));
            out.append(String.format("* %-13s%s\n", "COMPONENT", channel));
            out.append(String.format("* %-13s%s\n", "LOCATION", location));

            String buf1, buf2;

            // TODO: format date?
            buf1 = pzXpath.compile("/FDSNStationXML/Created").evaluate(pzDoc);
            out.append(String.format("* %-13s%s\n", "CREATED", buf1));

            buf1 = pzXpath.compile("/FDSNStationXML/Network/Station/@startDate").evaluate(pzDoc);
            out.append(String.format("* %-13s%s\n", "EFFECTIVE", buf1));

            buf1 = pzXpath.compile("/FDSNStationXML/Network/Station/@endDate").evaluate(pzDoc);
            if (buf1!=null && buf1.length()>0) {
                out.append(String.format("* %-13s%s\n", "ENDDATE", buf1));
            } else {
                out.append(String.format("* %-13s%s\n", "ENDDATE", "EMPTY"));
            }

            String inputUnit = pzXpath.compile("/FDSNStationXML/Network/Station/Channel/Response/InstrumentSensitivity/InputUnits/Name").evaluate(pzDoc);
            out.append(String.format("* %-13s%s\n", "INPUT UNIT", inputUnit));

            buf1 = pzXpath.compile("/FDSNStationXML/Network/Station/Channel/Response/InstrumentSensitivity/OutputUnits/Name").evaluate(pzDoc);
            out.append(String.format("* %-13s%s\n", "OUTPUT UNIT", buf1));

            String description = pzXpath.compile("/FDSNStationXML/Network/Station/Site/Name").evaluate(pzDoc);
            out.append(String.format("* %-13s%s\n", "DESCRIPTION", description));

            Float samples = safeFloat("/FDSNStationXML/Network/Station/Channel/SampleRateRatio/NumberSamples");
            Float seconds = safeFloat("/FDSNStationXML/Network/Station/Channel/SampleRateRatio/NumberSeconds");

            if(!samples.isNaN() && !seconds.isNaN()) {
                out.append(String.format("* %-13s%.1f\n", "RATE (HZ)", (samples/seconds)));
            }

            String owner = pzXpath.compile("/FDSNStationXML/Network/Description").evaluate(pzDoc);
            out.append(String.format("* %-13s%s\n", "OWNER", owner));

            Double lat = safeDouble("/FDSNStationXML/Network/Station/Channel/Latitude");
            Double lng = safeDouble("/FDSNStationXML/Network/Station/Channel/Longitude");
            Float elev = safeFloat("/FDSNStationXML/Network/Station/Channel/Elevation");

            out.append(String.format("* %-13s%s %s: %.4f  %.4f  %.1f\n", "COORD(NEIC)", network, station, lat, lng, elev));
            out.append(String.format("* %-13s%s %s: %.4f  %.4f  %.1f\n", "COORD(SEED)", network, station, lat, lng, elev));

            Float depth = safeFloat("/FDSNStationXML/Network/Station/Channel/Depth");
            Float dip = safeFloat("/FDSNStationXML/Network/Station/Channel/Dip");
            Float azim = safeFloat("/FDSNStationXML/Network/Station/Channel/Azimuth");

            out.append(String.format("* %-13s%.6f\n", "LAT-SEED", lat));
            out.append(String.format("* %-13s%.6f\n", "LONG-SEED", lng));
            out.append(String.format("* %-13s%.1f\n", "ELEV-SEED", elev));
            out.append(String.format("* %-13s%.1f\n", "DEPTH", depth));
            out.append(String.format("* %-13s%.1f\n", "DIP", dip));
            out.append(String.format("* %-13s%.1f\n", "AZIMUTH", azim));

            buf1 = pzXpath.compile("/FDSNStationXML/Network/Station/Channel/Sensor/Manufacturer").evaluate(pzDoc);
            buf2 = pzXpath.compile("/FDSNStationXML/Network/Station/Channel/Sensor/Model").evaluate(pzDoc);
            out.append(String.format("* %-13s%s %s\n", "INSTRMNTTYPE", buf1, buf2));
            out.append(String.format("* %-13s%s^%s^^\n", "INSTRMNTCMNT", description, owner));
            Float gain = safeFloat("/FDSNStationXML/Network/Station/Channel/Response/Stage[@number='1']/StageGain/Value");
            if (!gain.isNaN()) {
                out.append(String.format("* %-13s%.4E\n", "INSTMNTGAIN", gain));
            }

            buf1 = pzXpath.compile("/FDSNStationXML/Network/Station/Channel/Response/Stage[@number='1']/PolesZeros/OutputUnits/Name").evaluate(pzDoc);
            out.append(String.format("* %-13s%s\n", "INSTRMNTUNIT", buf1));

            Double sens = safeDouble("/FDSNStationXML/Network/Station/Channel/Response/InstrumentSensitivity/Value");
            if (!sens.isNaN()) {
                out.append(String.format("* %-13s%.4E\n", "SENS-SEED", sens));
                out.append(String.format("* %-13s%.4E\n", "SENS-CALC", sens));
            }

            Double normal = safeDouble("/FDSNStationXML/Network/Station/Channel/Response/Stage[@number='1']/PolesZeros/NormalizationFactor");
            if (!normal.isNaN()) {
                out.append(String.format("* %-13s%.4E\n", "A0-SEED", normal));
                out.append(String.format("* %-13s%.4E\n", "A0-CALC", normal));
            }
            out.append("* ****\n");
            if(!sens.isNaN() && !normal.isNaN()) {
                Double constant = sens * normal;
                out.append(String.format("CONSTANT %-14.4E\n", constant));
            }

            // Assume I don't have to care about ordering of Pole and Zero
            NodeList real = (NodeList)pzXpath.compile("/FDSNStationXML/Network/Station/Channel/Response/Stage/PolesZeros/Zero/Real/text()").evaluate(pzDoc, XPathConstants.NODESET);
            NodeList imaginary = (NodeList)pzXpath.compile("/FDSNStationXML/Network/Station/Channel/Response/Stage/PolesZeros/Zero/Imaginary/text()").evaluate(pzDoc, XPathConstants.NODESET);
            if (inputUnit.toLowerCase().contains("/s**2")) {
                // acceleration, add 2 zeroes
                out.append(String.format("ZEROS %d\n",real.getLength()+2));
                out.append(String.format("%-14.4E %-14.4E\n",0.0f, 0.0f));
                out.append(String.format("%-14.4E %-14.4E\n",0.0f, 0.0f));
            } else if (inputUnit.toLowerCase().contains("/s")) {
                // velocity, add 1 zero
                out.append(String.format("ZEROS %d\n",real.getLength()+1));
                out.append(String.format("%-14.4E %-14.4E\n",0.0f, 0.0f));
            } else {
                // displacement
                out.append(String.format("ZEROS %d\n",real.getLength()));
            }
            for (int i = 0; i < real.getLength(); i++) {
                out.append(String.format("%-14.4E %-14.4E\n",Double.parseDouble(real.item(i).getNodeValue()), Double.parseDouble(imaginary.item(i).getNodeValue())));
            }

            real = (NodeList)pzXpath.compile("/FDSNStationXML/Network/Station/Channel/Response/Stage/PolesZeros/Pole/Real/text()").evaluate(pzDoc, XPathConstants.NODESET);
            imaginary = (NodeList)pzXpath.compile("/FDSNStationXML/Network/Station/Channel/Response/Stage/PolesZeros/Pole/Imaginary/text()").evaluate(pzDoc, XPathConstants.NODESET);
            out.append(String.format("POLES %d\n",real.getLength()));
            for (int i = 0; i < real.getLength(); i++) {
                out.append(String.format("%-14.4E %-14.4E\n",Double.parseDouble(real.item(i).getNodeValue()), Double.parseDouble(imaginary.item(i).getNodeValue())));
            }
            out.append("* <EOE>\n* <EOR>\n");
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            logger.warning("Error parsing station xml:"+e.getLocalizedMessage());
        }

        pzXpath = null;
        pzDoc = null;

        return out.toString();
    }

    private Double safeDouble(String path) {
        try {
            String buf = pzXpath.compile(path).evaluate(pzDoc);
            return Double.parseDouble(buf);
        } catch (XPathExpressionException | NumberFormatException e) {
        }

        return Double.NaN;
    }
    private Float safeFloat(String path) {
        try {
            String buf = pzXpath.compile(path).evaluate(pzDoc);
            return Float.parseFloat(buf);
        } catch (XPathExpressionException | NumberFormatException e) {
        }

        return Float.NaN;
    }
}
