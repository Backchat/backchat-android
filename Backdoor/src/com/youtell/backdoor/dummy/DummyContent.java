package com.youtell.backdoor.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youtell.backdoor.models.Gab;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<Gab> ITEMS = new ArrayList<Gab>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, Gab> ITEM_MAP = new HashMap<String, Gab>();

    static {
        // Add 3 sample items.
        addItem(new Gab("1", "", "Item 1", false));
        addItem(new Gab("2", "John", "Item 2", true));
        addItem(new Gab("3", "", "Item 3", false));
    }

    private static void addItem(Gab item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getID(), item);
    }    
}
