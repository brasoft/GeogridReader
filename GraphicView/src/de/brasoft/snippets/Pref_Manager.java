package de.brasoft.snippets;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;

public class Pref_Manager {
	
	private static final String PREF = "myPref";
	private static final String KEY_POS_X = "posx";	
	private static final String KEY_POS_Y = "posy";	
	
	private SharedPreferences pref = null;
	private static Pref_Manager instance = null;
	
	private Pref_Manager(Context con) {
		pref = con.getSharedPreferences(PREF, Context.MODE_PRIVATE);
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

	
}
