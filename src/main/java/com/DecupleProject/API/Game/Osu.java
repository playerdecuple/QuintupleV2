package com.DecupleProject.API.Game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import osuAPI.OsuAPI;

import java.awt.*;

public class Osu {

    public Osu(String user, TextChannel tc) {
        OsuAPI api = new OsuAPI("45d294d7fbdb0fb9aa877dec2f48b8e5a8f08107");
        EmbedBuilder eb = new EmbedBuilder();

        String username = api.getUser(user, 0).getUsername();
        double Acc = Double.parseDouble(api.getUser(user, 0).getAccuracy());
        eb.setTitle("Osu! : " + username);

        eb.addField("레벨", api.getUser(user, 0).getLevel(), true);
        eb.addField("정확도", String.format("%.2f", Acc) + "%", true);
        eb.addField("PP", api.getUser(user, 0).getPPRaw() + "PP", true);

        eb.addField("300 카운트", api.getUser(user, 0).getCount300(), true);
        eb.addField("100 카운트", api.getUser(user, 0).getCount100(), true);
        eb.addField("50 카운트", api.getUser(user, 0).getCount50(), true);

        eb.addField("총 점수", api.getUser(user, 0).getTotalScore(), true);
        eb.addField("PP 랭크", api.getUser(user, 0).getPPRank() + "등", true);
        eb.addField("총 판수", api.getUser(user, 0).getPlayCount() + "판", true);

        eb.addField("A 개수", api.getUser(user, 0).getCountRankA(), true);
        eb.addField("S 개수", api.getUser(user, 0).getCountRankS(), true);
        eb.addField("SS 개수", api.getUser(user, 0).getCountRankSS(), true);

        eb.addField("모드", String.valueOf(api.getUser(user, 0).getMode()), true);
        eb.setColor(Color.CYAN);

        eb.setImage("https://cdn.discordapp.com/attachments/700151918357512283/703610673376264253/e7fe61bdf6d4b28ce0bc363301ad38d5292b50d9a3e2dc7ff537a8569e84cb2f.png");
        tc.sendMessage(eb.build()).queue();
    }

}
