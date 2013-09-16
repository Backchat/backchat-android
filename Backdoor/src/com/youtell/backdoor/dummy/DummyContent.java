package com.youtell.backdoor.dummy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youtell.backdoor.models.Friend;
import com.youtell.backdoor.models.Gab;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    public static List<Friend> FRIENDS = new ArrayList<Friend>();
    public static Map<String, Friend> FRIENDS_MAP = new HashMap<String, Friend>();
    public static List<Gab> ITEMS; 
    public static Map<String, Gab> ITEM_MAP;

    static {
        // Add 3 sample items.
    	ITEMS = new ArrayList<Gab>();
    	ITEM_MAP = new HashMap<String, Gab>();
    	
        addGab(new Gab(getNewGabID(), null, "Item 1", new Date("1/1/2013"), false));
        addGab(new Gab(getNewGabID(), "John", "Item 2", new Date("9/12/2013"), true));
        addGab(new Gab(getNewGabID(), null, "Item 3", new Date(), false));
        
        addFriend(new Friend("1", "John", "Smith"));
    }

    private static void addFriend(Friend f) {
    	FRIENDS.add(f);
    	FRIENDS_MAP.put(f.getID(), f);
    }
    
    public static String getNewGabID() {
    	return String.format("%d", ITEMS.size());
    }
    
    public static void addGab(Gab item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getID(), item);
    }

	public static void deleteGab(Gab gab) {
		ITEMS.remove(gab);
		ITEM_MAP.remove(gab.getID());
	}    
}
