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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.model.json.FocusObject;
import eu.focusnet.app.service.DataManager;

/**
 * An HTTP request to be issued to the REST server
 */
public class HttpRequest
{

	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_DELETE = "DELETE";
	String method = null;
	URL url = null;
	String payload = "";
	HttpResponse response = null;
	int errors = 0; // we count number of errors.

	/**
	 * A simple Request without payload (GET or DELETE)
	 *
	 * @param method
	 * @param url
	 */
	public HttpRequest(String method, String url)
	{
		// check validity of url and method
		this.method = method; // TODO check that is valid method -> ++this.errors;
		try {
			this.url = new URL(url);
		}
		catch (MalformedURLException e) {
			++this.errors;
		}
		this.payload = "";
	}

	/**
	 * A request with associated payload to be passed in the body (POST or PUT)
	 *
	 * @param method
	 * @param url
	 * @param payload An object to be GSON-ified
	 */
	public HttpRequest(String method, String url, FocusObject payload)
	{
		this(method, url, DataManager.getInstance().getGson().toJson(payload));
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
	 * We may need to include the access control token or any other mean; they would come from the NetworkManager (?)
	 * <p/>
	 * FIXME HttpsURLConnection for HTTPS connections?
	 * <p/>
	 * keepalive and persistent connections are automatically handled by Anroid. Nothing to do.
	 *
	 * @param url
	 * @param httpMethod
	 * @return
	 * @throws IOException
	 */
	private static HttpURLConnection getHTTPConnection(URL url, String httpMethod) throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setInstanceFollowRedirects(true); // FIXME we still have to resolve the redirection manually! TODO TODO
		connection.setRequestMethod(httpMethod);
		// no need to configure the persistence of HTTP connections. This is automatically handled
		// by Android
		return connection;
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
		if (this.errors != 0) {
			throw new FocusInternalErrorException("Bad configuration for HTTP Request");
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


}