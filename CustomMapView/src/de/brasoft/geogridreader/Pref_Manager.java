package de.brasoft.geogridreader;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;

public class Pref_Manager {
	
	private static final String PREF = "myPref";
	private static final String KEY_POS_X = "posx";	
	private static final String KEY_POS_Y = "posy";	
	private static final String KEY_CENTER_MERIDIAN = "centermeridian";	
	private static final String KEY_LAST_MAP = "lastmap";
	
	private SharedPreferences pref = null;
	private static Pref_Manager instance = null;
	
	private HashMap<String, Object> factory;
	
	private Pref_Manager(Context con) {
		pref = con.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		factory = new HashMap<String, Object>();
		setFactory();
	}

	public static Pref_Manager getInstance(Context con) {
		if (instance==null) {
			instance = new Pref_Manager(con);
		}
		return instance;
	}
	
	public boolean isPosition(String mapName) {
		return pref.contains(mapName+KEY_POS_X) && pref.contains(mapName+KEY_POS_Y);
	}
	
	public boolean isLastMap() {
		return pref.contains(KEY_LAST_MAP);
	}
	
	public void setPosition(Point point, String mapName) {
		Editor edit = pref.edit();
		edit.putInt(mapName+KEY_POS_X, point.x);
		edit.putInt(mapName+KEY_POS_Y, point.y);
		edit.commit();
	}
	
	public Point getPosition(String mapName) {
		return new Point(	pref.getInt(mapName+KEY_POS_X, 0),
							pref.getInt(mapName+KEY_POS_Y, 0));
	}
	
	public String getLastMap() {
		return pref.getString(KEY_LAST_MAP, "");
	}
	
	public void setLastMap(String mapName) {
		Editor edit = pref.edit();
		edit.putString(KEY_LAST_MAP, mapName);
		edit.commit();
	}

	public double getCenterMeridian(String mapName) {
		if (!pref.contains(mapName+KEY_CENTER_MERIDIAN)) {
			// Factory, wenn noch nicht gesetzt
			if (factory.containsKey(mapName+KEY_CENTER_MERIDIAN)) {
				Float value = (Float)factory.get(mapName+KEY_CENTER_MERIDIAN);
				Editor edit = pref.edit();
				edit.putFloat(mapName+KEY_CENTER_MERIDIAN, value);
				edit.commit();
			}
		}
		return pref.getFloat(mapName+KEY_CENTER_MERIDIAN, (float)6.0);
	}
	
	///////////////////////////////////////////////////////////////////
	// Factory-Werte
	///////////////////////////////////////////////////////////////////

	private void setFactory() {
		factory.put("Bundesrepublik 1:1 Mio" + KEY_CENTER_MERIDIAN, (float)9.0);
		factory.put("TÜK 1:200000 Bayern" + KEY_CENTER_MERIDIAN, (float)9.0);
		factory.put("Top. Karte 1:50000 Bayern (Süd)" + KEY_CENTER_MERIDIAN, (float)12.0);
		factory.put("Digitale Ortskarte 1:10000 Bayern (Süd)" + KEY_CENTER_MERIDIAN, (float)12.0);
		factory.put("Übersichtskarte 1:500 000 Bayern (ÜK500)" + KEY_CENTER_MERIDIAN, (float)12.0);
	}
}
