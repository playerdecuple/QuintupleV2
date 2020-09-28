package com.DecupleProject.Listener;

import com.DecupleProject.Contents.MiniGame.GameManager;
import com.DecupleProject.Contents.MiniGame.WordChain.WordChain;
import com.DecupleProject.Core.CustomCommand;
import com.DecupleProject.Core.ExceptionReport;
import com.DecupleProject.Core.Util.EasyEqual;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class GameListener extends ListenerAdapter {

    private final EasyEqual e = new EasyEqual();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

        try {

            TextChannel tc = event.getChannel();
            User user = event.getAuthor();
            Message msg = event.getMessage();

            String m = msg.getContentRaw();

            final GameManager gm = new GameManager(tc);

            if (gm.isGaming()) {

                if (gm.getGameCode() < 0) {

                    if (e.eq(m, "참가")) {
                        boolean joined = gm.join(user);

                        if (joined) {
                            tc.sendMessage("게임에 " + user.getAsMention() + "님이 참가하였습니다.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        } else {
                            tc.sendMessage("게임에 참가하지 못했습니다.").delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        }
                    }

                    if (e.eq(m, "취소")) {
                        boolean canceled = gm.cancelJoin(user);

                        if (canceled) {
                            tc.sendMessage("게임에 " + user.getAsMention() + "님이 참가를 취소하였습니다.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        } else {
                            tc.sendMessage("게임의 참가를 취소하는 데에 실패했습니다.").delay(5, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        }
                    }

                    if (e.eq(m, "시작", "OK")) {
                        gm.setGameCode(gm.getGameCode() * -1);
                        int gameCode = gm.getGameCode();

                        switch (gameCode) {
                            case 11:
                            case 10:
                                WordChain wc = new WordChain(user, tc);
                                wc.gameStart();
                        }
                    }

                    if (e.eq(m, "GG")) {
                        gm.gameOver();
                        tc.sendMessage("게임 참가 모드를 종료했습니다.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    }

                } else {

                    if (!e.eq(m, "GG")) {
                        switch (gm.getGameCode()) {
                            case 11:
                            case 10:
                                WordChain wc = new WordChain(user, tc);
                                wc.submitWord(m);
                        }
                    }

                    if (e.eq(m, "GG")) {
                        gm.gameOver();
                        tc.sendMessage("게임을 종료할게요!").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    }

                }

            } else {

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

                if (prefixCheck) {

                    if (e.eq(args[0], "게임")) {

                        if (e.eq(args[1], "끝말잇기", "워드체인", "wordChain")) {
                            gm.setModeToJoin(10);
                        }

                    }

                }

            }

        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }

    }

}
