package com.DecupleProject.API.Game;

import com.DecupleProject.Core.Util.GetJSON;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/*
 * Code by Player_Decuple
 *
 * API by OWAPI (v3)
 */

public class Overwatch {

    private final String tag;
    private final String JSONBody;

    public Overwatch(String tag) throws Exception {

        this.tag = tag.replace("#", "-");
        this.JSONBody = getJSONBody();

    }

    public String getJSONBody() throws Exception {

        GetJSON json = new GetJSON();
        String BASE_URL = "https://owapi.net/api/v3/u/";
        return json.getJsonByUrlForUserMode(BASE_URL + URLEncoder.encode(tag, "UTF-8") + "/stats");

    }

    public void sendOverwatchStats(TextChannel tc) {

        EmbedBuilder eb = new EmbedBuilder();
        JsonParser jp = new JsonParser();

        JsonObject body = (JsonObject) jp.parse(JSONBody);
        JsonObject koreanServer_stats = body.getAsJsonObject("kr").getAsJsonObject("stats");

        // COMPETITIVE //
        JsonObject ko_competitive = koreanServer_stats.getAsJsonObject("competitive").getAsJsonObject("overall_stats");

        eb.setTitle("오버워치 : " + tag.replace("-", "#"));
        eb.setColor(Color.ORANGE);

        if (ko_competitive != null) {

            int ko_cpPrestige = 0;

            try {
                ko_cpPrestige = ko_competitive.get("prestige").getAsInt();
            } catch (UnsupportedOperationException e) {
                // ignore
            }

            int ko_cpLevel = ko_competitive.get("level").getAsInt();
            int ko_level = (ko_cpPrestige * 100) + ko_cpLevel;

            eb.addField("플레이어 레벨", "Level " + ko_level, true);

            // RANK POINTS //


            int games = ko_competitive.get("games").getAsInt();
            int losses = ko_competitive.get("losses").getAsInt();
            int wins = ko_competitive.get("wins").getAsInt();
            double winRate = ko_competitive.get("win_rate").getAsDouble();
            eb.addField("판수 및 승률", games + "판 " + wins + "승 " + losses + "패\n(승률 " + winRate + "%)", true);

            try {
                String ko_tankTier = ko_competitive.get("tank_tier").getAsString();
                int ko_tankRank = ko_competitive.get("tank_comprank").getAsInt();

                if (ko_tankTier != null) {
                    eb.addField("탱커", "```" + ko_tankTier.toUpperCase() + " " + ko_tankRank + "점```", false);
                }
            } catch (NullPointerException | UnsupportedOperationException e) {
                // ignore
            }

            try {
                String ko_dealTier = ko_competitive.get("damage_tier").getAsString();
                int ko_dealRank = ko_competitive.get("damage_comprank").getAsInt();

                if (ko_dealTier != null) {
                    eb.addField("딜러", "```" + ko_dealTier.toUpperCase() + " " + ko_dealRank + "점```", false);
                }
            } catch (NullPointerException | UnsupportedOperationException e) {
                // ignore
            }

            try {
                String ko_healTier = ko_competitive.get("support_tier").getAsString();
                int ko_healRank = ko_competitive.get("support_comprank").getAsInt();

                if (ko_healTier != null) {
                    eb.addField("힐러", "```" + ko_healTier.toUpperCase() + " " + ko_healRank + "점```", false);
                }
            } catch (NullPointerException | UnsupportedOperationException e) {
                // ignore
            }

            eb.setThumbnail(ko_competitive.get("avatar").getAsString());

        }

        tc.sendMessage(eb.build()).delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();

    }

}
