package com.youtell.backchat.fragments;

import com.youtell.backchat.api.GetUserSettingsRequest;
import com.youtell.backchat.api.PostUserSettingsRequest;
import com.youtell.backchat.models.User;
import com.youtell.backchat.observers.UserObserver;
import com.youtell.backchat.observers.UserObserver.Observer;
import com.youtell.backchat.services.APIService;
import com.youtell.backchat.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;

public class NotificationSettingsFragment extends DialogFragment implements Observer {
	private View settings;
	private ProgressBar progress;
	private UserObserver user = new UserObserver(this);
	private Switch messagePreview;
	private Switch vibrate;
	private Switch sound;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		user.startListening();
		
        LayoutInflater factory = LayoutInflater.from(this.getActivity());
        final View view = factory.inflate(R.layout.fragment_notification_settings, null);
        progress = (ProgressBar) view.findViewById(R.id.notification_settings_progress);
        settings = view.findViewById(R.id.notification_settings_settings);
        vibrate = (Switch) view.findViewById(R.id.notification_settings_vibrate_switch);
        messagePreview = (Switch) view.findViewById(R.id.notification_settings_message_preview);
        sound = (Switch) view.findViewById(R.id.notification_settings_sound_switch);
        settings.setVisibility(View.INVISIBLE);
        
        APIService.fire(new GetUserSettingsRequest());
        
	    return new AlertDialog.Builder(getActivity())
	            .setTitle(R.string.notification_settings_dialog_title)
	            .setPositiveButton(R.string.save_button,
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	User user = User.getCurrentUser();
	                    	user.setVibratePref(getActivity(), vibrate.isChecked());
	                    	user.setMessagePreview(messagePreview.isChecked());
	                    	user.setSoundPref(getActivity(), sound.isChecked());
	                    	//TODO spinny
	                    	APIService.fire(new PostUserSettingsRequest());
	                    }
	                }
	            )
	            .setView(view)
	            .create();
	}

	@Override
	public void onPause() {
		super.onPause();
		user.stopListening();
	}
	
	@Override
	public void onUserChanged() {
		User user = User.getCurrentUser();
		
		vibrate.setChecked(user.getVibratePref(getActivity()));
		sound.setChecked(user.getSoundPref(getActivity()));
		messagePreview.setChecked(user.getMessagePreview());
		
		settings.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
	}

	@Override
	public void refresh() {
		
	}
}
