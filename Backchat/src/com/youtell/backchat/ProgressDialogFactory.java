package com.youtell.backchat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.WindowManager;

public class ProgressDialogFactory {
	public static ProgressDialog newDialog(Activity act)
	{
		ProgressDialog progressDialog = ProgressDialog.show(act, "", null, true, false);
		progressDialog.setContentView(R.layout.progress_layout_centered);
		return progressDialog;
	}
}
