package de.brasoft.geogridreader;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import android.graphics.RectF;


// Enthält Infos über eine Map. Item einer Liste aller vorhandenen Maps 
// (auch nicht geladene)
public class Info_MapSetItem {
	
	static private HashMap<String, Info_MapSetItem> allMaps;
	
	private String baseFileName;
	private RectF mapCoverage;
	private int massStab;

	public Info_MapSetItem(String baseFileName, int massStab, HashMap<String, Info_MapSetItem> allMaps) {
		this.baseFileName =  baseFileName;
		this.massStab = massStab;
		Info_MapSetItem.allMaps = allMaps;
		this.mapCoverage = null;
	}
	
	public String getFileName() {
		return baseFileName;
	}

	private int getMassStab() {
		return massStab;
	}
	
	/*private boolean matchZoom(int mstb, boolean zoomIn, double lat, double lon) {
		boolean cond1 = mapCoverage.contains((float)lon, (float)lat);
		boolean cond2 = zoomIn ? massStab < mstb : massStab > mstb;
		return cond1 && cond2;
	}*/
	
	private boolean isInMap(double lat, double lon) {
		return mapCoverage.contains((float)lon, (float)lat);
	}

	public void setMapCoverage(RectF rect) {
		this.mapCoverage = rect;
	}
	
	private String getMapCoverage() {
        // Format setzen
		DecimalFormat df;
        df = (DecimalFormat) NumberFormat.getInstance(Locale.US); // Dezimalpunkt immer als Punkt lassen
        df.applyPattern("0.000"); // 3 Nachkommastellen
		
		float lon = mapCoverage.left;
		float lat = mapCoverage.bottom;
		float width = mapCoverage.width();
		float height = mapCoverage.height();
        String text = df.format(lat) + "/" + df.format(lon) + 
        " (w=" + df.format(width) + "/h=" + df.format(height) + ")";
		return text;
	}
	
	/*public static String[] getZoomMapNames(int mstb, boolean zoomIn, double lat, double lon) {
		ArrayList<String> aryMapNames = new ArrayList<String>();
		Iterator<String> allKeys = allMaps.keySet().iterator();
		while (allKeys.hasNext()) {
			String key = allKeys.next();
			if (allMaps.get(key).matchZoom(mstb, zoomIn, lat, lon)) {
				aryMapNames.add(key);
			}
		}
		return (aryMapNames.size()==0 ? null : aryMapNames.toArray(new String[1]));
	}*/

	public static String[] getZoomInMapNames(int mstb, double lat, double lon) {
		ArrayList<String> aryMapNames = new ArrayList<String>();
		Iterator<String> allKeys = allMaps.keySet().iterator();
		int min = Integer.MIN_VALUE;
		// Nächst kleineren Maßstab suchen
		while (allKeys.hasNext()) {
			String key = allKeys.next();
			int tmp = allMaps.get(key).getMassStab();
			if ((tmp<mstb) && (tmp>min)) min = tmp;
		}
		if (min == Integer.MIN_VALUE) return null; // Kein kleinerer Maßstab mehr
		
		// Alle Karten mit diesem (min) Maßstab suchen
		allKeys = allMaps.keySet().iterator();
		while (allKeys.hasNext()) {
			String key = allKeys.next();
			if ((allMaps.get(key).getMassStab() == min) &&
					allMaps.get(key).isInMap(lat, lon)) {
				aryMapNames.add(key);
			}
		}
		return (aryMapNames.size()==0 ? null : aryMapNames.toArray(new String[1]));
	}

	public static String[] getZoomOutMapNames(int mstb, double lat, double lon) {
		ArrayList<String> aryMapNames = new ArrayList<String>();
		Iterator<String> allKeys = allMaps.keySet().iterator();
		int max = Integer.MAX_VALUE;
		// Nächst kleineren Maßstab suchen
		while (allKeys.hasNext()) {
			String key = allKeys.next();
			int tmp = allMaps.get(key).getMassStab();
			if ((tmp>mstb) && (tmp<max)) max = tmp;
		}
		if (max == Integer.MIN_VALUE) return null; // Kein kleinerer Maßstab mehr
		
		// Alle Karten mit diesem (max) Maßstab suchen
		allKeys = allMaps.keySet().iterator();
		while (allKeys.hasNext()) {
			String key = allKeys.next();
			if ((allMaps.get(key).getMassStab() == max) &&
					allMaps.get(key).isInMap(lat, lon)) {
				aryMapNames.add(key);
			}
		}
		return (aryMapNames.size()==0 ? null : aryMapNames.toArray(new String[1]));
	}

	
	public static String getHtmlInfo() {
		String retw = "<body><div class='main'>";
		Iterator<String> allKeys = allMaps.keySet().iterator();
		while (allKeys.hasNext()) {
			String key = allKeys.next();
			retw = retw + "<div class='mapheader'>" + key + "</div><ul>";
			String p1 = allMaps.get(key).getFileName();
			retw = retw + "<li>" + "Dateiname = " + p1 + "</li>";
			String p2 = allMaps.get(key).getMapCoverage();
			retw = retw + "<li>" + "Überdeckung = " + p2 + "</li>";
			int p3 = allMaps.get(key).getMassStab();
			retw = retw + "<li>" + "Maßstab = 1:" + p3 + "</li>";
			retw = retw + "</ul>";
			retw = retw + "<div class='seperator'></div>";
		}
		retw = retw + "</div></body></html>";
		return retw;
	}

}
