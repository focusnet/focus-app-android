package eu.focusnet.app.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An HTTP request to be issued to the REST server
 */
public class HttpRequest
{

	String method = null;
	String url = null;
	String payload = "";
	HttpResponse response = null;
	boolean goodConfiguration = true;

	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_DELETE = "DELETE";


	/**
	 * A simple Request without payload (GET or DELETE)
	 *
	 * @param method
	 * @param url
	 */
	public HttpRequest(String method, String url)
	{
		// check validity of url and method
		this.method = method;
		this.url = url;
		this.payload = "";

		// input saniztation FIXME TODO
		// do not mark as good if was false

/*		if ( ... ) {
			this.goodConfiguration = false;
		}
		*/
	}

	/**
	 * A request with associated payload to be passed in the body (POST or PUT)
	 *
	 * @param method
	 * @param url
	 * @param payload An object to be GSON-ified
	 */
	public HttpRequest(String method, String url, Object payload)
	{
		// do check that object is valid JSON output FIXME TODO
		/*		if ( ... ) {
			this.goodConfiguration = false;
		}
		*/


		this(method, url, payload.toString());
	}

	/**
	 * A request with associated payload to be passed in the body (POST or PUT)
	 *
	 * @param method
	 * @param url
	 * @param payload A simple string, to be inserted into the body as-is
	 */
	public HttpRequest(String method, String url, String payload)
	{
		this(method, url);
		this.payload = payload;
	}


	/**
	 * Actually run the request and build the output Response object.
	 *
	 * @return
	 * @throws RuntimeException
	 * @throws IOException
	 */
	public HttpResponse execute() throws RuntimeException, IOException
	{
		// input validation. url must be valid.
		if (!this.goodConfiguration) {
			throw new RuntimeException("Bad configuration for HTTP Request");
		}

		// object a new connection
		HttpURLConnection connection = HttpRequest.getHTTPConnection(this.url, this.method);

		// configure connection depending on method
		switch (this.method) {
			case HTTP_METHOD_POST:
			case HTTP_METHOD_PUT:
				connection.setDoOutput(true);
				break;
		}

		// do the actual HTTP request
		try {
			connection.connect();

			switch (this.method) {
				case HTTP_METHOD_POST:
				case HTTP_METHOD_PUT:
					OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
					wr.write(this.payload);
					wr.flush();
					wr.close();
					break;
			}

			// build the Response object
			this.response = new HttpResponse(connection);
		}
		finally {
			connection.disconnect();
		}


		return response;
	}

	/**
	 * We may need to include the access control token or any other mean; they would come from the NetworkManager (?)
	 * <p/>
	 * FIXME HttpsURLConnection ?
	 *
	 * @param path
	 * @param httpMethod
	 * @return
	 * @throws IOException
	 */
	private static HttpURLConnection getHTTPConnection(String path, String httpMethod) throws IOException
	{
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Accept", "application/json");
		connection.setInstanceFollowRedirects(true);
		connection.setRequestMethod(httpMethod);
		return connection;
	}


}