/*
 * MPHData.java
 *
 * Created on 28. Januar 2005, 11:51
 */

package de.brasoft.geogridreader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 *
 * @author Administrator
 */
public class MPHData {
    
    boolean bigEndian;    // Flag ob Big Endian oder umgekehrt
    private int xTileSize;     // Kachelgr�sse in Pixel (x-Richtung)
    private int yTileSize;     // Kachelgr�sse in Pixel (y-Richtung)
    private int xTiles;        // Anzahl Kachelreihen
    int yTiles;        // Anzahl der Kachelzeilen
    String MapName;   // Name der Karte
    private int Masstab;
    double BezugsMeridian; // F�r diese Karte
    int AnzCalPts;     // Anzahl Referenzpunkte
    private double PixelMM;    // Pixel pro MiliMeter beim Scan
    int AnzColors;     // Anzahl der Farben in der Palette
    TCALPTS CalPts[];    // Tabelle mit Kalibrierungs-Punkten
    TCOLPAL ColPal[];    // Farbpalette
    boolean force;       // Daten nur teilweise gelesen (Farbtabelle)
    
    //---------- diverse Maximalwerte ---------------
    int maxTiles;
    private int maxXPixel;
    private int maxYPixel;
    
    //-------------------------------------------------------------------------
    // Aktuelle Kartendaten - Dynamisch ver�ndert
    public int X0,Y0;   // Aktuelle Kartenposition (linke obere Ecke) in Pixel
    public int mode;    // Modus der Kartenbedienung
    public int tileLO;  // Markierte Tile Links Oben
    public int tileRU;  // Markierte Tile Rechts Unten
    
    //-------------------------------------------------------------------------
    // Parameter für TCalPunkt merken
    private int calXr;
    private int calYr;
    private double calLat;
    private double calLon;
    private TCALPTS extraCalPt;
    
