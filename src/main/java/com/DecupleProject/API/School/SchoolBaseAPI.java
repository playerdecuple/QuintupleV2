package com.DecupleProject.API.School;

import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.GetJSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URLEncoder;

public class SchoolBaseAPI {

    public static final ReadFile r = new ReadFile();
    public static final String apiKey = r.readString("D:/Database/SchoolAPIKey.txt");

    public JsonArray parseSchoolInfo(String schoolName) throws Exception {
        String encodedSchoolName = URLEncoder.encode(schoolName, "UTF-8");
        String urlStr = "https://open.neis.go.kr/hub/schoolInfo?Type=json&pIndex=1&pSize=100&SCHUL_NM=" + encodedSchoolName + "&KEY=" + apiKey;

        GetJSON jsonGetter = new GetJSON();
        String json = jsonGetter.getJsonByUrl(urlStr);

        JsonParser jp = new JsonParser();
        JsonObject parseResult = (JsonObject) jp.parse(json);
        JsonArray schoolInfo = parseResult.getAsJsonArray("schoolInfo");

        return schoolInfo.get(1).getAsJsonObject().get("row").getAsJsonArray();
    }

    public String[] getSchoolCode(String schoolName) throws Exception {
        JsonArray e = parseSchoolInfo(schoolName);

        String[] returnable = new String[e.size()];

        for (int i = 0; i < e.size(); i++) {
            returnable[i] = e.get(i).getAsJsonObject().get("SD_SCHUL_CODE").getAsString();
        }

        return returnable;
    }

    public String[] getEducationOfficeOfSchool(String schoolName) throws Exception {
        JsonArray e = parseSchoolInfo(schoolName);

        String[] returnable = new String[e.size()];

        for (int i = 0; i < e.size(); i++) {
            returnable[i] = e.get(i).getAsJsonObject().get("ATPT_OFCDC_SC_CODE").getAsString();
        }

        return returnable;
    }

    public String[] getSchoolName(String schoolName) throws Exception {
        JsonArray e = parseSchoolInfo(schoolName);

        String[] returnable = new String[e.size()];

        for (int i = 0; i < e.size(); i++) {
            returnable[i] = e.get(i).getAsJsonObject().get("SCHUL_NM").getAsString();
        }

        return returnable;
    }

    public String[] getSchoolAddress(String schoolName) throws Exception {
        JsonArray e = parseSchoolInfo(schoolName);

        String[] returnable = new String[e.size()];

        for (int i = 0; i < e.size(); i++) {
            returnable[i] = e.get(i).getAsJsonObject().get("ORG_RDNMA").getAsString();
        }

        return returnable;
    }

}
