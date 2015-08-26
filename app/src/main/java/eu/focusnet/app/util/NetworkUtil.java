package eu.focusnet.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 22.06.2015.
 */
public class NetworkUtil {

    public static final String HTTP_METHOD_DELETE = "DELETE";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_PUT = "PUT";

    private static final String TAG  = NetworkUtil.class.getName();

    public static HttpURLConnection getHTTPConnection(String path, String httpMethod)throws IOException {
        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod(httpMethod);
        return connection;
    }

    public static ResponseData getResponseData(Map<String, List<String>> headers, InputStream inputStream) throws IOException {
        ResponseData responseData = null;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = bufferedReader.readLine()) != null){
            buffer.append(line);
        }
        bufferedReader.close();
        responseData = new ResponseData(headers, buffer.toString());

        return responseData;
    }


    //TODO should the below classes be defined in another place?

    public static class RequestData{

        private String resource;

        public RequestData() {}

        public RequestData(String resource) {
            this.resource = resource;
        }
        public String getResource() {
            return resource;
        }
        public void setResource(String resource) {
            this.resource = resource;
        }
    }

    public static class RefreshData{
        //TODO add the other status
        public static final int STATUS_CONTENT_DIFFERENT = 210;

        private String resource;
        private int status;

        public RefreshData() {}

        public RefreshData(String resource, int status) {
            this.resource = resource;
            this.status = status;
        }
        public String getResource() {
            return resource;
        }
        public void setResource(String resource) {
            this.resource = resource;
        }
        public int getStatus() {
            return status;
        }
        public void setStatus(int status) {
            this.status = status;
        }


    }

    public static class ResponseData {

        private Map<String, List<String>> headers;
        private String data;

        public ResponseData(Map<String, List<String>> headers, String data) {
            this.headers = headers;
            this.data = data;
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, List<String>> headers) {
            this.headers = headers;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

}
