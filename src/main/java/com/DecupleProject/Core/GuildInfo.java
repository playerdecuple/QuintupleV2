package com.DecupleProject.Core;

import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.util.Objects;

public class GuildInfo {

    private final Guild guild;
    private final JDA jda = DefaultListener.jda;

    private final WriteFile w = new WriteFile();
    private final ReadFile r = new ReadFile();

    private final File BASE_DIRECTORY;

    public GuildInfo(Guild guild) {
        this.guild = guild;
        this.BASE_DIRECTORY = new File("D:/Database/Servers/" + guild.getId());
        createGuildDirectory();
    }

    public void createGuildDirectory() {
        if (!BASE_DIRECTORY.exists()) {
            if (!BASE_DIRECTORY.mkdir()) {
                System.out.println("Bot couldn't create directory : " + BASE_DIRECTORY.exists());
            }
        }
    }

    public void setMusicChannel(String id) {
        w.writeString(BASE_DIRECTORY.getPath() + "/MusicChannel.txt", id);
    }

    public TextChannel getMusicChannel() {

        String musicChannelId = null;
        File n = new File(BASE_DIRECTORY.getPath() + "/MusicChannel.txt");

        if (n.exists())
            musicChannelId = r.readString(n);

        if (musicChannelId == null) {
            return null;
        }

        if (musicChannelId.equals("0")) {
            return null;
        }

        return jda.getTextChannelById(Objects.requireNonNull(musicChannelId));
    }

    public Guild getGuild() {
        return guild;
    }

}
