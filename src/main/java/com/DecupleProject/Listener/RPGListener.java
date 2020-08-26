package com.DecupleProject.Listener;

import com.DecupleProject.Contents.RPG.Account;
import com.DecupleProject.Core.CustomCommand;
import com.DecupleProject.Core.Util.EasyEqual;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class RPGListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        try {

            // TODO : If playing game, return.

            EmbedBuilder eb = new EmbedBuilder();
            EasyEqual e = new EasyEqual();

            User user = event.getAuthor();
            Guild guild = event.getGuild();
            TextChannel tc = event.getChannel();
            Member member = event.getMember();
            Message msg = event.getMessage();

            AudioManager am = guild.getAudioManager();

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

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

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            if (msg.getContentRaw().charAt(0) == 'Q' | msg.getContentRaw().charAt(0) == 'q' | msg.getContentRaw().charAt(0) == '.' | prefixCheck) {

                if (args.length < 1) return;

                Account ac = new Account(user.getId(), user.getName(), tc);

                if (e.eq(args[0], "아르바이트", "알바", "돈")) {

                    ac.giveMoney(user.getId(), 0, true, true);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
