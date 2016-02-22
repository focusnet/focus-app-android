/**
 *
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
 *
 */

package eu.focusnet.app.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.model.json.FocusObject;


/**
 * This class contains all methods pertaining to networking. It follows a Singleton pattern.
 * <p/>
 * This class is an abstraction library for communicating with our REST server.
 */
public class NetworkManager
{
	private static NetworkManager ourInstance = new NetworkManager();
	private static Context context = null;

	// FIXME get the root of REST server on first request (such that we have the root of services)

	/**
	 * Dummy constructor for our Singleton.
	 */
	private NetworkManager()
	{
	}

	/**
	 * Singleton acquisition method.
	 *
	 * @return
	 */
	public static NetworkManager getInstance()
	{
		return ourInstance;
	}

	/**
	 * Set the context of this manager
	 */
	public void setContext(Context c)
	{
		context = c;
	}


	// FIXME TODO copy RefreshData service from Yandy's code


	/**
	 * Is the network currently available?
	 * <p/>
	 * FIXME perhaps move to another Helper class, such that NetworkManager does not need a Context at all
	 *
	 * @return true if network is available, false otherwise.
	 */
	public boolean isNetworkAvailable() throws RuntimeException
	{
		if (context == null) {
			throw new FocusInternalErrorException("You must define a context!");
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}


	/**
	 * Get the latest version of an existing resource
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public HttpResponse get(String url) throws IOException
	{
		HttpRequest request = new HttpRequest(HttpRequest.HTTP_METHOD_GET, url);
		return request.execute();
	}

	/**
	 * Get a specific version of an existing resource
	 * <p/>
	 * <p/>
	 * FIXME any use?
	 *
	 * @param url
	 * @param version
	 * @return
	 * @throws IOException
	 */
	public HttpResponse get(String url, int version) throws IOException
	{
		url = url + "/" + version;
		return this.get(url);
	}

	/**
	 * Update an existing resource
	 *
	 * @param url
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public HttpResponse put(String url, FocusObject data) throws IOException
	{
		HttpRequest request = new HttpRequest(HttpRequest.HTTP_METHOD_PUT, url, data);
		return request.execute();
	}

	/**
	 * Create a new resource
	 *
	 * @param url
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public HttpResponse post(String url, FocusObject data) throws IOException
	{
		HttpRequest request = new HttpRequest(HttpRequest.HTTP_METHOD_POST, url, data);
		return request.execute();
	}

	/**
	 * Delete a remote resource.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public HttpResponse delete(String url) throws IOException
	{
		HttpRequest request = new HttpRequest(HttpRequest.HTTP_METHOD_DELETE, url);
		return request.execute();
	}

	/**
	 * Do login to remote endpoint
	 * FIXME wait for Jussi's authentication server
	 *
	 * @param user
	 * @param password
	 * @param server
	 * @return true on success or false on failure (access forbidden). If a network error occurs,
	 * throw a IOException.
	 */
	public boolean login(String user, String password, String server) throws IOException
	{
		return true;
	}


}