    //-------------------------------------------------------------------------
    public MPHData(String file , boolean pforce) throws IOException, GeoReaderException {
        
        int FileLen;
        int FixPunkt;    // Fixpunkte in Eingabedatei
        int i;
        int namlen;      // L�nge des Karten-Namens
        boolean paletFault=false;
        
        // Daten als nur teilweise gelesen markieren
        force = pforce;
        
        // L�nge der Datei bestimmen
        File inFile = new File(file);
        FileLen = (int)(inFile.length());
        byte buf[] = new byte[FileLen];
        
        // Daten aus Datei in Buffer lesen
        FileInputStream fhnd = new FileInputStream(inFile);
        fhnd.read(buf,0,FileLen);
        fhnd.close();
        //System.out.printf("Datei %s, Laenge = %d Bytes\n", inFile.getAbsolutePath(), FileLen);
        
        // Einen DataInputStream �ber den Byte-Buffer legen
        MyArrayInputStream in = new MyArrayInputStream(buf);
        DataInputStream din = new DataInputStream(in);
        
        
        // Daten auswerten
        if (buf[0] == 'M' && buf[1] == 'M') bigEndian = true;
        else
            if (buf[0] == 'I' && buf[1] == 'I')  bigEndian=false;
            else throw new GeoReaderException("Falsches Format - MM oder II am Dateianfang erwartet");
        SpecialData Get = new SpecialData(in, din, bigEndian);
        xTileSize = Get.ConvInt(12);
        yTileSize = Get.ConvInt(16);
        xTiles = Get.ConvInt(20);
        yTiles = Get.ConvInt(24);
        
        // L�nge des Kartennamens bestimmen (0-terminierter String)
        namlen=0; i=596; while (buf[i++]!=0) namlen++;
        MapName = new String(buf,596,namlen,"ISO-8859-1");
        Protokoll.Prot("  Karte: " + MapName);
        
        if (!force) {
            // N�chsten Fixpunkt setzen, auf die n�chste 4-Byte Grenze ausrichten
            FixPunkt = ((596+1+namlen+3)/4)*4;
            
            // Massstab suchen
            i=0;
            while (Get.ConvInt(FixPunkt+i*4)!=1 ||
                    Get.ConvInt(FixPunkt+(i+1)*4) <=1000 ||
                    (Get.ConvInt(FixPunkt+(i+1)*4) % 1000) !=0 ||
                    Get.ConvInt(FixPunkt+(i+1)*4) >10000000) {
                i++;
                if (FixPunkt+(i+2)*4 >=FileLen)
                    throw new GeoReaderException("Falsches Format - Kein Ma�stab gefunden");
            }
            
            // Fixpunkt f�r Massstab
            FixPunkt = FixPunkt+i*4;         // 1:Massstab
            Masstab = Get.ConvInt(FixPunkt+4);
            
            PixelMM=Get.ConvDbl(FixPunkt+16);
            AnzCalPts=Get.ConvInt(FixPunkt+28) & 0x0000FFFF;
            
            // Fixpunkt f�r Kalibrierungspunkte
            FixPunkt = FixPunkt+32;
            
            // Wo kommt der Bezugsmeridian der Karte her ????
            BezugsMeridian = 9.0;  // Nicht in allen Karten gleich !!
            
            // Aus Ini-Datei, wenn schon mal ge�ndert
            /* ++ if (MapReaderProperties.exists(MapName+"BezugsMeridian")) {
                BezugsMeridian = Double.parseDouble(MapReaderProperties.getProperty(MapName+"BezugsMeridian"));
            } */
            
            // Neue Tabelle anlegen
            // Die Kalibrierungspunkte auslesen
            CalPts = new TCALPTS[AnzCalPts];
            for (i=0; i<AnzCalPts; i++) {
                int xr = Get.ConvInt(FixPunkt);
                int yr = Get.ConvInt(FixPunkt+4);
                int p3 = Get.ConvInt(FixPunkt+8);
                int p4 = Get.ConvInt(FixPunkt+12);
                double Lat = Get.ConvDbl(FixPunkt+16);
                double Lon = Get.ConvDbl(FixPunkt+24);
                CalPts[i] = new TCALPTS(xr,yr,p3,p4,Lat,Lon,BezugsMeridian);
                FixPunkt=FixPunkt+64;
                if (i==0) {
                	calXr = xr;
                	calYr = yr;
                	calLat = Lat;
                	calLon = Lon;
                	extraCalPt = CalPts[0];
                }
            }
            
            // Zum Testen
            //System.out.printf("\nAnzCalPts=%d\n", AnzCalPts);
            //for (i=0; i<AnzCalPts; i++) System.out.println(CalPts[i].toString());
            
            
            // Farbpalette auslesen
            AnzColors = Get.ConvSInt(FixPunkt+8);
            if (AnzColors<16) AnzColors=16;
            ColPal = new TCOLPAL[AnzColors];
            FixPunkt=FixPunkt+18;      // Paletten-Header
            for (i=0; i<AnzColors; i++) {
                ColPal[i] = new TCOLPAL();
                ColPal[i].r = Get.ConvSInt(FixPunkt);
                ColPal[i].g = Get.ConvSInt(FixPunkt+2);
                ColPal[i].b = Get.ConvSInt(FixPunkt+4);
                ColPal[i].rgbToColor();
                FixPunkt=FixPunkt+6;
                if (FixPunkt+6 >= FileLen)
                    throw new GeoReaderException("Falsches Format - �berlauf beim Farbpalettenaufbau");
            }
            
            // �berpr�fen der Farbpalette (kein r,g oder b-Wert darf �ber 255 sein
            for (i=0; i<AnzColors; i++)
                if (ColPal[i].r >= 256 || ColPal[i].g >= 256 || ColPal[i].b >= 256)
                    paletFault=true;
        }
        
        if (paletFault || force) {
            // Farbpaletten-Header suchen
            boolean treffer=false;
            for (i=0; i+8<FileLen; i+=2) {
                if ((Get.ConvSInt(i) == 0x0097) &&
                        (Get.ConvSInt(i+2) == 0x0190) &&
                        (Get.ConvSInt(i+4) == 0) &&
                        (Get.ConvSInt(i+6) == 0x0001)) {
                    treffer=true;
                    break;
                }
            }
            
            if (treffer) {
                FixPunkt = i-10;
                // Palette neu aufbauen
                AnzColors = Get.ConvSInt(FixPunkt+8);
                if (AnzColors<16) AnzColors=16;
                ColPal = new TCOLPAL[AnzColors];
                FixPunkt=FixPunkt+18;      // Paletten-Header
                for (i=0; i<AnzColors; i++) {
                    ColPal[i] = new TCOLPAL();
                    ColPal[i].r = Get.ConvSInt(FixPunkt);
                    ColPal[i].g = Get.ConvSInt(FixPunkt+2);
                    ColPal[i].b = Get.ConvSInt(FixPunkt+4);
                    ColPal[i].rgbToColor();
                    FixPunkt=FixPunkt+6;
                }
            } else throw new GeoReaderException("Formatfehler, falsche Palette erzeugt");
        }
        
        // Maximalwerte berechnen
        maxTiles = getXTiles() * yTiles;
        maxXPixel = (xTileSize * getXTiles()) - 1;
        maxYPixel = (yTileSize * yTiles) - 1;
        
        // Dynamische Standardwerte setzen
        
    }
    
    
    //-------------------------------------------------------------------------
    private int GetNearestCalPt(int x, int y) {
        if (AnzCalPts==1) return 0;
        double dist=Integer.MAX_VALUE;
        double minDist=Integer.MAX_VALUE;
        int retw=0;
        
        // Die ganze Liste durchgehen
        for (int i=0; i<AnzCalPts; i++) {
            dist = CalPts[i].GetPixelDist(x,y);
            if(dist<minDist) {
                retw=i;
                minDist=dist;
            }
        }
        return retw;
    }
    
