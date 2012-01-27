package de.brasoft.snippets;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class Thr_NetPosition implements Runnable {
	
	private static final int TIME_OUT = 10000;  // 10 sec
	
	private static final int MSG_TIME_OUT = 1;
	private static final int MSG_POSITION = 2;
	
	private Handler handler;
	private Context con;
    private localHandler lHandler;

	
	public Thr_NetPosition(Context con, Handler handler) {
		this.con = con;
		this.handler = handler;
	}

	public LocationListener locListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
    		Bundle obj = new Bundle();
    		obj.putDouble("lat", location.getLatitude());
    		obj.putDouble("lon", location.getLongitude());
    		Message message = Message.obtain(handler, GraphicViewActivity.MSG_MY_POSITION, obj);
    		handler.sendMessage(message);
    		lHandler.sendEmptyMessage(MSG_POSITION);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	@Override
	public void run() {
        // Abfragen, ob NET enabled
		LocationManager lm = (LocationManager) con.getSystemService(Context.LOCATION_SERVICE);
        boolean enabNET = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    	if (enabNET) {
            Looper.prepare();
    		lHandler = new localHandler();
            lHandler.sendEmptyMessageDelayed(MSG_TIME_OUT, TIME_OUT);
    		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
            Looper.loop();
            lm.removeUpdates(locListener);
    	}
	}
	
	/**
	 * Queue
	 */
	class localHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            
            case MSG_TIME_OUT:
            	handler.sendEmptyMessage(GraphicViewActivity.MSG_NO_POSITION);
            	Looper.myLooper().quit();
            	break;
            	
            case MSG_POSITION:
            	lHandler.removeMessages(MSG_TIME_OUT);
            	Looper.myLooper().quit();
            	break;
            	
            default:
                super.handleMessage(msg);
        	}
        }
	}
	

}
