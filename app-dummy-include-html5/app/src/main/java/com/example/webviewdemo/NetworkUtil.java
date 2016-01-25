package com.example.webviewdemo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtil {

    public static final String HTTP_METHOD_GET = "GET";

    public static HttpURLConnection getHTTPConnection(URL url, String httpMethod) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod(httpMethod);
        return connection;
    }

    public static String makeHttpRequest(URL url, String httpMethod){
        String data = null;
        try {
            HttpURLConnection connection = getHTTPConnection(url, httpMethod);
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
            bufferedReader.close();
            data = buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
