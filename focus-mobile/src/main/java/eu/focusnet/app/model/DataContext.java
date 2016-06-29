/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model;

import android.support.annotation.NonNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.focusnet.app.controller.DataManager;
import eu.focusnet.app.controller.PriorityTask;
import eu.focusnet.app.model.gson.FocusSample;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.FocusBadTypeException;
import eu.focusnet.app.util.FocusInternalErrorException;
import eu.focusnet.app.util.FocusMissingResourceException;
import eu.focusnet.app.util.FocusNotImplementedException;


// FIXME review doc. major rewrite.
// synchronized / volatile methods?

/**
 * In our model, all {@link AbstractInstance} have an associated {@code DataContext}. This data
 * context is used to perform data resolution, i.e. determine iterators and interpolate
 * variables in the instance content. The basic functioning is as follows:
 * - Instance request to {@link #register(String, String)} data to their {@code DataContext}.
 * - The {@code DataContext} then pushes the request in a queue such that the resource will
 * be downloaded. The queue is actually maintained by the {@link DataManager} of the application,
 * which is common to all {@code DataContext}s.
 * - Instances can consume data by calling {@code #resolve*()} methods. WThe
 * calling thread will then block until the resource is available. The actual blocking and waiting
 * is done in {@link #get(Object)}.
 * <p/>
 * {@link #register(String, String)}-ed resources are processed in order of {@link #priority}.
 * The later the instance is created in the application content, the lower is its priority.
 */
public class DataContext extends HashMap<String, PriorityTask<String>>
{

	/**
	 * The application {@link DataManager}.
	 */
	private DataManager dataManager;


	/**
	 * Priority of this data context. The deeper it is created in the application content, the lower
	 * it is.
	 *
	 * The higher the number is, the sooner the operation will be executed.
	 */
	private int priority;

	private String iteratorLabel;


	/*
	 * random doc
	 *
	 * project -> project -> project -> page
	 * page is always a leaf
	 * HENCE if in a context we have:
	 * project iterator -> this context can be used as parent context, and we should NOT use the project-iterator in child context. NEVER.
	 * page iterator -> we are in a leaf context, and we therefore will never use this context as parent
	 *
	 * conclusion: in a new context, ALWAYS dismiss project iterators.
	 * If they need to be used later in the hierarchy, we will save them in 'data'
	 * it is useless to dismiss page iterators as we will never build a new context on top of a context that has a page iterator (they are leaf contexts)
	 *
	 *
	 */

	/**
	 * Default c'tor
	 */
	public DataContext(@NonNull DataManager dm)
	{
		super();
		this.priority = 0;
		this.dataManager = dm;
	}

	/**
	 * Construct a new DataContext from another one.
	 *
	 * @param parentContext The {@code DataContext} on the top of which we must create the new one.
	 */
	public DataContext(@NonNull DataContext parentContext)
	{
		this(parentContext.getDataManager());

		// increment priority by a big enough number such that we can later insert intermediate priority tasks
		this.priority = parentContext.getPriority() - Constant.AppConfig.PRIORITY_BIG_DELTA;

		for (Map.Entry e : parentContext.entrySet()) {
			String key = (String) e.getKey();
			PriorityTask<String> task = (PriorityTask<String>) e.getValue();
			// if there is a parent project, skip. We will not use a project iterator. If we do, we will save it in the 'data'.
			if (key.startsWith(Constant.Navigation.LABEL_ITERATOR_PROJECT_PREFIX)) { // FIXME I CANNOT REMOVE PAGE ITERATOR !!!!
				continue;
			}
			this.put(key, task);
		}
	}

	/**
	 * Add a new FocusSample's urls entry in our current data context, based on a description of the
	 * new data to be included. These descriptions are the ones we can find in the 'data'
	 * properties of the Application Content template JSON representation, or in iterators
	 * definitions.
	 * <p/>
	 * All targets of data context entries are FocusSample's
	 * <p/>
	 * The different options for the description are the following:
	 * <p/>
	 * - a simple URL, e.g. http://data.example.org/test/123
	 * - a reference to an existing entry in this data context, with a selector to the
	 * appropriate key in its data object; e.g. <ctx/machine-ABC/woodpile-url>
	 * <p/>
	 * - a history request, e.g. <history|URL|since=now-86400;until=now;every=240>
	 * - a history request with a reference to a previously registered entry, e.g.
	 * <history|ctx/machine-ABC/woodpile-url|since=$date$>
	 *
	 *     FIXME *always* expect url back, not object.
	 *             iterators and 'data' expect single url to be returned. values are accessed in projects/pages/widgets
	 *
	 */
	synchronized public void register(String key, String description)
	{
		this.nonSynchronizedRegister(key, description, this.priority);
	}

