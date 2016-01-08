package eu.focusnet.app.network;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * A response to an HTTP request to the FOCUS platform
 */
public class HttpResponse
{
	private Map<String, List<String>> headers = null;
	private String method = "";
	private String data = "";
	private int returnCode = 0;

	/**
	 * An HttpResponse contains all information that may be useful by the application to
	 * decide what to do after a network call.
	 *
	 * @param connection
	 * @throws IOException
	 */
	public HttpResponse(HttpURLConnection connection) throws IOException
	{
		this.method = connection.getRequestMethod();
		this.returnCode = connection.getResponseCode();
		this.headers = connection.getHeaderFields();
		InputStream inputStream = connection.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			buffer.append(line);
		}
		bufferedReader.close();
		this.data = buffer.toString();
	}


	/**
	 * Does the response successfully returned?
	 *
	 * @return
	 */
	public boolean isSuccessful()
	{
		return (this.returnCode == HttpURLConnection.HTTP_OK);
	}

	/**
	 * Return the body of the response
	 *
	 * @return
	 */
	public String getData()
	{
		return this.data;
	}

}