package com.DecupleProject.API;

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

public class Shopping {

    String id;
    String keyword;

    public Shopping(String id, String keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    public String getJson() {
        try {
            String clientId = "XMIUubZqhsElf34bZqwQ";
            String clientSecret = "sF5mkwexWf";

            String text = keyword;

            try {
                text = URLEncoder.encode(keyword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            URL url = new URL("https://openapi.naver.com/v1/search/shop.json?query=" + text + "&display=1");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("X-Naver-Client-Id", clientId);
            httpURLConnection.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));

            String result = "";
            String line;

            while ((line = br.readLine()) != null) {
                result += line;
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    private String getName() {
        try {
            String shoppingJson = getJson();

            JsonParser jp = new JsonParser();
            JsonObject obj = (JsonObject) jp.parse(shoppingJson);
            JsonArray items = obj.getAsJsonArray("items");
            JsonElement element = items.get(0);

            String titleA = element.getAsJsonObject().get("title").getAsString();
            String titleB = titleA.replace("<b>", "");
            String titleC = titleB.replace("</b>", "");

            return titleC;
        } catch (Exception e) {
            return null;
        }
    }

    private int getLowPrice() {
        try {
            String shoppingJson = getJson();

            JsonParser jp = new JsonParser();
            JsonObject obj = (JsonObject) jp.parse(shoppingJson);
            JsonArray items = obj.getAsJsonArray("items");
            JsonElement element = items.get(0);

            int lowPrice = element.getAsJsonObject().get("lprice").getAsInt();
            return lowPrice;
        } catch (Exception e) {
            return 0;
        }
    }

    private int getHighPrice() {
        try {
            String shoppingJson = getJson();

            JsonParser jp = new JsonParser();
            JsonObject obj = (JsonObject) jp.parse(shoppingJson);
            JsonArray items = obj.getAsJsonArray("items");
            JsonElement element = items.get(0);

            int highPrice = element.getAsJsonObject().get("hprice").getAsInt();
            return highPrice;
        } catch (Exception e) {
            return 0;
        }
    }

    private String getMallName() {
        try {
            String shoppingJson = getJson();

            JsonParser jp = new JsonParser();
            JsonObject obj = (JsonObject) jp.parse(shoppingJson);
            JsonArray items = obj.getAsJsonArray("items");
            JsonElement element = items.get(0);

            String mallName = element.getAsJsonObject().get("mallName").getAsString();
            return mallName;
        } catch (Exception e) {
            return null;
        }
    }

    private String getProductType() {
        try {
            String shoppingJson = getJson();

            JsonParser jp = new JsonParser();
            JsonObject obj = (JsonObject) jp.parse(shoppingJson);
            JsonArray items = obj.getAsJsonArray("items");
            JsonElement element = items.get(0);

            int productType = element.getAsJsonObject().get("productType").getAsInt();
            String productTypeName = null;

            switch (productType) {
                case 12:
                case 11:
                case 10:
                    productTypeName = "판매 예정 상품";
                    break;
                case 9:
                case 8:
                case 7:
                    productTypeName = "단종된 상품";
                    break;
                case 6:
                case 5:
                case 4:
                    productTypeName = "중고 상품";
                    break;
                case 3:
                case 2:
                case 1:
                    productTypeName = "일반 상품";
                    break;
                default:
                    productTypeName = null;
                    break;
            }
            return productTypeName;
        } catch (Exception e) {
            return null;
        }
    }

    private String getMaker() {
        try {
            String shoppingJson = getJson();

            JsonParser jp = new JsonParser();
            JsonObject obj = (JsonObject) jp.parse(shoppingJson);
            JsonArray items = obj.getAsJsonArray("items");
            JsonElement element = items.get(0);

            String maker = element.getAsJsonObject().get("maker").getAsString();
            return maker;
        } catch (Exception e) {
            return null;
        }
    }

    private String getBrand() {
        try {
            String shoppingJson = getJson();

            JsonParser jp = new JsonParser();
            JsonObject obj = (JsonObject) jp.parse(shoppingJson);
            JsonArray items = obj.getAsJsonArray("items");
            JsonElement element = items.get(0);

            String brand = element.getAsJsonObject().get("brand").getAsString();
            return brand;
        } catch (Exception e) {
            return null;
        }
    }

    private String getCategory() {
        try {
            String shoppingJson = getJson();

            JsonParser jp = new JsonParser();
            JsonObject obj = (JsonObject) jp.parse(shoppingJson);
            JsonArray items = obj.getAsJsonArray("items");
            JsonElement element = items.get(0);

            String category1 = element.getAsJsonObject().get("category1").getAsString();
            String category2 = element.getAsJsonObject().get("category2").getAsString();
            String category3 = element.getAsJsonObject().get("category3").getAsString();
            String category4 = element.getAsJsonObject().get("category4").getAsString();

            return category1 + " > " + category2 + " > " + category3 + " > " + category4;
        } catch (Exception e) {
            return null;
        }
    }

    private String getLink() {
        try {
            String shoppingJson = getJson();

            JsonParser jp = new JsonParser();
            JsonObject obj = (JsonObject) jp.parse(shoppingJson);
            JsonArray items = obj.getAsJsonArray("items");
            JsonElement element = items.get(0);

            String url = element.getAsJsonObject().get("link").getAsString();
            return url;
        } catch (Exception e) {
            return null;
        }
    }

    private String getImage() {
        try {
            String shoppingJson = getJson();

            JsonParser jp = new JsonParser();
            JsonObject obj = (JsonObject) jp.parse(shoppingJson);
            JsonArray items = obj.getAsJsonArray("items");
            JsonElement element = items.get(0);

            String url = element.getAsJsonObject().get("image").getAsString();
            return url;
        } catch (Exception e) {
            return null;
        }
    }

    public void sendShopMessage(TextChannel tc) {
        EmbedBuilder eb = new EmbedBuilder();

        String name = getName();
        String mallName = getMallName();
        int lowPrice = getLowPrice();
        int highPrice = getHighPrice();
        String productType = getProductType();
        String maker = getMaker();
        String brand = getBrand();
        String category = getCategory();
        String url = getLink();

        eb.setTitle("『" + name + "』");

        try {

            eb.addField("판매점", mallName, true);
            eb.addField("제조사", maker, true);
            eb.addField("브랜드", brand, true);

            eb.addField("상품 상태", productType, true);
            eb.addField("최저가", lowPrice + "원", true);
            eb.addField("최고가", highPrice + "원", true);

            eb.addField("카테고리", category, false);
            eb.addField("링크", url, false);

            eb.setThumbnail(getImage());
            eb.setImage("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + url);

            eb.setColor(Color.CYAN);

            tc.sendMessage(eb.build()).queue();

        } catch (Exception e) {
            tc.sendMessage("상품을" +
                    " 불러오는 도중 오류가 발생했습니다.").queue();
        }
    }

}