	// if we give the same priority to iterators, we may have problems
	// if the 'data' array contains a reference to the iterator (for example for
	// saving the iterator as a variable for use in inner project).
	// so let's make sure that the priority of our iterators is slightly higher than the
	// one of other objects acquired in this context
	synchronized public void registerIterator(String guid, String description, boolean isPage)
	{
		this.iteratorLabel = (isPage ? Constant.Navigation.LABEL_ITERATOR_PAGE_PREFIX : Constant.Navigation.LABEL_ITERATOR_PROJECT_PREFIX)
				+ guid + Constant.Navigation.LABEL_ITERATOR_SUFFIX;
		this.nonSynchronizedRegister(this.iteratorLabel, description, this.priority + Constant.AppConfig.PRIORITY_SMALL_DELTA);
	}

	private void nonSynchronizedRegister(String key, String description, int priority)
	{
		// check if key already exists. if this is the case, then raise an error. We don't support that
		// we use super.get() as this.get() would be blocking, waiting for the result of the task
		// having 2 times the same key in a 'data' object array would trigger a
		// com.google.gson.JsonSyntaxException: duplicate key: ... exception
		// so we are consistent by using an unchecked exception here
		if (this.get(key) != null) {
			throw new FocusInternalErrorException("We do not support DataContext entry overriding.");
		}

		// Create the data retrieving task
		DataAcquisitionTask task = new DataAcquisitionTask(description);

		// Create the future
		PriorityTask future = new PriorityTask(priority, task);
		this.put(key, future);

		// once the task is saved for later reference, let's execute it
		this.dataManager.executeOnPool(future);
	}

	/**
	 * Fill data in this context based on the provided data input definition.
	 *
	 * @param data A list of descriptions for data fetching, as defined
	 *             in {@link #register(String, String)}
	 *
	 *
	 *             FIXME *always* expect url back, not object.
	 *             iterators and 'data' expect single url to be returned. values are accessed in projects/pages/widgets
	 *
	 */
	public void provideData(HashMap<String, String> data)
	{
		for (Map.Entry<String, String> entry : data.entrySet()) {
			this.register(entry.getKey(), entry.getValue());
		}
	}

	// FIXME doc
	private String getResolved(String key)
	{
		if (this.get(key) == null) {
			throw new FocusInternalErrorException("Attempt to access a non-registered resource");
		}
		try {
			return this.get(key).get(); // FIXME BLOCKING
		}
		// FIXME how to handle these errors?
		catch (InterruptedException e) {
			throw new FocusInternalErrorException("Interrupted data resolution");
		}
		catch (ExecutionException e) { // FIXME especially this one!
			throw new FocusInternalErrorException("ExecutionException at data resolution");
		}
	}

	synchronized private PriorityTask<String> get(String key)
	{
		return super.get(key);
	}


	/**
	 * Resolve the supplied request in the current data context.
	 * A request may be an arbitrary String with expected variable interpolation, e.g.
	 * {@code "Project number <ctx/my-project/id>"}
	 * <p/>
	 * Or a simple string that will return another type of Object (e.g. Double, Array of URLs, etc.)
	 * <p/>
	 * Can not be nested "test <ctx/simple-url/<ctx/other-url/field>> test" except if there is nothing around it.
	 * This is not a feature but a side effect of the implementation. not tested.
	 *
	 * @param request The request to satisfy
	 * @return An Object that answers the request, or {@code null}. We return either the URL
	 * of a registered {@link FocusSample} or the specifically requested property within a
	 * {@link FocusSample}. If the request does not match any of our expected formats, it will be
	 * returned as-is.
	 * @throws FocusMissingResourceException If the requested resource could not be obtained
	 */
	private Object resolve(String request) throws FocusMissingResourceException
	{
		Pattern toSearch = Pattern.compile(Constant.DataReference.SELECT_CONTEXT_FULL_PATTERN);
		Matcher m = toSearch.matcher(request);
		while (m.find()) {
			// if we have an exact match, e.g. only <ctx/...> in the request, then
			// get out of the loop to make a raw variable interpolation, without stringification
			if (m.start() == 0 && m.end() == request.length()) {
				break;
			}
			String found = m.group();
			String value;
			try {
				value = TypesHelper.asString(this.resolveVariable(found));
			}
			catch (FocusBadTypeException e) {
				value = ""; // do something else if bad type?
			}
			request = request.replace(found, value);
		}

		return this.resolveVariable(request);
	}

