package eu.focusnet.app.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yandypiedra on 10.10.15.
 */
public class EventBus {

    public interface IEventListener {
        void onBookmarksUpdated();
    }

    private static List<IEventListener> eventListeners = new ArrayList<>();

    public static void registerIEventListener(IEventListener eventListener){
        eventListeners.add(eventListener);
    }

    public static void unregisterIEventListener(IEventListener eventListener){
        eventListeners.remove(eventListener);
    }

    public static void fireBookmarksUpdate(){
        for(IEventListener eventListener : eventListeners)
            eventListener.onBookmarksUpdated();
    }
}
