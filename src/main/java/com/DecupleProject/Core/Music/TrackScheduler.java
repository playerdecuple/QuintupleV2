package com.DecupleProject.Core.Music;

import com.DecupleProject.Listener.DefaultListener;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TrackScheduler extends AudioEventAdapter {
    private boolean repeating = false;
    private boolean listRepeating = false;

    public final AudioPlayer pl;
    public final Queue<AudioInfo> queue;
    public TextChannel tc = null;
    // private Map<String, GuildMusicManager> musicManagers;

    AudioTrack lastTrack;

    public TrackScheduler(AudioPlayer pl) {
        this.pl = pl;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track, Member author, TextChannel tc) {
        AudioInfo info = new AudioInfo(track, author, tc);
        this.tc = tc;

        if (!pl.startTrack(track, true)) {
            queue.add(info);
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {

        lastTrack = track;
        if (queue.size() > 0) queue.remove();

    }

    @Override
    public void onTrackEnd(AudioPlayer pl, AudioTrack track, AudioTrackEndReason endReason) {

        if (endReason.mayStartNext) {
            if (repeating) {
                pl.startTrack(lastTrack.makeClone(), false);
            } else {
                if (queue.isEmpty()) return;
                nextTrack();
            }
        }

    }

    public Set<AudioInfo> getQueuedTracks() {
        return new LinkedHashSet<>(queue);
    }

    public void nextTrack() {
        Set<AudioInfo> queue = getQueuedTracks();
        ArrayList<AudioInfo> tracks = new ArrayList<>(queue);

        try {
            if (!queue.isEmpty()) {
                pl.startTrack(tracks.get(0).getTrack(), false);
                lastTrack = tracks.get(0).getTrack();

                if (listRepeating) this.queue.add(new AudioInfo(tracks.get(0).getTrack().makeClone(), tc.getGuild().getMember(DefaultListener.jda.getSelfUser()), tc));
                tracks.remove(0);
            }
        } catch (IndexOutOfBoundsException e) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setDescription("더 이상 스킵할 곡이 없어요.");
            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        } catch (Exception e) {
            // ignore
        }
    }

    public void nextTrack(int value) {
        Set<AudioInfo> queue = getQueuedTracks();
        ArrayList<AudioInfo> tracks = new ArrayList<>(queue);

        try {
            if (!queue.isEmpty()) {
                for (int i = 1; i < value - 1; i++) {
                    this.queue.remove();
                    tracks.remove(0);
                }

                pl.startTrack(tracks.get(0).getTrack(), false);
                lastTrack = tracks.get(0).getTrack();

                if (listRepeating) this.queue.add(new AudioInfo(tracks.get(0).getTrack().makeClone(), tc.getGuild().getMember(DefaultListener.jda.getSelfUser()), tc));
                tracks.remove(0);
            }
        } catch (IndexOutOfBoundsException e) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setDescription("더 이상 스킵할 곡이 없어요.");
            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        } catch (Exception e) {
            // ignore
            // RateLimitedException caused by 'RestAction - complete(false)'.
        }
    }

    public AudioTrack getLastTrack() { return lastTrack; }

    public boolean isRepeating() {
        return repeating;
    }

    public boolean isListRepeating() { return listRepeating; }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public void setListRepeating(boolean repeating) { this.listRepeating = repeating; }

    public void shuffle() {
        ArrayList<AudioInfo> tracks = new ArrayList<>(queue);

        for (int i = 0; i < queue.size(); i++) {
            queue.remove();
        }

        Collections.shuffle(tracks);
        queue.addAll(tracks);
    }
}