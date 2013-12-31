package com.youtell.backchat.api;

import com.youtell.backchat.models.Friend;
import com.youtell.backchat.observers.GCMNotificationObserver;

public class GetFriendsForNotificationRequest extends GetFriendsRequest {
	private static final String MESSAGE_ARG = "MESSAGE_ARG";
	private static final String FRIEND_ID_ARG = "FRIEND_ID_ARG";
	private StringArgumentHandler message = new StringArgumentHandler(MESSAGE_ARG, this);
	private IntegerArgumentHandler friendID = new IntegerArgumentHandler(FRIEND_ID_ARG, this);
	
	public GetFriendsForNotificationRequest() {
		super();
	}
	
	public GetFriendsForNotificationRequest(String message, int friendID) {
		this.message.content = message;
		this.friendID.value = friendID;
	}
	
	@Override
	protected void handleSuccess() {
		super.handleSuccess();
		Friend f = Friend.getByRemoteID(friendID.value);
		if(f != null) {
			GCMNotificationObserver.broadcastFriendNotification(this.context, message.content, f);
		}
	}
}
