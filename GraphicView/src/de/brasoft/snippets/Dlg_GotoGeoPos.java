package de.brasoft.snippets;

import java.util.Hashtable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

	public class Dlg_GotoGeoPos extends Dlg_Base {
		private static Hashtable<Dlg_Manager, Dlg_Base> instances = new Hashtable<Dlg_Manager, Dlg_Base>(); 

		private double latitude;
		private double longitude;
		private int plz;
		private boolean isPlz;
		private boolean isLatLon;
		private EditText lat;
		private EditText lon;
		
		
		public static Dlg_Base getInstance(Context con, Dlg_Manager dm, IDialogEnd end) {
			if (!instances.containsKey(dm)) {
				instances.put(dm, new Dlg_GotoGeoPos(con, end));
			}
			return instances.get(dm);
		}

		private Dlg_GotoGeoPos(Context con, IDialogEnd end) {
			super(con, end);
			LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.position, null);
			lat = (EditText)layout.findViewById(R.id.editLatitude);
			lon = (EditText)layout.findViewById(R.id.editLongitude);

	    	AlertDialog.Builder builder = new AlertDialog.Builder(con);
	    	builder.setPositiveButton("Ok", btnOk);
	    	builder.setNegativeButton("Cancel", btnCancel);
	    	builder.setTitle("Gehe zu Position");
	    	builder.setView(layout);
	    	dialog = builder.create();
		}
		
		DialogInterface.OnClickListener btnOk = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
				latitude = 0;
				latitude = Double.parseDouble(lat.getText().toString());
				longitude = Double.parseDouble(lon.getText().toString());
				if (latitude <= 90 && latitude >= -90 &&
					longitude <= 180  && longitude >= -180) {
					isLatLon = true;
					okClick();
				}
				} catch (NumberFormatException e) {
					// Zahl nicht vollständig eingegeben
					if (latitude != 0) {
						plz = (int) latitude;
						isPlz = true;
						okClick();
					}
				}
			} };
		
		DialogInterface.OnClickListener btnCancel = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			} };
			
		@Override
		public void prepare() {
			lat.setText("");
			lon.setText("");
			isPlz = false;
			isLatLon = false;
		}
		
		///////////////////////////////////////////////////////////////
		// Public Interface
		///////////////////////////////////////////////////////////////
		public double getLatitude() {
			return latitude;
		}
		
		public double getLongitude() {
			return longitude;
		}
		
		public boolean isLatLon() {
			return isLatLon;
		}
		
		public boolean isPlz() {
			return isPlz;
		}
		
		public int getPlz() {
			return plz;
		}
}
