package com.DecupleProject.Core;

import net.dv8tion.jda.api.entities.User;

import java.io.File;

public class CustomCommand {

    private File CCFile = new File("D:/Database/CustomCommand/");

    ReadFile r = new ReadFile();
    WriteFile w = new WriteFile();

    private User user;

    public CustomCommand(User user) {
        this.user = user;

        if (!CCFile.exists()) CCFile.mkdir();
    }

    public boolean setPrefix(String prefix) {

        String userId = user.getId();
        File prefixDirectory = new File(CCFile.getPath() + "/prefix/");

        if (!prefixDirectory.exists()) prefixDirectory.mkdir();

        File prefixFile = new File(prefixDirectory.getPath() + "/" + userId + ".txt");
        w.writeString(prefixFile.getPath(), prefix);

        return true;
    }

    public String getPrefixStr() {
        String userId = user.getId();
        File prefixDirectory = new File(CCFile.getPath() + "/prefix/");

        if (!prefixDirectory.exists()) return "Q";

        File prefixFile = new File(prefixDirectory.getPath() + "/" + userId + ".txt");
        if (!prefixFile.exists()) return "Q";

        String prefix = r.readString(prefixFile.getPath());

        return prefix;
    }

    /* It isn't using.
    public char getPrefix() {
        String userId = user.getId();
        File prefixDirectory = new File(CCFile.getPath() + "/prefix/");

        if (!prefixDirectory.exists()) return 'Q';

        File prefixFile = new File(prefixDirectory.getPath() + "/" + userId + ".txt");
        if (!prefixFile.exists()) return 'Q';

        String prefix = r.readString(prefixFile.getPath());

        char[] chars = prefix.toCharArray();
        char res = chars[0];

        return res;
    }
     */
}
