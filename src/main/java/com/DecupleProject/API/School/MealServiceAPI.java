package com.DecupleProject.API.School;

import com.DecupleProject.Core.Util.GetJSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MealServiceAPI {

    public void sendMealInfo(String schoolName, TextChannel tc) throws Exception {

        String apiKey = SchoolBaseAPI.apiKey;

        Date today = new Date();

        SimpleDateFormat yearF = new SimpleDateFormat("yyyy");
        SimpleDateFormat montF = new SimpleDateFormat("MM");
        SimpleDateFormat daysF = new SimpleDateFormat("dd");

        String year = yearF.format(today);
        String month = montF.format(today);
        String day = daysF.format(today);

        String[] scNames = new SchoolBaseAPI().getSchoolName(schoolName);
        int ct = 0;

        for (String scName : scNames) {

            String urlStr = "https://open.neis.go.kr/hub/mealServiceDietInfo?Type=json&pIndex=1&pSize=100&ATPT_OFCDC_SC_CODE=" +
                    new SchoolBaseAPI().getEducationOfficeOfSchool(scName)[ct] + "&SD_SCHUL_CODE=" +
                    new SchoolBaseAPI().getSchoolCode(schoolName)[ct] + "&MLSV_YMD=" + year + month + day +
                    "&KEY=" + apiKey;

            String jsonRes = new GetJSON().getJsonByUrl(urlStr);

            JsonParser p = new JsonParser();
            JsonObject obj = (JsonObject) p.parse(jsonRes);
            JsonArray mealServiceDietInfoArray = obj.getAsJsonArray("mealServiceDietInfo");

            try {
                JsonObject row = mealServiceDietInfoArray.get(1).getAsJsonObject().get("row").getAsJsonArray().get(0).getAsJsonObject();

                String mealData = row.get("DDISH_NM").getAsString().replace("<br/>", "\n").replace("*", "");
                String calInfo = row.get("CAL_INFO").getAsString().replace("<br/>", "\n").replace("*", "");
                String nutrientData = row.get("NTR_INFO").getAsString().replace("<br/>", "\n").replace("*", "");

                EmbedBuilder eb = new EmbedBuilder();

                eb.setTitle("급식 : " + scName);
                eb.addField("날짜", year + ". " + month + ". " + day + ".", false);
                eb.addField("급식 정보", mealData, true);
                eb.addField("영양소 정보", nutrientData + "\n(칼로리 " + calInfo + ")", true);
                eb.setColor(Color.GREEN);

                tc.sendMessage(eb.build()).queue();
                ct++;
            } catch (NullPointerException e) {
                tc.sendMessage("해당 날짜의 급식 데이터베이스를 발견하지 못했어요.").queue();
                return;
            }

        }

    }

    public void sendMealInfo(String schoolName, TextChannel tc, String dateInfo) throws Exception {

        String apiKey = SchoolBaseAPI.apiKey;

        String[] scNames = new SchoolBaseAPI().getSchoolName(schoolName);
        int ct = 0;

        for (String scName : scNames) {

            String urlStr = "https://open.neis.go.kr/hub/mealServiceDietInfo?Type=json&pIndex=1&pSize=100&ATPT_OFCDC_SC_CODE=" +
                    new SchoolBaseAPI().getEducationOfficeOfSchool(scName)[ct] + "&SD_SCHUL_CODE=" +
                    new SchoolBaseAPI().getSchoolCode(schoolName)[ct] + "&MLSV_YMD=" + dateInfo +
                    "&KEY=" + apiKey;

            String jsonRes = new GetJSON().getJsonByUrl(urlStr);

            JsonParser p = new JsonParser();
            JsonObject obj = (JsonObject) p.parse(jsonRes);
            JsonArray mealServiceDietInfoArray = obj.getAsJsonArray("mealServiceDietInfo");

            try {
                JsonObject row = mealServiceDietInfoArray.get(1).getAsJsonObject().get("row").getAsJsonArray().get(0).getAsJsonObject();

                String mealData = row.get("DDISH_NM").getAsString().replace("<br/>", "\n").replace("*", "");
                String calInfo = row.get("CAL_INFO").getAsString().replace("<br/>", "\n").replace("*", "");
                String nutrientData = row.get("NTR_INFO").getAsString().replace("<br/>", "\n").replace("*", "");

                EmbedBuilder eb = new EmbedBuilder();

                int yearN = Integer.parseInt(dateInfo.substring(0, 4));
                int monthN = Integer.parseInt(dateInfo.substring(4, 6));
                int dayN = Integer.parseInt(dateInfo.substring(6, 8));

                eb.setTitle("급식 : " + scName);
                eb.addField("날짜", yearN + ". " + monthN + ". " + dayN + ".", false);
                eb.addField("급식 정보", mealData, true);
                eb.addField("영양소 정보", nutrientData + "\n(칼로리 " + calInfo + ")", true);
                eb.setFooter(new SchoolBaseAPI().getSchoolAddress(scName)[ct]);
                eb.setColor(Color.GREEN);

                tc.sendMessage(eb.build()).queue();

                ct++;
            } catch (NullPointerException e) {
                tc.sendMessage("해당 날짜의 급식 데이터베이스를 발견하지 못했어요.").queue();
                return;
            }

        }

    }

}
