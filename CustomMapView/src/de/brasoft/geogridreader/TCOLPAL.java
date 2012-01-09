/*
 * TCOLPAL.java
 *
 * Created on 28. Januar 2005, 21:44
 */

package de.brasoft.geogridreader;

/**
 *
 * @author Administrator
 */
public class TCOLPAL {
    public int r;
    public int g;
    public int b;
    public int color;
    
    /** Creates a new instance of TCOLPAL */
    public TCOLPAL() {
    }
    
    public String toString() {
        String retw=new String();
        retw = new Integer(r).toString() + " " + new Integer(g).toString() + " " +
                new Integer(b).toString();
        return retw;
    }
 
    public void rgbToColor() {
        color = r<<16 | g<<8 | b;
    }
}
