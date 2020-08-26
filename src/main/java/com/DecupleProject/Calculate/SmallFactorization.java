package com.DecupleProject.Calculate;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class SmallFactorization {
    public SmallFactorization(int a, TextChannel tc) {
        String value = "1";
        int b = a;

        for (int i = 2; i <= a; i++) {
            while (a % i == 0) {
                value = value + " * " + i;
                a = a / i;
            }
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("소인수분해");

        eb.addField("값", "```" + b + "```", false);
        eb.addField("식", "```" + value + " = " + b + " 입니다.```", false);

        eb.setColor(Color.GREEN);
        tc.sendMessage(eb.build()).queue();
    }
}
