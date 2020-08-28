package com.DecupleProject.Core.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.*;

public class TrackScheduler extends AudioEventAdapter {
    private boolean repeating = false;

    public final AudioPlayer pl;
    public final Queue<AudioTrack> queue;
    // private Map<String, GuildMusicManager> musicManagers;

    AudioTrack lastTrack;


    public TrackScheduler(AudioPlayer pl) {
        this.pl = pl;
        this.queue = new LinkedList<>();
    }

    public void queue(AudioTrack track) {
        if (!pl.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void nextTrack() {
        pl.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer pl, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track;

        if (endReason.mayStartNext) {
            if (repeating) {
                pl.startTrack(lastTrack.makeClone(), false);
            } else {
                nextTrack();
            }
        }
    }

    public boolean isRepeating()
    {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public void shuffle() {
        Collections.shuffle((List<?>) queue);
    }
}

