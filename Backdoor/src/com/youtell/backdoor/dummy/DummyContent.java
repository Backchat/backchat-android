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
    public static List<Gab> ITEMS = new ArrayList<Gab>();
    public static Map<String, Gab> ITEM_MAP = new HashMap<String, Gab>();

    static {
        // Add 3 sample items.
        addItem(new Gab("1", "", "Item 1", new Date("1/1/2013"), false));
        addItem(new Gab("2", "John", "Item 2", new Date("9/12/2013"), true));
        addItem(new Gab("3", "", "Item 3", new Date(), false));
        
        addFriend(new Friend("1", "John", "Smith"));
    }

    private static void addFriend(Friend f) {
    	FRIENDS.add(f);
    	FRIENDS_MAP.put(f.getID(), f);
    }
    
    private static void addItem(Gab item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getID(), item);
    }    
}
