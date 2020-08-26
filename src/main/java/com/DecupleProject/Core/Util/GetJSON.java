package com.DecupleProject.Core.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetJSON {

    public GetJSON() {}

    public String getJsonByUrl(String urlS) throws Exception {
        BufferedReader br = null;

        URL url = new URL(urlS);

        HttpURLConnection uC = (HttpURLConnection) url.openConnection();
        uC.setRequestMethod("GET");
        br = new BufferedReader(new InputStreamReader(uC.getInputStream(), "UTF-8"));

        String result = "";
        String line;

        while ((line = br.readLine()) != null) {
            result = result + line;
        }

        return result;
    }

    public String postJsonByUrl(String urlS) throws Exception {
        BufferedReader br = null;

        URL url = new URL(urlS);

        HttpURLConnection uC = (HttpURLConnection) url.openConnection();
        uC.setRequestMethod("POST");
        br = new BufferedReader(new InputStreamReader(uC.getInputStream(), "UTF-8"));

        String result = "";
        String line;

        while ((line = br.readLine()) != null) {
            result = result + line;
        }

        return result;
    }

    public String getJsonByUrlForUserMode(String urlS) throws Exception {
        BufferedReader br = null;

        URL url = new URL(urlS);

        HttpURLConnection uC = (HttpURLConnection) url.openConnection();
        uC.setRequestProperty("User-Agent", "Mozilla");
        br = new BufferedReader(new InputStreamReader(uC.getInputStream(), "UTF-8"));

        String result = "";
        String line;

        while ((line = br.readLine()) != null) {
            result = result + line;
        }

        return result;
    }

}
