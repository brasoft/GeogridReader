/*
 * SpecialData.java
 *
 * Created on 29. Januar 2005, 13:12
 */

package de.brasoft.geogridreader;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class SpecialData {
    
    MyArrayInputStream  in;  // Bin�re Daten der Eingabedatei
    DataInputStream din;
    boolean bigEndian;
    byte buf[];
    
    /** Creates a new instance of SpecialData */
    public SpecialData(MyArrayInputStream  pin, DataInputStream pdin, boolean pbigEndian) {
        in = pin;
        din = pdin;
        bigEndian = pbigEndian;
        buf = in.GetBuf();
    }
    
    public SpecialData(byte pbuf[]) {
        buf = pbuf;
    }
    
    //-------------------------------------------------------------------------
    public int ConvInt(int offset) throws IOException {
        {
            in.Pos(offset);
            Integer retw=new Integer(din.readInt());
            if (bigEndian) return retw;
            else return Integer.reverseBytes(retw);
        }
    }
    
    //-------------------------------------------------------------------------
    public short ConvSInt(int offset) throws IOException {
        {
            in.Pos(offset);
            Short retw=new Short(din.readShort());
            if (bigEndian) return retw;
            else return Short.reverseBytes(retw);
        }
    }
    
    //-------------------------------------------------------------------------
    public double ConvDbl(int offset) throws IOException {
        {
            Long lv=new Long(0);
            in.Pos(offset);
            Double retw=new Double(din.readDouble());
            if (bigEndian) return retw;
            else {
                lv=Double.doubleToLongBits(retw);
                lv=Long.reverseBytes(lv);
                return Double.longBitsToDouble(lv);
            }
        }
        
    }
    
    
    //-------------------------------------------------------------------------
    public int GetBitVal(int Startbit, int Bitlen) {

        int erg=0;
        
        // Eine Anzahl von Bits als Zahl interpretieren
        for (int i=Startbit; i<Startbit+Bitlen; i++) {
            erg<<=1;
            if (((1<<(7-(i%8))) & buf[i/8])!=0) erg|=1;
        }
        return erg;
    }
}
