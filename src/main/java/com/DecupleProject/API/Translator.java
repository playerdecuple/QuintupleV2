package com.DecupleProject.API;

import com.DecupleProject.Core.Util.EasyEqual;
import com.DecupleProject.Core.Util.GetJSON;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Translator {

    GetJSON j = new GetJSON();
    EasyEqual eq = new EasyEqual();

    private final String clientId = "XMIUubZqhsElf34bZqwQ";
    private final String clientSecret = "sF5mkwexWf";

    public String getLangCode(String str) {

        if (eq.eq(str, "한국어", "kor", "한글", "한국", "korean", "한", "kr", "ko")) return "ko";
        if (eq.eq(str, "일본어", "jp", "일본", "japanese", "일")) return "ja";
        if (eq.eq(str, "영어", "en", "eng", "english", "영")) return "en";
        if (eq.eq(str, "중국어", "중국어간체", "간체", "chinese", "중", "간")) return "zh-CN";
        if (eq.eq(str, "중국어번체", "번체", "대만", "taiwan", "대", "번")) return "zh-TW";

        return null;

    }

    public Translator(String body, TextChannel tc, String l1, String l2) throws IOException {

        EmbedBuilder eb = new EmbedBuilder();

        String ln1 = l1;
        String ln2 = l2;

        l1 = getLangCode(l1);
        l2 = getLangCode(l2);

        if (eq.eqNull(l1, l2)) {
            eb.setTitle("언어가 올바르지 않네요.");
            eb.addField("가능한 언어들", "```한국어, 일본어, 영어, 중국어간체, 중국어번체```", false);
            eb.setColor(Color.RED);

            tc.sendMessage(eb.build()).queue();
            return;
        }

        String baseUrl = "https://openapi.naver.com/v1/papago/n2mt";
        String text = "";

        try {
            text = URLEncoder.encode(body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            eb.setTitle("인코딩에 실패했어요.");
            eb.setColor(Color.RED);

            tc.sendMessage(eb.build()).queue();
            return;
        }

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        String responseBody = post(baseUrl, requestHeaders, text, l1, l2);

        JsonParser jp = new JsonParser();
        JsonObject obj = (JsonObject) jp.parse(responseBody);
        JsonObject message = (JsonObject) obj.get("message");
        JsonObject result = (JsonObject) message.get("result");

        String res = result.get("translatedText").getAsString();

        eb.setTitle("번역 완료!");

        // 1열
        eb.addField("언어 1 (From)", "`" + ln1 + "`", true);
        eb.addBlankField(true);
        eb.addField("언어 2 (To)", "`" + ln2 + "`", true);

        // 2열
        eb.addField(ln1, "```" + body + "```", false);
        eb.addField(ln2, "```" + res + "```", false);

        eb.setColor(Color.GREEN);

        tc.sendMessage(eb.build()).queue();

    }

    private static String post(String apiUrl, Map<String, String> requestHeaders, String text, String l1, String l2) throws IOException {

        HttpURLConnection con = (HttpURLConnection) new URL(apiUrl).openConnection();
        String postParams = "source=" + l1 + "&target=" + l2 + "&text=" + text;

        System.out.println(apiUrl + "/" + postParams);

        try {

            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream());

            dataOutputStream.write(postParams.getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                return readBody(con.getInputStream());
            } else {
                return readBody(con.getErrorStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        return "";

    }

    private static String readBody(InputStream body) {

        InputStreamReader sR = new InputStreamReader(body);

        try (BufferedReader lR = new BufferedReader(sR)) {

            StringBuilder responseBody = new StringBuilder();

            String line;

            while ((line = lR.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

}
