package eu.focusnet.app.util;

import java.util.ArrayList;
import java.util.List;

import eu.focusnet.app.ui.fragments.BookmarkFragment;

/**
 * Event bus class which reacts to bookmark's update in the application. When a bookmark is
 * updated the {@link #fireBookmarksUpdate} will be called updating so the {@link BookmarkFragment}
 */
public class EventBus
{

	private static List<IEventListener> eventListeners = new ArrayList<>();

	public static void registerIEventListener(IEventListener eventListener)
	{
		eventListeners.add(eventListener);
	}

	public static void unregisterIEventListener(IEventListener eventListener)
	{
		eventListeners.remove(eventListener);
	}

	public static void fireBookmarksUpdate()
	{
		for (IEventListener eventListener : eventListeners) {
			eventListener.onBookmarksUpdated();
		}
	}

	public interface IEventListener
	{
		void onBookmarksUpdated();
	}
}
