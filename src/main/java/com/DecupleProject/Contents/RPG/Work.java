package com.DecupleProject.Contents.RPG;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Work {

    private final TextChannel tc;
    private final User user;

    private final Proficiency p;
    private final Account a;
    private final UserStatus u;

    private final Random random = new Random();
    private final EmbedBuilder eb = new EmbedBuilder();

    public Work(TextChannel tc, User user) {
        this.tc = tc;
        this.user = user;

        this.p = new Proficiency(user);
        this.a = new Account(user.getId(), user.getName(), tc);
        this.u = new UserStatus(user.getId(), tc);
    }

    public void fishing() {
        int fishingProficiency = p.getProficiencyValue(100) + 1;
        int giveMoney = random.nextInt(fishingProficiency);

        if (giveMoney < 5) {
            eb.setDescription("오늘은 허탕인가 봐요.. 한 푼도 못 벌으셨어요.");
            giveMoney = 0;
        } else {
            eb.setTitle("낚시 결과 : " + user.getAsTag());
            eb.setDescription("물고기를 제대로 낚았습니다!");
            eb.addField("벌은 금액", String.format("%,d", giveMoney) + " 플", true);
            eb.setThumbnail("https://png.pngtree.com/png-vector/20190726/ourlarge/pngtree-fish-vector-png-png-image_1610034.jpg");
            eb.setFooter(user.getAsTag(), user.getAvatarUrl());
            eb.setColor(Color.BLUE);
        }

        a.giveMoney(user.getId(), giveMoney, false, false);
        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        p.addValue(100, 1);
    }

    public void farming() {
        int fishingProficiency = p.getProficiencyValue(101) + 1;
        int giveMoney = random.nextInt(fishingProficiency);

        if (giveMoney < 5) {
            eb.setDescription("오늘은 허탕인가 봐요.. 한 푼도 못 벌으셨어요.");
            giveMoney = 0;
        } else {
            eb.setTitle("농사 결과 : " + user.getAsTag());
            eb.setDescription("수확물을 잘 재배하셨습니다!");
            eb.addField("벌은 금액", String.format("%,d", giveMoney) + " 플", true);
            eb.setThumbnail("https://i.pinimg.com/474x/45/b3/b5/45b3b54bb6822ea9b5ebc07bc2ac98c2.jpg");
            eb.setFooter(user.getAsTag(), user.getAvatarUrl());
            eb.setColor(Color.YELLOW);
        }

        a.giveMoney(user.getId(), giveMoney, false, false);
        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        p.addValue(101, 1);
    }

    public void woodCutting() {
        int fishingProficiency = p.getProficiencyValue(102) + 1;
        int giveMoney = random.nextInt(fishingProficiency);

        if (giveMoney < 5) {
            eb.setDescription("오늘은 허탕인가 봐요.. 한 푼도 못 벌으셨어요.");
            giveMoney = 0;
        } else {
            eb.setTitle("벌목 결과 : " + user.getAsTag());
            eb.setDescription("나무를 제대로 수확했습니다!");
            eb.addField("벌은 금액", String.format("%,d", giveMoney) + " 플", true);
            eb.setThumbnail("https://i.pinimg.com/originals/65/97/28/65972899f347f225471d93c160209716.jpg");
            eb.setFooter(user.getAsTag(), user.getAvatarUrl());
            eb.setColor(Color.getHSBColor(0f, 1.0f, 0.5f));
        }

        a.giveMoney(user.getId(), giveMoney, false, false);
        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        p.addValue(102, 1);
    }

    public void hunting() {
        int fishingProficiency = p.getProficiencyValue(103) + 1;
        int giveMoney = random.nextInt(fishingProficiency);

        if (giveMoney < 5) {
            eb.setDescription("오늘은 허탕인가 봐요.. 한 푼도 못 벌으셨어요.");
            giveMoney = 0;
        } else {
            eb.setTitle("사냥 결과 : " + user.getAsTag());
            eb.setDescription("많은 동물들을 잡았습니다!");
            eb.addField("벌은 금액", String.format("%,d", giveMoney) + " 플", true);
            eb.setThumbnail("https://img.danawa.com/prod_img/500000/814/630/img/1630814_1.jpg?shrink=500:500&_v=20160311134639");
            eb.setFooter(user.getAsTag(), user.getAvatarUrl());
            eb.setColor(Color.GREEN);
        }

        a.giveMoney(user.getId(), giveMoney, false, false);
        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        p.addValue(103, 1);
    }

    public void producing() {
        int fishingProficiency = p.getProficiencyValue(104) + 1;
        int giveMoney = random.nextInt(fishingProficiency);

        if (giveMoney < 5) {
            eb.setDescription("오늘은 허탕인가 봐요.. 한 푼도 못 벌으셨어요.");
            giveMoney = 0;
        } else {
            eb.setTitle("제조 결과 : " + user.getAsTag());
            eb.setDescription("물건을 제조했습니다!");
            eb.addField("벌은 금액", String.format("%,d", giveMoney) + " 플", true);
            eb.setThumbnail("https://w7.pngwing.com/pngs/10/840/png-transparent-minecraft-goblin-role-playing-game-potion-pathfinder-roleplaying-game-others-glass-game-video-game.png");
            eb.setFooter(user.getAsTag(), user.getAvatarUrl());
            eb.setColor(Color.GRAY);
        }

        a.giveMoney(user.getId(), giveMoney, false, false);
        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        p.addValue(104, 1);
    }

}
