package com.DecupleProject.Contents.RPG;

import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.WriteFile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Proficiency {

    private final User user;
    private final File BASE_FILE;

    private final WriteFile w = new WriteFile();
    private final ReadFile r = new ReadFile();

    public Proficiency(User user) {
        this.user = user;

        this.BASE_FILE = new File("D:/Database/Proficiency/");
        if (!BASE_FILE.exists()) {
            BASE_FILE.mkdir();
        }

        makeDatabase();
    }

    public boolean proficiencyDataExists() {
        File database = new File(BASE_FILE.getPath() + "/" + user.getId());
        return database.exists();
    }

    public void makeDatabase() {

        String id = user.getId();
        File database = new File(BASE_FILE.getPath() + "/" + id);
        String PATH = database.getPath();

        if (!database.exists()) {
            database.mkdir();
        } else {
            return;
        }

        File fishing = new File(PATH + "/100.txt");
        File farming = new File(PATH + "/101.txt");
        File woodCutting = new File(PATH + "/102.txt");
        File hunting = new File(PATH + "/103.txt");
        File producing = new File(PATH + "/104.txt");
        File selling = new File(PATH + "/200.txt");
        File teaching = new File(PATH + "/201.txt");
        File studying = new File(PATH + "/202.txt");
        File music = new File(PATH + "/203.txt");
        File experience = new File(PATH + "/204.txt");

        w.writeStringToFiles("0", fishing, farming, woodCutting, hunting, producing,
                selling, teaching, studying, music, experience);

    }

    public void addValue(int code, int value) {

        if (value == 0) {
            value = new Random().nextInt(3) + 1;
        }

        File targetFile = getFileForCode(code);

        if (!Objects.requireNonNull(targetFile).exists()) {
            return;
        }

        int proficiency = r.readInt(targetFile);

        if (proficiency >= 2100000000) return;

        w.writeInt(targetFile, proficiency + value);

    }

    public int getProficiencyValue(int code) {
        return r.readInt(getFileForCode(code));
    }

    public void sendProficiencyInformation(TextChannel tc) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("숙련도 : " + user.getAsTag());

        eb.addField("낚시", String.format("%,d", r.readInt(getFileForCode(100))), true);
        eb.addBlankField(true);
        eb.addField("판매", String.format("%,d", r.readInt(getFileForCode(200))), true);

        eb.addField("농사", String.format("%,d", r.readInt(getFileForCode(101))), true);
        eb.addBlankField(true);
        eb.addField("전수", String.format("%,d", r.readInt(getFileForCode(201))), true);

        eb.addField("벌목", String.format("%,d", r.readInt(getFileForCode(102))), true);
        eb.addBlankField(true);
        eb.addField("학습", String.format("%,d", r.readInt(getFileForCode(202))), true);

        eb.addField("사냥", String.format("%,d", r.readInt(getFileForCode(103))), true);
        eb.addBlankField(true);
        eb.addField("음악", String.format("%,d", r.readInt(getFileForCode(203))), true);

        eb.addField("제조", String.format("%,d", r.readInt(getFileForCode(104))), true);
        eb.addBlankField(true);
        eb.addField("경험", String.format("%,d", r.readInt(getFileForCode(204))), true);

        eb.setColor(Color.CYAN);
        eb.setFooter(user.getAsTag(), user.getAvatarUrl());
        tc.sendMessage(eb.build()).delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();
    }

    private File getFileForCode(int code) {
        File database = new File(BASE_FILE.getPath() + "/" + user.getId());
        String PATH = database.getPath();

        File fishing = new File(PATH + "/100.txt");
        File farming = new File(PATH + "/101.txt");
        File woodCutting = new File(PATH + "/102.txt");
        File hunting = new File(PATH + "/103.txt");
        File producing = new File(PATH + "/104.txt");
        File selling = new File(PATH + "/200.txt");
        File teaching = new File(PATH + "/201.txt");
        File studying = new File(PATH + "/202.txt");
        File music = new File(PATH + "/203.txt");
        File experience = new File(PATH + "/204.txt");

        switch (code) {
            case 100:
                return fishing;
            case 101:
                return farming;
            case 102:
                return woodCutting;
            case 103:
                return hunting;
            case 104:
                return producing;
            case 200:
                return selling;
            case 201:
                return teaching;
            case 202:
                return studying;
            case 203:
                return music;
            case 204:
                return experience;
            default:
                return null;
        }
    }

}