	/**
	 * Resolve the provided request considering the present data context, without variable
	 * interpolation within the string. e.g.
	 * - <ctx/simple-example/field-to-get> -> will retrieve the field-to-get field of the
	 * data being stored under the simple-example entry.
	 * - <ctx/simple-example/$this$> -> will return the url of the context identfied by simple-example
	 *
	 * @param request The request to satisfy
	 * @return Same as {@link #resolve(String)}, except that there is never variable
	 * interpolation; this is the job of {@link #resolve(String)}.
	 * @throws FocusMissingResourceException If a resource cannot be found
	 */
	private Object resolveVariable(String request) throws FocusMissingResourceException
	{
		if (request == null) {
			return "";
		}

		// if this is a normal url then just return it.
		if (!request.startsWith(Constant.DataReference.SELECTOR_OPEN + Constant.DataReference.SELECTOR_CONTEXT_LABEL + Constant.DataReference.SELECTOR_CONTEXT_SEPARATOR)
				|| !request.endsWith(Constant.DataReference.SELECTOR_CLOSE)) {
			return request;
		}
		request = request
				.replaceFirst("^" + Constant.DataReference.SELECTOR_OPEN, "")
				.replaceFirst(Constant.DataReference.SELECTOR_CLOSE + "$", "");

		String[] parts = request.split(Constant.DataReference.SELECTOR_CONTEXT_SEPARATOR);
		if (parts.length != 3) {
			throw new FocusInternalErrorException("Invalid context in resolve request.");
		}

		String url = this.getResolved(parts[1]); // FIXME blocking
		if (url == null) {
			throw new FocusMissingResourceException("Impossible to resolve the request in the current data context.", request);
		}
		// this is ok to call getSample(url) as url as we have already gotten the url.
		FocusSample fs = this.dataManager.getSample(url);
		if (fs == null) {
			return null;
		}
		return fs.get(parts[2]);
	}


	/**
	 * Resolve the supplied parameter against the current data context.
	 *
	 * @param probableString An object, but probably a String.
	 * @return The requested value as a String
	 * @throws FocusBadTypeException         If eventually the input object was not a String, or if the
	 *                                       output casting was not possible
	 * @throws FocusMissingResourceException If the resource could not be fetched
	 */
	public String resolveToString(Object probableString) throws FocusBadTypeException, FocusMissingResourceException
	{
		return TypesHelper.asString(this.resolve(TypesHelper.asString(probableString)));
	}

	/**
	 * Resolve the supplied parameter against the current data context.
	 *
	 * @param request A request
	 * @return The requested value as a String
	 * @throws FocusBadTypeException         If the output casting was not possible
	 * @throws FocusMissingResourceException If the resource could not be fetched
	 */
	public String resolveToString(String request) throws FocusBadTypeException, FocusMissingResourceException
	{
		return TypesHelper.asString(this.resolve(request));
	}

	/**
	 * Resolve the supplied parameter against the current data context. and retrieve an array of
	 * URLs.
	 *
	 * @param request A request
	 * @return The requested value as an array of URLs
	 * @throws FocusBadTypeException         If it is not possible to cast the output to an array of URLs
	 * @throws FocusMissingResourceException If the resource could not be fetched
	 */
	public ArrayList<String> resolveToArrayOfUrls(String request) throws FocusMissingResourceException, FocusBadTypeException
	{
		return TypesHelper.asArrayOfUrls(this.resolveVariable(request));
	}

	/**
	 * Resolve the supplied parameter against the current data context.
	 *
	 * @param probableString An object, but probably a String.
	 * @return The requested value as a Double
	 * @throws FocusBadTypeException         If eventually the input object was not a String, or if the
	 *                                       output casting was not possible
	 * @throws FocusMissingResourceException If the resource could not be fetched
	 */
	public Double resolveToDouble(Object probableString) throws FocusBadTypeException, FocusMissingResourceException
	{
		return TypesHelper.asDouble(this.resolve(TypesHelper.asString(probableString)));
	}

	/**
	 * Resolve the supplied parameter against the current data context.
	 *
	 * @param probableString An object, but probably a String.
	 * @return The requested value as an Object, without further casting
	 * @throws FocusBadTypeException         If eventually the input object was not a String
	 * @throws FocusMissingResourceException If the resource could not be fetched
	 */
	public Object resolveToObject(Object probableString) throws FocusBadTypeException, FocusMissingResourceException
	{
		return this.resolveVariable(TypesHelper.asString(probableString));
	}


	/**
	 * Get the {@link DataManager} used by this {@code DataContext}.
	 *
	 * @return The {@link DataManager}
	 */
	public DataManager getDataManager()
	{
		return this.dataManager;
	}

	/**
	 * Get current data context priority.
	 *
	 * @return The priority. The higher the better.
	 */
	public int getPriority()
	{
		return priority;
	}

	public String getIteratorValue()
	{
		try {

			// FIXME get(key) is synchronized. is that ok? should we relax that?
			return this.get(this.iteratorLabel).get();
		}
		catch (InterruptedException | ExecutionException e) {
			e.printStackTrace(); // FIXME exception handling
		}
		return null;
	}

