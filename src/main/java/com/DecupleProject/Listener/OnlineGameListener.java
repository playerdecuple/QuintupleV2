package com.DecupleProject.Listener;

import com.DecupleProject.API.Game.LeagueOfLegends;
import com.DecupleProject.API.Game.Osu;
import com.DecupleProject.Contents.RPG.UserStatus;
import com.DecupleProject.Core.CustomCommand;
import com.DecupleProject.Core.DatabaseManager;
import com.DecupleProject.Core.StealEmoji;
import com.DecupleProject.Core.Util.EasyEqual;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

public class OnlineGameListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {

            User user = event.getAuthor();
            Guild guild = event.getGuild();
            Message msg = event.getMessage();
            TextChannel tc = event.getTextChannel();
            JDA jda = event.getJDA();

            EasyEqual e = new EasyEqual();

            DatabaseManager db = new DatabaseManager(user.getId(), tc, jda);
            if (!db.nowExistsAllDatabase()) db.createAllDatabaseFromId();

            UserStatus us = new UserStatus(user.getId(), tc);
            us.setEXP(user.getId(), 1, false, true);

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

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            if (msg.getContentRaw().charAt(0) == 'Q' | msg.getContentRaw().charAt(0) == 'q' | msg.getContentRaw().charAt(0) == '.' | prefixCheck) {

                if (args.length <= 0) return;

                if (e.eq(args[0], "LL", "LOL", "롤", "LeagueOfLegends", "리그오브레전드")) {


                    if (e.eq(args[1], "np")) {
                        String inputted = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                        LeagueOfLegends lol = new LeagueOfLegends(inputted);

                        lol.sendNowPlayInfo(tc, inputted);
                    } else {
                        String inputted = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                        LeagueOfLegends lol = new LeagueOfLegends(inputted);

                        lol.sendInfo(tc);
                    }

                }

                if (e.eq(args[0], "Osu", "오스", "Osu!", "오스!")) {

                    String n = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    new Osu(n, tc);

                }



            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
