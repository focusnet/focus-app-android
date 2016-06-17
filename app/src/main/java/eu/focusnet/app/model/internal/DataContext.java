/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model.internal;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.exception.FocusNotImplementedException;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.model.util.Constant;
import eu.focusnet.app.service.DataManager;

/**
 * Created by julien on 13.01.16.
 * <p/>
 * TODO doc:
 * we always store FocusSamples, not actual scalar/leaf data
 * -> register() will retrieve a sample only
 * -> get() retrieves samples
 * -> resolve() used to get leaf-data (i.e. content of FocuSSample)
 * <p/>
 * iterators require a list of urls -> get a specific FocusSample -> resolve() it -> case to list of urls.
 */
public class DataContext extends HashMap<String, String>
{

	private DataManager dataManager;
	private HashMap<String, AsyncTask<Void, Void, String>> queue;


	/**
	 * Default c'tor
	 */
	public DataContext(@NonNull DataManager dm)
	{
		super();
		this.dataManager = dm;
		queue = new HashMap<>();
	}

	/**
	 * Construct a new DataContext from another one.
	 *
	 * @param c - we use the datamanager of this context as we will build the new context on top of the old one, so using the same data manager makes sense
	 */
	public DataContext(@NonNull DataContext c)
	{
		super(c);
		this.dataManager = c.dataManager;

		// keep reference to the existing queue
		// FIXME should we lock before starting? queue may be expanded while we are copying.
		this.queue = new HashMap<>();
		for(Map.Entry e : c.getQueue().entrySet()) {
			this.queue.put((String) e.getKey(), (AsyncTask<Void, Void, String>) e.getValue());
		}
	}

	/**
	 * Add a new FocusSample's urls entry in our current data context, based on a description of the
	 * new data to be included. These descriptions are the ones we can find in the 'data'
	 * properties of the Application Content template JSON representation.
	 * <p/>
	 * All data context entries are FocusSample's
	 * <p/>
	 * The different options for the description are the following:
	 * <p/>
	 * - a simple URL, e.g. http://data.example.org/test/123
	 * - a reference to an existing entry in this data context, with a selector to the
	 * appropriate key in its data object; e.g. <ctx/machine-ABC/woodpile-url>
	 * <p/>
	 * TODO
	 * - a history request, e.g. <history|URL|since=now-86400;until=now;every=240>
	 * - a lookup request, e.g. <lookup:http://schemas.focusnet.eu/my-special-type|URL>
	 * * where URL is the "context" of the FocusObject.
	 * - above services with references to context:
	 * <history|ctx/machine-ABC/woodpile-url|since=now-86400;until=now;every=240>
	 * <p/>
	 * Examples:
	 * "simple-url": "http://focus.yatt.ch/debug/focus-sample-1.json",
	 * "referenced-url": "<ctx/simple-url/url1>",
	 * <p/>
	 * FIXME TODO
	 * "history-test": "<history|http://focus.yatt.ch/debug/focus-sample-1.json|params>",
	 * "history-test-ref": "<history|ctx/simple-url/url1|params,params2>",
	 * "lookup-test": "<lookup|http://focus.yatt.ch/debug/focus-sample-1.json|http://www.type.com>",
	 * "lookup-test-ref": "<lookup|ctx/simple-url/url1|http://www.type.com>"
	 * lookup: no context for now.
	 * <p/>
	 *
	 * the register operation must be an AsyncTask(). we keep reference to the task such that this.get() can wait for it.
	 *
	 * FIXME implement custom queuing
	 *
	 */
	public void register(String key, String description)
	{
		AsyncTask<Void, Void, String> task = new DataAcquisitionTask(key, description).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		//	test what happens when I try to download a lot of files at the sametime (>12) -> queue working? -> crashing? -> no, but may be waiting too long
		// and/or deadlock
		// FIXME implement my own Executor
		// FIXME fine-tune this ?
		// public static final Executor THREAD_POOL_EXECUTOR
		// = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
		// 			TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
		queue.put(key, task);
	}

	/**
	 * We know that we have a context reference (starting with "ctx/")
	 * and we want to return the corresponding URL.
	 *
	 * @param description
	 * @return
	 */
	private String resolveReferencedUrl(String description) throws FocusMissingResourceException
	{
		String[] parts = description.split(Constant.SELECTOR_CONTEXT_SEPARATOR);
		if (parts.length != 3) {
			throw new FocusInternalErrorException("badly formatted reference");
		}

		String ref = parts[1];
		String res = this.get(ref);
		if (res == null) {
			throw new FocusInternalErrorException("invalid object reference");
		}
		FocusSample fs = this.dataManager.getSample(res);
		res = fs.getString(parts[2]);
		if (res == null) {
			throw new FocusInternalErrorException("Cannot find this field.");
		}
		// if that's indeed a url, then we can retrieve it.
		// FIXME check that this is a url!
		return res;
	}


