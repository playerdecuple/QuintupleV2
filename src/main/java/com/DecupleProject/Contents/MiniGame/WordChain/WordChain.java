package com.DecupleProject.Contents.MiniGame.WordChain;

import com.DecupleProject.API.Dictionary;
import com.DecupleProject.Contents.MiniGame.GameManager;
import com.DecupleProject.Core.ExceptionReport;
import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.EasyEqual;
import com.DecupleProject.Core.WriteFile;
import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class WordChain {

    // Hangeul
    private static final char[] CHO =
            {0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
                    0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};
    private static final char[] JUN =
            {0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158,
                    0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162,
                    0x3163};
    // JDA
    private static User user;
    private static TextChannel tc;
    private final JDA jda = DefaultListener.jda;
    private final EmbedBuilder eb = new EmbedBuilder();
    // Utility
    private final EasyEqual e = new EasyEqual();
    private final WriteFile w = new WriteFile();
    private final ReadFile r = new ReadFile();
    private Timer t = new Timer();
    // API & Database manager
    private final Dictionary dictionary = new Dictionary();
    private final GameManager gm;
    private TurnHandlerCheckService tHandler = new TurnHandlerCheckService();

    private final char[] startChar =
            {'가', '나', '다', '라', '마', '바', '사', '아', '자', '차', '카', '타', '파', '하',
                    '고', '노', '도', '로', '모', '보', '소', '오', '조', '초', '코', '토', '포', '호',
                    '거', '너', '더', '러', '머', '버', '서', '어', '저', '처', '커', '터', '퍼', '허',
                    '구', '누', '두', '루', '무', '부', '수', '우', '주', '추', '쿠', '투', '푸', '후'};

    // Other
    private double time = 10000;

    public WordChain(User user, TextChannel tc) {
        WordChain.user = user;
        WordChain.tc = tc;
        this.gm = new GameManager(tc);

        tHandler.setTextChannel(tc);
        tHandler.setRemainingTime(time);
        tHandler.setTimer(t);
    }

    // Basic method

    public void gameStart() {

        final String gameDirectory = gm.getBasicFile().getPath();
        List<File> gameFiles = Arrays.asList(new File(gameDirectory).listFiles());
        List<File> realFiles = new ArrayList<>();

        StringBuilder turn = new StringBuilder();
        Collections.shuffle(gameFiles);

        for (int i = 0; i < gameFiles.size(); i++) {

            if (!e.eq(gameFiles.get(i).getName(), "nowPlaying.txt", "startTime.txt", "turn.txt",
                    "nowTurn.txt", "char.txt", "history.txt", "elapsedTime.txt")) {
                if (i != gameFiles.size() - 2) {
                    turn.append(gameFiles.get(i).getName().replace(".txt", "")).append(" ");
                } else {
                    turn.append(gameFiles.get(i).getName().replace(".txt", ""));
                }
                realFiles.add(gameFiles.get(i));
            }

        }

        w.writeString(gameDirectory + "/turn.txt", turn.toString());
        w.writeString(gameDirectory + "/nowTurn.txt", realFiles.get(0).getName().replace(".txt", ""));
        w.writeString(gameDirectory + "/char.txt", String.valueOf(startChar[new Random().nextInt(startChar.length)]));
        w.writeLong(gameDirectory + "/startTime.txt", System.currentTimeMillis());
        w.writeString(gameDirectory + "/history.txt", "?");
        w.writeLong(gameDirectory + "/elapsedTime.txt", 0L);

        turnHandler();

        eb.setTitle("끝말잇기 게임을 시작합시다!");

        eb.setDescription("처음 차례는 " + Objects.requireNonNull(jda.getUserById(getNowTurn())).getAsMention() + "님입니다!");
        eb.addField("시작 글자", getChar() + "", true);
        eb.addField("제한 시간", "10.0 초", true);

        eb.setColor(Color.ORANGE);
        tc.sendMessage(eb.build()).delay(60, TimeUnit.SECONDS).flatMap(Message::delete).queue();

        eb.clear();

    }

    // Method about char

    public String getChar() {
        final String gameDirectory = gm.getBasicFile().getPath();
        return Objects.requireNonNull(r.readString(gameDirectory + "/char.txt"));
    }

    public void setChar(char c) {
        final String gameDirectory = gm.getBasicFile().getPath();
        w.writeString(gameDirectory + "/char.txt", String.valueOf(c));
    }

    public void setChar(String s) {
        final String gameDirectory = gm.getBasicFile().getPath();
        w.writeString(gameDirectory + "/char.txt", s);
    }

    // Method about turn

    public boolean isNowTurn() {
        final String gameDirectory = gm.getBasicFile().getPath();

        try {
            return e.eq(Objects.requireNonNull(r.readString(gameDirectory + "/nowTurn.txt")), user.getId());
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void turnHandler() {

        final String gameDirectory = gm.getBasicFile().getPath();

        t.cancel();
        t.purge();
        tHandler.stop();

        tHandler.resetElapsedTime();

        this.t = new Timer();
        this.tHandler = new TurnHandlerCheckService();

        tHandler.setTextChannel(tc);
        tHandler.setRemainingTime(time);
        tHandler.setTurnStartedTimeFile(new File(gameDirectory + "/startTime.txt"));
        tHandler.setTurnStartedTime();
        tHandler.setTimer(t);

        t.scheduleAtFixedRate(tHandler, 0, 1);

    }

    public void removeTurnHandler() {
        t.cancel();
        t.purge();

        tHandler.cancel();
        tHandler.resetElapsedTime();
        tHandler.setContinue(false);
        tHandler = null;
    }

    public String getNowTurn() {
        final String gameDirectory = gm.getBasicFile().getPath();
        return r.readString(gameDirectory + "/nowTurn.txt");
    }

    public String getNextTurn() {
        final String gameDirectory = gm.getBasicFile().getPath();
        String turnInfo = r.readString(gameDirectory + "/turn.txt");

        final String[] turns = Objects.requireNonNull(turnInfo).split(" ");

        for (int i = 0; i < turns.length; i++) {
            if (e.eq(turns[i], getNowTurn()) && i != turns.length - 1) {
                return turns[i + 1];
            } else if (e.eq(turns[i], getNowTurn()) && i == turns.length - 1) {
                return turns[0];
            }
        }

        return null;
    }

    public void setTurn(String id) {
        final String gameDirectory = gm.getBasicFile().getPath();
        w.writeString(gameDirectory + "/nowTurn.txt", id);
    }

    // Other methods

    public boolean existsInHistory(String word) {

        try {
            final String gameDirectory = gm.getBasicFile().getPath();
            final File historyFile = new File(gameDirectory + "/history.txt");

            String historiesStr = r.readString(historyFile);
            String[] histories = Objects.requireNonNull(historiesStr).split(",");

            for (String history : histories) {
                if (e.eq(word, history)) {
                    return true;
                }
            }

            return false;
        } catch (NullPointerException e) {
            return false;
        }

    }

    public void submitWord(String word) {
        final String gameDirectory = gm.getBasicFile().getPath();

        try {

            if (isNowTurn()) {

                String result = dictionary.getSearchResultFromWord(word);
                String v = r.readString(gameDirectory + "/history.txt");

                if (existsInHistory(word)) {
                    tc.sendMessage(word + "(은)는 이미 쓰였던 단어입니다.").queue();
                    return;
                }

                char[] crs = getChar().toCharArray();

                if (crs.length == 1 && crs[0] != word.charAt(0)) {
                    return;
                } else if (crs[0] != word.charAt(0) && crs[1] != word.charAt(0)) {
                    return;
                }

                if (e.eq(result, "그런 단어는 없습니다.")) {

                    String tw = getWordByTwoPronunciationRules(word);
                    if (!e.eq(tw, word)) {
                        tc.sendMessage("그런 단어는 없네요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        return;
                    } else {
                        String secResult = dictionary.getSearchResultFromWord(tw);

                        if (e.eq(secResult, "그런 단어는 없습니다.")) {
                            tc.sendMessage("그런 단어는 없네요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }
                }

                tHandler.resetElapsedTime();
                w.writeLong(gameDirectory + "/startTime.txt", System.currentTimeMillis());
                eb.setTitle("좋습니다!");

                if (canTwoPronunciationRules(word.charAt(word.length() - 1))) {
                    String word1 = String.valueOf(word.charAt(word.length() - 1));
                    setChar(word1
                            + getWordByTwoPronunciationRules(word1));
                } else {
                    setChar(word.charAt(word.length() - 1));
                }

                setTurn(getNextTurn());

                eb.setDescription("다음 차례는 " + Objects.requireNonNull(jda.getUserById(getNowTurn())).getAsMention() + "님입니다!");

                if (canTwoPronunciationRules(getChar().charAt(0))) {
                    eb.addField("시작 글자", getChar().charAt(0) + "(" + getWordByTwoPronunciationRules(getChar().charAt(0) + ")"), true);
                } else {
                    eb.addField("시작 글자", getChar() + "", true);
                }

                turnHandler();

                eb.addField("제한 시간", time / 1000 + " 초", true);
                eb.addField("단어의 뜻", "```md\n# " + word + "\n" + result + "\n```", false);

                eb.setColor(Color.GREEN);
                tc.sendMessage(eb.build()).queue();

                eb.clear();

                if (v != null && e.eq(v, "?")) {
                    w.writeString(gameDirectory + "/history.txt", word);
                } else {
                    w.writeString(gameDirectory + "/history.txt", v + "," + word);
                }

            }

        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }
    }

    public String getWordByTwoPronunciationRules(String word) {

        int chI, juI, joI;
        char c = word.charAt(0);

        if (c >= 0xAC00 && c <= 0xD7A3) {

            joI = c - 0xAC00;

            chI = joI / (21 * 28);
            joI = joI % (21 * 28);
            juI = joI / 28;
            joI = joI % 28;

            char ch = CHO[chI];
            char ju = JUN[juI];

            boolean canApply = (ju == 'ㅑ' | ju == 'ㅕ' | ju == 'ㅛ' | ju == 'ㅠ' | ju == 'ㅒ' | ju == 'ㅖ' | ju == 'ㅣ');

            switch (ch) {
                case 'ㄹ':
                case 'ㄴ':
                    chI = (canApply) ? 11 : 2;
                    break;
            }

            int res = (chI * 28 * 21) + (juI * 28) + joI;
            char resChar = (char) ((char) res + 0xAC00);

            word = resChar + word.substring(1);

        }

        return word;

    }

    public boolean canTwoPronunciationRules(char word) {

        int chI, juI, joI;

        if (word >= 0xAC00 && word <= 0xD7A3) {

            joI = word - 0xAC00;

            chI = joI / (21 * 28);
            joI = joI % (21 * 28);
            juI = joI / 28;

            char ch = CHO[chI];
            char ju = JUN[juI];

            boolean canApply = (ju == 'ㅑ' | ju == 'ㅕ' | ju == 'ㅛ' | ju == 'ㅠ' | ju == 'ㅒ' | ju == 'ㅖ' | ju == 'ㅣ');

            switch (ch) {
                case 'ㄹ':
                    return true;
                case 'ㄴ':
                    return canApply;
            }

        }

        return false;

    }

    public void setTime(double to) {
        time = to;
    }

}

class TurnHandlerCheckService extends TimerTask {

    private TextChannel tc;
    private Timer t;
    private double remainingTime;
    private long elapsedTime;
    private long turnStartedTime;
    private boolean con = true;
    private File turnStartedTimeFile;

    public void run() {

        if (getContinue()) {
            setTurnStartedTime();
            elapsedTime = System.currentTimeMillis() - turnStartedTime;

            final GameManager gm = new GameManager(getTextChannel());

            if (gm.getGameCode() == 0) {
                this.cancel();
            } else {
                if (elapsedTime >= getRemainingTime()) {
                    gm.gameOver(1);
                    this.cancel();
                }
            }
        } else {
            stop();
        }

    }

    public TextChannel getTextChannel() {
        return tc;
    }

    public void setTextChannel(TextChannel tc) {
        this.tc = tc;
    }

    public void resetElapsedTime() {
        elapsedTime = 0;
    }


    public double getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(double remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setTurnStartedTime() {
        try {
            this.turnStartedTime = new ReadFile().readLong(turnStartedTimeFile);
        } catch (Exception e) {
            this.turnStartedTime = System.currentTimeMillis() - 20000;
        }
    }

    public void setTimer(Timer timer) {
        t = timer;
    }

    public boolean getContinue() {
        return con;
    }

    public void setContinue(boolean con) {
        this.con = con;
    }

    public void stop() {
        t.cancel();
        t.purge();
        this.cancel();

        super.cancel();
        t = null;
    }

    public void setTurnStartedTimeFile(File x) {
        this.turnStartedTimeFile = x;
    }

}
