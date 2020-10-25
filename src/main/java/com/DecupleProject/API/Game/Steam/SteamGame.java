package com.DecupleProject.API.Game.Steam;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public class SteamGame {

    private final String country = "kr";
    private final String language = "koreana";

    public Games searchGames(String gameTitle, int numberOfResults, SearchMode src) {
        gameTitle = gameTitle.toLowerCase(Locale.ENGLISH);
        String sortBy = src.getSortBy();

        Games g = new Games();

        if (gameTitle.length() < 2 | numberOfResults < 1) return g;

        try {

            int count = 0, page = 0;
            boolean founding = true;

            while (founding) {
                founding = false;
                page++;

                Document doc = Jsoup.connect("http://store.steampowered.com/search/?term=" + gameTitle + "&sort_by=" + sortBy + "&page=" + page + "&cc=" + country + "&l=" + language)
                        .timeout(5000)
                        .get();

                Elements elements = doc.getElementsByAttributeValue("id", "search_result_container").select("a");

                for (Element element : elements) {
                    String id = element.attr("data-ds-appid").trim();
                    if (id.equals("")) continue;

                    String title = element.getElementsByClass("title").text().trim();
                    String discountPercent = element.getElementsByClass("search_discount").text().trim();

                    String price, discountedPrice;
                    if (discountPercent.equals("")) {
                        price = element.getElementsByClass("search_price").text().trim();
                        discountedPrice = "";
                    } else {
                        Elements priceElements = element.getElementsByClass("search_price");

                        int stInx = priceElements.toString().indexOf("<br>") + 4;
                        int enInx = priceElements.toString().indexOf("</div>");

                        price = priceElements.select("strike").text();
                        discountedPrice = priceElements.toString().substring(stInx, enInx).trim();
                    }

                    ArrayList<String> platforms = new ArrayList<>();
                    Elements platformsElms = element.select("p").select("span");

                    for (Element platformElm : platformsElms) {
                        String plf = platformElm.attr("class").split(" ")[0].trim();
                        platforms.add(plf);
                    }

                    String rvS = element.getElementsByClass("search_review_summary").attr("data-store-tooltip").trim();

                    if (!rvS.equals("")) {
                        String[] rvsA = rvS.split("<br>");
                        rvS = rvsA[0] + "(" + rvsA[1] + ")";
                    }

                    String addedOn = element.getElementsByClass("search_released").text().trim();
                    String thumbnailUrl = element.select("img").attr("src").trim();

                    /*

                    Document doc1 = Jsoup.connect("http://store.steampowered.com/app/" + id + "?l=" + language + "&cc=" + country + "/_/").timeout(5000).get();

                    ArrayList<String> tags = new ArrayList<>();
                    Elements tagElms = doc1.getElementsByClass("glance_tags").select("a");

                    for (Element tagElm : tagElms) {
                        String tag = tagElm.text().trim();
                        tags.add(tag);
                    }

                    String description = doc1.getElementsByClass("game_description_snippet").text().trim();
                    String releaseDate = doc1.getElementsByClass("date").text().trim();
                    String metascore = doc1.getElementsByAttributeValue("id", "game_area_metascore").text().trim();

                     */

                    ArrayList<String> tags = new ArrayList<>();
                    tags.add("");
                    String description = "", releaseDate = "", metascore = "";

                    g.add(new GameInfo(id, title, price, discountPercent, discountedPrice, rvS, platforms, addedOn, thumbnailUrl, tags, description, releaseDate, metascore));
                    founding = true;
                    count++;
                }

                if (count == numberOfResults) break;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            g.clear();
        }

        return g;
    }

    public MessageEmbed sendGameInfoEmbed(String gameName) {
        Games games = searchGames(gameName, 1, SearchMode.RELEVANCE);
        GameInfo game = games.get(0);

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("게임 : " + game.getTitle(), "http://store.steampowered.com/app/" + game.getId() + "/_/");
        eb.setDescription(game.getDescription());

        eb.addField("현재 가격", game.getDiscountedPrice() + " (원가 : " + game.getPrice() + ") [" + game.getDiscount() + " 할인]", false);

        /*
        eb.addField("게임 출시 날짜", game.getReleaseDate(), true);
        eb.addField("평점", game.getMetaScore(), true);

        StringBuilder tagStr = new StringBuilder();

        for (String tag : game.getTags()) {
            tagStr.append(tag).append(", ");
        }

        eb.setFooter(tagStr.toString());
         */
        eb.setImage(game.getThumbnailURL());
        eb.setColor(Color.GREEN);

        return eb.build();
    }

}
