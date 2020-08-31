package com.DecupleProject.Core.Music;

import net.dv8tion.jda.api.entities.Member;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

public class AudioInfo {

    private final AudioTrack track;
    private final Member author;
    private final TextChannel tc;

    AudioInfo(AudioTrack track, Member author, TextChannel tc) {
        this.track = track;
        this.author = author;
        this.tc = tc;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public Member getAuthor() {
        return author;
    }

    public TextChannel getTextChannel() {
        return tc;
    }

}
