package de.brasoft.snippets;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.webkit.WebView;

public class Act_InfoScreen extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Parameter auslesen
        String htmlContent = "<body>Keine Info vorhanden</body></html>"; // Wenn kein Parameter vorhanden
		String parName  = R.string.class.getPackage().getName() + ".showInfo";
        if (getIntent().hasExtra(parName)) {
        	htmlContent = getIntent().getExtras().getString(parName);
        }

        
        // Der Hilfe-Text als WebView
        WebView webview = new WebView(this);
        setContentView(webview);
        htmlContent = LoadDataFromAsset("mapinfoheader") + htmlContent;
		webview.loadDataWithBaseURL("", htmlContent, "text/html", "utf-8", null);
    }
    
    private String LoadDataFromAsset(String filename) {
        AssetManager am = getAssets();
        String html="";
        InputStream input=null;

        try {
     	   	input = am.open(filename);
 			int len = input.available();
 			byte[] buffer = new byte[len];
 			int erg = input.read(buffer, 0, len);
 			if (erg==len) html = new String(buffer);
 			input.close();
 		} catch (IOException e) {
 			if (input!=null)
 				try {
 					input.close();
 				} catch (IOException e1) {
 				}
 			e.printStackTrace();
 		}
 		return html;
    }


}
