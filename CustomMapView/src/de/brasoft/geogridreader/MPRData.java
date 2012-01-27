/*
 * MPRData.java
 *
 * Created on 28. Januar 2005, 22:50
 */

package de.brasoft.geogridreader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Point;
import android.util.Log;

/**
 *
 * @author Administrator
 */
public class MPRData {
    
    private String file;
    private MPHData mph;
    //private TileINFO ref[];
    //private int AnzTiles;
    
    /** Creates a new instance of MPRData */
    public MPRData(String pfile, MPHData pmph) throws IOException {
        //int i;
        //int AnzTiles;
        
        file=pfile;
        mph=pmph;
        
        /*
        AnzTiles=mph.getXTiles() * mph.yTiles;
        ref=new TileINFO[AnzTiles];
        
        // File-Objekt anlegen und Buffer f�r Positions- und L�ngen-Info
        //(in der Datei) von allen Tiles
        File inFile = new File(file);
        byte buf[] = new byte[AnzTiles*8];
        
        // Daten aus Datei in Buffer lesen
        FileInputStream fhnd = new FileInputStream(inFile);
        boolean tmpflag=true;
        while (tmpflag) {
            fhnd.read(buf,0,4);
            for (int q=0; q<4; q++) if (buf[q]!=0) { tmpflag=false; break; }
        }
        fhnd.read(buf,4,AnzTiles*8-4);
        fhnd.close();
        
        // Einen DataInputStream �ber den Byte-Buffer legen
        MyArrayInputStream in = new MyArrayInputStream(buf);
        DataInputStream din = new DataInputStream(in);
        SpecialData Get = new SpecialData(in, din, mph.bigEndian);
        
        for (i=0; i<AnzTiles; i++) {
            ref[i] = new TileINFO(mph, Get.ConvInt(i*8), Get.ConvInt(i*8+4));
        }*/
    }
    
    //-------------------------------------------------------------------------
    /*public Image xxGetImage(int idx) {
        
        // Image-Daten der Kachel einlesen
        try {
        	return ref[idx].DecodeLZW(file); 
        	} 
        catch(Exception e) {
        	Log.w("GetImage", "Fehler beim Lesen der Kachel " + idx);
        	e.printStackTrace();
        	return null;
        }
    }*/

    //-------------------------------------------------------------------------
    public Image GetImage(int idx) {
        
    	TileINFO tInfo = getTileInfo(idx);
    	if (idx>=mph.maxTiles) return null;
    	
        // Image-Daten der Kachel einlesen
        try {
        	return tInfo.DecodeLZW(file); 
        	} 
        catch(Exception e) {
        	Log.w("GetImage", "Fehler beim Lesen der Kachel " + idx);
        	e.printStackTrace();
        	return null;
        }
    }

    private TileINFO getTileInfo(int idx) {
    	int Len = 8;
        // Datei öffnen und Kacheldaten (roh) einlesen
		try {
        File inFile = new File(file);
        FileInputStream fhnd;
			fhnd = new FileInputStream(inFile);

        byte buf[]=new byte[Len];  // Speicher für die Kacheldaten(roh) anlegen

        fhnd.skip(idx*Len);   // Auf die Kachel positionieren
        fhnd.read(buf,0,Len);
        fhnd.close();
        
        // Einen DataInputStream �ber den Byte-Buffer legen
        MyArrayInputStream in = new MyArrayInputStream(buf);
        DataInputStream din = new DataInputStream(in);
        SpecialData Get = new SpecialData(in, din, mph.bigEndian);

        return new TileINFO(mph, Get.ConvInt(0), Get.ConvInt(4));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

    }
    
	/**
	 * Funktion ermittelt Pixelkoordinaten(links oben) aus einer tileNr
	 */
    public Point getMapXYFromTile(int nr) {
		int tileX = nr % mph.getXTiles();
		int tileY = nr / mph.getXTiles();
		return new Point(tileX*mph.getXTileSize(), tileY*mph.getYTileSize());
	}

	/**
	 * Funktion ermittelt eine tileNr aus PixelKoordinaten der Map
	 */
    public int getTileFromMapXY(int x, int y) {
		if ((x<0) || (y<0)) return -1;
		int tileX = x / mph.getXTileSize();
		int tileY = y / mph.getYTileSize();
		return mph.getXTiles() * tileY + tileX;
	}

    //-------------------------------------------------------------------------
    public MPHData getMph() {
    	return mph;
    }

}
