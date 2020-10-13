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

        String scName = new SchoolBaseAPI().getSchoolName(schoolName);

        String urlStr = "https://open.neis.go.kr/hub/mealServiceDietInfo?Type=json&pIndex=1&pSize=100&ATPT_OFCDC_SC_CODE=" +
                new SchoolBaseAPI().getEducationOfficeOfSchool(scName) + "&SD_SCHUL_CODE=" +
                new SchoolBaseAPI().getSchoolCode(schoolName) + "&MLSV_YMD=" + year + month + day +
                "&KEY=" + apiKey;

        String jsonRes = new GetJSON().getJsonByUrl(urlStr);

        JsonParser p = new JsonParser();
        JsonObject obj = (JsonObject) p.parse(jsonRes);
        JsonArray mealServiceDietInfoArray = obj.getAsJsonArray("mealServiceDietInfo");

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

    }

}
