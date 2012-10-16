#!/bin/bash
#
# Some basic black-box tests for CWB Query
#

tmpdir="/tmp/cwb-test.$$"

while getopts "hfqt:" opt; do
	case "$opt" in
		q) silence='2>&1 >/dev/null';;
		t) tmpdir="$OPTARG";;
		f) force="true";;
		h|*)
			echo "Usage: $0 [-q] [-t tmpdir]"
			exit 1
		;;
	esac
done

test -d "$tmpdir" && test -z "$force" && read -p "Removing $tmpdir [y/N]: " confirm
test -z "$force" && test "$confirm" = "y" && rm -rf $tmpdir

test ! -e "$tmpdir" || exit 1

mkdir -p $tmpdir

# Test a command line query
java -jar *CWBQuery*.jar -b "2009/01/01 00:00:00" -s "NZWLGT.LTZ40" -d 400 -t sac -o $tmpdir/cwb-test-1.out $silence

# Test running a channel listing
java -jar *CWBQuery*.jar -lsc -b "2009/01/01 00:00:00" -d 100.0 2>$tmpdir/cwb-test-2.out
# Scrub the output of inconsequential potential changes - i.e. today and the command line
sed -i -e 's/today=.*$//' $tmpdir/cwb-test-2.out
sed -i -e 's/configuration/confiuration/' $tmpdir/cwb-test-2.out
sed -i -e 's/00:00:00.000/00:00:00/' $tmpdir/cwb-test-2.out

cat > $tmpdir/CWB.batch <<EOF
-b "2009/01/01 00:00:00" -s "NZWLGT.LTZ40" -t sac -o $tmpdir/cwb-batch-test-1.out
-b "2009/01/01 00:00:00" -s "NZWLGT.LTZ40" -d 600 -t sac -o "$tmpdir/%N-%n-%s-%c-%l-%Y-%y-%j-%J-%M-%D-%h-%m-%S%z-cwb-batch-test-2.out"
-b "2009/01/01 00:00:00" -s "NZWEL..HH[ENZ]..|NZBFZ..HHZ..|.*WAZ.*20" -d 300 -t sac -sacpz nm -o "$tmpdir/%N-cwb-batch-test-3.out"
-b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t ms -o $tmpdir/cwb-batch-test-4.out
-b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t msz -o $tmpdir/cwb-batch-test-5.out
-b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t dcc -o $tmpdir/cwb-batch-test-6.out
-b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t dcc512 -o $tmpdir/cwb-batch-test-7.out
-b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t text -o $tmpdir/cwb-batch-test-8.out
-event geonet:3266622 -s "NZAPZ..HHZ" -o $tmpdir/cwb-batch-test-9.out
EOF

# Test the rest via a batch file
java -jar *CWBQuery*.jar -f $tmpdir/CWB.batch $silence
# Scrub the 'CREATED' lines in .pz output files
sed -i -e 's/^\(\* CREATED\s\+\).*$/\1<SCRUBBED>/' $tmpdir/*-cwb-batch-test-3.out.pz

cat > $tmpdir/CWB.md5 <<EOF
49d2825a2bfd257465e6a92f665a82ab  $tmpdir/cwb-test-1.out
deaeb787541e58b3cab655190502ad0d  $tmpdir/cwb-test-2.out
0a57cd3bc953208614c7357d80ab7080  $tmpdir/cwb-batch-test-1.out
493780556e7808a6c270ea416b693115  $tmpdir/NZWLGTLTZ40-NZ-WLGT-LTZ-40-09-2009-001-2454833-01-01-00-00-00-cwb-batch-test-2.out
e3040fdd0f38b46c31e79dd99b42b1b2  $tmpdir/NZBFZ__HHZ10-cwb-batch-test-3.out
c86cdaf2ae3a0f29b648cfbe2eb27220  $tmpdir/NZBFZ__HHZ10-cwb-batch-test-3.out.pz
229c3a5a13e4eac9d3822a258c41da53  $tmpdir/NZWAZ__LNE20-cwb-batch-test-3.out
76f3beeb09c7af063539bcfed744bd58  $tmpdir/NZWAZ__LNE20-cwb-batch-test-3.out.pz
324c3b8f325023c927c9dcbdedd3d8f7  $tmpdir/NZWAZ__LNZ20-cwb-batch-test-3.out
48cfb9b3e5259bd0f3fb735c1c991af9  $tmpdir/NZWAZ__LNZ20-cwb-batch-test-3.out.pz
82d7a91ecff7b37faf344c6f22062878  $tmpdir/NZWEL__HHE10-cwb-batch-test-3.out
4df0c59e2157af4e72555278a7d86ed6  $tmpdir/NZWEL__HHE10-cwb-batch-test-3.out.pz
576b136c952fb21e0e8c2868534aac3a  $tmpdir/NZWEL__HHN10-cwb-batch-test-3.out
a949571d503cd95b30754ec41b656860  $tmpdir/NZWEL__HHN10-cwb-batch-test-3.out.pz
363a1bb1e897df9d444f9439133cde75  $tmpdir/NZWEL__HHZ10-cwb-batch-test-3.out
35625bef570ade89407ab8ad5dbafea6  $tmpdir/NZWEL__HHZ10-cwb-batch-test-3.out.pz
3b103d7bcbb9ac99d6de26026fc93cf9  $tmpdir/cwb-batch-test-4.out
2c5ae4cc1bf1de7208ff62e65fa5b468  $tmpdir/cwb-batch-test-5.out
757476816a0da1ec5922385a4b18987e  $tmpdir/cwb-batch-test-6.out
3b103d7bcbb9ac99d6de26026fc93cf9  $tmpdir/cwb-batch-test-7.out
a85e23dcf59c9426e58bf57e4022cc86  $tmpdir/cwb-batch-test-8.out
579b0db4b758fac02b30aaf6def0cc09  $tmpdir/cwb-batch-test-9.out
EOF

md5sum -c $tmpdir/CWB.md5

