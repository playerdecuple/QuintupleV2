package com.DecupleProject.Calculate;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class QuadraticEquation {

    public QuadraticEquation(double a, double b, double c, TextChannel tc) {
        double determinant;
        double root;
        double x1, x2;

        determinant=(b*b)-(4*a*c);
        root = Math.sqrt(determinant);

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("이차방정식");

        boolean minusB = b < 0D;
        boolean minusC = c < 0D;

        String expression = a + "x²";

        if (minusB) {
            expression = expression + " - " + String.valueOf(b).replace("-", "") + "x";
        } else {
            expression = expression + " + " + b;
        }

        if (minusC) {
            expression = expression + " - " + String.valueOf(c).replace("-", "");
        } else {
            expression = expression + " + " + c;
        }

        expression = expression + " = 0";

        eb.addField("식", "```" + expression + "```", false);
        eb.setColor(Color.GREEN);

        if (determinant > 0) {
            x1 = (-b + root) / (2 * a);
            x2 = (-b - root) / (2 * a);

            eb.addField("결과", "```" + expression + " = 0일 때, \n\n" + (b * -1) + " ± √" + determinant + "\n─────────────── = x 입니다.\n" + (2 * a) + "\n\n" +
                    "소수로 나타내면 " + x1 + " or " + x2 + "입니다.```", false);

            tc.sendMessage(eb.build()).queue();
        } else if (determinant == 0) {
            x1 = (-b + root) / (2 * a);

            eb.addField("결과", "```" + expression + " = 0일 때, \n\n" + (b * -1) + " + √" + determinant + "\n─────────────── = x 입니다.\n" + (2 * a) + "\n\n" +
                    "소수로 나타내면 " + x1 + "입니다.```", false);
            tc.sendMessage(eb.build()).queue();
        } else if (determinant < 0) {
            eb.addField("결과", "```" + expression + " = 0일 때, x의 값은 없습니다.", false);
            tc.sendMessage(eb.build()).queue();
        }
    }

}
