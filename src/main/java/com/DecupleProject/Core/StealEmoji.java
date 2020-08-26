package com.DecupleProject.Core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class StealEmoji {

    private String id;
    private JDA jda;
    private TextChannel tc;

    private final String emojiDirectoryPath = "D:/Database/Emoji";
    private final WriteFile w = new WriteFile();
    private final ReadFile r = new ReadFile();

    public StealEmoji(String id, JDA jda, TextChannel tc) {

        this.id = id; // get User Id
        this.jda = jda; // set jda to cached jda
        this.tc = tc;

    }

    public void stealEmojiFromMessage(String emojiId, String emojiName) {

        try {

            File userDir = new File(emojiDirectoryPath + "/" + id + "/");

            EmbedBuilder eb = new EmbedBuilder();

            if (userDir.exists()) {

                File emojiFile = new File(userDir.getPath() + "/" + emojiName + ".txt");
                if (emojiFile.exists()) {
                    eb.setTitle("그 이름은 이미 있어요.");
                    eb.setColor(Color.RED);

                    eb.setThumbnail(jda.getEmoteById(emojiId).getImageUrl());
                    tc.sendMessage(eb.build()).queue();
                    return;
                }

                eb.setTitle(":" + emojiName + ": 이모티콘을 저장했습니다!");
                eb.setColor(Color.green);

                eb.setThumbnail(jda.getEmoteById(emojiId).getImageUrl());
                tc.sendMessage(eb.build()).queue();

                w.writeString(emojiFile.getPath(), emojiId);

            } else {

                userDir.mkdir();
                stealEmojiFromMessage(emojiId, emojiName); // 재귀

            }

        } catch (NullPointerException ex) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("이모티콘이 없는데요..?");
            eb.setDescription("아무리 니트로 이용 유저라고 하시더라도, 해당 길드에서 저장을 진행해 주셔야 해요..");
            eb.setColor(Color.RED);

            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        }

    }

    public void sendStealEmoji(String emojiName) {
        File userDir = new File(emojiDirectoryPath + "/" + id + "/");

        EmbedBuilder eb = new EmbedBuilder();

        if (userDir.exists()) {

            File emojiFile = new File(userDir.getPath() + "/" + emojiName + ".txt");
            if (emojiFile.exists()) {
                eb.setFooter(jda.retrieveUserById(id).complete().getAsTag(), jda.retrieveUserById(id).complete().getAvatarUrl());
                eb.setImage(jda.getEmoteById(r.readString(emojiFile.getPath())).getImageUrl());
                tc.sendMessage(eb.build()).queue();
                return;
            }

        } else {

            userDir.mkdir();

        }
    }

    public boolean hasEmoji(String emojiName) {
        File userDir = new File(emojiDirectoryPath + "/" + id + "/");
        File emojiFile = new File(userDir.getPath() + "/" + emojiName + ".txt");
        return emojiFile.exists();
    }

    public void deleteEmoji(String emojiName) {
        File userDir = new File(emojiDirectoryPath + "/" + id + "/");

        EmbedBuilder eb = new EmbedBuilder();

        if (userDir.exists()) {

            File emojiFile = new File(userDir.getPath() + "/" + emojiName + ".txt");
            if (emojiFile.exists()) {
                eb.setDescription(":" + emojiName + ": 이모티콘을 삭제했습니다.");
                tc.sendMessage(eb.build()).queue();

                emojiFile.delete();
                return;
            }

            eb.setTitle(":" + emojiName + ": 이모티콘이 없습니다..");
            eb.setColor(Color.red);

            tc.sendMessage(eb.build()).queue();

        } else {

            userDir.mkdir();

        }
    }

}
