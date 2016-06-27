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

package eu.focusnet.app.controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import eu.focusnet.app.model.gson.FocusObject;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.FocusInternalErrorException;

/**
 * An HTTP request to be issued to the REST server
 */
public class HttpRequest
{
	/**
	 * Type of HTTP method
	 */
	String method = null;

	/**
	 * Requested URL
	 */
	URL url = null;

	/**
	 * Payload of the request
	 */
	String payload = "";

	/**
	 * Response for the request
	 */
	HttpResponse response = null;

	/**
	 * Number of encountered errors when setting up the request
	 */
	int errors = 0;

	/**
	 * A simple Request without payload (GET or DELETE)
	 *
	 * @param method The HTTP method
	 * @param url    The requested URL
	 */
	public HttpRequest(String method, String url)
	{
		// check validity of url and method
		switch (method) {
			case Constant.Networking.HTTP_METHOD_GET:
			case Constant.Networking.HTTP_METHOD_POST:
			case Constant.Networking.HTTP_METHOD_PUT:
			case Constant.Networking.HTTP_METHOD_DELETE:
				this.method = method;
				break;
			default:
				++this.errors;
		}
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
	 * @param method  The HTTP method
	 * @param url     The requested URL
	 * @param payload An object to be GSON-ified
	 */
	public HttpRequest(String method, String url, FocusObject payload)
	{
		this(method, url, FocusAppLogic.getGson().toJson(payload));
	}

	/**
	 * A request with associated payload to be passed in the body (POST or PUT)
	 *
	 * @param method The HTTP method
	 * @param url The URL to request
	 * @param payload A simple string, to be inserted into the body as-is
	 */
	public HttpRequest(String method, String url, String payload)
	{
		this(method, url);
		this.payload = payload;
	}

	/**
	 * Create an HTTP(S) connection.
	 * <p/>
	 * keepalive and persistent connections are automatically handled by Anroid. Nothing to do.
	 * <p/>
	 * FIXME resolve redirections manually! Not done even if connection.setInstanceFollowRedirects(true)
	 * FIXME use a real access control manager (UserManager) instead of pushing headers registered in the application properties
	 *
	 * @param url        The URL to connect to
	 * @param httpMethod The HTTP method
	 * @return An HTTP(S) URL connection
	 * @throws IOException If the connection cannot be open
	 */
	private static HttpURLConnection httpConnectionFactory(URL url, String httpMethod) throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// HTTP-related configuration
		connection.setInstanceFollowRedirects(true);
		connection.setRequestMethod(httpMethod);
		// When appropriate, HTTPS-related configuration
		// Works out of the box if certificates are valid (with valid and registered CA)
		if (url.toString().startsWith("https://")) {
			SSLSocketFactory socket_factory = NetworkManager.getSslContext().getSocketFactory();
			((HttpsURLConnection) connection).setSSLSocketFactory(socket_factory);
		}

		// add custom headers when necessary
		String headers = ApplicationHelper.getProperty(Constant.AppConfig.PROPERTY_HTTP_REQUEST_MODIFIER_PREFIX + url.getHost());
		if (headers != null) {
			Pattern p = Pattern.compile("^([^\\s:]+):\\s*(.*)$");
			Matcher m = p.matcher(headers);
			boolean ma = m.matches();
			if (ma) {
				String label = m.group(1);
				String value = m.group(2);
				connection.setRequestProperty(label, value);
			}
			else {
				throw new FocusInternalErrorException("Invalid HTTP header description in property file.");
			}
		}

		return connection;
	}

	/**
	 * Actually run the request and build the output Response object.
	 *
	 * @return An {@link HttpResponse} object
	 * @throws IOException If the connection cannot be open
	 */
	public HttpResponse execute() throws IOException
	{
		if (this.errors != 0) {
			throw new FocusInternalErrorException("Bad configuration for HTTP Request");
		}

		// object a new connection
		HttpURLConnection connection = HttpRequest.httpConnectionFactory(this.url, this.method);

		// configure connection depending on method
		switch (this.method) {
			case Constant.Networking.HTTP_METHOD_POST:
			case Constant.Networking.HTTP_METHOD_PUT:
				connection.setDoOutput(true);
				break;
		}

		// do the actual HTTP request
		try {
			connection.connect();

			switch (this.method) {
				case Constant.Networking.HTTP_METHOD_POST:
				case Constant.Networking.HTTP_METHOD_PUT:
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