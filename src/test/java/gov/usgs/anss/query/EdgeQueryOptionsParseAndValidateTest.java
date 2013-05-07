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
package gov.usgs.anss.query;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author geoffc
 */
@RunWith(Parameterized.class)
public class EdgeQueryOptionsParseAndValidateTest {

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
			// args line,	expected valid,		failure message
			{new EdgeQueryOptions("-f file.txt"), true, "Batch file mode should pass."},
			{new EdgeQueryOptions("-f \"file with spaces.txt\""), true, "Batch file mode (quoted with spaces) should pass."},
			{new EdgeQueryOptions("-f file.txt -ls"), false, "Batch file mode should fail with extra args."},

			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\""), true, "seedname and begin options should be enough."},
			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\" -sacpz um"), true, "sacpz should have nm or um units."},
			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\" -sacpz fail"), false, "sacpz should have nm or um units."},
			{new EdgeQueryOptions("-s \"NZWLGT\""), false, "should fail without begin time."},
			{new EdgeQueryOptions("-b \"2009/01/01 00:00:00\""), false, "seedname is not optional."},

			// Ultimately this will probably have to go... Unless we do the TODOs
			{new EdgeQueryOptions("-s \"NZWLGT\" -event 2013p321497"), false, "event options should be qualified."},
			{new EdgeQueryOptions("-s \"NZWLGT\" -event geonet:2013p321497"), true, "seedname and event options should be valid."},
			{new EdgeQueryOptions("-event geonet:2013p321497"), true, "event by itself, should be valid (if event contains pics)."},

			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\" -t NULL"), true, "type null is legal."},

			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\" -t sac -o \"blah_%s\""), false, "Sac file names must include at least channel."},
			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\" -t msz -o \"blah_%s\""), false, "msz file names must include at least channel."},
		});
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
		// TODO: Start some embedded http server for static quakeml files.
	}

	private EdgeQueryOptions options;
	private boolean valid;
	private String message;

    public EdgeQueryOptionsParseAndValidateTest(EdgeQueryOptions options, boolean valid, String message) {
        this.options = options;
        this.valid = valid;
		this.message = message;
		// TODO: set the quakeml uri to the local server.
    }

    @Test
    public void testParseAndValidate() {
		assertEquals(message, valid, options.isValid());
    }

}