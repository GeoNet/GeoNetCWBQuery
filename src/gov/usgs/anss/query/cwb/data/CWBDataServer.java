/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.data;

import gov.usgs.anss.seed.MiniSeed;
import java.util.TreeSet;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public interface CWBDataServer {

	void query(DateTime begin, Double duration, String nsclSelectString);

	TreeSet<MiniSeed> getNext();

	boolean hasNext();

	void quiet();

	String getHost();

	int getPort();

	@Override
	boolean equals(Object o);

	@Override
	int hashCode();
}
