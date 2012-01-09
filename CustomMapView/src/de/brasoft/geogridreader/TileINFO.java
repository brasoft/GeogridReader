/*
 * TileINFO.java
 *
 * Created on 29. Januar 2005, 00:28
 */

package de.brasoft.geogridreader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.util.Log;

/**
 *
 * @author Administrator
 */
public class TileINFO {
    private MPHData mph;
    private long Adr;
    private int Len;     // L�nge der Komprimierten Daten
    private int AnzSegments;
    //private COLORSEG colseg[];
    //private int LZWStartByte;
    
    /** Creates a new instance of TileINFO */
    public TileINFO() {
    }
    
    public TileINFO(MPHData mph, long Adr, int Len) {
    	this.mph = mph;
    	this.Adr = Adr;
    	this.Len = Len;
    }
    
    //-------------------------------------------------------------------------
    public Image DecodeLZW(String file) throws IOException, GeoReaderException {
        IntString Out;
        
        // Datei öffnen und Kacheldaten (roh) einlesen
        File inFile = new File(file);
        FileInputStream fhnd = new FileInputStream(inFile);

        // Sonderbehandlung wenn Kachel an Adresse > 2 GByte
        if (Len > 0x1000000) {
          Len = Len - 0x1000000;
          Adr = Adr + 0x80000000L;
          Log.w("DecodeLZW", "Kachel > 2GB: Adr="+Adr+" Len="+Len);
        }

        byte buf[]=new byte[Len];  // Speicher für die Kacheldaten(roh) anlegen

        fhnd.skip(Adr);   // Auf die Kachel positionieren
        fhnd.read(buf,0,Len);
        fhnd.close();
        
        // Einen DataInputStream �ber den Byte-Buffer legen
        MyArrayInputStream in = new MyArrayInputStream(buf);
        DataInputStream din = new DataInputStream(in);
        
        AnzSegments = din.readUnsignedByte();
        
        // Segmenttabelle anlegen
        COLORSEG colseg[] = new COLORSEG[AnzSegments];
        
        // Segmenttabelle aufbauen
        int LZWStartByte = RecordHead(buf, colseg);
        
        // Grafikdaten LZW-Decodieren
        LZWDEC lzwdec = new LZWDEC(buf, LZWStartByte, Len, 256+AnzSegments, 9, 12);
        Out = lzwdec.Decode();
        
        if (Out==null)  throw new GeoReaderException("Fehler bei der LZW-Decodierung");
        else  return new Image(getGrafikImage(Out.GetAryPtr(),Out.Length(), colseg));
    }
    
    //---------------------------------------------------------------------------
    private int[] getGrafikImage(int colcod[], int arylen, COLORSEG colseg[]) {
        int i,k;
        int idx=0;
        int val;
        int val1,val2,seglen;
        
        // Speicher f�r Image anlegen
        int ImageSize = mph.getXTileSize()*mph.getYTileSize();
        int[] image = new int[ImageSize];

        // Alle Daten(Farb-Codes) aus dem LZW bearbeiten
        for (i=0; i<arylen; i++) {
            val = colcod[i];
            if (val<256) { // Einzelfarbe
                if (mph.AnzColors == 16) {    // 2 Farb-Pixel
                    image[idx++] = mph.ColPal[val%16].color;
                    image[idx++] = mph.ColPal[val/16].color;
                }
                //    Nur ein Pixel (daf�r mit bis zu 256 Farben)
                else  image[idx++] = mph.ColPal[val].color;
            } else {         // Farbsegment
                if (val-256 >= AnzSegments) {
                	Log.w("getGrafikImage", "Segmentindex zu gross"); break; }
                val1 = colseg[val-256].Color1;
                val2 = colseg[val-256].Color2;
                seglen = colseg[val-256].Len;
                if (mph.AnzColors == 16)
                    for (k=0; k<seglen; k++) { image[idx++] = val1; image[idx++] = val2; } else
                        for (k=0; k<seglen; k++) { image[idx++] = val1; }
            }
            if (idx>mph.getYTileSize()*mph.getXTileSize()) {
                Log.w("getGrafikImage","Speicherüberlauf image"); break; }
        }
        return image;
    }
    
    //-------------------------------------------------------------------------
    private int CalcBitmax(int value) {
      int i=1;
      while ((value >> i) > 0) i++;
      i++;
      if (i > 8) i=8;
      return i;
    }
    
    //-------------------------------------------------------------------------
    private int RecordHead(byte buf[], COLORSEG colseg[]) {
        int startbit;
        int bit2;
        int bitmax;
        int thiscol,thislen,lastcol;
        
        // Einen DataInputStream �ber den Byte-Buffer legen
        MyArrayInputStream in = new MyArrayInputStream(buf);
        DataInputStream din = new DataInputStream(in);
        SpecialData Get = new SpecialData(in, din, mph.bigEndian);
        
        
        // Das erste Segment ist einfach
        thiscol = Get.GetBitVal(8,8);
        thislen = Get.GetBitVal(16,8);
        if (mph.AnzColors==16)
            colseg[0] = new COLORSEG(thiscol,mph.ColPal[thiscol%16].color,mph.ColPal[thiscol/16].color,thislen);
        else
            colseg[0] = new COLORSEG(thiscol,mph.ColPal[thiscol].color, thislen);
        
        startbit=24;
        
        // Schleife über alle restlichen Segmente
        for (int j=1; j<AnzSegments; j++) {
            // Farbe immer 8 Bit
            lastcol = thiscol;
            thiscol = Get.GetBitVal(startbit,8);
            startbit+=8;
            bit2=2;
            bitmax=CalcBitmax(thislen);
            
            // Den nächsten Längenwert nur lesen, falls der vorhergehende Wert größer 2 ist
            if (thislen > 2) {
                // if first bit of next length value is 1, stick with previous lenghth, except color is the same
                if ((thislen<128) && (Get.GetBitVal(startbit,1)==1) && (thiscol!=lastcol))
                    startbit++;
                else {
                    // read next length value bit by bit until larger than previous one
                    // then go back 1 bit
                    while ((Get.GetBitVal(startbit,bit2+1)<=thislen) && (bit2<bitmax)) bit2++;
                    
                    thislen=Get.GetBitVal(startbit,bit2);
                    
                    // if length=2 only color values follow, but start at full byte boundary
                    if (thislen==2) startbit=((startbit+bit2-1)/8)*8+8;
                    else startbit=startbit+bit2;
                }
            }
            
            if (mph.AnzColors==16)
                colseg[j] = new COLORSEG(thiscol,mph.ColPal[thiscol%16].color,mph.ColPal[thiscol/16].color,thislen);
            else
                colseg[j] = new COLORSEG(thiscol,mph.ColPal[thiscol].color, thislen);
        }
        
        // LZW code of record always start at byte boundary
        return (startbit+7)/8;
    }
}

