package de.brasoft.snippets;

import java.util.Hashtable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dlg_ListAuswahl extends Dlg_Base {

	private static Hashtable<Dlg_Manager, Dlg_Base> instances = new Hashtable<Dlg_Manager, Dlg_Base>(); 

	private int position;  // Position des List-Items
	private String[] itemAry;
	
	
	public static Dlg_Base getInstance(Context con, Dlg_Manager dm, IDialogEnd end, String[] itemAry) {
		if (!instances.containsKey(dm)) {
			instances.put(dm, new Dlg_ListAuswahl(con, end, itemAry));
		}
		return instances.get(dm);
	}
	
	public static void remove(Dlg_Manager dm) {
		instances.remove(dm);
	}

	private Dlg_ListAuswahl(Context con, IDialogEnd end, String[] itemAry) {
		super(con, end);
		this.itemAry = itemAry;
    	AlertDialog.Builder builder = new AlertDialog.Builder(con);
    	builder.setNegativeButton("Cancel", btnCancel);
    	builder.setTitle("Karte auswählen");
    	builder.setItems(itemAry, btnOk);
    	dialog = builder.create();
	}
	
	DialogInterface.OnClickListener btnOk = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			position = which;
			okClick();
		} };
	
	DialogInterface.OnClickListener btnCancel = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			cancelClick();
		} };
		
	@Override
	public void prepare() {
	}

	public int getIntValue() {
		return Integer.parseInt(itemAry[position]);
	}
	
	public String getItem() {
		return itemAry[position];
	}
}
