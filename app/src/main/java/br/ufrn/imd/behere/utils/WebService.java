package br.ufrn.imd.behere.utils;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WebService {

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public String get(String urlInfo, String token, String apiKey) throws IOException {

        String response;

        URL url = new URL(urlInfo);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("authorization", "bearer " + token);
        connection.setRequestProperty("x-api-key", apiKey);

        InputStream in = new BufferedInputStream(connection.getInputStream());
        response = convertStreamToString(in);

        return response;
    }

    public String sendPost(String urlInfo, String query, Context context) {
        // Send data
        InputStream inputStream;
        HttpURLConnection urlConnection;
        byte[] outputBytes;
        String responseData = null;
        try {
            URL url = new URL(urlInfo);

        /* forming th java.net.URL object */
            urlConnection = (HttpURLConnection) url.openConnection();
            /* pass post data */
            outputBytes = query.getBytes("UTF-8");
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();
            OutputStream os = urlConnection.getOutputStream();
            os.write(outputBytes);
            os.close();

        /* Get Response and execute WebService request*/
            int statusCode = urlConnection.getResponseCode();

        /* 200 represents HTTP OK */
            if (statusCode == HttpsURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                responseData = convertStreamToString(inputStream);
            } else {
                responseData = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseData;
    }
}
