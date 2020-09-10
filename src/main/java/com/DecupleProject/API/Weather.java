package com.DecupleProject.API;

import com.DecupleProject.Core.Util.GetJSON;
import com.google.gson.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class Weather {

    private final GetJSON j = new GetJSON();

    public Weather() {
    }

    public String getCityCode(String cityName) {

        if (c(cityName, "서울", "Seoul")) return "Seoul";
        if (c(cityName, "부산", "Busan")) return "Busan";
        if (c(cityName, "대구", "Daegu")) return "Daegu";
        if (c(cityName, "인천", "Incheon")) return "Incheon";
        if (c(cityName, "광주", "Gwangju")) return "Gwangju";
        if (c(cityName, "대전", "Daejeon")) return "Daejeon";
        if (c(cityName, "울산", "Ulsan")) return "Ulsan";
        if (c(cityName, "세종", "Sejong")) return "Sejong";

        if (c(cityName, "경기", "Gyeonggi")) return "Gyeonggi-do";
        if (c(cityName, "강원", "Gangwon")) return "Gangwon-do";
        if (c(cityName, "충북", "충청북도", "Chungcheongbuk")) return "Chungcheongbuk-do";
        if (c(cityName, "충남", "충청남도", "Chungcheongnam")) return "Chungcheongnam-do";
        if (c(cityName, "전북", "전라북도", "Jeollabuk")) return "Jeollabuk-do";
        if (c(cityName, "전남", "전라남도", "Jeollanam")) return "Jeollanam-do";
        if (c(cityName, "경북", "경상북도", "Gyeongsangbuk")) return "Gyeongsangbuk-do";
        if (c(cityName, "경남", "경상남도", "Gyeongsangnam")) return "Gyeongsangnam-do";
        if (c(cityName, "제주", "Jeju")) return "Jeju";

        return "Seoul";

    }

    public void sendWeatherInformation(String cityName, TextChannel tc) throws Exception {

        EmbedBuilder eb = new EmbedBuilder();

        String apiKey = "6a87f92b26dd22add0f10e7d134d180c";
        String cityCode = getCityCode(cityName);
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=kr", cityCode, apiKey);

        String jsonBody = j.getJsonByUrl(url);
        JsonParser p = new JsonParser();

        JsonObject obj = (JsonObject) p.parse(jsonBody);
        JsonObject coordinates = obj.getAsJsonObject("coord");

        double lon = coordinates.get("lon").getAsDouble();
        double lat = coordinates.get("lat").getAsDouble();

        JsonArray weatherBasic = obj.getAsJsonArray("weather");

        String weather = weatherBasic.get(0).getAsJsonObject().get("description").getAsString();
        String weatherImage = "https://openweathermap.org/img/w/" + weatherBasic.get(0).getAsJsonObject().get("icon").getAsString() + ".png";

        JsonObject main = obj.getAsJsonObject("main");

        double nowTemp = main.get("temp").getAsDouble() - 273.15D;
        double minTemp = main.get("temp_min").getAsDouble() - 273.15D;
        double maxTemp = main.get("temp_max").getAsDouble() - 273.15D;
        double feelTemp = main.get("feels_like").getAsDouble() - 273.15D;

        int pressure = main.get("pressure").getAsInt();
        double humidity = main.get("humidity").getAsDouble();

        eb.setTitle(cityName + ", " + String.format("%.1f", nowTemp) + "˚C / " + weather);
        eb.setThumbnail(weatherImage);

        eb.addField("도시 위치", "위도 : " + lat + "\n경도 : " + lon, true);
        eb.addField("온도 정보", "최저 " + String.format("%.1f", minTemp) + "˚C / 최대 " + String.format("%.1f", maxTemp) + "˚C\n" +
                "(체감 온도 " + String.format("%.1f", feelTemp) + "˚C)", true);
        eb.addField("기압 정보", pressure + " hPa", true);
        eb.addField("습도", String.format("%.1f", humidity) + "%", true);

        JsonObject sys = obj.getAsJsonObject("sys");

        long sunrise = sys.get("sunrise").getAsLong() + 32400000L;
        long sunset = sys.get("sunset").getAsLong() + 32400000L;

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh시 mm분 ss초");
        String sunriseTime = dateFormat.format(sunrise);
        String sunsetTime = dateFormat.format(sunset);

        JsonObject wind = obj.getAsJsonObject("wind");

        double speed = wind.get("speed").getAsDouble();
        int deg = wind.get("deg").getAsInt();
        String degCardinal = getCardinalDirection(deg);

        eb.addField("바람", "풍속 " + String.format("%.1f", speed) + "m/s, " + degCardinal + "풍", true);

        JsonObject clouds = obj.getAsJsonObject("clouds");

        double cloud = clouds.get("all").getAsDouble();

        eb.addField("구름", "구름 " + String.format("%.1f", cloud) + "%", true);
        eb.addField("일출과 일몰", "일출 " + sunriseTime + "\n일몰 " + sunsetTime, true);

        try {
            JsonObject rain = obj.getAsJsonObject("rain");
            double rainVol1h = rain.get("1h").getAsDouble();
            eb.addField("강우량", rainVol1h + "mm/h", true);
        } catch (NullPointerException e) {
            // ignore
        }

        try {
            JsonObject snow = obj.getAsJsonObject("snow");
            double snowVol1h = snow.get("1h").getAsDouble();
            eb.addField("직설량", snowVol1h + "mm/h", true);
        } catch (NullPointerException e) {
            // ignore
        }

        eb.setColor(Color.CYAN);
        tc.sendMessage(eb.build()).delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();

    }

    public boolean c(String first, String... obj) {
        for (int i = 0; i < obj.length; i++) {
            if (first.toLowerCase().contains(obj[i].toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public String getCardinalDirection(int angle) {
        float degreePerDirection = 360f / 8f;

        float offsetAngle = angle + degreePerDirection / 2;

        return (offsetAngle >= 0 * degreePerDirection && offsetAngle < 1 * degreePerDirection) ? "북"
                : (offsetAngle >= 1 * degreePerDirection && offsetAngle < 2 * degreePerDirection) ? "북동"
                : (offsetAngle >= 2 * degreePerDirection && offsetAngle < 3 * degreePerDirection) ? "동"
                : (offsetAngle >= 3 * degreePerDirection && offsetAngle < 4 * degreePerDirection) ? "남동"
                : (offsetAngle >= 4 * degreePerDirection && offsetAngle < 5 * degreePerDirection) ? "남"
                : (offsetAngle >= 5 * degreePerDirection && offsetAngle < 6 * degreePerDirection) ? "남서"
                : (offsetAngle >= 6 * degreePerDirection && offsetAngle < 7 * degreePerDirection) ? "서"
                : "북서";
    }

}
