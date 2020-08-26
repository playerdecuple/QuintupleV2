package com.DecupleProject.Core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.File;

public class Authority {

    private final ReadFile r = new ReadFile();
    private final WriteFile w = new WriteFile();
    private File authFile;

    public Authority(String id) {
        this.authFile = new File("D:/Database/Authority/" + id + ".txt");
    }

    public void authorizationUser(int authorityLevel, String id) {
        File authFile = new File("D:/Database/Authority/" + id + ".txt");
        w.writeInt(authFile, authorityLevel);
    }

    public int getAuthorityForId(String id) {
        File authFile = new File("D:/Database/Authority/" + id + ".txt");
        if (authFile.exists()) return r.readInt(authFile);
        return 0;
    }

    public String getAuthorityTitle(int authorityLevel) {
        switch (authorityLevel) {
            case 1: return "JUDGEMENT";
            case 2: return "POLICE";
            case 3: return "MANAGER";
            case 4: return "ADMINISTRATOR";
            default: return "NORMAL";
        }
    }

    public void sendAuthErrorMessage(TextChannel tc, String id) {
        EmbedBuilder e = new EmbedBuilder();

        e.setTitle("액세스 경고");
        e.setDescription("해당 기능에 액세스할 절대적 권한이 없습니다. \n" +
                "해당 기능에 액세스하기 위해서, 적절한 권한이 필요합니다.");
        e.addField("접근 최소 권한", "MANAGER", true);
        e.addField("현재 권한", getAuthorityTitle(getAuthorityForId(id)), true);
        e.setColor(Color.RED);

        tc.sendMessage(e.build()).queue();
    }

}
