package com.DecupleProject.Contents.RPG;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.Random;

public class BettingGame {

    private final User user;
    private final Account ac;

    private final EmbedBuilder eb = new EmbedBuilder();

    public BettingGame(User user) {
        this.user = user;
        this.ac = new Account(user);
    }

    public boolean normalBetting(long betValue, float percentage) {

        if (percentage == 0f) {
            percentage = 0.5f;
        }

        Random r = new Random();
        float v = r.nextFloat();

        if (v <= percentage) {
            ac.giveMoney(user.getId(), betValue, false, false);
            return true;
        } else {
            if (ac.getNowMoneyForId() - (betValue * -1) < 0)
                betValue = ac.getNowMoneyForId();
            ac.giveMoney(user.getId(), betValue * -1, false, false);
            return false;
        }

    }

}
