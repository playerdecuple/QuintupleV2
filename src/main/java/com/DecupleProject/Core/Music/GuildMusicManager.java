package com.DecupleProject.Core.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {

    public AudioPlayer pl;
    public TrackScheduler scheduler;
    public AudioPlayerSendHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager manager) {
        pl = manager.createPlayer();
        scheduler = new TrackScheduler(pl);
        sendHandler = new AudioPlayerSendHandler(pl);
        pl.addListener(scheduler);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(pl);
    }
}