package eu.focusnet.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 22.06.2015.
 */
public class NetworkUtil {

    public static final String HTTP_METHOD_DELETE = "DELETE";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_PUT = "PUT";

    private static final String TAG  = NetworkUtil.class.getName();

    public static InputStream openHTTPConnection(String path)throws IOException {

        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod(HTTP_METHOD_GET);
        connection.connect();
        return connection.getInputStream();
    }
}
