package com.DecupleProject.API;

import net.dv8tion.jda.api.entities.TextChannel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Melon {

    public Melon () {

    }


    public String getCharts(int range) throws IOException {
        Document doc = Jsoup.connect("https://www.melon.com/chart/week/index.htm")
                .userAgent("Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko").get();

        Element body = doc.body();
        Elements titles = body.getElementsByClass("ellipsis rank01");
        Elements singers = body.getElementsByClass("ellipsis rank02");
        Elements albums = body.getElementsByClass("ellipsis rank03");

        String[] titleStr = new String[range];
        String[] singer = new String[range];
        String[] album = new String[range];

        StringBuilder chart = new StringBuilder("# 멜론 주간 차트 " + range + "위까지의 순위\n\n");

        for (int i = 0; i < range; i++) {
            titleStr[i] = titles.tagName("a").get(i).text();
            singer[i] = singers.tagName("a").get(i).text();
            singer[i] = singer[i].substring(0, singer[i].length() / 2);
            album[i] = albums.tagName("a").get(i).text();

            chart.append(i + 1).append(". ").append(titleStr[i]).append(" **").append(singer[i]).append(", ").append(album[i]).append("**\n");
        }

        return chart.toString();
    }

    public String getChartForRank(int rank) throws IOException {
        Document doc = Jsoup.connect("https://www.melon.com/chart/week/index.htm")
                .userAgent("Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko").get();

        Element body = doc.body();
        Elements titles = body.getElementsByClass("ellipsis rank01");
        Elements singers = body.getElementsByClass("ellipsis rank02");

        String titleStr;
        String singer;

        titleStr = titles.tagName("a").get(rank).text();
        singer = singers.tagName("a").get(rank).text();
        singer = singer.substring(0, singer.length() / 2);

        return singer + " - " + titleStr;
    }

    public void sendCharts(int range, TextChannel tc) throws IOException {
        tc.sendMessage("```md\n" + getCharts(range) + "```").queue();
    }

}
