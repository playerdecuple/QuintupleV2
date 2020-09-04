package com.DecupleProject.Core;

import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class Vote {

    private final TextChannel tc;
    private final User user;
    private static final EmbedBuilder eb = new EmbedBuilder();

    public Vote(TextChannel tc, User user) {
        this.tc = tc;
        this.user = user;
    }

    public Emote getEmojiCode(int type) {
        JDA jda = DefaultListener.jda;

        switch (type) {
            case 1: return jda.getEmoteById("746085443497623665");
            case 2: return jda.getEmoteById("746085443094970461");
            case 3: return jda.getEmoteById("746085443438641312");
            case 4: return jda.getEmoteById("746085443463807067");
            case 5: return jda.getEmoteById("746085443334045816");
            case 6: return jda.getEmoteById("746085443577315492");
            case 7: return jda.getEmoteById("746085443464069120");
            case 8: return jda.getEmoteById("746085443434578111");
            case 9: return jda.getEmoteById("746085443208085675");
            case 10: return jda.getEmoteById("746085443157884999");
            default:
                return null;
        }
    }

    public void startVote(String ... obj) {

        File guildVoteFile = new File("D:/Database/Vote/" + tc.getGuild().getId() + ".txt");

        if (guildVoteFile.exists()) {
            tc.sendMessage(":no_entry_sign: 투표가 진행 중입니다..! 어서 **다른 투표에 투표**해 주세요..!").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
            return;
        }

        if (obj.length > 10) {
            eb.setTitle("투표를 시작할 수 없었어요.");
            eb.setDescription("투표 목록 개수는 10개까지만 만들 수 있기 때문이죠.");
            eb.setColor(Color.RED);
            tc.sendMessage(eb.build()).queue();
            return;
        }

        eb.setTitle(user.getAsTag() + "님이 투표를 시작했습니다!");
        eb.setDescription("투표는 3분 뒤에 자동으로 종료됩니다! 반응으로 투표해 주세요!");
        eb.setColor(Color.CYAN);

        eb.clearFields();

        new WriteFile().writeString(guildVoteFile, "이이이잉 앗살라말라이쿰~");

        for (int i = 0; i < obj.length; i++) {
            eb.addField((i + 1) + "번 투표 시", obj[i], true);
        }

        Message msg = tc.sendMessage(eb.build()).complete();

        for (int i = 0; i < obj.length; i++) {
            msg.addReaction(getEmojiCode(i + 1)).queue();
        }

        int[] votes = new int[obj.length];

        msg.editMessage(eb.build()).delay(3, TimeUnit.MINUTES)
                .queue((it) -> {
                        for (int i = 0; i < obj.length; i++) {
                            votes[i] = msg.retrieveReactionUsers(getEmojiCode(i + 1)).complete().size() - 1;
                        }

                        EmbedBuilder eb2 = new EmbedBuilder();
                        eb2.setTitle("투표가 끝났습니다!");

                        for (int i = 0; i < votes.length; i++) {
                            eb2.addField((i + 1) + "(" + obj[i] + ")에 투표하신 분", votes[i] + "분", true);
                        }

                        eb2.setColor(Color.CYAN);
                        eb2.setThumbnail(getEmojiCode(getMaxValueOfArray(votes) + 1).getImageUrl());
                        tc.sendMessage(eb2.build()).queue();

                        msg.delete().queue();
                        new DeleteFile().deleteFile(guildVoteFile);
                    }
                );

    }

    public int getMaxValueOfArray(int[] value) {
        if (value == null || value.length == 0) return 0;

        int maxValue = value[0];

        for (int i : value) {

            if (maxValue < i) maxValue = i;

        }

        for (int j : value) {

            if (maxValue == j) return j;

        }

        return 0;
    }

}
