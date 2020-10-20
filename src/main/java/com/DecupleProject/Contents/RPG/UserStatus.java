package com.DecupleProject.Contents.RPG;

import com.DecupleProject.Core.ExceptionReport;
import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.LogWriter;
import com.DecupleProject.Core.WriteFile;
import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.awt.*;
import java.io.File;
import java.util.Objects;

public class UserStatus {

    private final String id;
    private final TextChannel tc;
    private final JDA jda = DefaultListener.jda;

    final ReadFile r = new ReadFile();
    final WriteFile w = new WriteFile();

    public UserStatus(String id, TextChannel textChannel) {
        this.id = id;
        this.tc = textChannel;
    }

    public int getLevel() {
        File levelFile = new File("D:/Database/Level/" + id + ".txt");

        return r.readInt(levelFile);
    }

    public long getEXP() {
        File expFile = new File("D:/Database/EXP/" + id + ".txt");

        return r.readLong(expFile);
    }

    public void setEXP(String id, int expAmount, boolean showMessage, boolean showLvUpMessage) {

        EmbedBuilder eb = new EmbedBuilder();

        try {
            if (id.equalsIgnoreCase(jda.getSelfUser().getId())) return;

            File expFile = new File("D:/Database/EXP/" + id + ".txt");
            File levelFile = new File("D:/Database/Level/" + id + ".txt");
            File abilityFile = new File("D:/Database/Item/Accessories/Ability/" + id + ".txt");

            long exp = getEXP();
            int level = getLevel();

            if (abilityFile.exists()) {
                int abilityCode = r.readInt(abilityFile);

                switch (abilityCode) {
                    case 2:
                        expAmount += 100;
                        break;
                    case 12:
                        expAmount = expAmount + expAmount / 10;
                        break;
                    case 22:
                        expAmount = expAmount + expAmount / 4;
                        break;
                    case 31:
                        expAmount = expAmount * 2;
                        break;
                }
            }

            long finalEXP = exp + expAmount;
            int finalLevel = level;

            if (expFile.exists()) {
                int maxEXP = level * 10 + 5;

                if (finalEXP >= maxEXP) {
                    finalLevel = finalLevel + 1;
                    finalEXP = 0;
                }

                double afterEXPPercent = ((double) finalEXP / (double) maxEXP) * 100D;

                eb.setTitle("새로운 경험을 느꼈습니다!");
                eb.setDescription("<@" + id + "> :arrow_up: " + "Lv. " + finalLevel + " / EXP : " + String.format("%.2f", afterEXPPercent) + "%");
                eb.setColor(Color.GREEN);

                if (finalLevel == level + 1) {
                    if (showLvUpMessage) {
                        eb.setTitle("레벨 업!");
                        eb.setDescription("<@" + id + "> :up: " + "Lv. " + finalLevel + " / EXP : 0.00%");
                        eb.setColor(Color.YELLOW);
                    }
                } else {
                    showLvUpMessage = false;
                }

                w.writeLong(expFile, finalEXP);
                w.writeInt(levelFile, finalLevel);

                if ((showMessage || showLvUpMessage) && Objects.requireNonNull(tc.getGuild().getMember(jda.getSelfUser())).hasPermission(Permission.MESSAGE_WRITE)) {
                    try {
                        tc.sendMessage(eb.build()).queue();
                    } catch (InsufficientPermissionException ex) {
                        // ignore
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionReport(e);
        }
    }

}
