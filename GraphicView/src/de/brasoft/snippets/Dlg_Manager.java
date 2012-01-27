package de.brasoft.snippets;

import java.util.Enumeration;
import java.util.Hashtable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;

public class Dlg_Manager {
	private static Hashtable<Context, Dlg_Manager> instances = new Hashtable<Context, Dlg_Manager>(); 
	
	private Hashtable<Dlg_Base,Integer> dlgMap;
	private int dlgId;
	
	// Für jeden verschiedenen Kontext (verschiedene Activities) einen eigenen DialogManager
	public static Dlg_Manager getInstance(ContextWrapper con) {
		Context baseCon = con.getBaseContext();
		if (!instances.containsKey(baseCon)) {
			instances.put(baseCon, new Dlg_Manager());
		}
		return instances.get(baseCon);
	}
	
	public static void destroy(ContextWrapper con) {
		// DialogManager löschen, der zu diesem Context gehört
		instances.remove(con.getBaseContext());
	}
	
	private Dlg_Manager() {
		dlgMap = new  Hashtable<Dlg_Base,Integer>();
		dlgId = 1;
	}
	
	public int addDialog(Dlg_Base obj) {
		// Dieses Objekt noch nicht vorhanden?
		if (!dlgMap.containsKey(obj)) {
			// Erzeugen
			dlgMap.put(obj, new Integer(dlgId++));
		}
		return dlgMap.get(obj);
	}
	
	public void removeDialog(Dlg_Base obj, Activity con) {
		if(dlgMap.containsKey(obj)) {
			int id = dlgMap.get(obj);
			dlgMap.remove(obj);
			con.removeDialog(id);
		}
	}
	
	public Dialog getDialog(int id) {
		Dialog retw=null;
	      Enumeration<Dlg_Base> e = dlgMap.keys();
	      while (e.hasMoreElements()) {
	    	  Dlg_Base key = e.nextElement();
	    	  if (id==dlgMap.get(key)) {
	    		 return key.getDialog(); 
	    	  }
	      }
		return retw;
	}

	public Dlg_Base getCustomDialog(int id) {
		Dlg_Base retw=null;
	      Enumeration<Dlg_Base> e = dlgMap.keys();
	      while (e.hasMoreElements()) {
	    	  Dlg_Base key = e.nextElement();
	    	  if (id==dlgMap.get(key)) {
	    		 return key; 
	    	  }
	      }
		return retw;
	}
}