	/**
	 * Fill data in this context based on the provided data input definition.
	 *
	 * @param data
	 */
	public void provideData(HashMap<String, String> data)
	{
		if (data == null) {
			return;
		}
		for (Map.Entry<String, String> entry : data.entrySet()) {
			this.register(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Resolve the provided request considering the present data context. e.g.
	 * <ctx/simple-example/field-to-get> -> will retrieve the field-to-get field of the
	 * data being stored under the simple-example entry.
	 * <ctx/simple-example> -> will return the url of the context identfied by simlpe-example (a FocusSample)
	 * <p/>
	 * It is then the responsibility of the caller to create FocusSample's when appropriate. FIXME FIXME FIXME - i never do it! to test.
	 * <p/>
	 * If the request format is not recognized, return it as-is.
	 * <p/>
	 * If the request does not succeed, throw a FocusMissingResourceException
	 *
	 * @return
	 */
	public Object resolve(String request) throws FocusMissingResourceException
	{
		if (request == null) {
			return "";
		}

		// if this is a normal url then just return it.
		if (!request.startsWith(Constant.SELECTOR_OPEN + Constant.SELECTOR_CONTEXT_LABEL + Constant.SELECTOR_CONTEXT_SEPARATOR)
				|| !request.endsWith(Constant.SELECTOR_CLOSE)) {
			return request;
		}
		request = request
				.replaceFirst("^" + Constant.SELECTOR_OPEN, "")
				.replaceFirst(Constant.SELECTOR_CLOSE + "$", "");

		String[] parts = request.split(Constant.SELECTOR_CONTEXT_SEPARATOR);
		if (parts.length < 2) {
			return new FocusInternalErrorException("Invalid context in resolve request.");
		}

		String url = this.get(parts[1]);
		if (url == null) {
			throw new FocusMissingResourceException("Impossible to resolve |" + request + "| in the current data context.");
		}
		FocusSample fs = this.dataManager.getSample(url); // wait for completion
		if (parts.length == 3) {
			if (fs == null) {
				return null;
			}
			return fs.get(parts[2]);
		}
		else {
			return fs.getUrl();
		}
	}

	public DataManager getDataManager()
	{
		return this.dataManager;
	}

	/**
	 * Acquire data from the DataManager in a background task.
	 */
	private class DataAcquisitionTask extends AsyncTask<Void, Void, String>
	{
		String key;
		String description;


		private DataAcquisitionTask(String key, String description)
		{
			this.key = key;
			this.description = description;
		}

		@Override
		protected String doInBackground(Void... params)
		{
			// what kind of data do we have?
			FocusSample f;
			try {
				if (description.startsWith(Constant.SELECTOR_OPEN)
						&& description.endsWith(Constant.SELECTOR_CLOSE)) {

					description = description
							.replaceFirst("^" + Constant.SELECTOR_OPEN, "")
							.replaceFirst(Constant.SELECTOR_CLOSE + "$", "");

					if (description.startsWith(Constant.SELECTOR_CONTEXT_LABEL
							+ Constant.SELECTOR_CONTEXT_SEPARATOR)) {
						String u = resolveReferencedUrl(description);
						f = dataManager.getSample(u);
					}
					else {
						String[] parts = description.split(Constant.SELECTOR_SERVICE_SEPARATOR);
						if (parts.length != 3) {
							throw new FocusInternalErrorException("Wrong number of fields for description of service.");
						}
						if (parts[0].equals(Constant.SELECTOR_SERVICE_HISTORY)) {
							String u;
							if (parts[1].startsWith(Constant.SELECTOR_CONTEXT_LABEL + Constant.SELECTOR_CONTEXT_SEPARATOR)) {
								u = resolveReferencedUrl(parts[1]);
							}
							else {
								u = parts[1];
							}
							f = dataManager.getHistory(u, parts[2]);
						}
						else {
							throw new FocusNotImplementedException("DataContext.register() -> services");
						}
					}
				}
				else {
					// simple URL
					f = dataManager.getSample(description); // e.g. on success -> do something.
				}
			}
			catch (FocusMissingResourceException ex) {
				// we highlight the fact that we could not retrieve a resource by NOT saving
				// it into the DataContext. When get()-ing, the caller will
				// receive null, and can act accordingly.
				return null;
			}
			DataContext.super.put(key, f.getUrl()); // synchronized function
			return f.getUrl();
		}
	}

	/**
	 * Queue getter
	 */
	public HashMap<String, AsyncTask<Void, Void, String>> getQueue()
	{
		return this.queue;
	}

	/**
	 * FIXME TODO override get, make it wait for the pending async operation
	 *
	 * in some situtations, I may need not to wait (UI Thread). to be cchecked.
	 *
	 * @param key
	 * @return the url of the object that is gotten, or null if an error occured and the object
	 * could not be retrieved. Callers of this method MUST check for null and act accordingly.
	 */
	@Override
	public String get(Object key)
	{
		AsyncTask<Void, Void, String> task = this.queue.get(key);
		if (task == null) {
			throw new FocusInternalErrorException("No task for requested data. Logic problem.");
		}
		if (task.isCancelled()) {
			return null;
		}
		if (task.getStatus() == AsyncTask.Status.FINISHED) {
			return super.get(key);
		}
		// else wait for completion
		try {
			return task.get();
		}
		catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

}
