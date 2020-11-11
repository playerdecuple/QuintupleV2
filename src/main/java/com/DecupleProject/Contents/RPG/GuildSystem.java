package com.DecupleProject.Contents.RPG;

import com.DecupleProject.Core.DeleteFile;
import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.WriteFile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GuildSystem {

    private final JDA jda;
    private final TextChannel textChannel;
    private final String userId;

    public GuildSystem(JDA jda, TextChannel tc, String userId) {
        this.jda = jda;
        this.textChannel = tc;
        this.userId = userId;

        File guildFile = new File("D:/Database/Guild/");
        File guildsFile = new File("D:/Database/Guild/Guilds/");
        File requestFile = new File("D:/Database/Guild/Requests/");

        if (!guildFile.exists()) guildFile.mkdir();
        if (!guildsFile.exists()) guildsFile.mkdir();
        if (!requestFile.exists()) requestFile.mkdir();
    }

    // GUILD INFORMATION GETTER METHODS
    public boolean hasGuild() {

        /*
        This method checks whether the user is joined to the guild.
        If the users has joined the guild, it returns true.
         */

        File guildFile = new File("D:/Database/Guild/" + userId + ".txt");
        return guildFile.exists();

    }

    public String getGuildId(String target) {

        /*
        This method returns the ID of the guild to which the user is currently joined.
        If the user has not joined the guild, this method returns null.
         */

        File guildFile = new File("D:/Database/Guild/" + (target == null ? userId : target) + ".txt");
        return guildFile.exists() ? new ReadFile().readString(guildFile) : null;

    }

    public int getGuildLevel(String guildId) {

        /*
        This method returns the guild's level.
        If the guild does not exist, this method returns 0.
         */

        File guildLevelFile = new File("D:/Database/Guild/Guilds/" + (guildId == null ? userId : guildId) + "/Level.txt");
        return guildLevelFile.exists() ? new ReadFile().readInt(guildLevelFile) : null;

    }

    public String getGuildName(String guildId) {

        /*
        This method returns the guild's name.
        If the guild does not exist, this method returns null.
         */

        File guildNameFile = new File("D:/Database/Guild/Guilds/" + (guildId == null ? userId : guildId) + "/Name.txt");
        return guildNameFile.exists() ? new ReadFile().readString(guildNameFile) : null;

    }


    public int getGuildTierFromLevel(int level) {

        /*

        Guild Tier
            Level : ~ 5, Tier : 3
            Level : 6 ~ 10, Tier : 2
            Level : 11 ~ 20, Tier : 1
            Level : 21 ~ 30, Tier : 0

        Max Level is 30.
        For debugging purposes, -1 is returned when the guild level exceeds level 30.

         */

        if (level <= 5) return 3;
        if (level <= 10) return 2;
        if (level <= 20) return 1;
        if (level <= 30) return 0;

        return -1;

    }

    public int getMaxUser(int tier) {

        /*

        Max Guild Crews
            Tier 3: 15 crews
            Tier 2: 20 crews
            Tier 1: 30 crews
            Tier 0: 50 crews

        For debugging purposes, 99 is returned when the guild tier is less than 0 tier.

         */

        switch (tier) {

            case 3:
                return 15;
            case 2:
                return 20;
            case 1:
                return 30;
            case 0:
                return 50;

            default:
                return 99;

            /*
            In QuintupleV1, Tier case was set in reverse.
             */

        }

    }

    public boolean isAdmin() {

        if (hasGuild()) {
            File v = new File("D:/Database/Guild/Guilds/" + userId + "/");
            return v.exists();
        } else {
            return false;
        }

    }

    public long getGuildMoney() {
        File f = new File("D:/Database/Guild/Guilds/" + getGuildId(userId) + "/Money.txt");
        return new ReadFile().readLong(f);
    }

    public void investToGuild(long money) {
        File f = new File("D:/Database/Guild/Guilds/" + getGuildId(userId) + "/Money.txt");

        Account a = new Account(userId, jda.retrieveUserById(userId).complete().getName(), textChannel);
        if (money > a.getNowMoneyForId()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setDescription("돈이 부족합니다.");

            textChannel.sendMessage(eb.build()).queue();
        }

        if (f.exists()) {
            new WriteFile().writeLong(f.getPath(), new ReadFile().readLong(f.getPath()) + money);
            new Account(userId, "", textChannel).giveMoney(userId, money * -1, false, false);
        }
    }

    public int getGuildEXP() {
        String expFile = "D:/Database/Guild/Guilds/" + getGuildId(getGuildId(userId)) + "/EXP.txt";
        File ef = new File(expFile);

        if (!ef.exists()) return 0;
        return new ReadFile().readInt(expFile);
    }

    public void sendGuildMembersList() {
        if (!hasGuild()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setDescription("길드가 없습니다. 길드를 생성해보거나 다른 길드에 가입하는 건 어떠세요?");

            textChannel.sendMessage(eb.build()).queue();
            return;
        }

        File f = new File("D:/Database/Guild/");
        File[] fs = f.listFiles();

        String nowGuildId = getGuildId(userId);
        StringBuilder text = new StringBuilder();

        int rank = 1;

        for (File file : fs) {
            if (!file.isDirectory()) {
                String gld = new ReadFile().readString(file.getPath());
                if (Objects.requireNonNull(gld).equals(nowGuildId)) {
                    text.append("\n").append(rank).append(". ").append(jda.retrieveUserById(file.getName().replace(".txt", "")).complete().getAsTag());

                    if (file.getName().replace(".txt", "").equals(nowGuildId)) text.append(" <길드장>");
                    rank++;
                }
            }
        }

        textChannel.sendMessage("```md\n# 《 " + getGuildName(nowGuildId) + " 》 길드의 멤버들\n" + text + "```").queue();
    }

    // GUILD MANAGE
    public boolean createGuild(String guildName) {

        /*
        This method creates a guild.
        If the guild is successfully created, True is returned, otherwise, False is returned.
         */

        File guildDirectory = new File("D:/Database/Guild/Guilds/" + userId + "/");
        File guildFile = new File("D:/Database/Guild/" + userId + ".txt");

        if (guildFile.exists() | guildDirectory.exists()) return false;
        // If the user has already joined the guild, return false.

        guildDirectory.mkdir();

        String levelPath = guildDirectory.getPath() + "/Level.txt";
        String expPath = guildDirectory.getPath() + "/EXP.txt";
        String moneyPath = guildDirectory.getPath() + "/Money.txt";
        String namePath = guildDirectory.getPath() + "/Name.txt";
        String leaderPath = guildDirectory.getPath() + "/Leader.txt";

        /* Never used code yet. (GUILD SKILLS)
        File skillDir = new File(guildDirectory.getPath() + "/Skill");
        skillDir.mkdir();
         */

        new WriteFile().writeInt(levelPath, 1);
        new WriteFile().writeInt(expPath, 0);
        new WriteFile().writeInt(moneyPath, 0);
        new WriteFile().writeString(namePath, guildName);
        new WriteFile().writeString(leaderPath, userId);
        new WriteFile().writeString(guildFile, userId);

        return true;

    }

    public void sendInviteRequest(String targetId) {

        if (!isAdmin()) {
            textChannel
                    .sendMessage("길드장만 길드에 멤버를 초대할 수 있습니다.")
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
            return;
        }

        File targetGuildFile = new File("D:/Database/Guild/" + targetId + ".txt");

        if (targetGuildFile.exists()) {
            textChannel
                    .sendMessage(jda.retrieveUserById(targetId).complete().getAsTag() + "님은 이미 가입된 길드가 존재합니다.")
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
            return;
        }

        File joinRequestFile = new File("D:/Database/Guild/Requests/" + targetId + "_join.txt");

        if (joinRequestFile.exists()) {
            joinToGuild(targetId, userId);
            return;
        }

        File inviteRequestFile = new File("D:/Database/Guild/Requests/" + targetId + "_invt.txt");
        new WriteFile().writeString(inviteRequestFile, targetId);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("길드에서 초대장을 받았습니다!");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.addField("길드 이름", getGuildName(userId), true);
        embedBuilder.addField("길드 레벨", "레벨 " + getGuildLevel(userId), true);
        embedBuilder.addField("길드장", jda.retrieveUserById(userId).complete().getAsTag(), true);
        embedBuilder.setImage(getGuildImage());

        try {
            jda.retrieveUserById(targetId)
                    .complete()
                    .openPrivateChannel()
                    .complete()
                    .sendMessage(embedBuilder.build())
                    .queue();
        } catch (Exception e) {
            // ignore
        }

    }

    public void sendJoinRequest(String guildId) {

        if (hasGuild()) {
            textChannel
                    .sendMessage("이미 길드에 가입되어 있습니다. 길드에서 탈퇴한 다음 시도해 주세요.")
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();

            return;
        }

        if (!new File("D:/Database/Guild/Guilds/" + guildId + "/").exists()) {
            textChannel
                    .sendMessage("해당 길드는 존재하지 않습니다.")
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();

            return;
        }

        File inviteRequestFile = new File("D:/Database/Guild/Requests/" + guildId + "_invt.txt");

        if (inviteRequestFile.exists()) {
            joinToGuild(userId, guildId);
            return;
        }

        File joinRequestFile = new File("D:/Database/Guild/Requests/" + userId + "_join.txt");
        new WriteFile().writeString(joinRequestFile, guildId);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("길드 가입 요청 서류를 받았습니다!");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.addField("유저 태그", jda.retrieveUserById(userId).complete().getAsTag(), true);
        embedBuilder.addField("유저 레벨", new UserStatus(userId, textChannel).getLevel() + " 레벨", true);
        embedBuilder.addField("유저 현금", new Account(userId, "", textChannel).getMoneyForHangeul(new Account(userId, "", textChannel).getNowMoneyForId()) + "플", true);
        embedBuilder.setImage(jda.retrieveUserById(userId).complete().getAvatarUrl());

        try {
            jda.retrieveUserById(guildId)
                    .complete()
                    .openPrivateChannel()
                    .complete()
                    .sendMessage(embedBuilder.build())
                    .queue();
        } catch (Exception e) {
            // ignore
        }

    }

    public void joinToGuild(String from, String to) {

        User newUser = jda.retrieveUserById(from).complete();
        User toUser = jda.retrieveUserById(to).complete();

        new WriteFile().writeString(new File("D:/Database/Guild/" + from + ".txt"), to);
        new DeleteFile().deleteFile(new File("D:/Database/Guild/Request/" + from + "_join.txt"));
        new DeleteFile().deleteFile(new File("D:/Database/Guild/Request/" + to + "_invt.txt"));

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("가입이 성사되었습니다!");
        embedBuilder.setDescription(newUser.getAsTag() + "님이 " + getGuildName(getGuildId(newUser.getId())) + " 길드에 가입했습니다!");
        embedBuilder.setImage(getGuildImage());
        embedBuilder.setFooter(newUser.getAsTag(), newUser.getAvatarId());
        embedBuilder.setColor(Color.YELLOW);

        try {
            newUser.openPrivateChannel()
                    .complete()
                    .sendMessage(embedBuilder.build())
                    .queue();
        } catch (Exception e) {
            // ignore
        }

        try {
            toUser.openPrivateChannel()
                    .complete()
                    .sendMessage(embedBuilder.build())
                    .queue();
        } catch (Exception e) {
            // ignore
        }

    }

    public void editGuildEXP(boolean showMessage, int amount, boolean levelUpShowMessage) {

        File expFile = new File("D:/Database/Guild/Guilds/" + getGuildId(userId) + "/EXP.txt");
        File levelFile = new File("D:/Database/Guild/Guilds/" + getGuildId(userId) + "/Level.txt");

        if (expFile.exists()) {

            int nowExp = new ReadFile().readInt(expFile);
            int finalExp = nowExp + amount;

            int guildLevel = getGuildLevel(getGuildId(userId));

            if (finalExp > getGuildLevel(getGuildId(userId)) * 10000D && guildLevel < 30) {
                if (levelUpShowMessage) {
                    EmbedBuilder eb = new EmbedBuilder();

                    eb.setDescription(getGuildName(getGuildId(userId)) + " 길드가 레벨업 하였습니다!\n" +
                            "`Lv. " + getGuildLevel(getGuildId(userId)) + " ≫ Lv. " + (getGuildLevel(getGuildId(userId)) + 1) + "`");
                    eb.setColor(Color.CYAN);
                    textChannel.sendMessage(eb.build()).queue();
                }

                guildLevel += 1;
                new WriteFile().writeInt(levelFile, guildLevel);
                new WriteFile().writeInt(expFile, 0);

                return;
            }

            if (showMessage) {
                EmbedBuilder eb = new EmbedBuilder();

                double percentage1 = (double) getGuildEXP() / ((double) getGuildLevel(getGuildId(userId)) * 10000D) * 100D;
                double percentage2 = (double) finalExp / ((double) getGuildLevel(getGuildId(userId)) * 10000D) * 100D;
                eb.setDescription(getGuildName(getGuildId(userId)) + " 길드의 경험치가 올랐습니다!\n" +
                        "`EXP : " + String.format("%.2f", percentage1) + "% ≫ EXP : "
                        + String.format("%.2f", percentage2) + "%`");
                eb.setColor(Color.CYAN);
            }

            new WriteFile().writeInt(expFile, finalExp);

        }
    }

    public void editGuildMoney(boolean showMessage, long amount) {
        File f = new File("D:/Database/Guild/Guilds/" + getGuildId(userId) + "/Money.txt");
        long nowMoney = new ReadFile().readLong(f.getPath());
        new WriteFile().writeLong(f.getPath(), nowMoney + amount);

        if (showMessage) {
            EmbedBuilder eb = new EmbedBuilder();

            if (amount >= 0) {
                eb.setDescription(amount + "플을 길드 자금으로 획득했습니다.");
            } else {
                eb.setDescription(amount + "플을 길드 자금에서 사용했습니다.");
            }

            textChannel.sendMessage(eb.build()).queue();
        }
    }

    public String getGuildImage() {
        File f = new File("D:/Database/Guild/Guilds/" + getGuildId(null) + "/Image.txt");

        if (!f.exists()) return jda.retrieveUserById(userId).complete().getAvatarUrl();
        return new ReadFile().readString(f);
    }

    public void setGuildImage(String url) {
        if (isAdmin()) {
            File f = new File("D:/Database/Guild/Guilds/" + getGuildId(null) + "/Image.txt");
            new WriteFile().writeString(f, url);
        }
    }


}
