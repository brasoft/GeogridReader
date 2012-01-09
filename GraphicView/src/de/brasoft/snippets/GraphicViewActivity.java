package de.brasoft.snippets;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import de.brasoft.geogridreader.GraphicTestView;

public class GraphicViewActivity extends Activity {
	
	// Menü-Konstanten
	private static final int MENU_LOAD_MAP = Menu.FIRST;
	private static final int MENU_TEST_LOCATION = Menu.FIRST+1;
	private static final int MENU_GOTO_POSITION = Menu.FIRST+2;
	private static final int MENU_NET_POSITION = Menu.FIRST+3;
	
	// Message-Konstanten
	private static final int MSG_MAP_POSITION = 100;
	//private static final int MSG_MAP_INTERN_POSITION = 101; 
	public static final int MSG_MY_POSITION = 102;
	
	private static final String PLZ_FILE = "plzHashAndroid.txt";
	
	//private Pref_Manager G;
	private GraphicTestView map;
	private MainHandler mainHandler;
	
	// Format
    private DecimalFormat df;

	
	// Handler
	// Click on Map
	View.OnClickListener clickOnMap = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.w("setOnClickListener", "-------- Click kommt durch ---------");
			//map.setRelPosition();
		}};

	// Long Click on Map
	View.OnLongClickListener clickLongOnMap = new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			if (!map.isMoving()) {
				Log.w("setOnClickListener", "-------- Long Click kommt durch ---------");
				IDialogEnd dlgEnd = new IDialogEnd() {
					@Override
					public void onOkClick(Dlg_Base costomDialog) {
					}};

				// Popup
		    	Dlg_Manager dlgMan = Dlg_Manager.getInstance(GraphicViewActivity.this);
		    	Dlg_Base dialog = Dlg_Popup_Main.getInstance(GraphicViewActivity.this, dlgMan, dlgEnd);
		    	int id = dlgMan.addDialog(dialog);
		    	showDialog(id);

			}
			return true;
		}};
		
	// Click bei Map-Auswahl
	IDialogEnd clickOnMapSelect = new IDialogEnd() {
		@Override
		public void onOkClick(Dlg_Base costomDialog) {
			String mapName = ((Dlg_ListAuswahl) costomDialog).getItem();
			map.loadMap(mapName);
	        // Set Title
	        setTitle(map.getName());
		}};	
		
	// Click bei GotoGeoPosition
	IDialogEnd clickOnGotoPosition = new IDialogEnd() {
		@Override
		public void onOkClick(Dlg_Base costomDialog) {
			double lat;
			double lon;
			if (((Dlg_GotoGeoPos) costomDialog).isLatLon()) {
				// Direkte angabe von Breite und Länge
				lat = ((Dlg_GotoGeoPos) costomDialog).getLatitude();
				lon = ((Dlg_GotoGeoPos) costomDialog).getLongitude();
			} else if (((Dlg_GotoGeoPos) costomDialog).isPlz()) {
				// Suche nach Plz
				int plz = ((Dlg_GotoGeoPos) costomDialog).getPlz();
				PlzSearch plzS = new PlzSearch(GraphicViewActivity.this, PLZ_FILE, plz);
				if (plzS.getPlz()==0) return; // Wenn Suche erfolglos
				lat = plzS.getLat();
				lon = plzS.getLon();
			} else {
				return;
			}
			map.setGeoPosition(lat, lon);
		}};	
			
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Format setzen
        df = (DecimalFormat) NumberFormat.getInstance(Locale.US); // Dezimalpunkt immer als Punkt lassen
        df.applyPattern("0.000"); // 3 Nachkommastellen
        
        //G = Pref_Manager.getInstance(this);
        mainHandler = new MainHandler();
        setContentView(R.layout.main);
        map = (GraphicTestView)findViewById(R.id.geogridMap);
        
        // Click-Handler setzen
        map.setOnClickListener(clickOnMap);
        
        // Click-Handler setzen
        map.setOnLongClickListener(clickLongOnMap);
        
        // Message Handler übergeben
        map.setHandler(mainHandler);
        
        // Set Title
        setTitle(map.getName());
    }
    
    @Override
    protected void onPause () {
    	super.onPause();
    }
    
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_LOAD_MAP, Menu.NONE, "Karte laden");
		menu.add(Menu.NONE, MENU_TEST_LOCATION, Menu.NONE, "Testlocation");
		menu.add(Menu.NONE, MENU_GOTO_POSITION, Menu.NONE, "Gehe zu");
		menu.add(Menu.NONE, MENU_NET_POSITION, Menu.NONE, "Net Position");
		
		return result;
	}

	public boolean onOptionsItemSelected( MenuItem item) {
		switch (item.getItemId()) {

		case MENU_LOAD_MAP:
			// Neue Karte laden (Dialog)
		{
			String[] names = map.getMapNames();
	    	Dlg_Manager dlgMan = Dlg_Manager.getInstance(this);
	    	Dlg_Base dialog = Dlg_ListAuswahl.getInstance(this, dlgMan, clickOnMapSelect, names);
	    	int id = dlgMan.addDialog(dialog);
	    	showDialog(id);
		}
			return true;
			
		case MENU_TEST_LOCATION:
			map.setGeoPosition(48.137, 11.576);
			return true;
			
		case MENU_GOTO_POSITION:
			// Dialog für Position aufrufen
		{
	    	Dlg_Manager dlgMan = Dlg_Manager.getInstance(GraphicViewActivity.this);
	    	Dlg_Base dialog = Dlg_GotoGeoPos.getInstance(GraphicViewActivity.this, dlgMan, clickOnGotoPosition);
	    	int id = dlgMan.addDialog(dialog);
	    	showDialog(id);
		}
			return true;
			
		case MENU_NET_POSITION:
			// Thread zu Positionsbestimmung aufrufen
		{
			new Thread(new Thr_NetPosition(this, mainHandler)).start();
		}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    /**
     * Dialog erstellen
     */
    protected Dialog onCreateDialog(int id) {
    	Dlg_Manager dlgMan = Dlg_Manager.getInstance(this);
    	return dlgMan.getDialog(id);
    }
    
    /** 
     * Dialog mit Daten füllen
     */	
	protected void onPrepareDialog(int id, Dialog dialog) {
    	Dlg_Manager dlgMan = Dlg_Manager.getInstance(this);
    	dlgMan.getCustomDialog(id).prepare();
		super.onPrepareDialog(id, dialog);
	}
	
	/**
	 * Queue
	 */
	class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            
            case MSG_MAP_POSITION:
            {
            	Bundle obj = (Bundle)msg.obj;
            	double lat = obj.getDouble("lat");
            	double lon = obj.getDouble("lon");
            	TextView bottom = (TextView)findViewById(R.id.bottom);
            	String text = df.format(lat) + " " + df.format(lon);
            	bottom.setText(text);
            }
            	break;

            case MSG_MY_POSITION:
            {
            	Bundle obj = (Bundle)msg.obj;
            	double lat = obj.getDouble("lat");
            	double lon = obj.getDouble("lon");
    			map.setGeoPosition(lat, lon);
            }
            	break;
            	
            default:
                super.handleMessage(msg);
        	}
        }
	}
}