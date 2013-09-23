package com.youtell.backdoor.services;

import java.sql.SQLException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.youtell.backdoor.api.PostGabClueRequest;
import com.youtell.backdoor.api.PostGabRequest;
import com.youtell.backdoor.api.PostNewGabRequest;
import com.youtell.backdoor.api.PostMessageRequest;
import com.youtell.backdoor.models.Clue;
import com.youtell.backdoor.models.Gab;
import com.youtell.backdoor.models.Message;
import com.youtell.backdoor.observers.ClueObserver;
import com.youtell.backdoor.observers.GabObserver;
import com.youtell.backdoor.observers.MessageObserver;

//TODO listen for gab delete, clean up the ApiService calls
//TODO is it necessary to refresh since we're on a different thread without setting some setting?
public class ORMUpdateService extends Service {
	MessageObserver messageObserver;
	GabObserver gabObserver;
	ClueObserver clueObserver;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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
		
		}, null);
		
		gabObserver = new GabObserver(new GabObserver.Observer() {			
			@Override
			public void onChange(String action, int gabID) {
				Gab g = Gab.getByID(gabID);

				g.refresh();
				
				
				if(action == GabObserver.GAB_INSERTED) {
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
					APIService.fire(new PostGabRequest(g));
				}
			}
		});
		
		clueObserver = new ClueObserver(new ClueObserver.Observer() {

			@Override
			public void onChange(String action, int gabID, int objectID) {
				if(action == ClueObserver.CLUE_INSERTED) {
					Log.e("ORM", String.format("clue %d %d", gabID, objectID));
					Clue clue = Clue.getByID(objectID);
					APIService.fire(new PostGabClueRequest(clue));
				}
			}
		
		}, null);
		gabObserver.startListening();
		messageObserver.startListening();
		clueObserver.startListening();
		
		return Service.START_STICKY;	
	}
	
	@Override
    public void onDestroy() {
		Log.v("ORMUpdate", "stopped");
		messageObserver.stopListening();
		gabObserver.stopListening();
		clueObserver.stopListening();
		super.onDestroy();		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
