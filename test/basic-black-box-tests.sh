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
50ae2628ee1a65d553a46a4bce70d0ef  $tmpdir/cwb-test-2.out
0a57cd3bc953208614c7357d80ab7080  $tmpdir/cwb-batch-test-1.out
493780556e7808a6c270ea416b693115  $tmpdir/NZWLGTLTZ40-NZ-WLGT-LTZ-40-09-2009-001-2454833-01-01-00-00-00-cwb-batch-test-2.out
e3040fdd0f38b46c31e79dd99b42b1b2  $tmpdir/NZBFZ__HHZ10-cwb-batch-test-3.out
8924ad0693db4220d213be69201971b9  $tmpdir/NZBFZ__HHZ10-cwb-batch-test-3.out.pz
9dd8e5d9dcb427f0b4e992301f13f48b  $tmpdir/NZWAZ__LNE20-cwb-batch-test-3.out
44acfaa18e0b04337a83d3774352ea5e  $tmpdir/NZWAZ__LNE20-cwb-batch-test-3.out.pz
07d9b9d763cf96da4013388c6ec452cb  $tmpdir/NZWAZ__LNZ20-cwb-batch-test-3.out
e8b63d76a386cf34af723e6cb442ee7f  $tmpdir/NZWAZ__LNZ20-cwb-batch-test-3.out.pz
82d7a91ecff7b37faf344c6f22062878  $tmpdir/NZWEL__HHE10-cwb-batch-test-3.out
9867ce0ba334c484de5fbd4ed5d3e243  $tmpdir/NZWEL__HHE10-cwb-batch-test-3.out.pz
576b136c952fb21e0e8c2868534aac3a  $tmpdir/NZWEL__HHN10-cwb-batch-test-3.out
8cde9e5a2de663cc3fcc1edb6b067cb9  $tmpdir/NZWEL__HHN10-cwb-batch-test-3.out.pz
363a1bb1e897df9d444f9439133cde75  $tmpdir/NZWEL__HHZ10-cwb-batch-test-3.out
9e04667a31aa72c40b886093a343910e  $tmpdir/NZWEL__HHZ10-cwb-batch-test-3.out.pz
3b103d7bcbb9ac99d6de26026fc93cf9  $tmpdir/cwb-batch-test-4.out
2c5ae4cc1bf1de7208ff62e65fa5b468  $tmpdir/cwb-batch-test-5.out
757476816a0da1ec5922385a4b18987e  $tmpdir/cwb-batch-test-6.out
3b103d7bcbb9ac99d6de26026fc93cf9  $tmpdir/cwb-batch-test-7.out
a85e23dcf59c9426e58bf57e4022cc86  $tmpdir/cwb-batch-test-8.out
c732f41bfb663286e9268e2107f89b64  $tmpdir/cwb-batch-test-9.out
EOF

md5sum -c $tmpdir/CWB.md5

