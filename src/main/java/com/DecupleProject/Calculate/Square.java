package com.DecupleProject.Calculate;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class Square {
    public Square(long number, int square, TextChannel tc) {
        long Final = number;
        for (int i = 1; i < square; i++) {
            Final = Final * number;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("제곱");
        eb.addField("식","```" + number + " ^ " + square + "```", false);
        eb.addField("결과", "```" + number + " ^ " + square + " = " + Final + "입니다.```", false);
        eb.setColor(Color.GREEN);

        tc.sendMessage(eb.build()).queue();
    }
}
