package com.youtell.backchat.services;

import java.sql.SQLException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.squareup.otto.Subscribe;
import com.youtell.backchat.api.PostGabClueRequest;
import com.youtell.backchat.api.PostGabRequest;
import com.youtell.backchat.api.PostMessageRequest;
import com.youtell.backchat.api.PostNewGabRequest;
import com.youtell.backchat.models.Clue;
import com.youtell.backchat.models.DBClosedEvent;
import com.youtell.backchat.models.Gab;
import com.youtell.backchat.models.Message;
import com.youtell.backchat.models.ModelBus;
import com.youtell.backchat.observers.ClueObserver;
import com.youtell.backchat.observers.GabObserver;
import com.youtell.backchat.observers.MessageObserver;

//TODO listen for gab delete, clean up the ApiService calls
//TODO is it necessary to refresh since we're on a different thread without setting some setting?
public class ORMUpdateService extends Service {
	MessageObserver messageObserver;
	GabObserver gabObserver;
	ClueObserver clueObserver;
	
	@Override
	public void onCreate() {
		Log.v("ORMUpdate", "started");
		messageObserver = new MessageObserver(new MessageObserver.Observer() {
			@Override
			public void onChange(String action, int gabID, int messageID) {
				if(action == MessageObserver.MESSAGE_ADDED) {
					Log.v("ORMUpdate", String.format("a new message added! %d %d", gabID, messageID));
					Gab g = Gab.getByID(gabID);
					g.refresh();
					
					/* if the gab is new, fire off a post new gab for the first message only. */
					if(g.isNew()) 
					{
						Message first = g.getFirstMessage();
						if(first.getID() == messageID)
						{
							//the very first message:
							APIService.fire(new PostNewGabRequest(g));
						}
					}
					else
						APIService.fire(new PostMessageRequest(g, messageID));
				}				
			}
		

			@Override
			public void refresh() {}
		}, null);
		
		gabObserver = new GabObserver(new GabObserver.Observer() {			
			@Override
			public void onChange(String action, int gabID) {
				if(action == GabObserver.GAB_INSERTED) {				
					Gab g = Gab.getByID(gabID);
					g.refresh();

					/* fire off post message requests for all the new messages */		
					try {
						g.getMessages().refreshCollection();
						
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
				else if(action == GabObserver.GAB_UNREAD_COUNT_CHANGED) {
					/* did we mark something as 0?
					 * TODO make this better and use dirty markers
					 */
					Gab g = Gab.getByID(gabID);
					g.refresh();
					
					APIService.fire(new PostGabRequest(g));
				}
			}
			

			@Override
			public void refresh() {}
		});
		
		clueObserver = new ClueObserver(new ClueObserver.Observer() {

			@Override
			public void onChange(String action, int gabID, int objectID) {
				if(action == ClueObserver.CLUE_INSERTED) {
					Clue clue = Clue.getByID(objectID);
					APIService.fire(new PostGabClueRequest(clue));
				}
			}
			
			@Override
			public void refresh() {}
		
		}, null);
		gabObserver.startListening();
		messageObserver.startListening();
		clueObserver.startListening();
		
		ModelBus.events.register(this);
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId)
	{
		return Service.START_STICKY;	
	}
	
	@Subscribe public void dbIsGone(DBClosedEvent e) {
		Log.v("ORMUpdate", "closed listeners");
		messageObserver.stopListening();
		gabObserver.stopListening();
		clueObserver.stopListening();	
	}	

	@Override
    public void onDestroy() {
		Log.v("ORMUpdate", "stopped");
		ModelBus.events.unregister(this);
		super.onDestroy();		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
