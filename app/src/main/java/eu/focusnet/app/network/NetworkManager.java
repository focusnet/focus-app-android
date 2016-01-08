package eu.focusnet.app.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;


/**
 * This class contains all methods pertaining to networking. It follows a Singleton pattern.
 * <p/>
 * This class is an abstraction library for communicating with our REST server.
 */
public class NetworkManager
{
	private static NetworkManager ourInstance = new NetworkManager();
	private static Context context = null;

	/**
	 * Singleton acquisition method.
	 *
	 * @param c
	 * @return
	 */
	public static NetworkManager getInstance(Context c)
	{
		context = c;
		return ourInstance;
	}

	/**
	 * Dummy constructor for our Singleton.
	 */
	private NetworkManager()
	{
	}


	// FIXME TODO copy RefreshData service


	/**
	 * Is the network currently available?
	 *
	 * @return true if network is available, false otherwise.
	 */
	public boolean isNetworkAvailable()
	{
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
	 *
	 *
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
	public HttpResponse put(String url, Object data) throws IOException
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
	public HttpResponse post(String url, Object data) throws IOException
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
	 * @return
	 */
	public boolean login(String user, String password, String server)
	{
		return true;
	}



}
