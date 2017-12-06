package br.ufrn.imd.behere.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Get {

    public String serviceCall(String urlInfo, String token, String apiKey) throws IOException {

        String response;

        URL url = new URL(urlInfo);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("authorization", "bearer " + token);
        connection.setRequestProperty("x-api-key", apiKey);


        InputStream in = new BufferedInputStream(connection.getInputStream());
        response = convert(in);

        return response;
    }

    public String convert(InputStream is) {
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
}
