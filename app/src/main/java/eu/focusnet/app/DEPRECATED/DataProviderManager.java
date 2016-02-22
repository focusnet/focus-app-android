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

package eu.focusnet.app.DEPRECATED;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 22.06.2015.
 */
public class DataProviderManager
{

	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_DELETE = "DELETE";
	private static final String TAG = DataProviderManager.class.getName();

	public static ResponseData retrieveData(String path) throws IOException
	{
		return makeHttpRequest(path);
	}

	public static ArrayList<String> checkDataFreshness(String path, List<RequestData> requestData) throws IOException
	{
		ArrayList<String> resourcesToRefresh = new ArrayList<>();
		Gson gson = new Gson();
		String requestDataJson = gson.toJson(requestData);
		Log.d(TAG, "The resources to check for freshness: " + requestDataJson);
		ResponseData responseData = makeHttpRequest(path, HTTP_METHOD_POST, requestDataJson);

		if (responseData != null) {
			String jsonResponseContent = responseData.getData();
			Log.d(TAG, "The Response for the freshness request: " + jsonResponseContent);
			ArrayList<RefreshData> refreshData = gson.fromJson(jsonResponseContent, new TypeToken<List<RefreshData>>()
			{
			}.getType());
			for (RefreshData rd : refreshData) {
				//TODO add the code for the other status
				if (rd.getStatus() == RefreshData.STATUS_CONTENT_DIFFERENT) {
					String resource = rd.getResource();
					int lastIndex = resource.lastIndexOf("/");
					resource = resource.substring(0, lastIndex);
					resourcesToRefresh.add(resource);
				}
			}
		}

		return resourcesToRefresh;
	}


	public static ResponseData updateData(String path, String jsonData) throws IOException
	{
		return makeHttpRequest(path, HTTP_METHOD_PUT, jsonData);
	}


	private static ResponseData makeHttpRequest(String path, String httpMethod, String jsonData) throws IOException
	{
		ResponseData responseData = null;
		HttpURLConnection connection = getHTTPConnection(path, httpMethod);
		connection.setDoOutput(true);
		try {
			connection.connect();
			OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			wr.write(jsonData);
			wr.flush();
			wr.close();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				responseData = getResponseData(connection.getHeaderFields(), connection.getInputStream());
			}
		}
		finally {
			connection.disconnect();
		}
		return responseData;
	}


	private static ResponseData makeHttpRequest(String path) throws IOException
	{
		ResponseData responseData = null;
		HttpURLConnection connection = getHTTPConnection(path, HTTP_METHOD_GET);
		try {
			connection.connect();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) { // FIXME we may be interested in keep the result if not HTTP_OK!!!
				responseData = getResponseData(connection.getHeaderFields(), connection.getInputStream());
			}
		}
		finally {
			connection.disconnect();
		}
		return responseData;
	}


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


	private static ResponseData getResponseData(Map<String, List<String>> headers, InputStream inputStream) throws IOException
	{
		ResponseData responseData = null;

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			buffer.append(line);
		}
		bufferedReader.close();
		responseData = new ResponseData(headers, buffer.toString());

		return responseData;
	}


	//TODO create other methods like(save, delete)


	//TODO should the below classes be defined in another place?

	/**
	 * Representation of the RequestData
	 * send to the webservice
	 */
	public static class RequestData
	{

		private String resource;

		public RequestData()
		{
		}

		public RequestData(String resource)
		{
			this.resource = resource;
		}

		public String getResource()
		{
			return resource;
		}

		public void setResource(String resource)
		{
			this.resource = resource;
		}
	}


	/**
	 * Representation of the RefreshData
	 * received from the webservice
	 */
	public static class RefreshData
	{
		//TODO add the other status
		public static final int STATUS_CONTENT_DIFFERENT = 210;

		private String resource;
		private int status;

		public RefreshData()
		{
		}

		public RefreshData(String resource, int status)
		{
			this.resource = resource;
			this.status = status;
		}

		public String getResource()
		{
			return resource;
		}

		public void setResource(String resource)
		{
			this.resource = resource;
		}

		public int getStatus()
		{
			return status;
		}

		public void setStatus(int status)
		{
			this.status = status;
		}


	}

	/**
	 * Representation of the response data
	 * received from the webservice
	 */
	public static class ResponseData
	{

		private Map<String, List<String>> headers;
		private String data;

		public ResponseData(Map<String, List<String>> headers, String data)
		{
			this.headers = headers;
			this.data = data;
		}

		public Map<String, List<String>> getHeaders()
		{
			return headers;
		}

		public void setHeaders(Map<String, List<String>> headers)
		{
			this.headers = headers;
		}

		public String getData()
		{
			return data;
		}

		public void setData(String data)
		{
			this.data = data;
		}
	}

}
