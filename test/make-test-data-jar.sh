#!/bin/bash

jarversion=1.0.1

tmpdir="/tmp/cwb-test.$$"
mkdir -p $tmpdir

cp make-test-data-jar.sh $tmpdir

mkdir -p $tmpdir/test-data/gov/usgs/anss/query/filefactory/no-gaps
java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0.jar -t ms -b "2009/01/01 00:00:00" -d 1800 -s "NZMRZ..HH.10" -o $tmpdir/test-data/gov/usgs/anss/query/filefactory/no-gaps/%N.ms
java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0.jar -t sac -sacpz nm -b "2009/01/01 00:00:00" -d 1800 -s "NZMRZ..HH.10" -o $tmpdir/test-data/gov/usgs/anss/query/filefactory/no-gaps/%N.sac


mkdir -p $tmpdir/test-data/gov/usgs/anss/query/filefactory/no-meta
java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0.jar -t ms -b "2009/01/01 00:00:00" -d 1800 -s "NZMRZ..HH.10" -o $tmpdir/test-data/gov/usgs/anss/query/filefactory/no-meta/%N.ms
java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0.jar -t sac -nometa -b "2009/01/01 00:00:00" -d 1800 -s "NZMRZ..HH.10" -o $tmpdir/test-data/gov/usgs/anss/query/filefactory/no-meta/%N.sac


# This query has gaps.  Use the mseed for testing -nogaps option. Use the SAC for testing gap fill options
mkdir -p $tmpdir/test-data/gov/usgs/anss/query/filefactory/gaps
java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0.jar -t ms -b "2009/01/01 00:00:00" -d 1800 -s "NZBFZ..HHE10" -o $tmpdir/test-data/gov/usgs/anss/query/filefactory/gaps/%N.ms
java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0.jar -t sac -sacpz nm -b "2009/01/01 00:00:00" -d 1800 -s "NZBFZ..HHE10" -o $tmpdir/test-data/gov/usgs/anss/query/filefactory/gaps/%N.sac


# The sac to use to test this has to have the headers hand massaged.
mkdir -p $tmpdir/test-data/gov/usgs/anss/query/filefactory/event
java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0.jar -t ms -b "2007/05/12 07:30:00" -s "NZTSZ..HHN10" -d 1800 -o $tmpdir/test-data/gov/usgs/anss/query/filefactory/event/%N.ms
java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0.jar -t sac -sacpz nm -b "2007/05/12 07:30:00" -s "NZTSZ..HHN10" -d 1800 -o $tmpdir/test-data/gov/usgs/anss/query/filefactory/event/%N.sac
rm $tmpdir/test-data/gov/usgs/anss/query/filefactory/event/*.sac

mkdir -p $tmpdir/test-data/gov/usgs/anss/query/metadata
java -jar ../lib-ivy/external/GeoNetCWBQuery-2.0.0.jar -t sac -sacpz nm -b "2009/01/01 11:11:11" -d 1800 -s "NZWEL..HH.10" -o $tmpdir/test-data/gov/usgs/anss/query/metadata/%N.sac
rm $tmpdir/test-data/gov/usgs/anss/query/metadata/*.sac

cd $tmpdir

jar -0cvf CWBQueryTestData-${jarversion}.jar test-data  make-test-data-jar.sh

cd -

mkdir -p $tmpdir/CWBQueryTestData/$jarversion

mv $tmpdir/CWBQueryTestData-${jarversion}.jar $tmpdir/CWBQueryTestData/$jarversion/

rsync -v --archive --no-perms --rsh=ssh $tmpdir/CWBQueryTestData repoadmin@repo.geonet.org.nz:/work/maven/public_html/ivy/repo/manual/nz/org/geonet/

#rm -rf $tmpdir

