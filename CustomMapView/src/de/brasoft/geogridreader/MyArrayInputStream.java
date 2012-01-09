/*
 * MyArrayInputStream.java
 *
 * Created on 28. Januar 2005, 11:57
 */

package de.brasoft.geogridreader;

import java.io.ByteArrayInputStream;

/**
 *
 * @author Administrator
 */
public class MyArrayInputStream extends ByteArrayInputStream {
    
    public MyArrayInputStream(byte[] buf) {
        super(buf);
    }
    
    public void Pos(int mypos) {
        pos = mypos;
    }
    
    public byte[] GetBuf() {
        return buf;
    }
    
}
