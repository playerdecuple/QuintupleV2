package com.DecupleProject.Contents.MiniGame;

import com.DecupleProject.Core.DeleteFile;
import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.WriteFile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.File;

public class GameManager {

    public final File BASIC_PATH;
    private final ReadFile r = new ReadFile();
    private final WriteFile w = new WriteFile();
    private final TextChannel tc;

    public GameManager(TextChannel tc) {
        this.tc = tc;
        this.BASIC_PATH = new File("D:/Database/Game/" + tc.getId() + "/");

        if (!BASIC_PATH.exists()) {
            BASIC_PATH.mkdir();
            File NPI_FILE = new File(BASIC_PATH.getPath() + "/nowPlaying.txt");

            w.writeInt(NPI_FILE, 0);
        }
    }

    public File getBasicFile() {
        return BASIC_PATH;
    }

    public TextChannel getTextChannel() {
        return tc;
    }

    public boolean isGaming() {
        File NPI_FILE = new File(BASIC_PATH.getPath() + "/nowPlaying.txt");

        if (NPI_FILE.exists()) {
            int nowGameCode = r.readInt(NPI_FILE);
            return nowGameCode != 0;
        } else
            return false;
    }

    public int getGameCode() {
        File NPI_FILE = new File(BASIC_PATH.getPath() + "/nowPlaying.txt");
        return r.readInt(NPI_FILE);
    }

    public void setGameCode(int gameCode) {
        File NPI_FILE = new File(BASIC_PATH.getPath() + "/nowPlaying.txt");
        w.writeInt(NPI_FILE, gameCode);
    }

    public String getGameNameKOR(int gameCode) {

        switch (gameCode) {
            case 10:
                return "끝말잇기";
            case 11:
                return "끝말잇기 타임어택";
            case 12:
                return "끝말잇기 체인잇기";
            case 13:
                return "끝말잇기 두자잇기";
            case 14:
                return "끝말잇기 세자잇기";
            case 15:
                return "끝말잇기 넓혀잇기";
            case 20:
                return "타자대결";
            case 21:
                return "타자대결 랜덤문제";
            case 22:
                return "타자대결 영타문제";
            case 23:
                return "타자대결 한영변환";
            default:
                return null;
        }

    }

    public String getGameInfoKOR(int gameCode) {

        switch (gameCode) {
            case 10:
                return "여러분들이 아는 끝말잇기입니다! 앞 사람이 말한 단어의 맨 뒷 글자로 시작하는 단어를 적으세요!";
            case 11:
                return "총 1분 안에 몇 점을 얻을 수 있나요?";
            case 12:
                return "마지막으로 단어가 제출된 지 5초 안에 단어를 제출해 보세요. 체인을 몇 번 이을 수 있나요?";
            case 13:
                return "매우 많은 두 글자 단어들로만 끝말잇기를 해 보세요!";
            case 14:
                return "세 글자 단어가 아니라면 탈락할 것입니다!";
            case 15:
                return "앞 사람이 말한 단어의 글자 갯수보다 하나 더 많게 이으세요! (ex. 가시 > 시루떡 > 떡갈나무)";
            case 20:
                return "출제자가 낸 문제를 제일 먼저 입력하고 많은 점수를 얻으세요!";
            case 21:
                return "제가 직접 출제하도록 하죠! 긴 문장일수록 더욱더 많은 점수를 획득할 수 있습니다!";
            case 22:
                return "제가 직접 영어 문장을 출제하겠습니다! 긴 문장일수록 더욱더 많은 점수를 획득할 수 있습니다!";
            case 23:
                return "한영 변환 상태로 출제하고 답안을 제출하세요! (ex. 문제 : dkssudgktpdy | 정답 : 안녕하세요)";
            default:
                return null;
        }

    }

    public String getGameHelp(int gameCode) {
        switch (gameCode) {
            case 23:
            case 22:
            case 21:
            case 20:
                return "```md\n* 문장 출제(출제자인 경우) : [문장]\n" +
                        "* 답안 제출(출제자가 아닌 경우) : [답안]\n" +
                        "* 제시어 보기 : .제시어```";
            case 15:
            case 14:
            case 13:
            case 12:
            case 11:
            case 10:
                return "```md\n* 단어 제출 : [단어]\n" +
                        "* 현재 글자 보기 : .글자\n" +
                        "* 누구 차례인가요? : .차례\n" +
                        "* 남은 시간 보기 : .시간```";
            default:
                return "ERROR";
        }
    }

    // Method about join

    public void setModeToJoin(int gameCode) {
        setGameCode(gameCode * -1);
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("참가 단계");
        eb.setDescription(getGameInfoKOR(gameCode));
        eb.addField("선택한 게임", getGameNameKOR(gameCode), true);
        eb.addField("참가 명령어", "```md\n참가 : 게임에 참가합니다.\n취소 : 게임에 참가하지 않습니다.\nOK : 게임을 시작합니다.\nGG : 게임을 시작하지 않고 참가 단계를 끝냅니다.```", false);
        eb.setColor(Color.ORANGE);

        tc.sendMessage(eb.build()).queue();
    }

    public boolean join(User user) {
        if (getGameCode() < 0) {
            File USER_FILE = new File(BASIC_PATH.getPath() + "/" + user.getId() + ".txt");
            w.writeInt(USER_FILE, 0);
            return true;
        } else {
            return false;
        }
    }

    public boolean cancelJoin(User user) {
        if (getGameCode() < 0) {
            File USER_FILE = new File(BASIC_PATH.getPath() + "/" + user.getId() + ".txt");

            if (USER_FILE.exists()) {
                new DeleteFile().deleteFile(USER_FILE);
            } else {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }


    public void gameOver() {

        // TODO : add user's EXP value.

        DeleteFile d = new DeleteFile();
        d.deleteFile(BASIC_PATH);

    }

}
