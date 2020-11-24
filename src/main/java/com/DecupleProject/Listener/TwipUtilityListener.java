package com.DecupleProject.Listener;

import com.DecupleProject.Listener.TwipUtil.TwipUtility.TwipUtilityEventHandler;
import com.DecupleProject.Listener.TwipUtil.TwipUtility.TwipUtilityEventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.DecimalFormat;

public class TwipUtilityListener implements TwipUtilityEventListener {

    public TwipUtilityListener() {
        TwipUtilityEventHandler.addListener(this);
    }

    @Override
    public void onDonateReceived(@NotNull String streamer, int amount, @NotNull String comment, @NotNull String nickname) {

        EmbedBuilder eb = new EmbedBuilder();
        char amC;

        if (amount <= 1000) {
            amC = 'D';
        } else if (amount <= 10000){
            amC = 'C';
        } else if (amount <= 20000) {
            amC = 'B';
        } else if (amount <= 30000) {
            amC = 'A';
        } else {
            amC = 'S';
        }

        eb.setTitle("후원 도착!");
        eb.addField("후원하신 분", nickname, true);
        eb.addBlankField(true);
        eb.addField("금액", new DecimalFormat("#,###").format(amount) + "원", true);
        eb.setDescription(comment);

        eb.setColor(amC == 'D' ? Color.ORANGE : amC == 'C' ? Color.YELLOW : amC == 'B' ? Color.CYAN : amC == 'A' ? Color.GREEN : Color.BLUE);

        DefaultListener.owner.openPrivateChannel().complete().sendMessage(eb.build()).queue();

    }

}
