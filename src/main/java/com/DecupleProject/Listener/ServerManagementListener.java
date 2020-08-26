package com.DecupleProject.Listener;

import com.DecupleProject.Contents.RPG.UserStatus;
import com.DecupleProject.Core.CustomCommand;
import com.DecupleProject.Core.DatabaseManager;
import com.DecupleProject.Core.ServerManager.ServerManager;
import com.DecupleProject.Core.StealEmoji;
import com.DecupleProject.Core.Util.EasyEqual;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ServerManagementListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        User user = event.getUser();
        ServerManager manager = new ServerManager(event.getGuild(), member);

        manager.sendWelcomeMessage(user);

        super.onGuildMemberJoin(event);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        EmbedBuilder eb = new EmbedBuilder();

        try {

            Guild guild = event.getGuild();
            User user = event.getAuthor();
            TextChannel tc = event.getTextChannel();
            Message msg = event.getMessage();
            Member member = event.getMember();

            EasyEqual e = new EasyEqual();

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            DatabaseManager db = new DatabaseManager(user.getId(), tc, DefaultListener.jda);
            if (!db.nowExistsAllDatabase()) db.createAllDatabaseFromId();

            UserStatus us = new UserStatus(user.getId(), tc);
            us.setEXP(user.getId(), 1, false, true);

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            // TODO : If That TextChannel Playing Game, Return.

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ // Prefix Check

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

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            if (msg.getContentRaw().charAt(0) == 'Q' | msg.getContentRaw().charAt(0) == 'q' | msg.getContentRaw().charAt(0) == '.' | prefixCheck) {

                if (args.length < 1) return;
                ServerManager manager = new ServerManager(guild, member);

                if (e.eq(args[0], "청소", "cl", "cls", "clean", "삭제")) {
                    if (args.length == 1) {
                        eb.setDescription("몇 개의 메시지를 삭제하고 싶으신지 말해주세요.");
                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    } else {
                        manager.deleteMessages(tc, Integer.parseInt(args[1]));
                    }
                }

                if (e.eq(args[0], "서버", "server")) {
                    if (args.length == 1) {
                        eb.setTitle("서버 관리하기!");

                        eb.addField("서버 환영 메시지 추가하기", "```.서버 환영메시지\n>[제목]\n*[설명]\n=[필드제목],[필드설명]\n#[이미지URL]\n^[썸네일URL]\n_[푸터설명],[푸터URL]\n" +
                                "R[R],[G],[B]```", false);

                        eb.setColor(Color.CYAN);
                        tc.sendMessage(eb.build()).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();
                    }

                    if (e.eq(args[1], "환영메시지", "welcomeMessage", "환영", "인사")) {
                        String r = msg.getContentRaw();
                        r = r.replace(prefix + args[0] + " " + args[1] + "\n", "");
                        manager.setWelcomeMessage(tc, r);
                    }

                    if (e.eq(args[1], "경고", "Attention", "경고하기")) {
                        User target = msg.getMentionedMembers().get(0).getUser();

                        String info = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                        manager.attention(tc, target, info);
                    }

                    if (e.eq(args[1], "킥", "kick", "추방", "강퇴")) {
                        Member target = msg.getMentionedMembers().get(0);

                        String info = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                        manager.kickMember(tc, target, info);
                    }

                    if (e.eq(args[1], "밴", "ban", "차단")) {
                        Member target = msg.getMentionedMembers().get(0);

                        String info = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                        manager.banMember(tc, target, info, Integer.parseInt(args[2]));
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onMessageReceived(event);

    }
}
