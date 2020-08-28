package com.DecupleProject.Core.Util;

import com.DecupleProject.QuintupleMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class LogWriter {

    private final ModeChecker mc = new ModeChecker();
    private final TextChannel log;

    EmbedBuilder eb = new EmbedBuilder();

    public LogWriter(JDA jda) {
        this.log = jda.getTextChannelById("740647419234222181");
    }

    public void sendStartingLog() {
        if (mc.isTestMode()) return;
        eb.setTitle("[Quintuple] Quintuple Activating");
        eb.setColor(Color.CYAN);
        eb.setDescription("PROJECT: DECUPLE <Discord Quintuple> By Decuple(데큐플#6056)" + QuintupleMain.version);
        log.sendMessage(eb.build()).queue();
    }

    public void sendMessage(String msg) {
        if (mc.isTestMode()) return;
        log.sendMessage(msg).queue();
    }

    public void sendEmbed(MessageEmbed eb) {
        if (mc.isTestMode()) return;
        log.sendMessage(eb).queue();
    }

}
