package com.DecupleProject.Listener;

import com.DecupleProject.API.*;
import com.DecupleProject.API.Dictionary;
import com.DecupleProject.API.School.MealServiceAPI;
import com.DecupleProject.Calculate.QuadraticEquation;
import com.DecupleProject.Calculate.SmallFactorization;
import com.DecupleProject.Calculate.Square;
import com.DecupleProject.Contents.AttendanceCheck;
import com.DecupleProject.Contents.RPG.Account;
import com.DecupleProject.Contents.RPG.UserStatus;
import com.DecupleProject.Core.*;
import com.DecupleProject.Core.ServerManager.ServerManager;
import com.DecupleProject.Core.Util.*;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.management.MBeanServerConnection;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultListener extends ListenerAdapter {

    public static JDA jda;
    public static User owner;
    public static TextChannel twitchTextChannel;
    public static Twirk twirk;

    LogWriter lW;

    @Override
    public void onReady(ReadyEvent event) {

        EmbedBuilder eb = new EmbedBuilder();

        jda = event.getJDA();
        this.lW = new LogWriter(jda);
        owner = jda.retrieveUserById("419116887469981708").complete();

        twitchTextChannel = jda.getTextChannelById("727947460944855202");

        try {
            twirk = new TwirkBuilder("#playerdecuple", "oauth:rhhsunhtgg4hlyr1cfkfkl939u6wfk", "oauth:rhhsunhtgg4hlyr1cfkfkl939u6wfk").setVerboseMode(false).build();
            twirk.addIrcListener(new TwitchListener());
            twirk.connect();
        } catch (IOException | InterruptedException e) {
            new ExceptionReport(e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ㅡㅡㅡㅡㅡㅡ Sending 'NOW STATUS' ㅡㅡㅡㅡㅡㅡ //

        int sleepSec = 600; // Sending log cooldown.
        final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

        new TopicServerInfo();

        exec.scheduleAtFixedRate(() -> {
            eb.clear();
            eb.setTitle("Quintuple Self Check Service");
            eb.addField("Now Status", "Fine", true);
            eb.addField("JDA Heartbeats", jda.getGatewayPing() + " ms", true);

            eb.setColor(Color.CYAN);
            lW.sendEmbed(eb.build());
        }, 0, sleepSec, TimeUnit.SECONDS);

        final ScheduledThreadPoolExecutor execR = new ScheduledThreadPoolExecutor(1);

        final int[] mode = {0};

        execR.scheduleAtFixedRate(() -> {
            switch (mode[0]) {
                case 0:
                    jda.getPresence().setActivity(Activity.playing(jda.getUsers().size() + " 분들과 함께"));
                    mode[0] = 1;
                    break;
                case 1:
                    jda.getPresence().setActivity(Activity.watching(jda.getGuilds().size() + " 개의 길드를"));
                    mode[0] = 2;
                    break;
                case 2:
                    jda.getPresence().setActivity(Activity.listening("언제든지 '.도움말'을"));
                    mode[0] = 3;
                    break;
                case 3:
                    ReadFile r = new ReadFile();

                    File timeLog = new File("D:/Database/StartTime.txt");
                    long startTime = r.readLong(timeLog.getPath());

                    long af = System.currentTimeMillis() - startTime;

                    int seconds = (int) (af / 1000) % 60;
                    int minutes = (int) ((af / (1000 * 60)) % 60);
                    int hours = (int) ((af / (1000 * 60 * 60)));

                    jda.getPresence().setActivity(Activity.playing(hours + "시간 " + minutes + "분 " + seconds + "초동안 동작"));
                    mode[0] = 0;
                    break;
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Quintuple Self Checking");
        eb.addField("Now Status", "Worst", true);
        eb.addField("Discord Status Checking", "Unknown", true);
        eb.addField("LavaPlayer Status Checking", "Fine", true);
        eb.setDescription("Project : DECUPLE / Quintuple] Quintuple & JDA Missed 2 heartbeats! Try to reconnecting..");
        eb.setColor(Color.RED);

        lW.sendEmbed(eb.build());

    }

    @Override
    public void onReconnect(@NotNull ReconnectedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Quintuple Self Checking");
        eb.addField("Now Status", "Bad(Reconnected)", true);
        eb.addField("Discord Status Checking", "Fine(Reconnected)", true);
        eb.addField("LavaPlayer Status Checking", "Fine(Reconnected)", true);
        eb.setColor(Color.ORANGE);

        lW.sendEmbed(eb.build());
    }

    @Override
    public void onException(ExceptionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Quintuple Self Report : Occurred Exception");
        eb.setDescription(event.getCause().getMessage());
        eb.setColor(Color.RED);

        lW.sendEmbed(eb.build());
    }

    @Override
    public void onResume(@NotNull ResumedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Quintuple Self Checking");
        eb.addField("Now Status", "Bad(Reconnected)", true);
        eb.addField("Discord Status Checking", "Fine(Reconnected)", true);
        eb.addField("LavaPlayer Status Checking", "Fine(Reconnected)", true);
        eb.setColor(Color.ORANGE);

        lW.sendEmbed(eb.build());
    }

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        TextChannel tc = e.getGuild().getDefaultChannel();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("퀸튜플이 이 서버에 도착했습니다!");
        eb.setDescription("퀸튜플을 어떻게 사용하는지 알아보세요!");
        eb.addField("공식 위키", "<https://decupleproject.fandom.com/ko/wiki/%EB%8D%B0%ED%81%90%ED%94%8C%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8:%EB%94%94%EC%8A%A4%EC%BD%94%EB%93%9C%ED%80%B8%ED%8A%9C%ED%94%8C>", false);
        eb.setThumbnail(jda.getSelfUser().getAvatarUrl());
        eb.setFooter("Quintuple, a part of PROJECT: DECUPLE, made by TEAM DECUPLE.");
        eb.setColor(Color.CYAN);

        if (tc == null) return;
        tc.sendMessage(eb.build()).queue();
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

            DatabaseManager db = new DatabaseManager(user.getId(), tc, jda);
            if (!db.existsBasicFiles()) db.createAllDatabaseFromId();

            UserStatus us = new UserStatus(user.getId(), tc);

            GuildInfo guildInfo = new GuildInfo(guild);
            TextChannel logChannel = guildInfo.getLoggingChannel();

            us.setEXP(user.getId(), 1, false, true, logChannel);

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

            if (msg.getContentRaw().charAt(0) == ':') {
                String name = msg.getContentRaw().replace(":", "");
                StealEmoji se = new StealEmoji(user.getId(), jda, tc);
                String code = ":" + name + ":";

                if (se.hasEmoji(name) && msg.getContentRaw().contains(code)) {
                    se.sendStealEmoji(name);
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                }
            }

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            if (msg.getContentRaw().charAt(0) == 'Q' | msg.getContentRaw().charAt(0) == 'q' | msg.getContentRaw().charAt(0) == '.' | prefixCheck) {

                if (args.length <= 0) return;

                if (e.eq(args[0], "도움말") | e.eq(args[0], "도움") | e.eq(args[0], "help")) {

                    if (user.isBot()) return;
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    eb.setTitle("도움말을 보여드릴게요!");
                    eb.setDescription("아쉽지만, 명령어가 너무 많아서 정리를 해놓은 [인터넷 사이트](https://playerdecuple.github.io/posts/%ED%80%B8%ED%8A%9C%ED%94%8C%EC%9D%84-%EC%93%B0%EB%8A%94-%EB%B0%A9%EB%B2%95%EC%97%90-%EB%8C%80%ED%95%B4-%EC%95%8C%EC%95%84-%EB%B4%85%EC%8B%9C%EB%8B%A4/)로 이동시켜 드리겠습니다!");
                    eb.setColor(Color.CYAN);

                    sendPrivateMessage(user, eb.build());

                }

                if (e.eq(args[0], "핑") | e.eq(args[0], "ping")) {

                    if (user.isBot()) return;
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    long time = System.currentTimeMillis();

                    tc.sendMessage("불러오는 중입니다!").queue(response -> {
                        long ping = System.currentTimeMillis() - time;

                        eb.setTitle("퐁!");
                        eb.setColor(Color.GREEN);
                        eb.addField(":white_check_mark: 지연 시간(Latency)", ping + "ms", false);
                        eb.addField(":white_check_mark: 라이브러리 지연 시간(Heartbeats)", event.getJDA().getGatewayPing() + "ms", false);
                        eb.setThumbnail("https://bipum.com/web/product/small/201709/7244_shop1_502721.jpg");

                        response.delete().queue();
                        tc.sendMessage(eb.build()).queue();
                    });

                }

                if (e.eq(args[0], "초대") | e.eq(args[0], "invite")) {

                    if (user.isBot()) return;
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    sendPrivateMessage(user, "이 링크를 이용하시면, 데큐플 공식 서버로 가입하실 수 있어요! <http://decuple-d.o-r.kr/>");

                }

                if (e.eq(args[0], "시간") | e.eq(args[0], "시각") | e.eq(args[0], "time") | e.eq(args[0], "t")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy년 MM월 dd일");
                    SimpleDateFormat format2 = new SimpleDateFormat("HH시 mm분 ss초");

                    Date time = new Date();
                    Calendar calendar = Calendar.getInstance();

                    String[] days = {"일", "월", "화", "수", "목", "금", "토"};

                    String dt = format1.format(time);
                    String tm = format2.format(time);

                    eb.setTitle("현재 시간은 : " + tm, null);
                    eb.setColor(Color.CYAN);

                    eb.setDescription("오늘은 " + dt + " " + days[calendar.get(Calendar.DAY_OF_WEEK) - 1] + "요일이에요!");
                    eb.setFooter("GMT +9:00 / 서울시 기준입니다.");

                    eb.setImage("https://royaldesign.kr/images/3e201b25-77bb-4916-bb02-7dea45829997");

                    tc.sendMessage(eb.build()).queue();

                }

                if (e.eq(args[0], "봇", "Bot", "정보")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    ReadFile r = new ReadFile();

                    eb.setTitle("봇 : 퀸튜플");
                    eb.setColor(Color.CYAN);
                    eb.addField("이용 서버 수", event.getJDA().getGuilds().size() + "개", true);
                    eb.addField("이용 유저 수", event.getJDA().getUsers().size() + "분", true);

                    File timeLog = new File("D:/Database/StartTime.txt");
                    long startTime = r.readLong(timeLog.getPath());

                    long af = System.currentTimeMillis() - startTime;

                    int seconds = (int) (af / 1000) % 60;
                    int minutes = (int) ((af / (1000 * 60)) % 60);
                    int hours = (int) ((af / (1000 * 60 * 60)));

                    eb.addField("업타임", hours + "시간 " + minutes + "분 " + seconds + "초", true);
                    eb.addField("개발 시작", "2020. 08. 20. 04:00", true);
                    eb.addField("태그", event.getJDA().getSelfUser().getAsTag(), true);
                    eb.addField("봇 ID", event.getJDA().getSelfUser().getId(), true);
                    eb.addField("언어", "Java 8 | Maven", true);
                    eb.addField("라이브러리", "JDA", true);

                    eb.addField("사용 OS", System.getProperty("os.name") + " " + System.getProperty("os.arch"), false);

                    MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();

                    OperatingSystemMXBean ops = ManagementFactory.newPlatformMXBeanProxy(
                            mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);


                    eb.addField("사용 CPU",
                            "사용률 : " + String.format("%.2f", ops.getSystemCpuLoad() * 100D) + "%\n" +
                                    "사용 가능 코어 개수 : " + Runtime.getRuntime().availableProcessors() + "개", false);

                    double maxM = (double) ops.getTotalPhysicalMemorySize() / 1024D / 1024D / 1024D;
                    double freeM = (double) ops.getFreePhysicalMemorySize() / 1024D / 1024D / 1024D;
                    double usingM = maxM - freeM;

                    eb.addField("메모리", "커밋됨 : " + String.format("%.2f", (ops.getCommittedVirtualMemorySize() / 1024D / 1024D / 1024D)) + "GB\n " + "사용함 : " + String.format("%.2f", usingM) + "GB / " + String.format("%.2f", maxM) + "GB", false);

                    File f = new File("D:/Database/");

                    eb.addField("디스크", "사용함 : " + String.format("%.2f", (double) (f.getTotalSpace() - f.getFreeSpace()) / 1024D / 1024D / 1024D) + "GB / "
                            + String.format("%.2f", (double) f.getTotalSpace() / 1024D / 1024D / 1024D) + "GB", false);
                    eb.setFooter("QUINTUPLE, a part of 'Project: Decuple', in Team Decuple.");
                    eb.setThumbnail(jda.getSelfUser().getAvatarUrl());

                    tc.sendMessage(eb.build()).queue();

                }

                if (e.eq(args[0], "계산") | e.eq(args[0], "Calculate") | e.eq(args[0], "Calculating") | e.eq(args[0], "Calculator") | e.eq(args[0], "calc") | e.eq(args[0], "계산기")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    if (user.isBot()) return;

                    if (args.length < 2) {
                        eb.setTitle("계산 기능!");
                        eb.setDescription("기호는 +, - , *, /를 사용하세요!");
                        eb.addField("기본 계산", "`.계산 4 + 4`", false);
                        eb.addField("이차방정식", "`.계산 이차방정식 10 -9 -6`", false);
                        eb.addField("제곱근", "`.계산 루트 16`", false);
                        eb.addField("소인수분해", "`.계산 소인수분해 64`", false);
                        eb.addField("제곱", ".계산 제곱 4 3", false);
                        eb.setColor(Color.CYAN);

                        tc.sendMessage(eb.build()).queue();
                        return;
                    } else if (e.eq(args[1], "이차방정식")) {
                        try {
                            double a = Double.parseDouble(args[2]);
                            double b = Double.parseDouble(args[3]);
                            double c = Double.parseDouble(args[4]);

                            new QuadraticEquation(a, b, c, tc);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            eb.setTitle("이런, 계산에 오류가 있군요!");
                            eb.setDescription("`.계산 이차방정식 10 -6 -9` 형식으로 입력해 보세요.");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).queue();
                        } catch (NumberFormatException ex) {
                            eb.setTitle("이런, 계산에 오류가 있군요!");
                            eb.setDescription("값은 `2,147,483,647`까지만 허용됩니다.");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).queue();
                        }
                    } else if (e.eq(args[1], "소인수분해")) {
                        try {
                            int val = Integer.parseInt(args[2]);
                            new SmallFactorization(val, tc);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            eb.setTitle("이런, 계산에 오류가 있군요!");
                            eb.setDescription("`.계산 소인수분해 63` 형식으로 입력해 보세요.");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).queue();
                        } catch (NumberFormatException ex) {
                            eb.setTitle("이런, 계산에 오류가 있군요!");
                            eb.setDescription("값은 `2,147,483,647`까지만 허용됩니다.");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).queue();
                        }
                    } else if (e.eq(args[1], "제곱")) {
                        try {
                            int val = Integer.parseInt(args[2]);
                            int sq = Integer.parseInt(args[3]);

                            new Square(val, sq, tc);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            eb.setTitle("이런, 계산에 오류가 있군요!");
                            eb.setDescription("`.계산 제곱 2 3` 형식으로 입력해 보세요.");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).queue();
                        } catch (NumberFormatException ex) {
                            eb.setTitle("이런, 계산에 오류가 있군요!");
                            eb.setDescription("값은 `2,147,483,647`까지만 허용됩니다.");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).queue();
                        }
                    } else if (e.eq(args[1], "루트") | e.eq(args[1], "제곱근")) {
                        try {
                            int val = Integer.parseInt(args[2]);
                            double res = Math.sqrt(val);

                            eb.setTitle("루트");
                            eb.setColor(Color.GREEN);
                            eb.addField("식", "```√" + val + "```", false);
                            eb.addField("결과", "```√" + val + " = " + res + "```", false);
                            tc.sendMessage(eb.build()).queue();
                        } catch (NumberFormatException ex) {
                            eb.setTitle("이런, 계산에 오류가 있군요!");
                            eb.setDescription("값은 `2,147,483,647`까지만 허용됩니다.");
                            eb.setColor(Color.RED);
                            tc.sendMessage(eb.build()).queue();
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            eb.setTitle("이런, 계산에 오류가 있군요!");
                            eb.setDescription("`.계산 루트 16` 형식으로 입력해 보세요.");
                            eb.setColor(Color.RED);
                            tc.sendMessage(eb.build()).queue();
                        }
                    } else {
                        try {
                            ScriptEngineManager mgr = new ScriptEngineManager();
                            ScriptEngine engine = mgr.getEngineByName("JavaScript");
                            String foo = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                            eb.setTitle("계산");
                            eb.addField("식", "```" + foo + "```", false);
                            eb.addField("결과", "```" + foo + " = " + engine.eval(foo) + "입니다!```", false);
                            eb.setColor(Color.GREEN);

                            tc.sendMessage(eb.build()).queue();
                        } catch (Exception ex) {
                            eb.setTitle("이런, 계산에 오류가 있군요!");
                            eb.setDescription("혹시, 기호 이외의 다른 것을 써 넣은 것이 아닐까요?");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).queue();
                        }
                    }

                }

                if (e.eq(args[0], "번역", "translate", "번역기", "파파고", "통역", "통역기")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    try {
                        String body = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                        new Translator(body, tc, args[1], args[2]);
                    } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
                        eb.setTitle("번역을 하는 데 오류가 발생했어요..");
                        eb.setDescription("`.번역 한국어 영어 안녕하세요?` 형식으로 입력해 보세요.");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).queue();
                    }
                }

                if (e.eq(args[0], "출석", "출석체크", "출첵", "Attendance", "AttendanceCheck", "나님등장")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    AttendanceCheck at = new AttendanceCheck(user.getId(), tc, user.getName());
                    at.attendance();
                }

                if (e.eq(args[0], "백과사전", "네이버백과", "백과")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    new Encyclopedia(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), tc);
                }

                if (e.eq(args[0], "내정보", "나", "")) {

                    if (args.length == 1) {
                        if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                            tc.deleteMessageById(msg.getId()).queue();
                        eb.setTitle("유저 : " + user.getAsTag());

                        eb.addField("닉네임", user.getName(), true);
                        eb.addField("태그", user.getAsTag(), true);
                        eb.addField("ID", user.getId(), true);

                        eb.addField("가입한 날짜", user.getTimeCreated().toString().replace("T", "\n").replace("Z", "") + "", true);
                        eb.addField("봇 여부", user.isBot() ? "맞음" : "아님", true);
                        eb.addBlankField(true);

                        Account a = new Account(user.getId(), user.getName(), tc);
                        eb.addField(":moneybag: 자금(플)", String.format("%,d", a.getNowMoneyForId()) + "플", true);
                        eb.addField(":bulb: 경험치", "Lv. " + us.getLevel() + " / " + String.format("%.2f", ((double) us.getEXP() / ((double) us.getLevel() * 10D + 5D)) * 100D) + "%", true);

                        StringBuilder roles = new StringBuilder("@everyone");

                        if (member == null) return;

                        for (int i = 0; i < member.getRoles().size(); i++) {
                            roles.append(", @").append(member.getRoles().get(i).getName());
                        }

                        eb.addField("이 서버에 들어온 시각", member.getTimeJoined().toString().replace("T", "\n").replace("Z", ""), false);
                        eb.addField("이 서버에서의 역할 목록", "```" + roles.toString() + "```", false);

                        eb.setThumbnail(user.getAvatarUrl());
                        eb.setColor(member.getColor());

                        tc.sendMessage(eb.build()).queue();
                    } else {
                        try {
                            if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                                tc.deleteMessageById(msg.getId()).queue();

                            User targetUser = msg.getMentionedUsers().get(0);
                            eb.setTitle("유저 : " + targetUser.getAsTag());

                            eb.addField("닉네임", targetUser.getName(), true);
                            eb.addField("태그", targetUser.getAsTag(), true);
                            eb.addField("ID", targetUser.getId(), true);

                            eb.addField("가입한 날짜", targetUser.getTimeCreated().toString().replace("T", "\n").replace("Z", "") + "", true);
                            eb.addField("봇 여부", targetUser.isBot() ? "맞음" : "아님", true);
                            eb.addBlankField(true);

                            Account a = new Account(targetUser.getId(), targetUser.getName(), tc);
                            UserStatus usR = new UserStatus(targetUser.getId(), tc);

                            eb.addField(":moneybag: 자금(플)", String.format("%,d", a.getNowMoneyForId()) + "플", true);
                            eb.addField(":bulb: 경험치", "Lv. " + usR.getLevel() + " / " + String.format("%.2f", ((double) usR.getEXP() / ((double) usR.getLevel() * 10D + 5D)) * 100D) + "%", true);

                            StringBuilder roles = new StringBuilder("@everyone");

                            if (member == null) return;

                            for (int i = 0; i < member.getRoles().size(); i++) {
                                if (guild.getMember(targetUser) != null)
                                    roles.append(", @").append(Objects.requireNonNull(guild.getMember(targetUser)).getRoles().get(i).toString());
                            }

                            eb.addField("이 서버에 들어온 시각", member.getTimeJoined().toString().replace("T", "\n").replace("Z", ""), false);
                            eb.addField("이 서버에서의 역할 목록", "```" + roles.toString() + "```", false);


                            eb.setThumbnail(targetUser.getAvatarUrl());
                            eb.setColor(member.getColor());

                            tc.sendMessage(eb.build()).queue();
                        } catch (NullPointerException ex) {
                            // ignore
                        }
                    }

                }

                if (e.eq(args[0], "보고", "report", "리폿", "버그", "이슈", "issue", "bug")) {

                    /*
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    try {
                        eb.setTitle("누군가로부터 도착한 보고서입니다!");
                        eb.addField("보고하신 분", user.getAsTag(), false);
                        eb.setColor(Color.CYAN);
                        eb.addField("보고 내용", "```" + String.join(" ", Arrays.copyOfRange(args, 1, args.length)) + "```", false);
                        sendPrivateMessage(jda.retrieveUserById("419116887469981708").complete(), eb.build());

                        eb.clear();

                        eb.setDescription("보고를 완료했습니다.");

                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        return;
                    }
                     */
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    eb.setTitle("의견 및 피드백 해보기!");
                    eb.setDescription("환영합니다! 팀 데큐플은 여러분의 의견과 피드백을 받는 것에 대해 항상 감사하게 생각합니다. 다음과 같은 수단을 써서, 팀 데큐플에 연락을 주세요.");
                    eb.addField("팀 데큐플 공식 포럼(NodeBB 기반)", "[바로 가기](http://www.developerdecuple.kro.kr/)", true);
                    eb.addField("팀 데큐플 공식 이메일", "playerdecuple@gmail.com", true);
                    eb.addField("팀 데큐플 공식 블로그", "[바로 가기](https://playerdecuple.github.io/)", true);
                    eb.setColor(Color.BLUE);

                    tc.sendMessage(eb.build()).queue();

                }

                if (e.eq(args[0], "멜론차트", "인기차트")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    try {
                        Melon melon = new Melon();

                        if (Integer.parseInt(args[1]) > 100 | Integer.parseInt(args[1]) < 1) {
                            eb.setTitle("순위 범위를 벗어났어요.");
                            eb.setDescription("순위는 1위부터 100위까지만 집계할 수 있습니다.");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        }

                        melon.sendCharts(Integer.parseInt(args[1]), tc);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        eb.setTitle("범위를 입력하지 않으셨나 봐요..");
                        eb.setDescription("`.인기차트 10` 형식으로 입력해 보세요.");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    }
                }

                if (e.eq(args[0], "쇼핑", "shopping", "최저가", "구매")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    Shopping sh = new Shopping(user.getId(), String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                    sh.sendShopMessage(tc);
                }

                if (e.eq(args[0], "DB", "데이터베이스", "database")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    if (e.eq(args[1], "del", "delete", "삭제")) {
                        db.editDatabase(args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)), true);
                        return;
                    }
                    if (e.eq(args[1], "view", "viewer", "열람", "확인", "뷰어")) {
                        db.getDatabaseAndSendInfo(args[1]);
                        return;
                    }

                    db.editDatabase(args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)), false);
                }

                if (e.eq(args[0], "사전", "국어사전", "국어", "뜻")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (args.length == 1) {
                        eb.setTitle("뜻을 찾으려면..");
                        eb.setDescription("`.사전 단어` 형식으로 입력해 주세요.");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).queue();
                        return;
                    }

                    Dictionary dic = new Dictionary();
                    String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    String means = dic.getSearchResultFromWord(input);

                    if (e.eq(means, "그런 단어는 없습니다.")) {
                        eb.setTitle("이런, 그 단어를 사전에서 못 찾았어요.");
                        eb.setDescription("잘못 입력한 게 아닐까요? 한 번만 다시 확인해 보세요.");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).queue();
                        return;
                    }

                    eb.setTitle("국어 사전에서 찾아본 결과에요!");
                    eb.addField("찾아본 단어", "```" + input + "```", false);
                    eb.addField("뜻", "```md" + means + "```", false);
                    eb.setColor(Color.GREEN);

                    tc.sendMessage(eb.build()).queue();

                }

                if (e.eq(args[0], "auth", "권한", "authorization")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    try {

                        Authority a = new Authority();

                        if (a.getAuthorityForId(user.getId()) >= 3) {
                            if (e.eq(args[1], "부여", "empower", "add")) {
                                if (Integer.parseInt(args[2]) >= 3 && !user.getId().equals(owner.getId())) {
                                    eb.setTitle("권한을 부여할 수 없습니다.");
                                    eb.setDescription("`MANAGER` 권한 이상의 권한은 부여할 수 없습니다.");
                                    eb.setColor(Color.RED);

                                    tc.sendMessage(eb.build()).queue();
                                    return;
                                }

                                a.authorizationUser(Integer.parseInt(args[2]), args[3]);
                                eb.setTitle("권한을 부여했습니다.");

                                eb.addField("부여 대상", jda.retrieveUserById(args[3]).complete().getAsTag(), true);
                                eb.addBlankField(true);
                                eb.addField("부여한 권한", a.getAuthorityTitle(Integer.parseInt(args[2])), true);
                                eb.setColor(Color.GREEN);

                                tc.sendMessage(eb.build()).queue();
                            }
                        } else {
                            a.sendAuthErrorMessage(tc, user.getId());
                        }

                    } catch (ArrayIndexOutOfBoundsException ex) {
                        eb.setTitle("권한을 부여할 수 없었어요.");
                        eb.setDescription("권한을 부여하려면, `.권한 부여 [단계] [ID]` 형식으로 입력해 주세요.");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).queue();
                    }
                }

                if (e.eq(args[0], "한영변환", "gksdud", "en", "e2k")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    if (args.length == 1) {
                        eb.setTitle("무엇을 보낼 건가요?");
                        eb.setDescription("`.en dkssudgktpdy?`와 같은 형식으로 입력해 주세요.");
                        eb.setColor(Color.RED);
                        tc.sendMessage(eb.build()).queue();
                        return;
                    }

                    String result = new TextTool().qwertyToHangeul(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));

                    eb.setDescription(result);
                    eb.setFooter(user.getAsTag(), user.getAvatarUrl());

                    if (member != null) eb.setColor(member.getColor());

                    tc.sendMessage(eb.build()).queue();
                }

                if (e.eq(args[0], "eb", "임베드메시지", "임베드", "엄베드", "엄베드메시지", "깔끔")) {
                    if (args.length == 1) {
                        eb.setTitle("무엇을 보낼 건가요?");
                        eb.setDescription("`.en dkssudgktpdy?`와 같은 형식으로 입력해 주세요.");
                        eb.setColor(Color.RED);
                        tc.sendMessage(eb.build()).queue();
                        return;
                    }

                    String value = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    eb.setDescription(value);
                    eb.setFooter(user.getAsTag(), user.getAvatarUrl());

                    if (member != null) eb.setColor(member.getColor());

                    tc.sendMessage(eb.build()).queue();
                }

                if (e.eq(args[0], "vote", "투표")) {
                    Vote v = new Vote(tc, user);

                    v.startVote(Arrays.copyOfRange(args, 1, args.length));
                }

                if (e.eq(args[0], "Emote", "Emoji", "Emoticon", "EM", "이모티콘")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    try {

                        StealEmoji se = new StealEmoji(user.getId(), event.getJDA(), tc);

                        if (e.eq(args[1], "저장", "save", "추가", "add")) {

                            if (e.eq(args[2], "")) {
                                eb.setTitle("이름이.. 뭐라구요?");
                                eb.setDescription("저장할 이름을 안 적으신 것 같은데요.");
                                eb.setColor(Color.RED);

                                tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS)
                                        .flatMap(Message::delete).queue();

                                return;
                            }

                            try {

                                se.stealEmojiFromMessage(msg.getEmotes().get(0).getId(), args[2]);

                            } catch (IndexOutOfBoundsException ex) {
                                // ignored
                            }

                        }

                        if (e.eq(args[1], "삭제", "delete", "제거", "destroy")) {

                            se.deleteEmoji(args[2]);

                        }

                    } catch (ArrayIndexOutOfBoundsException ex) {

                        eb.setTitle("커스텀 이모티콘 설정에 실패했어요..");
                        eb.setDescription("저장하려면 `.이모티콘 저장 [이름] [이모티콘]`,\n" +
                                "삭제하려면 `.이모티콘 삭제 [이름]` 형식으로 써 주세요.");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS)
                                .flatMap(Message::delete).queue();

                    }
                }

                if (e.eq(args[0], "배송", "택배", "Delivery", "운송장", "배송", "운송장번호", "운송장조회", "배송조회")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    Delivery dv = new Delivery(tc, args[1], args[2]);

                    if (args.length != 3) {
                        eb.setTitle("그 운송장 조회를 못 할 것 같네요.");
                        eb.setDescription("정보 조회 컴퓨터가 먹통이 아니라면, 유저 님께서 잘못 입력한 걸 거에요. `.배송 [회사] [운송장번호]` 형식으로 해 보시겠어요?");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    }

                    dv.sendDeliveryProcess();

                }

                if (e.eq(args[0], "날씨", "weather", "웨더")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    Weather w = new Weather();

                    w.sendWeatherInformation(args[1], tc);
                }

                if (e.eq(args[0], "소스", "코드", "코드뷰어")) {
                    if (args.length == 1) {
                        return;
                    }

                    Authority a = new Authority();

                    if (a.getAuthorityForId(user.getId()) >= 3) {

                        try {

                            SendSource s = new SendSource(user);

                            if (args.length == 4) {
                                s.sendSource(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                            } else if (args.length == 3) {
                                s.sendSource(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[2]));
                            } else if (args.length == 2) {
                                tc.sendMessage(s.returnSource(args[1], 1, 1)).queue();
                            }

                        } catch (ArrayIndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }

                    }
                }

                if (e.eq(args[0], "소스T", "코드T", "코드뷰어T")) {
                    if (args.length == 1) {
                        return;
                    }

                    Authority a = new Authority();

                    if (a.getAuthorityForId(user.getId()) >= 3) {

                        try {

                            SendSource s = new SendSource(user);

                            if (args.length == 4) {
                                tc.sendMessage(s.returnSource(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]))).queue();
                            } else if (args.length == 3) {
                                tc.sendMessage(s.returnSource(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[2]))).queue();
                            } else if (args.length == 2) {
                                tc.sendMessage(s.returnSource(args[1], 1, 1)).queue();
                            }

                        } catch (ArrayIndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }

                    }
                }

                if (e.eq(args[0], "급식", "급식정보", "Meal")) {

                    if (args.length == 2) {

                        new MealServiceAPI().sendMealInfo(args[1], tc);

                    } else if (args.length == 3) {

                        new MealServiceAPI().sendMealInfo(args[1], tc, args[2]);

                    }

                }

                if (e.eq(args[0], "한강", "퐁당", "나락", "풍덩", "FALLGUYS")) {

                    String url = "http://hangang.dkserver.wo.tc/";
                    String json = new GetJSON().getJsonByUrl(url);

                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(json);
                    double temp = jsonObject.get("temp").getAsDouble();
                    String time = jsonObject.get("time").getAsString();

                    eb.setTitle("현재 한강 : " + String.format("%.1f", temp) + "˚C");
                    eb.addField("조회 시각", time, true);

                    TextTool t = new TextTool();

                    if (t.between(-40, Integer.parseInt(String.format("%.0f", temp)), -10)) {
                        eb.setColor(Color.BLUE);
                    } else if (t.between(-10, Integer.parseInt(String.format("%.0f", temp)), 10)) {
                        eb.setColor(Color.CYAN);
                    } else if (t.between(10, Integer.parseInt(String.format("%.0f", temp)), 15)) {
                        eb.setColor(Color.GREEN);
                    } else if (t.between(15, Integer.parseInt(String.format("%.0f", temp)), 25)) {
                        eb.setColor(Color.YELLOW);
                    } else if (t.between(25, Integer.parseInt(String.format("%.0f", temp)), 30)) {
                        eb.setColor(Color.ORANGE);
                    } else {
                        eb.setColor(Color.RED);
                    }

                    tc.sendMessage(eb.build()).queue();

                }

                if (e.eq(args[0], "인증", "verification", "verify")) {

                    if (new Authority().getAuthorityForId(user.getId()) >= 3) {

                        String guildId = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                        Guild targetGuild = jda.getGuildById(guildId);

                        switch (args[1]) {
                            case "부여":
                                new ServerManager(guild, member).verifyServer(Objects.requireNonNull(targetGuild));
                                Objects.requireNonNull(targetGuild.getOwner())
                                        .getUser()
                                        .openPrivateChannel()
                                        .complete()
                                        .sendMessage("`" + guild.getName() + "` 서버가 인증되었습니다!")
                                        .queue();
                                break;
                            case "해제":
                                new ServerManager(guild, member).verifyResetServer(Objects.requireNonNull(targetGuild));
                                Objects.requireNonNull(targetGuild.getOwner())
                                        .getUser()
                                        .openPrivateChannel()
                                        .complete()
                                        .sendMessage("`" + guild.getName() + "` 서버가 인증 해제되었습니다..")
                                        .queue();
                                break;
                        }

                    }

                }

            }

        } catch (StringIndexOutOfBoundsException | IllegalStateException e) {
            // ignore
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionReport(e, event.getAuthor(), event.getTextChannel());
        }

    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        User user = event.getAuthor();
        Message message = event.getMessage();
        EasyEqual e = new EasyEqual();

        String[] args = message.getContentRaw().split(" ");

        if (e.eq(args[0], "정지", "ban", "밴", "금지")) {

            Authority a = new Authority();

            if (a.getAuthorityForId(user.getId()) >= 3) {

                if (args.length == 1) {
                } else {
                    if (jda.retrieveUserById(args[1]).complete() != null) {
                        PrivateChannel targetDM = jda.retrieveUserById(args[1]).complete().openPrivateChannel().complete();

                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("귀하의 퀸튜플 계정이 정지되었습니다.");
                        eb.setDescription("귀하는 이제부터 퀸튜플에 대한 서비스를 일절 이용할 수 없습니다.");
                        eb.addField("사유", String.join(" ", Arrays.copyOfRange(args, 2, args.length)), false);
                        eb.setFooter("이의 제기 : playerdecuple@gmail.com");

                        targetDM.sendMessage(eb.build()).queue();
                    }
                }

            }
        }
    }

    public void sendPrivateMessage(User target, String message) {
        target.openPrivateChannel().complete().sendMessage(message).queue();
    }

    public void sendPrivateMessage(User target, MessageEmbed eb) {
        target.openPrivateChannel().complete().sendMessage(eb).queue();
    }

}
