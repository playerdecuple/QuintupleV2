package com.DecupleProject.Listener;

import com.DecupleProject.Contents.RPG.Account;
import com.DecupleProject.Core.CustomCommand;
import com.DecupleProject.Core.Util.EasyEqual;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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

            }

        } catch (StringIndexOutOfBoundsException e) {
            // ignore
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
