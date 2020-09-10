package com.DecupleProject.Core.Util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class LogWriter {

    private final ModeChecker mc = new ModeChecker();
    private final TextChannel log;

    public LogWriter(JDA jda) {
        this.log = jda.getTextChannelById("740647419234222181");
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
