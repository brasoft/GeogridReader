package de.brasoft.snippets;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class PlzSearch {

	private static final int HASH_BLK_CNT = 50;

	private int plz;
	private float lat;
	private float lon;
	
	public PlzSearch(Context con, String fileName, int cmpPlz) {
		long t1 = System.currentTimeMillis();
		AssetManager am = con.getAssets();
		if (cmpPlz>99999 || cmpPlz<1)  {
			plz = 0;
			lat = (float) 0.0;
			lon = (float) 0.0;
			return;
		}
		try {
			InputStream fhnd = am.open(fileName);
			DataInputStream in = new DataInputStream(fhnd);

			// Sprungadresse auslesen
			int lookUp = (cmpPlz / HASH_BLK_CNT) * 4;
			in.skip(lookUp);
			int adr = in.readInt();

			// Zu dieser Adresse springen
			in.skip(adr-lookUp-4);
			
			// Ab dieser Stelle suchen
			for (int i=0; i<HASH_BLK_CNT; i++) {
				try {
					int plz = in.readInt();
					float lat = in.readFloat();
					float lon = in.readFloat();
					if (cmpPlz == plz) {
						this.plz = plz;
						this.lat = lat;
						this.lon = lon;
						break;
					}
				} catch (EOFException e) {
					break;
				}
			}

			in.close();
			fhnd.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		Log.w("PlzSearch", "Used time="+(System.currentTimeMillis()-t1)+" ms");
	}
	
	public int getPlz() {
		return plz;
	}
	
	public float getLat() {
		return lat;
	}
	
	public float getLon() {
		return lon;
	}
}
