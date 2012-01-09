package de.brasoft.snippets;

import java.util.Hashtable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dlg_Popup_Main extends Dlg_Base {
	private static Hashtable<Dlg_Manager, Dlg_Base> instances = new Hashtable<Dlg_Manager, Dlg_Base>(); 

	private int position;  // Position des List-Items
	private String[] itemAry;
	
	
	public static Dlg_Base getInstance(Context con, Dlg_Manager dm, IDialogEnd end) {
		if (!instances.containsKey(dm)) {
			instances.put(dm, new Dlg_Popup_Main(con, end));
		}
		return instances.get(dm);
	}

	private Dlg_Popup_Main(Context con, IDialogEnd end) {
		super(con, end);
		this.itemAry = new String[3];
		itemAry[0] = "Item 1";
		itemAry[1] = "Item 2";
		itemAry[2] = "Item 3";
    	AlertDialog.Builder builder = new AlertDialog.Builder(con);
    	//builder.setPositiveButton("Ok", btnOk);
    	builder.setNegativeButton("Cancel", btnCancel);
    	builder.setTitle("Aktionen");
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
