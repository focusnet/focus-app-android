package eu.focusnet.app.DEPRECATED;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class RandomPiecesOfCode
{
/*
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
				//TODO register the code for the other status
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
	*/
}
