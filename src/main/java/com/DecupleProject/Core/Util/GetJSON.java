package com.DecupleProject.Core.Util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetJSON {

    public GetJSON() {}

    public String getJsonByUrl(String urlS) throws Exception {
        BufferedReader br;

        URL url = new URL(urlS);

        HttpURLConnection uC = (HttpURLConnection) url.openConnection();
        uC.setRequestMethod("GET");
        br = new BufferedReader(new InputStreamReader(uC.getInputStream(), StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

    /* Never used code yet.
    public String postJsonByUrl(String urlS) throws Exception {
        BufferedReader br;

        URL url = new URL(urlS);

        HttpURLConnection uC = (HttpURLConnection) url.openConnection();
        uC.setRequestMethod("POST");
        br = new BufferedReader(new InputStreamReader(uC.getInputStream(), StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }
     */

    public String getJsonByUrlForUserMode(String urlS) throws Exception {
        try {
            BufferedReader br;

            URL url = new URL(urlS);

            HttpURLConnection uC = (HttpURLConnection) url.openConnection();
            uC.setRequestProperty("User-Agent", "Mozilla");
            br = new BufferedReader(new InputStreamReader(uC.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (FileNotFoundException ex) {
            // ignore
            return null;
        }
    }

}
