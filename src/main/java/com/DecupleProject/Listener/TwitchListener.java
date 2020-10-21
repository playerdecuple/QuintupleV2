package com.DecupleProject.Listener;

import com.DecupleProject.API.Game.LeagueOfLegends;
import com.DecupleProject.Core.Util.EasyEqual;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.python.indexer.Def;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TwitchListener implements TwirkListener {

    private static final EasyEqual e = new EasyEqual();
    public String senderDisplayName;
    public String senderUserName;
    public String msgContents;
    public String msgTimeStamp;



    @Override
    public void onConnect() {
        TextChannel[] tc = {DefaultListener.jda.getTextChannelById("704541586062573638"), DefaultListener.jda.getTextChannelById("699439025479745556")};

        for (TextChannel t : tc) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("데큐플 트위치 방송 ON!", "https://twitch.tv/playerdecuple");
            eb.setColor(Color.MAGENTA);
            eb.setDescription("지금 데큐플이 트위치 방송을 켰어요! 어서 접속해 보세요!");

            assert t != null;
            t.sendMessage(eb.build()).queue();
        }
    }

    @Override
    public void onPrivMsg(TwitchUser user, TwitchMessage msg) {
        this.senderDisplayName = user.getDisplayName();
        this.senderUserName = user.getUserName();
        this.msgContents = msg.getContent();

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        msgTimeStamp = df.format(msg.getSentTimestamp());
        DefaultListener.twitchTextChannel.sendMessage(getMsgContents()).queue();

        String mc = msg.getContent();

        Twirk t = DefaultListener.twirk;

        if (e.eq(mc, "!솔랭", "!솔로랭크", "!롤솔랭")) {
            try {
                LeagueOfLegends l = new LeagueOfLegends("데큐플");
                String tier = l.getTier(l.getSummonerId("데큐플"), 0);
                t.channelMessage("데큐플의 솔랭 점수 : " + tier);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        if (e.eq(mc, "!자랭", "!자유랭크", "!롤자랭")) {
            try {
                LeagueOfLegends l = new LeagueOfLegends("데큐플");
                String tier = l.getTier(l.getSummonerId("데큐플"), 1);
                t.channelMessage("데큐플의 솔랭 점수 : " + tier);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        if (e.eq(mc, "!야스오", "!모스트", "!모스트챔프", "!숙련도")) {
            try {
                LeagueOfLegends l = new LeagueOfLegends("데큐플");
                t.channelMessage(l.getPlayedChampInfo(l.getSummonerId("데큐플"), 157));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private MessageEmbed getMsgContents() {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setDescription(msgContents);
        eb.setFooter(senderUserName);

        return eb.build();

    }

}
