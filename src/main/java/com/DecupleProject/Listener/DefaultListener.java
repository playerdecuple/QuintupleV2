package com.DecupleProject.Listener;

import com.DecupleProject.API.*;
import com.DecupleProject.Calculate.QuadraticEquation;
import com.DecupleProject.Calculate.SmallFactorization;
import com.DecupleProject.Calculate.Square;
import com.DecupleProject.Contents.AttendanceCheck;
import com.DecupleProject.Contents.RPG.Account;
import com.DecupleProject.Contents.RPG.UserStatus;
import com.DecupleProject.Core.*;
import com.DecupleProject.Core.Util.EasyEqual;
import com.DecupleProject.Core.Util.LogWriter;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultListener extends ListenerAdapter {

    public static JDA jda;
    private User owner;

    LogWriter lW;

    @Override
    public void onReady(ReadyEvent event) {

        EmbedBuilder eb = new EmbedBuilder();

        jda = event.getJDA();
        this.lW = new LogWriter(jda);
        this.owner = jda.retrieveUserById("419116887469981708").complete();

        try {
            Twirk twirk = new TwirkBuilder("#playerdecuple", "oauth:rhhsunhtgg4hlyr1cfkfkl939u6wfk", "oauth:rhhsunhtgg4hlyr1cfkfkl939u6wfk").build();
            twirk.addIrcListener(new TwitchListener());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ㅡㅡㅡㅡㅡㅡ Sending 'NOW STATUS' ㅡㅡㅡㅡㅡㅡ //

        int sleepSec = 600; // Sending log cooldown.
        final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

        exec.scheduleAtFixedRate(() -> {
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

            if (msg.getContentRaw().charAt(0) == ':') {
                String name = msg.getContentRaw().replace(":", "");
                StealEmoji se = new StealEmoji(user.getId(), jda, tc);
                String code = ":" + name + ":";

                if (se.hasEmoji(name) && msg.getContentRaw().contains(code)) {
                    se.sendStealEmoji(name);
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                }
            }

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            if (msg.getContentRaw().charAt(0) == 'Q' | msg.getContentRaw().charAt(0) == 'q' | msg.getContentRaw().charAt(0) == '.' | prefixCheck) {

                if (args.length <= 0) return;

                if (e.eq(args[0], "도움말") | e.eq(args[0], "도움") | e.eq(args[0], "help")) {

                    if (user.isBot()) return;
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    eb.setTitle("도움말을 보여드릴게요!");
                    eb.setDescription("아쉽지만, 명령어가 너무 많아서 정리를 해놓은 인터넷 사이트로 이동시켜 드리겠습니다!");
                    eb.addField("링크", "https://decupleproject.fandom.com/ko/wiki/%EB%8D%B0%ED%81%90%ED%94%8C%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8:%EB%94%94%EC%8A%A4%EC%BD%94%EB%93%9C%ED%80%B8%ED%8A%9C%ED%94%8C", true);
                    eb.setColor(Color.CYAN);

                    sendPrivateMessage(user, eb.build());

                }

                if (e.eq(args[0], "핑") | e.eq(args[0], "ping")) {

                    if (user.isBot()) return;
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
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
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    sendPrivateMessage(user, "이 링크를 이용하시면, 데큐플 공식 서버로 가입하실 수 있어요! <http://decuple-d.o-r.kr/>");

                }

                if (e.eq(args[0], "시간") | e.eq(args[0], "시각") | e.eq(args[0], "time") | e.eq(args[0], "t")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
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

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
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

                    com.sun.management.OperatingSystemMXBean ops = ManagementFactory.newPlatformMXBeanProxy(
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
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
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
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    try {
                        String body = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                        new Translator(body, tc, args[1], args[2]);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        eb.setTitle("번역을 하는 데 오류가 발생했어요..");
                        eb.setDescription("`.번역 한국어 영어 안녕하세요?` 형식으로 입력해 보세요.");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).queue();
                    }
                }

                if (e.eq(args[0], "출석", "출석체크", "출첵", "Attendance", "AttendanceCheck", "나님등장")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    AttendanceCheck at = new AttendanceCheck(user.getId(), tc, user.getName());
                    at.attendance();
                }

                if (e.eq(args[0], "백과사전", "네이버백과", "백과")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
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
                            eb.addField(":moneybag: 자금(플)", String.format("%,d", a.getNowMoneyForId()) + "플", true);
                            eb.addField(":bulb: 경험치", "Lv. " + us.getLevel() + " / " + String.format("%.2f", ((double) us.getEXP() / ((double) us.getLevel() * 10D + 5D)) * 100D) + "%", true);

                            StringBuilder roles = new StringBuilder("@everyone");

                            if (member == null) return;

                            for (int i = 0; i < member.getRoles().size(); i++) {
                                roles.append(", @").append(member.getRoles().get(i).getName());
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
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
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
                }

                if (e.eq(args[0], "멜론차트", "인기차트")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
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
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    Shopping sh = new Shopping(user.getId(), String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                    sh.sendShopMessage(tc);
                }

                if (e.eq(args[0], "DB", "데이터베이스", "database")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    if (e.eq(args[1], "del")) {
                        db.editDatabase(args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)), true);
                    }

                    db.editDatabase(args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)), false);
                }

                if (e.eq(args[0], "사전", "국어사전", "국어", "뜻")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();

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
                    eb.addField("뜻", "```" + means + "```", false);
                    eb.setColor(Color.GREEN);

                    tc.sendMessage(eb.build()).queue();

                }

                if (e.eq(args[0], "auth", "권한", "authorization")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
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
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    if (args.length == 1) {
                        eb.setTitle("무엇을 보낼 건가요?");
                        eb.setDescription("`.en dkssudgktpdy?`와 같은 형식으로 입력해 주세요.");
                        eb.setColor(Color.RED);
                        tc.sendMessage(eb.build()).queue();
                        return;
                    }

                    msg.delete().queue();
                    ScriptEngineManager mgr = new ScriptEngineManager();

                    String value = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                    ScriptEngine engine = mgr.getEngineByName("JavaScript");
                    String vol = String.valueOf(engine.eval("var e2k = (function() {\n" +
                            "var en_h = \"rRseEfaqQtTdwWczxvg\";\n" +
                            "var reg_h = \"[\" + en_h + \"]\";\n" +
                            "\n" +
                            "var en_b = {k:0,o:1,i:2,O:3,j:4,p:5,u:6,P:7,h:8,hk:9,ho:10,hl:11,y:12,n:13,nj:14,np:15,nl:16,b:17,m:18,ml:19,l:20};\n" +
                            "var reg_b = \"hk|ho|hl|nj|np|nl|ml|k|o|i|O|j|p|u|P|h|y|n|b|m|l\";\n" +
                            "\n" +
                            "var en_f = {\"\":0,r:1,R:2,rt:3,s:4,sw:5,sg:6,e:7,f:8,fr:9,fa:10,fq:11,ft:12,fx:13,fv:14,fg:15,a:16,q:17,qt:18,t:19,T:20,d:21,w:22,c:23,z:24,x:25,v:26,g:27};\n" +
                            "var reg_f = \"rt|sw|sg|fr|fa|fq|ft|fx|fv|fg|qt|r|R|s|e|f|a|q|t|T|d|w|c|z|x|v|g|\";\n" +
                            "\n" +
                            "var reg_exp = new RegExp(\"(\"+reg_h+\")(\"+reg_b+\")((\"+reg_f+\")(?=(\"+reg_h+\")(\"+reg_b+\"))|(\"+reg_f+\"))\",\"g\");\n" +
                            "\n" +
                            "var replace = function(str,h,b,f) {\n" +
                            "return String.fromCharCode(en_h.indexOf(h) * 588 + en_b[b] * 28 + en_f[f] + 44032);\n" +
                            "};\n" +
                            "\n" +
                            "return (function(str) {\n" +
                            "return str.replace(reg_exp,replace);\n" +
                            "});\n" +
                            "})();" +
                            "" +
                            "e2k(\"" + value + "\")"));

                    eb.setDescription(vol);
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

                    msg.delete().queue();

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
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
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

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    Delivery dv = new Delivery(tc, args[1], args[2]);

                    if (args.length != 3) {
                        eb.setTitle("그 운송장 조회를 못 할 것 같네요.");
                        eb.setDescription("정보 조회 컴퓨터가 먹통이 아니라면, 유저 님께서 잘못 입력한 걸 거에요. `.배송 [회사] [운송장번호]` 형식으로 해 보시겠어요?");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    }

                    dv.sendDeliveryProcess();

                }

                if (e.eq(args[0], "UT")) {
                    tc.sendMessage("OK").queue();
                }

                if (e.eq(args[0], "날씨", "weather", "웨더")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE)) tc.deleteMessageById(msg.getId()).queue();
                    Weather w = new Weather();

                    w.sendWeatherInformation(args[1], tc);
                }

                if (e.eq(args[0], "reload", "restart", "apply")) {
                    Authority a = new Authority();

                    if (a.getAuthorityForId(user.getId()) == 4) {
                        sendPrivateMessage(owner, "\"**Bot will restart myself!** __Please don't turn off computer.__\"");
                        restartQuintuple();
                    }
                }

                if (e.eq(args[0], "end", "shutdown", "exit")) {

                    Authority a = new Authority();

                    if (a.getAuthorityForId(user.getId()) == 4) {
                        sendPrivateMessage(owner, "**Bot is shutting down!**");
                        shutdownQuintuple();
                    }

                }

            }

        } catch (StringIndexOutOfBoundsException e) {
            // ignore
        } catch (Exception e) {
            lW.sendMessage("Exception occurred! \n```" + e.getMessage() + "```");
            e.printStackTrace();
        }

    }

    public void sendPrivateMessage(User target, String message) {
        target.openPrivateChannel().complete().sendMessage(message).queue();
    }

    public void sendPrivateMessage(User target, MessageEmbed eb) {
        target.openPrivateChannel().complete().sendMessage(eb).queue();
    }

    public void restartQuintuple() throws IOException {
        Runtime.getRuntime().exec("\"C:\\Program Files\\Java\\jdk1.8.0_241\\bin\\java.exe\" \"-javaagent:C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2019.3.4\\lib\\idea_rt.jar=54509:C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2019.3.4\\bin\" -Dfile.encoding=UTF-8 -classpath \"C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\charsets.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\deploy.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\access-bridge-64.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\cldrdata.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\dnsns.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\jaccess.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\jfxrt.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\localedata.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\nashorn.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\sunec.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\sunjce_provider.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\sunmscapi.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\sunpkcs11.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\ext\\zipfs.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\javaws.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\jce.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\jfr.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\jfxswt.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\jsse.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\management-agent.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\plugin.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\resources.jar;C:\\Program Files\\Java\\jdk1.8.0_241\\jre\\lib\\rt.jar;D:\\QuintupleV2\\target\\classes;C:\\Users\\elred\\.m2\\repository\\net\\dv8tion\\JDA\\4.2.0_199\\JDA-4.2.0_199.jar;C:\\Users\\elred\\.m2\\repository\\com\\google\\code\\findbugs\\jsr305\\3.0.2\\jsr305-3.0.2.jar;C:\\Users\\elred\\.m2\\repository\\org\\jetbrains\\annotations\\16.0.1\\annotations-16.0.1.jar;C:\\Users\\elred\\.m2\\repository\\org\\slf4j\\slf4j-api\\1.7.25\\slf4j-api-1.7.25.jar;C:\\Users\\elred\\.m2\\repository\\com\\neovisionaries\\nv-websocket-client\\2.10\\nv-websocket-client-2.10.jar;C:\\Users\\elred\\.m2\\repository\\com\\squareup\\okhttp3\\okhttp\\3.13.0\\okhttp-3.13.0.jar;C:\\Users\\elred\\.m2\\repository\\club\\minnced\\opus-java-api\\1.0.4\\opus-java-api-1.0.4.jar;C:\\Users\\elred\\.m2\\repository\\net\\java\\dev\\jna\\jna\\4.4.0\\jna-4.4.0.jar;C:\\Users\\elred\\.m2\\repository\\club\\minnced\\opus-java-natives\\1.0.4\\opus-java-natives-1.0.4.jar;C:\\Users\\elred\\.m2\\repository\\org\\apache\\commons\\commons-collections4\\4.1\\commons-collections4-4.1.jar;C:\\Users\\elred\\.m2\\repository\\net\\sf\\trove4j\\trove4j\\3.0.3\\trove4j-3.0.3.jar;C:\\Users\\elred\\.m2\\repository\\com\\fasterxml\\jackson\\core\\jackson-databind\\2.10.1\\jackson-databind-2.10.1.jar;C:\\Users\\elred\\.m2\\repository\\com\\fasterxml\\jackson\\core\\jackson-annotations\\2.10.1\\jackson-annotations-2.10.1.jar;C:\\Users\\elred\\.m2\\repository\\com\\sedmelluq\\lavaplayer\\1.3.50\\lavaplayer-1.3.50.jar;C:\\Users\\elred\\.m2\\repository\\com\\sedmelluq\\lava-common\\1.1.0\\lava-common-1.1.0.jar;C:\\Users\\elred\\.m2\\repository\\com\\sedmelluq\\lavaplayer-natives\\1.3.13\\lavaplayer-natives-1.3.13.jar;C:\\Users\\elred\\.m2\\repository\\org\\apache\\httpcomponents\\httpclient\\4.5.10\\httpclient-4.5.10.jar;C:\\Users\\elred\\.m2\\repository\\org\\apache\\httpcomponents\\httpcore\\4.4.12\\httpcore-4.4.12.jar;C:\\Users\\elred\\.m2\\repository\\commons-logging\\commons-logging\\1.2\\commons-logging-1.2.jar;C:\\Users\\elred\\.m2\\repository\\commons-codec\\commons-codec\\1.11\\commons-codec-1.11.jar;C:\\Users\\elred\\.m2\\repository\\commons-io\\commons-io\\2.6\\commons-io-2.6.jar;C:\\Users\\elred\\.m2\\repository\\com\\fasterxml\\jackson\\core\\jackson-core\\2.10.0\\jackson-core-2.10.0.jar;C:\\Users\\elred\\.m2\\repository\\net\\iharder\\base64\\2.3.9\\base64-2.3.9.jar;C:\\Users\\elred\\.m2\\repository\\com\\google\\apis\\google-api-services-youtube\\v3-rev222-1.25.0\\google-api-services-youtube-v3-rev222-1.25.0.jar;C:\\Users\\elred\\.m2\\repository\\com\\google\\api-client\\google-api-client\\1.25.0\\google-api-client-1.25.0.jar;C:\\Users\\elred\\.m2\\repository\\com\\google\\oauth-client\\google-oauth-client\\1.25.0\\google-oauth-client-1.25.0.jar;C:\\Users\\elred\\.m2\\repository\\com\\google\\http-client\\google-http-client\\1.25.0\\google-http-client-1.25.0.jar;C:\\Users\\elred\\.m2\\repository\\com\\google\\j2objc\\j2objc-annotations\\1.1\\j2objc-annotations-1.1.jar;C:\\Users\\elred\\.m2\\repository\\com\\google\\http-client\\google-http-client-jackson2\\1.25.0\\google-http-client-jackson2-1.25.0.jar;C:\\Users\\elred\\.m2\\repository\\com\\google\\guava\\guava\\20.0\\guava-20.0.jar;C:\\Users\\elred\\.m2\\repository\\com\\github\\taycaldwell\\riot-api-java\\4.3.0\\riot-api-java-4.3.0.jar;C:\\Users\\elred\\.m2\\repository\\com\\google\\code\\gson\\gson\\2.5\\gson-2.5.jar;C:\\Users\\elred\\.m2\\repository\\org\\json\\json\\20190722\\json-20190722.jar;C:\\Users\\elred\\.m2\\repository\\com\\github\\Bumbleboss\\osu_api\\1.1\\osu_api-1.1.jar;C:\\Users\\elred\\.m2\\repository\\com\\squareup\\okio\\okio\\1.14.1\\okio-1.14.1.jar;C:\\Users\\elred\\.m2\\repository\\com\\github\\mautini\\pubg-java\\1.0-SNAPSHOT\\pubg-java-1.0-20181229.163403-14.jar;C:\\Users\\elred\\.m2\\repository\\com\\squareup\\retrofit2\\retrofit\\2.5.0\\retrofit-2.5.0.jar;C:\\Users\\elred\\.m2\\repository\\com\\squareup\\retrofit2\\converter-gson\\2.5.0\\converter-gson-2.5.0.jar;C:\\Users\\elred\\.m2\\repository\\com\\typesafe\\config\\1.3.3\\config-1.3.3.jar;C:\\Users\\elred\\.m2\\repository\\com\\github\\mautini\\pubg-java-utils\\1.0-SNAPSHOT\\pubg-java-utils-1.0-20181229.163408-14.jar;C:\\Users\\elred\\.m2\\repository\\com\\github\\Gikkman\\Java-Twirk\\0.6.2\\Java-Twirk-0.6.2.jar;C:\\Users\\elred\\.m2\\repository\\org\\slf4j\\slf4j-simple\\1.6.1\\slf4j-simple-1.6.1.jar;C:\\Users\\elred\\.m2\\repository\\org\\jsoup\\jsoup\\1.10.3\\jsoup-1.10.3.jar;C:\\Users\\elred\\.m2\\repository\\org\\python\\jython-standalone\\2.7.2\\jython-standalone-2.7.2.jar\" com.DecupleProject.QuintupleMain");
        System.exit(-1);
    }

    public void shutdownQuintuple() {
        System.exit(-1);
    }

}
