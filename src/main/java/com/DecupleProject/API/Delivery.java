package com.DecupleProject.API;

import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.GetJSON;
import com.DecupleProject.Core.WriteFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Delivery {

    private final TextChannel tc;

    protected WriteFile w = new WriteFile();
    protected ReadFile r = new ReadFile();
    protected GetJSON g = new GetJSON();

    protected String code;
    protected String id;

    protected String carrier;


    public Delivery(TextChannel tc, String code, String id) {

        this.tc = tc;
        this.code = code.replace("택배", "").replace("편의점", "").replace("-", "");
        this.id = id;

        this.carrier = getCarrier();
    }

    public boolean eq(String anotherString) {
        return anotherString.equalsIgnoreCase(code);
    }

    public String getCarrier() {
        if (eq("CJ") || eq("CJ대한통운")) return "kr.cjlogistics";
        if (eq("DHL")) return "de.dhl";
        if (eq("Sagawa")) return "jp.sagawa";
        if (eq("Yamato") || eq("KuronekoYamato")) return "jp.yamato";
        if (eq("JapanPost")) return "jp.yuubin";
        if (eq("천일")) return "kr.chunlips";
        if (eq("CU")) return "kr.cupost";
        if (eq("GS포스트박스") || eq("GS")) return "kr.cvsnet";
        if (eq("CWAY") || eq("씨웨이") || eq("우리")) return "kr.cway";
        if (eq("대신")) return "kr.daesin";
        if (eq("우체국") || eq("ePost")) return "kr.epost";
        if (eq("한의사랑")) return "kr.hanips";
        if (eq("한진")) return "kr.hanjin";
        if (eq("합동")) return "kr.hdexp";
        if (eq("홈픽")) return "kr.homepick";
        if (eq("한서호남") || eq("한서") || eq("호남")) return "kr.honamlogis";
        if (eq("일양") || eq("일양로지스")) return "kr.ilyanglogis";
        if (eq("경동")) return "kr.kdexp";
        if (eq("건영")) return "kr.kunyoung";
        if (eq("로젠")) return "kr.logen";
        if (eq("롯데")) return "kr.lotte";
        if (eq("slx")) return "kr.slx";
        if (eq("성원") || eq("성원글로벌") || eq("성원글로벌카고")) return "kr.swgexp";
        if (eq("TNT")) return "nl.tnt";
        if (eq("EMS")) return "un.upu.ems";
        if (eq("FedEx")) return "us.fedex";
        if (eq("UPS")) return "us.ups";
        if (eq("USPS")) return "us.usps";

        return null;
    }

    public void sendDeliveryProcess() {
        try {
            EmbedBuilder eb = new EmbedBuilder();

            if (carrier == null) {
                eb.setDescription("잘못된 택배 회사를 입력하셨나 보네요.");
                tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
            }

            eb.setTitle("여기, 유저님의 배송 현황입니다!");
            eb.addField("택배 회사", code, true);
            eb.addBlankField(true);
            eb.addField("운송장 번호", id, true);
            eb.setColor(Color.CYAN);

            String urlStr = "https://apis.tracker.delivery/carriers/" + carrier + "/tracks/" + id;
            String jsonResult = g.getJsonByUrl(urlStr);

            JsonParser jp = new JsonParser();
            JsonObject obj_1 = (JsonObject) jp.parse(jsonResult);

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            JsonObject fromObj = obj_1.getAsJsonObject("from");

            String fromName = fromObj.getAsJsonPrimitive("name").getAsString();

            eb.addField("보내는 분", fromName, true);


            JsonObject toObj = obj_1.getAsJsonObject("to");

            String toName = toObj.getAsJsonPrimitive("name").getAsString();

            eb.addField("받는 분", toName, true);

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            JsonArray pg = obj_1.getAsJsonArray("progresses");

            for (int i = 0; i < pg.size(); i++) {
                JsonElement e = pg.get(i);
                String time = e.getAsJsonObject().get("time").getAsString().replace("T", " ").replace("+09:00", "");
                String[] t = time.split(" ");
                String status = e.getAsJsonObject().get("status").getAsJsonObject().get("text").getAsString();
                String description = e.getAsJsonObject().get("description").getAsString();

                String emoticon;
                if (status.equals("배송완료")) emoticon = ":white_check_mark: ";
                else emoticon = ":truck: ";

                String inf = ":timer: " + t[1] + ": " + emoticon + e.getAsJsonObject().get("location").getAsJsonObject().get("name").getAsString() + ", (" + status + ")";

                for (int j = 0; j < 999; j++) {
                    if (i + 1 == pg.size()) break;

                    JsonElement e1 = pg.get(i + 1);
                    String time1 = e1.getAsJsonObject().get("time").getAsString().replace("T", " ").replace("+09:00", "");
                    String[] t1 = time1.split(" ");
                    String inf1 = e1.getAsJsonObject().get("location").getAsJsonObject().get("name").getAsString();
                    String status1 = e1.getAsJsonObject().get("status").getAsJsonObject().get("text").getAsString();
                    String description1 = e1.getAsJsonObject().get("description").getAsString();

                    String emoticonR;
                    if (status1.equals("배송완료")) emoticonR = ":white_check_mark: ";
                    else emoticonR = ":truck: ";

                    if (t1[0].equalsIgnoreCase(t[0])) {
                        inf = inf + "\n:timer: " + t1[1] + ": " + emoticonR + inf1 + ", (" + status1 + ")";
                        description = description1;
                        i++;

                        if (i + 1 == pg.size()) break;
                    } else {
                        break;
                    }

                    if (i + 1 == pg.size()) break;
                }

                eb.addField(":calendar_spiral: " + t[0], inf, false);
                eb.setDescription(description);
            }

            tc.sendMessage(eb.build()).queue();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

