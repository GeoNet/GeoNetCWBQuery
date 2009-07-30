/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nz.org.geonet.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A Bare log message formatter - returns the message string only.
 * @author	richardg
 * @version	$Id$
 */
public class BareFormatter extends Formatter {
    /**
     * Format the given LogRecord.
     * <p>
     * This method can be overridden in a subclass.
     * It is recommended to use the {@link Formatter#formatMessage}
     * convenience method to localize and format the message field.
     *
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public synchronized String format(LogRecord record) {
		return (formatMessage(record) + '\n');
	}
}
