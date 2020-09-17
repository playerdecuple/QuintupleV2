package com.DecupleProject.Listener;

import com.DecupleProject.Contents.RPG.*;
import com.DecupleProject.Core.CustomCommand;
import com.DecupleProject.Core.ExceptionReport;
import com.DecupleProject.Core.Util.EasyEqual;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RPGListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        try {

            // TODO : If playing game, return.

            EasyEqual e = new EasyEqual();

            User user = event.getAuthor();
            TextChannel tc = event.getTextChannel();
            Guild guild = event.getGuild();
            Message msg = event.getMessage();

            EmbedBuilder eb = new EmbedBuilder();

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            CustomCommand CC = new CustomCommand(user);
            String prefix = CC.getPrefixStr();

            String[] args;
            boolean prefixCheck = false;

            if (user.isBot()) return;

            if (msg.getContentRaw().substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
                args = msg.getContentRaw().substring(prefix.length()).split(" ");
                prefixCheck = true;
            } else {
                args = msg.getContentRaw().substring(1).split(" ");
            }

            Work work = new Work(tc, user);
            Inventory inv = new Inventory(user);

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            if (msg.getContentRaw().charAt(0) == 'Q' | msg.getContentRaw().charAt(0) == 'q' | msg.getContentRaw().charAt(0) == '.' | prefixCheck) {

                if (args.length < 1) return;

                Account ac = new Account(user.getId(), user.getName(), tc);

                if (e.eq(args[0], "아르바이트", "알바", "돈")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();

                    if (args.length == 1) {
                        ac.giveMoney(user.getId(), 0, true, true);
                    } else if (e.eq(args[1], "랭킹", "랭크", "ranking", "rank")) {
                        if (args.length == 2) {
                            ac.sendMoneyRanking(null);
                        } else if (args.length == 3 && e.eq(args[2], "서버", "server", "우리", "이곳")) {
                            ac.sendMoneyRanking(guild);
                        }
                    }

                }

                if (e.eq(args[0], "도박", "배팅", "베팅", "Betting")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();

                    BettingGame bt = new BettingGame(user);

                    if (args.length == 1) {
                        tc.sendMessage("배팅 금액을 정해 주세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        return;
                    }

                    long betValue = Long.parseLong(args[1]);
                    float bettingPercentage = 1f / (float) betValue * 500000f;

                    if (bettingPercentage > 1f) {
                        bettingPercentage = 1f - new Random().nextFloat();
                    }

                    boolean bettingResult = bt.normalBetting(betValue, bettingPercentage);
                    eb.addField("배팅 금액", ac.getMoneyForHangeul(betValue) + "플", true);
                    eb.addField("성공 확률", (bettingPercentage * 100f) + "%", true);

                    if (bettingResult) {
                        eb.setTitle("운이 좋군요.");
                        eb.setColor(Color.GREEN);

                        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    } else {
                        eb.setTitle("잃어버렸습니다.");
                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    }

                }

                if (e.eq(args[0], "올인", "전부", "allin")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();

                    BettingGame bt = new BettingGame(user);

                    if (args.length != 1) {
                        return;
                    }

                    long betValue = ac.getNowMoneyForId();
                    float bettingPercentage = 1f / (float) betValue * 50000000f;

                    if (bettingPercentage > 1f) {
                        bettingPercentage = new Random().nextFloat();
                    }

                    boolean bettingResult = bt.normalBetting(betValue, bettingPercentage);
                    eb.addField("전 재산", ac.getMoneyForHangeul(betValue) + "플", true);
                    eb.addField("성공 확률", (bettingPercentage * 100f) + "%", true);

                    if (bettingResult) {
                        eb.setTitle("대박!");
                        eb.setColor(Color.GREEN);

                        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    } else {
                        eb.setTitle("처음부터 다시..");
                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    }

                }

                if (e.eq(args[0], "파산", "삭제", "초기화")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();

                    if (args.length != 1) {
                        return;
                    }

                    ac.giveMoney(user.getId(), ac.getNowMoneyForId() * -1L, false, false);

                    eb.setDescription("성공적으로 파산했어요.");
                    tc.sendMessage(eb.build()).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();

                }

                if (e.eq(args[0], "계좌")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();

                    if (args.length != 1) return;
                    ac.sendAccountMessage(tc);

                }

                if (e.eq(args[0], "벌목", "나무", "woodcut", "woodcutting")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    work.woodCutting();
                    inv.addItem("000", 1);
                }

                if (e.eq(args[0], "낚시", "물고기", "fishing", "낚싯대", "횟감수획")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    work.fishing();
                    inv.addItem("001", 1);
                }

                if (e.eq(args[0], "농사", "농작물", "수확물", "farming", "farm")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    work.farming();
                    inv.addItem("002", 1);
                }

                if (e.eq(args[0], "제작", "제조", "produce")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    work.producing();
                    inv.addItem("003", 1);
                }

                if (e.eq(args[0], "사냥", "사냥감", "hunt", "hunting")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    work.hunting();
                    inv.addItem("004", 1);
                }

                if (e.eq(args[0], "숙련도", "숙련", "일")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    Proficiency p = new Proficiency(user);

                    p.sendProficiencyInformation(tc);
                }

                if (e.eq(args[0], "아이템", "인벤토리", "Item", "Inventory")) {
                    inv.sendItemInfo();
                }

            }

        } catch (StringIndexOutOfBoundsException | IllegalStateException e) {
            // ignore
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }

    }

}
