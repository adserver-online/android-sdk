package com.adserver.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient extends AsyncTask<String, Void, Void> {

    ResultListener listener;

    public HttpClient(ResultListener listener) {
        this.listener = listener;
    }

    public static String httpGet(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    public Void doInBackground(String... url) {
        try {
            String result = httpGet(url[0]);
            listener.GetCallback(result);
        } catch (Exception ex) {
            Log.e("HttpClient", "Url could not be retrieved");
        }
        return null;
    }


    public interface ResultListener {
        void GetCallback(String str);
    }
}

