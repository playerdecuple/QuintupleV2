package com.DecupleProject.Listener;

import com.DecupleProject.Contents.RPG.UserStatus;
import com.DecupleProject.Core.CustomCommand;
import com.DecupleProject.Core.DatabaseManager;
import com.DecupleProject.Core.ExceptionReport;
import com.DecupleProject.Core.GuildInfo;
import com.DecupleProject.Core.ServerManager.ServerManager;
import com.DecupleProject.Core.Util.EasyEqual;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
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
            if (!db.existsBasicFiles()) db.createAllDatabaseFromId();

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
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    if (args.length == 1) {
                        eb.setDescription("몇 개의 메시지를 삭제하고 싶으신지 말해주세요.");
                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    } else {
                        manager.deleteMessages(tc, Integer.parseInt(args[1]));
                    }
                }

                if (e.eq(args[0], "서버", "server")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (args.length == 1) {
                        eb.setTitle("서버 : " + guild.getName());

                        eb.addField(":name_badge: 서버 이름", guild.getName(), true);
                        eb.addField(":id: 서버 ID", guild.getId(), true);
                        eb.addField(":computer: 서버 관리자", Objects.requireNonNull(guild.getOwner()).getAsMention(), true);
                        eb.addField(":flag_kr: 서버 위치", guild.getRegion().getName(), true);
                        eb.addField(":desktop: 서버 채널 개수", ":speech_balloon: " + guild.getTextChannels().size() + "개의 텍스트 채널\n:microphone2: " +
                                guild.getVoiceChannels().size() + "개의 보이스 채널\n:ballot_box_with_check: " +
                                guild.getChannels().size() + "개의 모든 채널", true);
                        eb.addBlankField(true);
                        eb.addField(":people_holding_hands: 서버 멤버", guild.getMembers().size() + "명", true);
                        eb.addField(":compass: 서버 역할", guild.getRoles().size() + "개", true);
                        eb.addField(":date: 서버 생성 날짜", guild.getTimeCreated().toString().replace("T", "\n").replace("Z", ""), true);

                        eb.setThumbnail(guild.getIconUrl());
                        eb.setColor(Color.CYAN);
                        tc.sendMessage(eb.build()).queue();
                    }

                    if (e.eq(args[1], "환영메시지", "welcomeMessage", "환영", "인사")) {

                        if (member != null) {
                            if (!member.hasPermission(Permission.MANAGE_SERVER)) {
                                tc.sendMessage(":no_entry_sign: 이 명령어는 서버의 `서버 관리` 권한을 받은 사람만 사용할 수 있어요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                return;
                            }
                        }

                        String r = msg.getContentRaw();
                        r = r.replace(prefix + args[0] + " " + args[1] + "\n", "");
                        manager.setWelcomeMessage(tc, r);
                    }

                    if (e.eq(args[1], "경고", "Attention", "경고하기")) {

                        if (member != null) {
                            if (!member.hasPermission(Permission.MANAGE_SERVER)) {
                                tc.sendMessage(":no_entry_sign: 이 명령어는 서버의 `서버 관리` 권한을 받은 사람만 사용할 수 있어요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                return;
                            }
                        }

                        User target = msg.getMentionedMembers().get(0).getUser();

                        String info = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                        manager.attention(tc, target, info);

                    }

                    if (e.eq(args[1], "킥", "kick", "추방", "강퇴")) {

                        if (member != null) {
                            if (!member.hasPermission(Permission.KICK_MEMBERS)) {
                                tc.sendMessage(":no_entry_sign: 이 명령어는 서버의 `멤버 추방` 권한을 받은 사람만 사용할 수 있어요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                return;
                            }
                        }

                        Member target = msg.getMentionedMembers().get(0);

                        String info = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                        manager.kickMember(tc, target, info);
                    }

                    if (e.eq(args[1], "밴", "ban", "차단")) {

                        if (member != null) {
                            if (!member.hasPermission(Permission.BAN_MEMBERS)) {
                                tc.sendMessage(":no_entry_sign: 이 명령어는 서버의 `멤버 차단` 권한을 받은 사람만 사용할 수 있어요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                return;
                            }
                        }

                        Member target = msg.getMentionedMembers().get(0);

                        String info = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                        manager.banMember(tc, target, info, Integer.parseInt(args[2]));
                    }

                    if (e.eq(args[1], "용도", "채널")) {

                        GuildInfo guildInfo = new GuildInfo(guild);

                        if (member != null) {
                            if (!member.hasPermission(Permission.MANAGE_SERVER)) {
                                tc.sendMessage(":no_entry_sign: 이 명령어는 서버의 `서버 관리` 권한을 받은 사람만 사용할 수 있어요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                return;
                            }
                        }

                        if (args.length == 2) {
                            String message =
                                    "**채널의 용도를 정하는 명령어입니다.**\n\n" +
                                            ":one: __.서버 [용도 또는 채널] [음악]__\n" +
                                            "해당 텍스트 채널을 서버의 **음악 텍스트 채널**로 지정합니다.\n" +
                                            "__.서버 [용도 또는 채널] [음악] 제거__로 입력할 경우 지정된 음악 텍스트 채널을 제거합니다.";
                            tc.sendMessage(message).delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();
                        }

                        if (e.eq(args[2], "음악")) {

                            if (args.length == 3) {

                                String message = "`" + tc.getName() + "` 채널의 용도를 **음악 채널**로 설정했습니다!";
                                tc.sendMessage(message).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();

                                guildInfo.setMusicChannel(tc.getId());

                            } else if (e.eq(args[3], "제거", "삭제", "리셋")) {

                                String message = "`" + tc.getGuild().getName() + "` 서버의 **음악 채널** 용도를 제거했습니다!";
                                tc.sendMessage(message).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();

                                guildInfo.setMusicChannel("0");

                            }

                        }
                    }
                }

            }

        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException | IllegalStateException e) {
            // ignore
        } catch (Exception e) {
            new ExceptionReport(e, event.getAuthor(), event.getTextChannel());
            e.printStackTrace();
        }

        super.onMessageReceived(event);

    }
}
