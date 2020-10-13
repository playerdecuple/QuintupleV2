package com.DecupleProject.API.School;

import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.GetJSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URLEncoder;

public class SchoolBaseAPI {

    public static final ReadFile r = new ReadFile();
    public static final String apiKey = r.readString("D:/Database/SchoolAPIKey.txt");

    public JsonElement parseSchoolInfo(String schoolName) throws Exception {
        String encodedSchoolName = URLEncoder.encode(schoolName, "UTF-8");
        String urlStr = "https://open.neis.go.kr/hub/schoolInfo?Type=json&pIndex=1&pSize=100&SCHUL_NM=" + encodedSchoolName + "&KEY=" + apiKey;

        GetJSON jsonGetter = new GetJSON();
        String json = jsonGetter.getJsonByUrl(urlStr);

        JsonParser jp = new JsonParser();
        JsonObject parseResult = (JsonObject) jp.parse(json);
        JsonArray schoolInfo = parseResult.getAsJsonArray("schoolInfo");

        return schoolInfo.get(1).getAsJsonObject().get("row").getAsJsonArray().get(0);
    }

    public String getSchoolCode(String schoolName) throws Exception {
        JsonElement e = parseSchoolInfo(schoolName);

        return e.getAsJsonObject().get("SD_SCHUL_CODE").getAsString();
    }

    public String getEducationOfficeOfSchool(String schoolName) throws Exception {
        JsonElement e = parseSchoolInfo(schoolName);

        return e.getAsJsonObject().get("ATPT_OFCDC_SC_CODE").getAsString();
    }

    public String getSchoolName(String schoolName) throws Exception {
        JsonElement e = parseSchoolInfo(schoolName);

        return e.getAsJsonObject().get("SCHUL_NM").getAsString();
    }

}