	/**
	 * Block and wait for some data to be retrieved, and then return it to the caller.
	 * <p/>
	 * FIXME check stash, better version there.
	 *
	 * @param key Key corresponding to the data retrieval request that was
	 *            {@link #register(String, String)}-ed.
	 * @return the url of the object that is gotten, or null if an error occured and the object
	 * could not be retrieved. Callers of this method MUST check for null and act accordingly.
	 */
	/*
	@Override
	public String xxxxget(Object key)
	{
		if (super.get(key) != null) {
			return super.get(key);
		}
		FutureTask<String> task = this.queue.get(key);
		if (task == null) {
			throw new FocusInternalErrorException("No task for requested data. Logic problem. Perhaps a badly written JSON file with invalid variables?");
// FIXME problem here with nested projects?
		}
		try {
			return task.get();
		}
		catch (InterruptedException e) {
			return null;
		}
		catch (ExecutionException e) {
			return null;
		}
	}
	*/


	/**
	 * Task responsible for acquiring data
	 */
	private class DataAcquisitionTask implements Callable
	{

		/**
		 * Description of the data to retrieve, also as {@link #register(String, String)}-ed.
		 */
		private String description;


		/**
		 * Constructor
		 *
		 * @param description Input value for instance variable
		 */
		private DataAcquisitionTask(String description)
		{
			this.description = description;
		}

		/**
		 * Do the data fetching.
		 *
		 * @return The url of the fetched data
		 */
		@Override
		public String call()
		{
			FocusSample f;
			try {
				if (description.startsWith(Constant.DataReference.SELECTOR_OPEN)
						&& description.endsWith(Constant.DataReference.SELECTOR_CLOSE)) {

					// <ctx/.../...>
					description = description
							.replaceFirst("^" + Constant.DataReference.SELECTOR_OPEN, "")
							.replaceFirst(Constant.DataReference.SELECTOR_CLOSE + "$", "");

					if (description.startsWith(Constant.DataReference.SELECTOR_CONTEXT_LABEL
							+ Constant.DataReference.SELECTOR_CONTEXT_SEPARATOR)) {
						String u = resolveReferencedUrl(description);
						f = dataManager.getSample(u);
					}
					else {
						String[] parts = description.split(Constant.DataReference.SELECTOR_SERVICE_SEPARATOR_PATTERN);
						if (parts.length != 3) {
							throw new FocusInternalErrorException("Wrong number of fields for description of service.");
						}
						// <history|...|...>
						if (parts[0].equals(Constant.DataReference.SELECTOR_SERVICE_HISTORY)) {
							String u;
							if (parts[1].startsWith(Constant.DataReference.SELECTOR_CONTEXT_LABEL + Constant.DataReference.SELECTOR_CONTEXT_SEPARATOR)) {
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
					f = dataManager.getSample(description);
				}
			}
			catch (FocusMissingResourceException ex) { // FIXME very big try/catch block
				// we highlight the fact that we could not retrieve a resource by NOT saving
				// it into the DataContext. When get()-ing, the caller will
				// receive null, and can act accordingly.
				// FIXME is that really the case? might be a problem if we remove put(key, null) from register(); check the stash.
				return null;
			}
			return f.getUrl();
		}

		/**
		 * We know that we have a context reference (starting with "ctx/")
		 * and we want to return the corresponding URL.
		 *
		 * @param description The description of the resource to retrieve
		 * @return The url of the retrieved resource
		 */
		private String resolveReferencedUrl(String description) throws FocusMissingResourceException
		{
			String[] parts = description.split(Constant.DataReference.SELECTOR_CONTEXT_SEPARATOR);
			if (parts.length != 3 || !parts[0].equals(Constant.DataReference.SELECTOR_CONTEXT_LABEL)) {
				throw new FocusInternalErrorException("badly formatted reference");
			}

			String ref = parts[1];
			String res = getResolved(ref); // FIXME blocking
			if (res == null) {
				throw new FocusInternalErrorException("invalid object reference");
			}
			// This is ok to getSample(res) here as res has already been gotten
			FocusSample fs = dataManager.getSample(res);
			res = fs.get(parts[2]).toString();
			if (res == null) {
				throw new FocusInternalErrorException("Cannot find this field."); // quite strict. would be better to have checked exception FIXME
			}
			// we onyl refer to urls, so check if that is a valid one.
			try {
				new URL(res);
			}
			catch (MalformedURLException e) {
				throw new FocusInternalErrorException("Reference to a non-URL."); // quite strict. would be better to have checked exception FIXME
			}
			return res;
		}

	}


}
