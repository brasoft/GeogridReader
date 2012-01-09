package de.brasoft.snippets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

abstract public class Dlg_Base {
	
	protected IDialogEnd end;
	protected Context con;
	protected Dialog dialog;
	protected int whatBtn;

	
	public Dlg_Base(Context con, IDialogEnd end) {
		this.end = end;
		this.con = con;
	}

	public Dlg_Base() {
	}
	
	public Dialog getDialog() {
		return dialog;
	}
	
	public int getWhatBtn() {
		return whatBtn;
	}
	
	protected void okClick() {
		whatBtn = AlertDialog.BUTTON_POSITIVE;
		if (end!=null) end.onOkClick(this);
	}

	protected void cancelClick() {
		whatBtn = AlertDialog.BUTTON_NEGATIVE;
		if (end!=null) end.onOkClick(this);
	}

	protected void neutralClick() {
		whatBtn = AlertDialog.BUTTON_NEUTRAL;
		if (end!=null) end.onOkClick(this);
	}

	abstract public void prepare();

}
