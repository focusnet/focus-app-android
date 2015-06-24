package eu.focusnet.app.service;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import eu.focusnet.app.util.NetworkUtil;

/**
 * Created by admin on 22.06.2015.
 */
public class DataProviderService {

    private static final String TAG  = DataProviderService.class.getName();

    public static String retrieveData(String path){
        StringBuffer buffer = new StringBuffer();

        try {
            InputStream inputStream = NetworkUtil.openHTTPConnection(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                buffer.append(line);
            }
            bufferedReader.close();
        }
        catch (IOException e) {
            Log.d(TAG, e.getLocalizedMessage());
            return null;
        }

        return buffer.toString();
    }

    //TODO create other methods like(save, update, delete)
}
