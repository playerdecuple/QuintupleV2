package com.DecupleProject.API;

import com.DecupleProject.Core.ExceptionReport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Encyclopedia {
    public Encyclopedia(String sName, TextChannel tc) {
        try {
            EmbedBuilder eb = new EmbedBuilder();
            BufferedReader br;

            String clientId = "XMIUubZqhsElf34bZqwQ";
            String clientSecret = "sF5mkwexWf";

            String text = sName;

            try {
                text = URLEncoder.encode(sName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                new ExceptionReport(e);
                tc.sendMessage("인코딩에 실패했습니다.").queue();
            }

            URL url = new URL("https://openapi.naver.com/v1/search/encyc.json?query=" + text + "&display=1&start=1&sort=sim");
            HttpURLConnection uC = (HttpURLConnection) url.openConnection();
            uC.setRequestProperty("X-Naver-Client-Id", clientId);
            uC.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            br = new BufferedReader(new InputStreamReader(uC.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            JsonParser jp = new JsonParser();

            JsonObject obj = (JsonObject) jp.parse(result.toString());
            JsonArray items = obj.getAsJsonArray("items");
            JsonElement i_El = items.get(0);

            String title = i_El.getAsJsonObject().get("title").getAsString();
            String t = title.replaceAll("<b>", "");
            String f_title = t.replaceAll("</b>", "");

            String description = i_El.getAsJsonObject().get("description").getAsString();
            String d = description.replaceAll("<b>", "");
            String f_description = d.replaceAll("</b>", "");

            String link = i_El.getAsJsonObject().get("link").getAsString();
            String imglink = i_El.getAsJsonObject().get("thumbnail").getAsString();

            eb.setTitle("『" + f_title + "』");
            eb.setDescription(f_description);
            eb.setImage(imglink);

            eb.addField("링크", link, false);
            eb.setFooter("네이버 백과사전 API를 이용한 기능입니다.");

            eb.setColor(Color.CYAN);

            tc.sendMessage(eb.build()).queue();
        } catch (Exception e) {
            new ExceptionReport(e);
            EmbedBuilder eb = new EmbedBuilder();

            eb.setDescription("검색 결과가 없거나 잘못된 결과에요!");
            tc.sendMessage(eb.build()).queue();
        }
    }
}
