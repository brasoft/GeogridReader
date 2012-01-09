/*
 * GeoReaderException.java
 *
 * Created on 26. Februar 2005, 14:25
 */

package de.brasoft.geogridreader;

/**
 *
 * @author peter
 */
public class GeoReaderException extends Exception {
    
	private static final long serialVersionUID = 1L;

	/** Creates a new instance of GeoReaderException */
    public GeoReaderException() {
    }

    public GeoReaderException(String pmsg) {
        super("GeoReaderException: " + pmsg);
    }
}
