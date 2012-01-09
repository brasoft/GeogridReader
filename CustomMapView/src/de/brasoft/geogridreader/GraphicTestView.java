package de.brasoft.geogridreader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GraphicTestView extends View {
	
    static private final String MPH = ".mph"; 
    static private final String MPR = ".mpr"; 
    
	// Preferences
    private Pref_Manager G;
    
	// Message-Konstanten
	private static final int MSG_MAP_POSITION = 100;
	//private static final int MSG_MAP_INTERN_POSITION = 101;

    private Handler msgHnd = null;
	
    //Daten für die Berechnung der Tiles-List
    //private int mapWidth;		Werden später vielleicht noch gebraucht
    //private int mapHeight;	Werden später vielleicht noch gebraucht
    private int tileSizeX=1;
    private int tileSizeY=1;
    private int dispWidth;
    private int dispHeight;
    private int posX;         // Als Pixel in Karte
    private int posY;
    
    // Abgeleitete Werte
    private int mapTilesX;  // Anzahl tiles einer Map-Zeile
    
    // Display abhängige Werte
	private int leftX;
	private int rightX;
	private int upY;
	private int downY;

	// Base-File-Name auf SD-Card
	//private String mapBaseFileName = "map";
	
	// Hashmap mit <Katenname, baseFilename>
	private HashMap<String, String> mapNames;
	
	// Interne GeoPosition
	private double internLat;
	private double internLon;
    
	// Referenz auf eingelesene Daten
	private MPHData mph = null;
	private MPRData mpr = null;
	private ArrayList<Image> tiles = null;

	///////////////////////////////////////////////////////////////////
	// Konstruktoren
	///////////////////////////////////////////////////////////////////

	public GraphicTestView(Context context) {
		super (context);
		//File file = new File(Environment.getExternalStorageDirectory(), mapBaseFileName);
		initMapView(null);
	}
	
	public GraphicTestView(Context context, AttributeSet attrs) {
		super (context, attrs);
		getCustomAttribs(context, attrs);
		//File file = new File(Environment.getExternalStorageDirectory(), mapBaseFileName);
		initMapView(null);
	}
	
	private void getCustomAttribs(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
			    R.styleable.GraphicTestView);
			 
			int N = a.getIndexCount();
			for (int i = 0; i < N; ++i)
			{
			    int attr = a.getIndex(i);
			    switch (attr)
			    {
			        /*case R.styleable.GraphicTestView_mapFile:
			        	mapBaseFileName = a.getString(attr);
			            break;*/
			        case R.styleable.GraphicTestView_posX:
			        	posX = a.getInteger(attr, 1000);
			            break;
			        case R.styleable.GraphicTestView_posY:
			        	posY = a.getInteger(attr, 1000);
			            break;
			    }
			}
			a.recycle();
	}

	
	///////////////////////////////////////////////////////////////////
	// Tiles Management
	///////////////////////////////////////////////////////////////////
	/**
	 * Funktion ermittelt die 4 tiles an den Ecken 
	 */
	private void eckTiles() {
		if (mph==null) return;
		int d1;  // Links oben
		int d2;  // Rechts oben
		int d3;  // Links unten
		int d4;  // Rechts unten
		
		tiles.clear();
		
		d1 = mpr.getTileFromMapXY(posX-leftX, posY-upY);
		d2 = mpr.getTileFromMapXY(posX+rightX, posY-upY);
		d3 = mpr.getTileFromMapXY(posX-leftX, posY+downY);
		d4 = mpr.getTileFromMapXY(posX+rightX, posY+downY);
		
		// Anzahl der benötigten Tiles berechnen
		// X-Richtung
		int dispX = getCntTiles(posX-leftX, dispWidth, tileSizeX);
		int dispY = getCntTiles(posY-upY, dispHeight, tileSizeY);
		Log.w("eckTiles", "Tiles="+dispX+"x"+dispY);
		
		if (d1>=0 && d2>=0 && d3>=0 && d3>=0) {
			// Eine Liste der tiles erstellen
			int k=d1;
			int tilesInDispX = d2-d1;
			while (k < d4) {
				for (int i=0; i<=tilesInDispX; i++) {
					Image tile = Image.getTileImage(mpr, k + i);
					if (tile!=null) tiles.add(tile);
				}
				k = k + mapTilesX;
			}
			Log.w("eckTiles", "CntTiles="+tiles.size());
		} else {
			// Sonderfall: Karte am Rand
			if (d4>=0) {
				// Rechte untere Ecke ist von Karte bedeckt
				int aktX;
				int aktY = d4;
				Point d4Pt = mpr.getMapXYFromTile(d4);
				for (int i=0; i<dispY; i++) {
					aktX = aktY;
					for (int k=0; k<dispX; k++) {
						if (aktX<0) {
							int blTX = d4Pt.x - k*tileSizeX;
							int blTY = d4Pt.y - i*tileSizeY;
							Image blankTile = new Image(new Point(blTX,blTY), tileSizeX, tileSizeY);
							tiles.add(blankTile);
						} else {
							Image tile = Image.getTileImage(mpr, aktX);
							if (tile!=null) tiles.add(tile);
						}
						aktX = eckLeft(aktX);
					}
					aktY = eckUp(aktY);
				}
			} else if (d3>=0) {
				// Karten Kachel mindestens links unten
				Log.w("eckTiles", "noch nicht implementiert");
			} else if (d1>=0) {
				// Karten Kachel mindestens links oben
				Log.w("eckTiles", "noch nicht implementiert");
			} else if (d2>=0) {
				// Karten Kachel mindestens rechts oben
				Log.w("eckTiles", "noch nicht implementiert");
			} else {
				// Karte nicht sichtbar
				Log.w("eckTiles", "karte nicht sichtbar");
				// Nur Blank-Kacheln darstellen
				int offX = posX-leftX;
				int offY = posY-upY;
				int maxX = ((dispWidth-1)/tileSizeX)+1;
				int maxY = ((dispHeight-1)/tileSizeY)+1;
				for (int y=0; y<maxY; y++) {
					int py = y * tileSizeY + offY;
					for (int x=0; x<maxX; x++) {
						Image blankTile = new Image(new Point(x*tileSizeX+offX,py), tileSizeX, tileSizeY);
						tiles.add(blankTile);
					}
				}
			}
		}
	}
	
	private int eckLeft(int vor) {
		if (vor<0) return vor-1;
		if (vor%mapTilesX == 0) return -1;
		return vor-1;
	}
	
	private int eckUp(int vor) {
		if (vor<0) return vor-1;
		if (vor/mapTilesX == 0) return -1;
		return vor-mapTilesX;
	}
	
	// Berechnet die Tiles im Display in einer Richtung
	// offs = Abstand der ersten Kachel zum Displayrand
	private int getCntTiles(int offs, int dispSize, int tileSize) {
		offs = (((offs / Math.abs(offs) < 0) ? (Math.abs(offs) % tileSize) : tileSize - (Math.abs(offs) % tileSize)));
		int rechts = ((dispSize - offs - 1) / tileSize) + 1;
		int links = (offs==0 ? 0 : 1);
		return links + rechts;
	}
	
	/**
	 * Alle tiles in Liste zeichnen
	 */
	private void paintMap(Canvas canvas) {

		// Tiles festlegen
		eckTiles();
		
		// Offset wegen Display-Geometrie und aktueller Position
		int offsetX = leftX - posX; 
		int offsetY = upY - posY;

		if (tiles!=null) {
			for (Image tile:tiles) {
				if (tile.getAry()!=null)  {
				int img[] = tile.getAry();
				int posx = tile.getX() + offsetX;
				int posy = tile.getY() + offsetY;
				
				canvas.drawBitmap(img, 0, tileSizeX, posx, posy, tileSizeX, tileSizeY, false, null);
				}
			}
		}
		Log.w("Paint", "Finish Paint");
	}
	
	/**
	 * Init - vom Konstruktor aufgerufen
	 */
	private void initMapView(String mapFileName) {
		
        G = Pref_Manager.getInstance(getContext());
        
        if (mapFileName==null) {
	        // Alle Maps suchen
	        getMapNames();
	        // Letzte angezeigte Karte wieder laden
	        if (G.isLastMap()) {
	        	String mapName = G.getLastMap();
	        	mapFileName = mapNames.get(mapName);
	        } else {
	        	// Programm wahrscheinlich zum ersten Mal geladen
	        	// Die erste gefundene Karte anzeigen (oder die mit dem größten Maßstab)
	        	// Oder ohne Aktion zurück, wenn keine Karte vorhanden
	        	if (mapNames.size() > 0) {
	        		mapFileName = getFirstMapFileName();
	        		if (mapFileName==null) return;
	        	} else {
	        		return; // Keine Karte gefunden
	        	}
	        }
        }

		tiles = new ArrayList<Image>();
		
		mph = loadMphFromFile(mapFileName);
		if (mph!=null) {
			mpr = loadMprFromFile(mapFileName);

			tileSizeX = mph.getXTileSize();
		    tileSizeY = mph.getYTileSize();
		    
		    // Letzte Position der Karte laden, falls vorhanden
	        if (G.isPosition(getName())) {
	        	setPosition(G.getPosition(getName()));
	        } else {
	        	// Wenn Karte noch nie positioniert war, in Mitte der Karte
	        	setPosition(new Point(mph.getMaxXPixel() / 2, mph.getMaxYPixel() / 2 ));
	        }
	        
	        // Center Meridian setzen
	        setCenterMeridian(G.getCenterMeridian(getName()));

		    
		    // Abgeleitete Werte
		    mapTilesX = mph.getXTiles();  // Anzahl tiles einer Map-Zeile

		    // Wenn erfolgreich geladen, mapName als geladen speichern
		    G.setLastMap(mph.MapName);
		    Log.w("initMapView", "Map-Name=" + mph.MapName);
		}
	}
	
	/**
	 * Den Dateinamen der ersten Karte holen
	 */
	private String getFirstMapFileName() {
		Iterator<String> allKeys = mapNames.keySet().iterator();
		while (allKeys.hasNext()) {
			String key = allKeys.next();
			return mapNames.get(key);
		}
		return null;
	}
	
	/**
	 * MPH-Datei (Info) laden
	 */
	private MPHData loadMphFromFile(String mapBaseFileName) {
		MPHData mph;
		// Erst mal eine mph-datei von SD-Karte lesen
		String fName = mapBaseFileName + MPH;
        try {
			mph = new MPHData(fName, false);
		} catch (IOException e) {
			e.printStackTrace();
			mph = null;
		} catch (GeoReaderException e) {
			e.printStackTrace();
			mph = null;
		}
		return mph;
	}		

			
		    //mapWidth = mph.getMaxXPixel()+1;   Werden später vielleicht noch gebraucht
		    //mapHeight = mph.getMaxYPixel()+1;  Werden später vielleicht noch gebraucht
	
			
	/**
	 * MPR-Datei (Kacheln) laden
	 */
	private MPRData loadMprFromFile(String mapBaseFileName) {
		MPRData mpr;
		// mpr-Tile Info lesen
		String fName = mapBaseFileName + MPR;
        try {
        	mpr = new MPRData(fName, mph);
		} catch (IOException e) {
			e.printStackTrace();
			mpr = null;
		} 
		return mpr;
	}
		
	
	/**
	 * Hier wird die View aktualisiert
	 */
	protected void onDraw(Canvas canvas) {
		paintMap(canvas);
	}
	
	/**
	 * Bei Größenänderung der View (wird auch am Anfang aufgerufen)
	 */
	protected void onSizeChanged (int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		// Center bestimmen
		// ---> Ändert sich immer, wenn sich Größe der Anzeige ändert
		// Display center
	    dispWidth = w;
	    dispHeight = h;
		int centerX = dispWidth / 2;
		int centerY = dispHeight / 2;
		leftX = centerX;
		rightX = (dispWidth-1) - centerX;
		upY = centerY;
		downY = (dispHeight-1) - centerY;
		
		// Die Eck-Tiles neu bestimmen 
		eckTiles();

		Log.w("onSizeChanged", "new width="+w+"new height="+h+"old width="+oldw+"old height="+oldh);
	}
	
	/**
	 * Bearbeiten von Touch-Events
	 */
	private boolean moving = false;
	private int tapX;
	private int tapY;
	public boolean onTouchEvent (MotionEvent event) {
		//Log.w("onTouchEvent", "Wird grundsätzlich aufgerufen");
		int x = (int)event.getX();
		int y = (int)event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(x-tapX) > 32 || Math.abs(y-tapY) > 32) {
				moving = true;
				Log.w("onTouchEvent", "MoveToNew Paint: x="+x+" y="+y);
				// Neu zeichnen anstossen
				posX = posX + (tapX-x);
				posY = posY + (tapY-y);
				invalidate();
				tapX = x;
				tapY = y;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			tapX = x;
			tapY = y;
			Log.w("onTouchEvent", "Down: x="+x+" y="+y);
			break;
		case MotionEvent.ACTION_UP:
			moving = false;
			Log.w("onTouchEvent", "Up: x="+x+" y="+y);
			// Neue Position speichern
			setPosition(new Point(posX + (tapX-x), posY + (tapY-y)));
			Log.w("paintMap", "Loaded tiles before=" + Image.getLoadedTiles());
			Image.reduceTiles(tiles);
			Log.w("paintMap", "Loaded tiles after=" + Image.getLoadedTiles());
			// Neu zeichnen anstossen
			invalidate();
			break;
		}
		return super.onTouchEvent(event);
	}
	
	///////////////////////////////////////////////////////////////////
	// Private Routinen
	///////////////////////////////////////////////////////////////////
	private void setPosition(Point point) {
		posX = point.x;
		posY = point.y;
		// Kartenposition speichern
		G.setPosition(new Point(posX, posY), getName());
		setInternGeoPoint(posX, posY);
		sendInternGeoPoint();
	}
	
	private void setInternGeoPoint(int x, int y) {
		// Set
		GeodeticPoint geoPt = mph.getCalPt().getGeoKoordinate(
				x, y, mph.getMasstab(), mph.getPixelMM());
		internLat = geoPt.GeoLat * 180 / Math.PI;
		internLon = geoPt.GeoLon * 180 / Math.PI;
	}

	private void sendInternGeoPoint() {
		// Send
		if (msgHnd==null) return;
		Bundle obj = new Bundle();
		obj.putDouble("lat", internLat);
		obj.putDouble("lon", internLon);
		Message msg = Message.obtain(msgHnd, MSG_MAP_POSITION, obj);
		msgHnd.sendMessage(msg);
	}

	///////////////////////////////////////////////////////////////////
	// Public Interface
	///////////////////////////////////////////////////////////////////
	public void setGeoPosition(double lat, double lon) {
    	GeodeticPoint geoPt = new GeodeticPoint(lat,lon,0);
    	// Umrechnung 
    	int x = mph.getCalPt().getMapPixelX(geoPt, mph.getMasstab(), mph.getPixelMM());
    	int y = mph.getCalPt().getMapPixelY(geoPt, mph.getMasstab(), mph.getPixelMM());
    	setPosition(new Point(x,y));
    	invalidate();
	}
	
	public void setCenterMeridian(double centerMeridian) {
		mph.makeCalPt(centerMeridian);
	}
	
	public void setRelPosition() {
		posX = (int)(mapTilesX * tileSizeX * (tapX/(float)dispWidth));
		posY = (int)(mph.yTiles * tileSizeY * (tapY/(float)dispHeight));
		Log.w("setRelPosition", "posX="+posX);
		Log.w("setRelPosition", "posY="+posY);
		invalidate();
	}
	
	public Point getPosition() {
		return new Point(posX, posY);
	}

	public String getName() {
		return (mph==null ? "no map available" : mph.MapName);
	}
	
	public boolean isMoving() {
		return moving;
	}
	
	public void loadMap(String mapName) {
		initMapView(mapNames.get(mapName));
		invalidate();
	}
	
	public  String[] getMapNames() {
		// Suche nach Karten
		
		mapNames = new HashMap<String, String>();

		// Filter
		FilenameFilter filter = new FilenameFilter(){
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(MPH);
			}};
			
		// Suche im Rootverzeichnis der SD-Karte
		// Suche im Verzeichnis des Pakets
			
		ArrayList<String> aryMapNames = new ArrayList<String>();
		
		File root = Environment.getExternalStorageDirectory();
		// Schleife über alle Files
		for (File file: root.listFiles(filter)) {
			int len = file.getName().length();
			String base = file.getName().substring(0, len-4);
			File file1 = new File(file.getParent(), base+MPH);
			File file2 = new File(file.getParent(), base+MPR);
			String fBase = new File(file.getParent(), base).getAbsolutePath();
			// Wenn beide existieren
			if (file1.exists() && file2.exists()) {
				// Lesen des Karten-Namens aus file1
				MPHData mph = loadMphFromFile(fBase);
				if (mph!=null) {
					String mapName = mph.MapName;
					// Zu einer Map hinzufügen <Kartenname, baseFilename>
					mapNames.put(mapName, fBase);
					aryMapNames.add(mapName);
				}
			}
		}
		return (aryMapNames.size()==0 ? null : aryMapNames.toArray(new String[1]));
	}
	
	public void setHandler(Handler msgHnd) {
		this.msgHnd = msgHnd;
		// Gleich eine Position senden
		sendInternGeoPoint();
	}
}
