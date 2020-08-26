package com.DecupleProject.API;

import net.dv8tion.jda.api.entities.TextChannel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.python.core.Py;
import org.python.util.PythonInterpreter;

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

        String titleStr[] = new String[range];
        String singer[] = new String[range];
        String album[] = new String[range];

        String chart = "# 멜론 주간 차트 " + range + "위까지의 순위\n\n";

        for (int i = 0; i < range; i++) {
            titleStr[i] = titles.tagName("a").get(i).text();
            singer[i] = singers.tagName("a").get(i).text();
            singer[i] = singer[i].substring(0, singer[i].length() / 2);
            album[i] = albums.tagName("a").get(i).text();

            chart = chart + (i + 1) + ". " + titleStr[i] + " **" + singer[i] + ", " + album[i] + "**\n";
        }

        return chart;
    }

    public String getChartForRank(int rank) throws IOException {
        Document doc = Jsoup.connect("https://www.melon.com/chart/week/index.htm")
                .userAgent("Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko").get();

        Element body = doc.body();
        Elements titles = body.getElementsByClass("ellipsis rank01");
        Elements singers = body.getElementsByClass("ellipsis rank02");
        Elements albums = body.getElementsByClass("ellipsis rank03");

        String titleStr;
        String singer;
        String album;

        String chart = "";
        titleStr = titles.tagName("a").get(rank).text();
        singer = singers.tagName("a").get(rank).text();
        singer = singer.substring(0, singer.length() / 2);
        album = albums.tagName("a").get(rank).text();

        return singer + " - " + titleStr;
    }

    public void sendCharts(int range, TextChannel tc) throws IOException {
        tc.sendMessage("```md\n" + getCharts(range) + "```").queue();
    }

}
