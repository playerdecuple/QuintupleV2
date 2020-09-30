package com.DecupleProject.Contents.RPG;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class CraftingTable {

    private final User user;
    private final TextChannel tc;

    public CraftingTable(User user, TextChannel tc) {
        this.user = user;
        this.tc = tc;

        // TODO : 아이템 조합과 분해 기능 생성
        // TODO : 조합법 생성
    }

}
