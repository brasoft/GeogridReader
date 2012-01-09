package de.brasoft.geogridreader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import android.graphics.Point;

public class Image {
	
	private static Image instance = null;
	private static Hashtable<Integer, Image> instances = new Hashtable<Integer, Image>();
	
	private int img[];
	private Point pos;
	//private boolean display;
	
	// Konstruktor für TileINFO
	public Image(int ary[]) {
		img = ary;
	}
	
	/*public Image(Point pt) {
		pos = pt;
	}*/
	
	/*public static Image old_getTileImage(MPRData mpr, int nr) {
		if(!instances.containsKey(nr)) {
			instances.put(nr, mpr.GetImage(nr));
		}
		instances.get(nr).setBasePosition(mpr.getMapXYFromTile(nr));
		return instances.get(nr);
	}*/
	
	// Echte Tile lesen
	public static Image getTileImage(MPRData mpr, int nr) {
		if(!instances.containsKey(nr)) {
			Image img = mpr.GetImage(nr);
			if (img==null) {
				// Im Fehlerfall Blank-Image
				img = new Image(mpr.getMapXYFromTile(nr), 
						mpr.getMph().getXTileSize(), mpr.getMph().getYTileSize());
			}
			instances.put(nr, img);
		}
		instances.get(nr).setBasePosition(mpr.getMapXYFromTile(nr));
		return instances.get(nr);
	}
	
	// Privater Konstruktor für Blank-Tile
	private Image(int x, int y) {
		int cnt = x*y;
		img = new int[cnt];
		for (int i=0; i<cnt; i++) {
			if (i%16==0 || (i/x)%16==0) {
				img[i] = 0xffcccccc;
			} else {
				img[i] = 0xffdddddd;	
			}
		}
	}
	
	// Öffentlicher Konstruktor für Blank-Tile
	public Image(Point pt, int x, int y) {
		pos = pt;
		img = Image.getBlankInstance(pt, x, y).getAry();
	}
	
	static private Image getBlankInstance(Point pt, int x, int y) {
		if (instance==null) {
			instance = new Image(x,y);
		}
		return instance;
	}

	public int[] getAry() {
		return img;
	}
	
	public void setBasePosition(Point pt) {
		pos = pt;
	}
	
	public int getX() {
		return pos.x;
	}

	public int getY() {
		return pos.y;
	}
	
	static public int getLoadedTiles() {
		return instances.keySet().size();
	}

	static public void reduceTiles(ArrayList<Image> tiles) {
		Enumeration<Integer> tmp = instances.keys();
		while (tmp.hasMoreElements()) {
			int key = tmp.nextElement();
			Image obj = instances.get(key);
			if (!tiles.contains(obj)) {
				instances.remove(key);
			}
		}
	}
}
