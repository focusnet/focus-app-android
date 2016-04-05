/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.service.util;

import java.util.ArrayList;
import java.util.List;

import eu.focusnet.app.ui.fragment.BookmarkFragment;

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
