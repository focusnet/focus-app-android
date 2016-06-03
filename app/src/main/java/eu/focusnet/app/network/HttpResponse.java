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

package eu.focusnet.app.network;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
		if (!this.isSuccessful()) {
			throw new IOException("HTTP request failed.");
		}

		this.headers = connection.getHeaderFields();
		InputStream inputStream = connection.getInputStream();

		// depending on the content type, we decide whether the object is binary or text.
		List<String> content_types = this.headers.get("Content-Type");

		// basically, only application/json is text
		if (content_types.contains("application/json")) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				buffer.append(line);
			}
			bufferedReader.close();
			this.data = buffer.toString();
			return;
		}
		// the rest is considered as data
		else {
			byte[] buffer = new byte[4096];
			int n = -1;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			while ((n = inputStream.read(buffer)) != -1) {
				output.write(buffer, 0, n);
			}

			output.close();

			// convert the output stream into a base64-encoded string
			this.data = Base64.encodeToString(output.toByteArray(), Base64.DEFAULT);
			return;
		}
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