package com.youtell.backdoor.services;

import java.sql.SQLException;
import java.util.Iterator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.ForeignCollection;
import com.youtell.backdoor.api.PostGabRequest;
import com.youtell.backdoor.api.PostMessageRequest;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;
import com.youtell.backdoor.observers.GabObserver;
import com.youtell.backdoor.observers.MessageObserver;

public class ORMUpdateService extends Service {
	MessageObserver messageObserver;
	GabObserver gabObserver;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("ORMUpdate", "started");
		messageObserver = new MessageObserver(new MessageObserver.Observer() {
			@Override
			public void onChange(String action, int gabID, int messageID) {
				if(action == MessageObserver.MESSAGE_ADDED) {
					Log.v("ORMUpdate", String.format("a new message added! %d %d", gabID, messageID));
					Gab g = Gab.getByID(gabID);
					
					/* if the gab is new, fire off a post new gab for the first message only. */
					if(g.isNew()) 
					{
						Message first = g.getFirstMessage();
						if(first.getID() == messageID)
						{
							//the very first message:
							APIService.fire(new PostGabRequest(g));
						}
					}
					else
						APIService.fire(new PostMessageRequest(g, messageID));
				}				
			}
		
		}, null);
		
		gabObserver = new GabObserver(new GabObserver.Observer() {			
			@Override
			public void onChange(String action, int gabID) {
				if(action == GabObserver.GAB_INSERTED) {
					/* fire off post message requests for all the new messages */		
					try {
						Gab g = Gab.getByID(gabID);
						CloseableWrappedIterable<Message> it = g.getMessages().getWrappedIterable();
						for(Message m : it) 
						{
							if(m.isNew())
								APIService.fire(new PostMessageRequest(g, m));
						}			
						it.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		gabObserver.startListening();
		messageObserver.startListening();
		
		
		return Service.START_STICKY;	
	}
	
	@Override
    public void onDestroy() {
		Log.v("ORMUpdate", "stopped");
		messageObserver.stopListening();
		super.onDestroy();		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
