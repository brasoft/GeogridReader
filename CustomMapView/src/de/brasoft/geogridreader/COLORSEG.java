/*
 * COLORSEG.java
 *
 * Created on 28. Januar 2005, 23:41
 */

package de.brasoft.geogridreader;

/**
 *
 * @author Administrator
 */
public class COLORSEG {
    
    public int Color1;
    public int Color2;
    public int index;
    public int Len;
    
    /** Creates a new instance of COLORSEG */
    public COLORSEG(int pindex, int col1, int col2, int plen) {
        Color1 = col1;
        Color2 = col2;
        index = pindex;
        Len = plen;
    }
    
    public COLORSEG(int pindex, int col1, int plen) {
        Color1 = col1;
        index = pindex;
        Len = plen;
    }
    
    public COLORSEG() {
    }
  
    public String toString() {
        String retw=new String();
        retw = new Integer(Color1).toString() + " " + new Integer(Color2).toString() + " " +
                new Integer(Len).toString();
        return retw;
    }
    
}
