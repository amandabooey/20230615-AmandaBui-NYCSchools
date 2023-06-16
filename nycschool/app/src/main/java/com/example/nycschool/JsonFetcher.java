package com.example.nycschool;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Single;

/** Fetches JSON from the URL. */
public class JsonFetcher {
    JsonFetcher() {}

    /** Attempts to fetch the JSON data given the JSON URL. If the connection fails or there are
     * any IO errors, the caller is responsible for handling the error. This gives the caller the
     * ability to determine which UI to show.
     */
    public static Single<JSONArray> getJsonArray(String jsonUrl) {
        return Single.fromCallable(() -> {
            URL url = new URL(jsonUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();


            InputStream inputStream = urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuffer = new StringBuilder();

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }

            urlConnection.disconnect();
            bufferedReader.close();
            return new JSONArray(stringBuffer.toString());
        });
    }
}
