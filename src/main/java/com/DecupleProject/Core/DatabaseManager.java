package com.DecupleProject.Core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {
    
    private final ReadFile r = new ReadFile();
    private final WriteFile w = new WriteFile();
    private final DeleteFile d = new DeleteFile();
    
    private final String id;
    private final TextChannel tc;
    private final JDA jda;

    String dFilePath = "D:/Database/";

    public DatabaseManager(String id, TextChannel tc, JDA jda) {
        
        this.id = id;
        this.tc = tc;
        this.jda = jda;
        
    }
    
    public String getDatabase(String databasePath) {
        File f = new File(dFilePath + databasePath.replace(".txt", "") + ".txt");

        return f.exists() ? r.readString(f) : null;
    }

    public void getDatabaseAndSendInfo(String databasePath) {
        File f = new File(dFilePath + databasePath.replace(".txt", "") + ".txt");
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Database Viewer");
        eb.addField("Loaded DB", databasePath.replace(".txt", ""), false);
        eb.addField("Now Data", "```" + (f.exists() ? r.readString(f) : null) + "```", false);
        eb.setColor(Color.GREEN);

        tc.sendMessage(eb.build()).queue();
    }
    
    public void setDatabase(String databasePath, String to) {
        File f = new File(dFilePath + databasePath.replace(".txt", "") + ".txt");

        if (f.exists()) w.writeString(f, String.valueOf(to));
    }
    
    public void editDatabase(String databasePath, String editTo, boolean delete) {
        
        File dbFile = new File(dFilePath + databasePath.replace(".txt", "") + ".txt");
        Authority a = new Authority();
        EmbedBuilder eb = new EmbedBuilder();
        
        int nowAuthority = a.getAuthorityForId(id);
        
        if (nowAuthority >= 3) {
            
            if (dbFile.exists()) {
                String before = getDatabase(databasePath);
                setDatabase(databasePath, editTo);

                if (delete) {

                    d.deleteFile(dbFile);
                    eb.setTitle("Database Delete Completed.");

                    eb.addField("Deleted DB", databasePath.replace(".txt", ""), false);
                    eb.setColor(Color.ORANGE);
                    tc.sendMessage(eb.build()).queue();

                    return;
                }
                
                eb.setTitle("Database Edit Completed.");
                eb.addField("Edited DB", databasePath.replace(".txt", ""), false);
                eb.addField("Before", "```" + before + "```", false);
                eb.addField("After", "```" + editTo + "```", false);
                eb.setColor(Color.GREEN);

                tc.sendMessage(eb.build()).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();
            } else {
                eb.setTitle("Database Edit Failed.");
                eb.addField("Edited DB", databasePath.replace(".txt", ""), false);
                eb.setDescription("Quintuple couldn't find that database.");

                tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
            }
            
        } else {
            
            a.sendAuthErrorMessage(tc, id);
            
        }
                
    }

    public void sendProfile(String targetId) {
        File usersLevelPointFile = new File(dFilePath + "Level/" + targetId + ".txt");
        File experiencePointFile = new File(dFilePath + "EXP/" + targetId + ".txt");
        File usrMoneyAccountFile = new File(dFilePath + "Money/" + targetId + ".txt");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("《 " + jda.retrieveUserById(targetId).complete().getAsTag() + " 》");
        eb.setColor(Color.CYAN);
        eb.setThumbnail(jda.retrieveUserById(targetId).complete().getAvatarUrl());

        double exp = (double) r.readInt(experiencePointFile.getPath()) / ((double) r.readInt(usersLevelPointFile.getPath()) * 10D + 5D);
        double expP = exp * 100D;

        eb.addField("레벨", "Lv. " + r.readInt(usersLevelPointFile.getPath()) + ", EXP : " + String.format("%.2f",
                expP) + "%", true);
        eb.addField("금액", String.format("%,d", r.readLong(usrMoneyAccountFile.getPath())) + "플", true);

        tc.sendMessage(eb.build()).queue();
    }

    public void createAllDatabaseFromId() {

        File attendanceCheckFile = new File(dFilePath + "AttendanceCheck/" + id + ".txt");
        File experiencePointFile = new File(dFilePath + "EXP/" + id + ".txt");
        File usersLevelPointFile = new File(dFilePath + "Level/" + id + ".txt");
        File investCountFirsFile = new File(dFilePath + "Invest/Count/1/" + id + ".txt");
        File investCountSecoFile = new File(dFilePath + "Invest/Count/2/" + id + ".txt");
        File investCountThirFile = new File(dFilePath + "Invest/Count/3/" + id + ".txt");
        File investCountFortFile = new File(dFilePath + "Invest/Count/4/" + id + ".txt");
        File investCountFiftFile = new File(dFilePath + "Invest/Count/5/" + id + ".txt");
        File usrMoneyAccountFile = new File(dFilePath + "Money/" + id + ".txt");
        File userCoolTimeSETFile = new File(dFilePath + "Time/" + id + "T.txt");

        if (!existsBasicFiles()) {
            w.writeString(attendanceCheckFile.getPath(), "20051105");
            w.writeInt(experiencePointFile.getPath(), 0);
            w.writeInt(usersLevelPointFile.getPath(), 1);
            w.writeInt(investCountFirsFile.getPath(), 0);
            w.writeInt(investCountSecoFile.getPath(), 0);
            w.writeInt(investCountThirFile.getPath(), 0);
            w.writeInt(investCountFortFile.getPath(), 0);
            w.writeInt(investCountFiftFile.getPath(), 0);
            w.writeInt(usrMoneyAccountFile.getPath(), 0);
            w.writeInt(userCoolTimeSETFile.getPath(), 0);

        }
    }

    public boolean existsBasicFiles() {

        File[] databaseFiles = getFilesFromPaths(dFilePath + "AttendanceCheck/" + id + ".txt",
                        dFilePath + "EXP/" + id + ".txt",
                        dFilePath + "Level/" + id + ".txt",
                        dFilePath + "Invest/Count/1/" + id + ".txt",
                        dFilePath + "Invest/Count/2/" + id + ".txt",
                        dFilePath + "Invest/Count/3/" + id + ".txt",
                        dFilePath + "Invest/Count/4/" + id + ".txt",
                        dFilePath + "Invest/Count/5/" + id + ".txt",
                        dFilePath + "Money/" + id + ".txt",
                        dFilePath + "Time/" + id + "T.txt");

        return ex(databaseFiles);

    }

    public File[] getFilesFromPaths(String... paths) {

        File[] files = new File[paths.length];

        for (int i = 0; i < paths.length; i++) {

            files[i] = new File(paths[i]);

        }

        return files;

    }

    public boolean ex(File[] fs) {

        for (File f : fs) {

            if (!f.exists()) return false;

        }

        return true;

    }

}
