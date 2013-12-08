package com.youtell.backchat.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.youtell.backchat.R;

public class WebViewDialogFragment extends DialogFragment {

	public static final String URI_ARGUMENT = "URI_ARGUMENT";
	public static final String TITLE_RES_ARGUMENT = "TITLE_RES_ARGUMENT";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog d = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog);
	    d.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
		int titleRes = getArguments().getInt(TITLE_RES_ARGUMENT);
	    d.setTitle(titleRes);
		return d;
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View allViews = inflater.inflate(R.layout.fragment_webview, container, false);

		String uri = getArguments().getString(URI_ARGUMENT);
		WebView view = (WebView) allViews.findViewById(R.id.webview_webview);

		view.loadUrl(uri);

		view.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				view.loadUrl(url);

				return true;
			}
		});
		
		Button okButton = (Button) allViews.findViewById(R.id.webview_ok_button);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});
		return allViews;
	}
}
