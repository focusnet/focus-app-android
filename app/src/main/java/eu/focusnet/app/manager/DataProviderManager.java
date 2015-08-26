package eu.focusnet.app.manager;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import eu.focusnet.app.util.NetworkUtil;
import eu.focusnet.app.util.NetworkUtil.*;

/**
 * Created by admin on 22.06.2015.
 */
public class DataProviderManager {

    private static final String TAG  = DataProviderManager.class.getName();

    public static ResponseData retrieveData(String path)throws IOException {
        return makeHttpRequest(path);
    }

    private static List<String> checkDataFreshness(String path, List<RequestData> requestData) throws IOException {
        List<String> resourcesToRefresh  = new ArrayList<>();
        Gson gson = new Gson();
        String requestDataJson = gson.toJson(requestData);
        Log.d(TAG, "The resources to check for freshness: "+requestDataJson);
        ResponseData responseData = makeHttpRequest(path, NetworkUtil.HTTP_METHOD_POST, requestDataJson);

        if(responseData != null) {
            String jsonResponseContent = responseData.getData();
            List<RefreshData> refreshData = gson.fromJson(jsonResponseContent, new TypeToken<List<RefreshData>>(){}.getType());
            for(RefreshData rd : refreshData){
                //TODO add the code for the other status
                if(rd.getStatus() == RefreshData.STATUS_CONTENT_DIFFERENT){
                    String resource = rd.getResource();
                    int lastIndex = resource.lastIndexOf("/");
                    resource =  resource.substring(0, lastIndex);
                    resourcesToRefresh.add(resource);
                }
            }
        }

        return resourcesToRefresh;
    }

    private static ResponseData makeHttpRequest(String path, String httpMethod, String jsonData)throws IOException {
        ResponseData responseData = null;
        HttpURLConnection connection = NetworkUtil.getHTTPConnection(path, httpMethod);
        connection.setDoOutput(true);
        connection.connect();
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(jsonData);
        wr.flush();
        wr.close();
        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            responseData = NetworkUtil.getResponseData(connection.getHeaderFields(), connection.getInputStream());

        return  responseData;
    }

    private static ResponseData makeHttpRequest(String path) throws IOException {
        ResponseData responseData = null;

        HttpURLConnection connection = NetworkUtil.getHTTPConnection(path, NetworkUtil.HTTP_METHOD_GET);
        connection.connect();
        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            responseData = NetworkUtil.getResponseData(connection.getHeaderFields(), connection.getInputStream());

        return  responseData;
    }

    //TODO create other methods like(save, update, delete)
}
