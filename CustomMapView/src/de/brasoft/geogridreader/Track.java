package de.brasoft.geogridreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.location.Location;

public class Track {
	
	private static final int XLINE = 1;
	private static final int YLINE = 2;
	
	private static ArrayList<Track> tracks = new ArrayList<Track>();
	
	private ArrayList<Location> points;
	private int color;
	
	public Track(File fTrack, int color) {
		this.color = color;
		points = new ArrayList<Location>();
		parseFmtASC(fTrack, points,	"XKoord", "YKoord");
		tracks.add(this);
	}

	private void parseFmtASC(File fTrack, ArrayList<Location> points,
			String xkey, String ykey) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(fTrack));
			// Datei zeilenweise lesen
			String line;
			int status = XLINE;
			double xval=0;
			double yval=0;
			while ((line = in.readLine()) != null)   {
				switch(status) {
				case XLINE:
					// wenn Line xkey enthält
					if (line.startsWith(xkey)) {
						// xval belegen
						int idx = line.indexOf('=');
						xval = Double.parseDouble(line.substring(idx+1));
						// status auf YLINE
						status = YLINE;
					}
					break;
				case YLINE:
					// wenn Line ykey enthält
					if (line.startsWith(ykey)) {
						// yval belegen
						int idx = line.indexOf('=');
						yval = Double.parseDouble(line.substring(idx+1));
						// status auf XLINE
						status = XLINE;
						// Punkt x,y in ArrayList speichern
						Location tmp = new Location("import");
						tmp.setLatitude(yval);
						tmp.setLongitude(xval);
						points.add(tmp);
					}
					break;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	public double getLen() {
		// Die Länge aller Teilstücke summieren
		Location first = null;
		Location second = null;
		double sum=0;
		for (Location pt : points) {
			first = second;
			second = pt;
			if (first!=null) {
				sum = sum + first.distanceTo(second);
			}
		}
		return sum;
	}

	public Location getCenterPosition() {
		// Die Länge aller Teilstücke summieren
		double lat=0;
		double lon=0;
		int cnt=0;
		for (Location pt : points) {
				lat = lat + pt.getLatitude();
				lon = lon + pt.getLongitude();
				cnt++;
		}
		Location retw = new Location("ceterPosition");
		retw.setLatitude(lat/cnt);
		retw.setLongitude(lon/cnt);
		return retw;
	}
	
	public static ArrayList<Track> getAllTracks() {
		return tracks;
	}
	
	public ArrayList<Location> getPoints() {
		return points;
	}

	public int getColor() {
		return color;
	}
}