    //-------------------------------------------------------------------------
    public GeodeticPoint getGeoKoordinate(int x, int y) {
        if (force) return null; else
            return CalPts[GetNearestCalPt(x,y)].getGeoKoordinate(x,y,Masstab,PixelMM);
    }
    
    
    //-------------------------------------------------------------------------
    public void Test() {
        int i;
        
        System.out.printf("xTileSize=%d\n", xTileSize);
        System.out.printf("yTileSize=%d\n", yTileSize);
        System.out.printf("xTiles=%d\n", getXTiles());
        System.out.printf("yTiles=%d\n", yTiles);
        System.out.println(MapName);
        System.out.printf("Massstab=%d\n", Masstab);
        System.out.printf("PixelMM=%f\n", PixelMM);
        System.out.printf("\nAnzCalPts=%d\n", AnzCalPts);
        for (i=0; i<AnzCalPts; i++) System.out.println(CalPts[i].toString());
        System.out.printf("\nAnzColors=%d\n", AnzColors);
        for (i=0; i<AnzColors; i++) System.out.println(ColPal[i].toString());
    }
    
    public TCALPTS makeCalPt(double centerMeridian) {
    	extraCalPt = new TCALPTS(calXr, calYr, 0, 0, calLat, calLon, centerMeridian);
    	return extraCalPt;
    }
    

	public int getMaxYPixel() {
		return maxYPixel;
	}


	public int getMaxXPixel() {
		return maxXPixel;
	}


	public int getXTileSize() {
		return xTileSize;
	}


	public int getYTileSize() {
		return yTileSize;
	}


	public int getXTiles() {
		return xTiles;
	}
	
	public TCALPTS getCalPt() {
		return extraCalPt;
	}


	public int getMasstab() {
		return Masstab;
	}


	public double getPixelMM() {
		return PixelMM;
	}
}
