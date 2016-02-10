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