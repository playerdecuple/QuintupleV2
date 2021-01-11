package com.DecupleProject.Listener;

import com.DecupleProject.API.Melon;
import com.DecupleProject.API.Youtube;
import com.DecupleProject.Contents.RPG.Proficiency;
import com.DecupleProject.Core.*;
import com.DecupleProject.Core.Music.AudioInfo;
import com.DecupleProject.Core.Music.GuildMusicManager;
import com.DecupleProject.Core.Music.MusicPlaylist;
import com.DecupleProject.Core.Music.TrackScheduler;
import com.DecupleProject.Core.Util.EasyEqual;
import com.DecupleProject.Core.Util.LinkUtility;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MusicListener extends ListenerAdapter {

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private int defaultVolume = 20;

    public MusicListener() {
        this.musicManagers = new HashMap();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {

        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        musicManager.pl.setVolume(defaultVolume);

        return musicManager;

    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

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

                if (args.length <= 0) return;
                if (user.isBot()) return;

                GuildInfo guildInfo = new GuildInfo(guild);

                if (e.eq(args[0], "C", "Connect", "Enter", "입장", "들어와", "연결")) {

                    msg.delete().queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    try {

                        if (member == null) return;
                        else {
                            VoiceChannel vc = Objects.requireNonNull(member.getVoiceState()).getChannel();
                            connectToVoiceChannel(vc, tc, user, true);
                        }


                    } catch (NullPointerException ex) {

                        eb.setTitle("보이스 채널에 연결할 수 없었어요.");
                        eb.setDescription("혹시 보이스 채널에 계시지 않으신가요?");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                    }

                }

                if (e.eq(args[0], "E", "D", "Disconnect", "Exit", "Quit", "퇴장", "나가")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    am.closeAudioConnection();
                    skipAllTrack(tc, false);

                    eb.setDescription("보이스 채널 연결을 끊었어요.");
                    eb.setColor(Color.ORANGE);

                    tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                }

                if (e.eq(args[0], "P", "Play", "Queue", "재생", "틀어")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                    File playCoolTimeFile = new File("D:/Database/MusicCoolTime/");
                    if (!playCoolTimeFile.exists()) playCoolTimeFile.mkdir();

                    File lastTimeFile = new File("D:/Database/MusicCoolTime/" + guild.getId() + ".txt");
                    long lastTime = 0;

                    if (lastTimeFile.exists()) {
                        lastTime = new ReadFile().readLong(lastTimeFile);
                    }

                    if (System.currentTimeMillis() - lastTime < 3000) {

                        eb.setColor(Color.ORANGE);
                        eb.setDescription("음악을 다시 재생하려면 " + (int) (10 - ((System.currentTimeMillis() - lastTime) / 1000)) + "초를 기다려야 합니다.");
                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                        return;

                    }

                    try {

                        if (member == null) return;

                        VoiceChannel vc = Objects.requireNonNull(member.getVoiceState()).getChannel();

                        if (vc == null) {
                            tc.sendMessage("먼저 보이스 채널에 연결해 주세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        } else {
                            GuildVoiceState nowConnecting = Objects.requireNonNull(guild.getMember(event.getJDA().getSelfUser())).getVoiceState();
                            boolean showMessage = false;

                            if (nowConnecting == null) showMessage = true;
                            else {
                                if (nowConnecting.getChannel() == null) showMessage = true;
                                if (!vc.getId().equals(nowConnecting.getChannel().getId())) showMessage = true;
                            }

                            connectToVoiceChannel(vc, tc, user, showMessage);
                        }

                        Youtube youtube = new Youtube();
                        String youtubeSearched = youtube.searchYoutube(input);

                        if (youtubeSearched == null) {
                            eb.setDescription("곡이 YouTube에 없네요.");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }

                        input = youtubeSearched;
                        loadAndPlay(event.getChannel(), input, true, member);
                        new WriteFile().writeLong(lastTimeFile, System.currentTimeMillis());


                    } catch (NullPointerException ex) {

                        eb.setTitle("보이스 채널에 연결할 수 없었어요.");
                        eb.setDescription("혹시 보이스 채널에 계시지 않으신가요?");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                    }
                }

                if (e.eq(args[0], "S", "skip", "넘겨", "스킵", "다음")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    if (args.length == 1) {
                        skipTrack(tc, true);
                        return;
                    } else if (e.eq(args[1], "ALL", "모두", "전체", "전부")) {
                        skipAllTrack(tc, true);
                    } else {
                        int skipTrack = Integer.parseInt(args[1]);

                        if (skipTrack < 1 | skipTrack > 10) {
                            eb.setTitle("몇 개의 곡을 건너 뛰라구요?");
                            eb.setDescription("1곡에서, 10곡까지만 건너뛸 수 있어요.");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        } else {
                            skipTrack(tc, Integer.parseInt(args[1]), true);
                        }
                    }

                }

                if (e.eq(args[0], "r", "replay", "last", "마지막", "다시")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    GuildMusicManager musicManager = getGuildAudioPlayer(guild);

                    try {

                        if (member == null) return;
                        else {
                            VoiceChannel vc = Objects.requireNonNull(member.getVoiceState()).getChannel();

                            if (vc == null) {
                                tc.sendMessage("먼저 보이스 채널에 연결해 주세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                return;
                            } else {
                                GuildVoiceState nowConnecting = Objects.requireNonNull(guild.getMember(event.getJDA().getSelfUser())).getVoiceState();
                                boolean showMessage = false;

                                if (nowConnecting == null) showMessage = true;
                                else {
                                    if (nowConnecting.getChannel() == null) showMessage = true;
                                    if (!vc.getId().equals(nowConnecting.getChannel().getId())) showMessage = true;
                                }

                                connectToVoiceChannel(vc, tc, user, showMessage);
                            }

                        }

                        loadAndPlay(tc, musicManager.scheduler.getLastTrack().getInfo().uri, true, member);

                    } catch (NullPointerException ex) {

                        eb.setTitle("보이스 채널에 연결할 수 없었어요.");
                        eb.setDescription("혹시 보이스 채널에 계시지 않으신가요?");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                    }

                }

                if (e.eq(args[0], "ps", "pause", "잠깐", "멈춰")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    GuildMusicManager musicManager = getGuildAudioPlayer(guild);

                    boolean pauseResult = !musicManager.pl.isPaused();
                    musicManager.pl.setPaused(pauseResult);

                    eb.setDescription(pauseResult ? "곡을 잠시 멈추겠습니다!" : "계속 들어 보실까요?");
                    eb.setColor(pauseResult ? Color.ORANGE : Color.GREEN);
                    eb.setFooter(user.getAsTag(), user.getAvatarUrl());

                    tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                }

                if (e.eq(args[0], "vol", "volume", "볼륨", "음량")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    if (args.length == 1) {

                        eb.setTitle("볼륨을 몇으로 조절해 드릴까요?");
                        eb.setDescription("`.vol [숫자]`의 형식으로 입력해 주세요.");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        return;

                    }

                    int vol = Integer.parseInt(args[1]);
                    defaultVolume = vol;
                    setVolume(tc, vol, true);

                }

                if (e.eq(args[0], "멜론", "멜론차트", "Melon", "Chart", "Chats")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    Melon melon = new Melon();

                    try {

                        if (!am.isConnected()) {

                            if (member == null) return;
                            else {
                                VoiceChannel vc = Objects.requireNonNull(member.getVoiceState()).getChannel();

                                if (vc == null) {
                                    tc.sendMessage("먼저 보이스 채널에 연결해 주세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                    return;
                                } else {
                                    GuildVoiceState nowConnecting = Objects.requireNonNull(guild.getMember(event.getJDA().getSelfUser())).getVoiceState();
                                    boolean showMessage = false;

                                    if (nowConnecting == null) showMessage = true;
                                    else {
                                        if (nowConnecting.getChannel() == null) showMessage = true;
                                        if (!vc.getId().equals(nowConnecting.getChannel().getId())) showMessage = true;
                                    }

                                    connectToVoiceChannel(vc, tc, user, showMessage);
                                }
                            }

                        }

                        int range = 10;

                        if (args.length != 1) {
                            range = Integer.parseInt(args[1]);
                        }

                        if (range > 30 | range < 1) {

                            eb.setTitle("차트 범위를 벗어나 버렸어요..");
                            eb.setDescription("차트는 1위부터 30위까지만 재생 가능해요.");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;

                        }


                        for (int i = 0; i < range; i++) {

                            String input = melon.getChartForRank(i);

                            Youtube youtube = new Youtube();

                            String youtubeSearched = youtube.searchYoutube(input);

                            if (youtubeSearched == null) {
                                eb.setDescription("곡이 YouTube에 없네요.");
                                eb.setColor(Color.RED);

                                tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                return;
                            }

                            input = youtubeSearched;
                            loadAndPlay(event.getChannel(), input, false, member);

                        }

                        tc.sendMessage("```md\n" + melon.getCharts(range) + "```").delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();


                    } catch (NullPointerException ex) {

                        eb.setTitle("보이스 채널에 연결할 수 없었어요.");
                        eb.setDescription("혹시 보이스 채널에 계시지 않으신가요?");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                    }

                }

                if (e.eq(args[0], "np", "nowPlaying", "재생중", "곡")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    getNowPlaying(tc);

                }

                if (e.eq(args[0], "sf", "shuffle", "셔플", "섞어", "섞기")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    GuildMusicManager musicManager = getGuildAudioPlayer(tc.getGuild());

                    musicManager.scheduler.shuffle();

                    eb.setTitle("큐가 셔플되었어요!");
                    eb.setDescription("순서를 모두 섞었답니다.");
                    eb.setColor(Color.CYAN);

                    tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                }

                if (e.eq(args[0], "반복", "rp", "repeat", "rep", "반복해", "계속")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    GuildMusicManager musicManager = getGuildAudioPlayer(tc.getGuild());

                    if (args.length == 1) {

                        musicManager.scheduler.setRepeating(!musicManager.scheduler.isRepeating());

                        eb.setDescription("반복을 " + (musicManager.scheduler.isRepeating() ? "시작할게요!" : "끌게요!"));
                        eb.setColor(Color.CYAN);

                        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                    } else if (e.eq(args[1], "list", "ls", "리스트", "목록", "플레이리스트", "pl")) {

                        musicManager.scheduler.setListRepeating(!musicManager.scheduler.isListRepeating());

                        eb.setDescription(musicManager.scheduler.queue.size() + "개의 곡이 담긴 플레이리스트를 반복" +
                                (musicManager.scheduler.isListRepeating() ? "할게요!" : " 취소할게요!"));
                        eb.setColor(Color.CYAN);

                        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                    }

                }

                if (e.eq(args[0], "리스트", "list", "목록")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    GuildMusicManager musicManager = getGuildAudioPlayer(tc.getGuild());
                    TrackScheduler scheduler = musicManager.scheduler;

                    Queue<AudioInfo> queue = scheduler.queue;

                    if (queue.isEmpty()) {

                        eb.setTitle("음.. 플레이리스트를 어디다 뒀더라?");
                        eb.setDescription("플레이리스트가 아무리 찾아봐도 없네요... 비어있다면 못 찾을 수도 있어요.");
                        eb.setColor(Color.RED);

                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                    } else {

                        int trackCount = 0;
                        long queueLength = 0;
                        StringBuilder sb = new StringBuilder();

                        sb.append("```md\n# 현재 플레이리스트에요!\n<개수: ").append(queue.size()).append(">\n\n");

                        for (AudioInfo info : queue) {
                            queueLength += info.getTrack().getDuration();

                            if (trackCount < 20) {
                                sb.append(trackCount + 1).append(". ").append(info.getTrack().getInfo().title).append("\n");
                                trackCount++;
                            }
                        }

                        sb.append("\n전체 재생 길이 : ").append(getTimeStamp(queueLength, true)).append("```");
                        tc.sendMessage(sb.toString()).queue();

                    }

                }

                if (e.eq(args[0], "pl", "playlist", "플레이리스트", "재생목록")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (guildInfo.getMusicChannel() != null) {
                        if (!tc.getId().equals(guildInfo.getMusicChannel().getId())) {
                            tc.sendMessage("이 곳에서는 **음악 명령어**를 사용할 수 없습니다! 대신, " + guildInfo.getMusicChannel().getAsMention() + " 채널에서 써 주세요.")
                                    .delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }
                    }

                    MusicPlaylist mp = new MusicPlaylist();

                    if (args.length < 2) {

                        VoiceChannel vc = Objects.requireNonNull(Objects.requireNonNull(member).getVoiceState()).getChannel();

                        if (vc == null) {
                            tc.sendMessage("먼저 보이스 채널에 연결해 주세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }

                        GuildVoiceState nowConnecting = Objects.requireNonNull(guild.getMember(event.getJDA().getSelfUser())).getVoiceState();
                        boolean showMessage = false;

                        if (nowConnecting == null) showMessage = true;
                        else {
                            if (nowConnecting.getChannel() == null) showMessage = true;
                            if (!vc.getId().equals(nowConnecting.getChannel().getId())) showMessage = true;
                        }

                        connectToVoiceChannel(vc, tc, user, showMessage);

                        if (mp.playlistExists(user.getId())) {

                            int i = 1;

                            while (mp.exists(user.getId(), i)) {
                                loadAndPlay(tc, mp.getMusicUrl(user.getId(), i), false, member);

                                if (!mp.exists(user.getId(), i + 1)) break;
                                i++;
                            }

                            eb.setTitle("커스텀 플레이리스트 목록을 적어둘게요!");
                            eb.setDescription(user.getAsMention() + "님의 커스텀 플레이리스트에 있는 " + mp.getUserPlaylistLength(user.getId()) + "곡을 재생할게요!");
                            eb.setColor(Color.CYAN);

                            tc.sendMessage(eb.build()).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();

                        } else {

                            eb.setTitle("커스텀 플레이리스트를 찾지 못했어요..");
                            eb.setDescription("`.pl 추가 [URL]`을 입력하여 플레이리스트를 만드는 건 어떤가요?");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                        }

                    } else if (e.eq(args[1], "추가", "add", "저장", "save", "a", "s")) {

                        String input = String.join(" ", Arrays.copyOfRange(args, 2, args.length)).replace(" ", "");
                        String[] songs = input.split("[|]");

                        if (input.contains("https://twitch.tv") || input.contains("https://www.twitch.tv")) {
                            tc.sendMessage("트위치 라이브는 플레이리스트에 저장해 둘 수 없습니다.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }

                        Youtube y = new Youtube();

                        LinkUtility l = new LinkUtility();

                        for (int i = 0; i < songs.length; i++) {
                            if (songs[i].charAt(0) == ' ') {

                                songs[i] = songs[i].substring(0, 1);

                            }
                        }

                        StringBuilder addedSongs = new StringBuilder();

                        for (int i = 0; i < songs.length; i++) {
                            if (!l.isURL(songs[i])) {

                                String ytSearched = y.searchYoutube(songs[i]);
                                System.out.println(ytSearched);

                                if (ytSearched == null) {

                                    eb.setDescription("제가 제대로 봤는지 모르겠지만, 해당하는 곡이 없었어요. 유튜브 검색 기능이 잘못 된 것이 아니라면, 잘못 입력 했을 수도 있겠죠.");
                                    eb.setColor(Color.RED);

                                    tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                    return;

                                } else {

                                    mp.addMusic(user.getId(), ytSearched);

                                    addedSongs.append(i + 1).append(". ").append(y.getTitle(ytSearched)).append("\n");

                                }

                            } else {

                                mp.addMusic(user.getId(), songs[i].replace(" ", ""));

                                addedSongs.append(y.getTitle(songs[i].replace(" ", ""))).append("\n");

                            }
                        }

                        eb.setTitle("커스텀 플레이리스트에 새 곡을 적었습니다!");
                        eb.setDescription("");

                        eb.addField("추가된 곡들", "```md\n" + addedSongs.toString() + "```", false);
                        eb.setColor(Color.GREEN);

                        tc.sendMessage(eb.build()).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();

                    } else if (e.eq(args[1], "리스트", "list", "l", "목록")) {

                        if (args.length == 2) {
                            mp.sendAllPlaylist(user.getId(), tc);
                        } else {
                            try {
                                if (msg.getMentionedMembers().get(0) != null) {
                                    mp.sendAllPlaylist(msg.getMentionedMembers().get(0).getUser().getId(), tc);
                                }
                            } catch (IndexOutOfBoundsException ex) {
                                // ignore
                            }
                        }

                    } else if (e.eq(args[1], "리셋", "reset", "r")) {

                        mp.resetMusicPlaylist(user.getId());

                        eb.setDescription("플레이리스트를 리셋했어요.");
                        tc.sendMessage(eb.build()).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();

                    } else if (e.eq(args[1], "삭제", "제거", "delete", "d")) {

                        if (args.length != 3) {
                            eb.setTitle("무엇을 삭제할까요?");
                            eb.setDescription("무엇을 삭제할 지 안 정하셨네요. `.pl 리스트`를 입력하고, 원하는 번호를 기억하여 `.pl 삭제 [번호]` 형식으로 써 주세요.");
                            eb.setColor(Color.RED);

                            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        } else {
                            int no = Integer.parseInt(args[2]);

                            if (!"".equals(args[2])) {
                                boolean v = mp.deleteOneMusic(user.getId(), no);

                                if (v) {
                                    eb.setDescription("성공적으로 " + no + "번 음악을 삭제했어요.");
                                } else {
                                    eb.setDescription("해당 음악을 삭제할 수 없었어요.");
                                }

                                tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            }
                        }

                    } else if (e.eq(args[1], "replace", "바꾸기", "순서바꾸기", "리플레이스", "rp")) {

                        if (args.length == 4 && mp.playlistExists(user.getId()) && mp.exists(user.getId(), Integer.parseInt(args[2])) && mp.exists(user.getId(), Integer.parseInt(args[3]))) {
                            mp.replaceMusic(user.getId(), Integer.parseInt(args[2]), Integer.parseInt(args[3]));

                            eb.setDescription(Integer.parseInt(args[2]) + "번 음악과 " + Integer.parseInt(args[3]) + "번 음악의 순서를 서로 바꾸었어요.");
                            eb.setColor(Color.CYAN);
                            tc.sendMessage(eb.build()).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();
                        } else {
                            eb.setDescription("음악의 순서를 바꾸지 못했어요. `.pl 바꾸기 [번호1] [번호2]` 형식으로 입력하지 않았거나, 해당하는 번호가 틀린 번호일수도 있어요.");
                            eb.setColor(Color.RED);
                            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        }

                    } else if (e.eq(args[1], "제목", "title", "제목설정", "공개")) {

                        if (args.length == 2) {
                            eb.setTitle("플레이리스트의 제목을 설정합니다.");
                            eb.setDescription("플레이리스트의 제목을 설정하게 되면 다른 디스코드 및 퀸튜플 이용자가 귀하의 플레이리스트를 볼 수 있게 됩니다. (즉, 공개하게 됩니다.)");
                            eb.addField("공개해도 괜찮다면!", "`.pl 제목 [제목]` 형식으로 써 주세요.", false);
                            eb.addField("공개를 끌래요.", "`.pl 제목 ` 형식으로 써 주세요. 저 공백까지 포함해야 합니다.", false);
                            eb.setColor(Color.ORANGE);

                            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }

                        String title = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                        if (e.eq(title.replace(" ", ""), "")) {
                            mp.setTitle(user.getId(), "");
                            tc.sendMessage(user.getAsMention() + "님의 플레이리스트를 **비공개** 상태로 바꾸었습니다..!").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        } else {
                            mp.setTitle(user.getId(), title);
                            tc.sendMessage(user.getAsMention() + "님의 플레이리스트를 **공개** 상태로 바꾸었습니다..!\n이 플레이리스트의 제목을 **" + title + "**로 설정했습니다!").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        }

                    } else if (e.eq(args[1], "검색", "search")) {

                        if (args.length == 2) {
                            eb.setTitle("플레이리스트를 검색합니다.");
                            eb.setDescription("`.pl 검색 [검색어]`로 검색해 주세요!");
                            eb.setColor(Color.ORANGE);

                            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            return;
                        }

                        String word = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                        tc.sendMessage("**" + word + "**에 대한 검색 결과입니다!:\n```md\n" + mp.searchPlayLists(word) + "```").delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();

                    } else {

                        try {
                            String victim = msg.getMentionedUsers().get(0).getId();

                            VoiceChannel vc = Objects.requireNonNull(Objects.requireNonNull(member).getVoiceState()).getChannel();

                            if (vc == null) {
                                tc.sendMessage("먼저 보이스 채널에 연결해 주세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                return;
                            }

                            GuildVoiceState nowConnecting = Objects.requireNonNull(guild.getMember(event.getJDA().getSelfUser())).getVoiceState();
                            boolean showMessage = false;

                            if (nowConnecting == null) showMessage = true;
                            else {
                                if (nowConnecting.getChannel() == null) showMessage = true;
                                if (!vc.getId().equals(nowConnecting.getChannel().getId())) showMessage = true;
                            }

                            connectToVoiceChannel(vc, tc, user, showMessage);

                            if (mp.playlistExists(victim)) {

                                int v = 0;

                                for (int i = 0; i < mp.getUserPlaylistLength(victim); i++) {
                                    loadAndPlay(tc, mp.getMusicUrl(victim, i), false, member);
                                    v++;
                                    if (!mp.exists(victim, i + 1)) {
                                        break;
                                    }
                                }

                                eb.setTitle("커스텀 플레이리스트를 재생합니다!");
                                eb.setDescription(event.getJDA().retrieveUserById(victim).complete().getName() + "님의 커스텀 플레이리스트에 있는 " + v + "개의 곡을 모두 재생합니다.");
                                eb.setColor(Color.CYAN);

                                tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            } else {
                                tc.sendMessage("<@" + victim + ">님은 갖고 계신 플레이리스트가 없습니다.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            }
                        } catch (IndexOutOfBoundsException ex) {

                            String targetId = args[1];
                            User targetUser = DefaultListener.jda.retrieveUserById(targetId).complete();

                            if (targetUser != null) {

                                VoiceChannel vc = Objects.requireNonNull(Objects.requireNonNull(member).getVoiceState()).getChannel();

                                if (vc == null) {
                                    tc.sendMessage("먼저 보이스 채널에 연결해 주세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                    return;
                                }

                                GuildVoiceState nowConnecting = Objects.requireNonNull(guild.getMember(event.getJDA().getSelfUser())).getVoiceState();
                                boolean showMessage = false;

                                if (nowConnecting == null) showMessage = true;
                                else {
                                    if (nowConnecting.getChannel() == null) showMessage = true;
                                    if (!vc.getId().equals(nowConnecting.getChannel().getId())) showMessage = true;
                                }

                                connectToVoiceChannel(vc, tc, user, showMessage);

                                if (mp.playlistExists(targetId)) {

                                    int v = 0;

                                    for (int i = 0; i < mp.getUserPlaylistLength(targetId); i++) {
                                        loadAndPlay(tc, mp.getMusicUrl(targetId, i), false, member);
                                        v++;
                                        if (!mp.exists(targetId, i + 1)) {
                                            break;
                                        }
                                    }

                                    eb.setTitle("커스텀 플레이리스트를 재생합니다!");
                                    eb.setDescription(event.getJDA().retrieveUserById(targetId).complete().getName() + "님의 커스텀 플레이리스트에 있는 " + v + "개의 곡을 모두 재생합니다.");
                                    eb.setColor(Color.CYAN);

                                    tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                                } else {

                                    tc.sendMessage("<@" + targetId + ">님은 갖고 계신 플레이리스트가 없습니다.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                                }

                            } else {

                                File codeFile = new File("D:/Database/MusicPlayList/" + args[1]);
                                if (codeFile.exists()) {

                                    ReadFile r = new ReadFile();
                                    User playlistOwner = DefaultListener
                                            .jda.retrieveUserById(Objects.requireNonNull(r.readString(new File(codeFile.getPath() + "/owner.txt")))).complete();

                                    if (mp.playlistExists(args[1])) {

                                        int v = 0;

                                        for (int i = 0; i < mp.getUserPlaylistLength(args[1]); i++) {
                                            loadAndPlay(tc, mp.getMusicUrl(args[1], i), false, member);
                                            v++;
                                            if (!mp.exists(args[1], i + 1)) {
                                                break;
                                            }
                                        }

                                        eb.setTitle("커스텀 플레이리스트를 재생합니다!");
                                        eb.setDescription(playlistOwner.getName() + "님의 커스텀 플레이리스트에 있는 " + v + "개의 곡을 모두 재생합니다.");
                                        eb.setColor(Color.CYAN);

                                        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                                        return;
                                    }
                                }

                                tc.sendMessage("디스코드 데이터베이스를 훑어봤는데요, 아무리 찾아봐도 그런 ID는 없더라구요.\n**AXSDG**와 같은 5자리 코드나 **멘션, 유저 ID**를 입력해 주세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                            }

                        }

                    }

                }


            }

        } catch (StringIndexOutOfBoundsException | IllegalStateException e) {
            // ignore
        } catch (NumberFormatException e) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setDescription("무언가 잘못 입력한 것 같네요..");
            event.getChannel().sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionReport(e, event.getAuthor(), event.getChannel());
        }

    }

    public void loadAndPlay(final TextChannel tc, String url, boolean showMessage, Member user) {

        if (user == null) return;

        GuildMusicManager musicManager = getGuildAudioPlayer(tc.getGuild());
        Proficiency p = new Proficiency(user.getUser());

        final String trackUrl;

        if (url.startsWith("<") && url.endsWith(">"))
            trackUrl = url.substring(1, url.length() - 1);
        else
            trackUrl = url;

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {

                EmbedBuilder eb = new EmbedBuilder();
                p.addValue(203, 1);

                eb.setTitle("새로운 곡을 적었습니다!", trackUrl);

                eb.addField("곡 이름", audioTrack.getInfo().title, true);

                long af = audioTrack.getInfo().length;

                int seconds = (int) (af / 1000) % 60;
                int minutes = (int) ((af / (1000 * 60)) % 60);
                int hours = (int) ((af / (1000 * 60 * 60)));

                eb.addField("곡 길이", hours + "시간 " + minutes + "분 " + seconds + "초", true);
                eb.addField("채널", audioTrack.getInfo().author, true);
                eb.setDescription(musicManager.scheduler.isListRepeating() ? "플레이리스트 반복이 진행 중이므로 순서가 꼬일 수 있어요." : "자, 들어 보실까요?");
                eb.setColor(Color.CYAN);

                Youtube y = new Youtube();
                eb.setImage(y.getThumbnail(trackUrl));

                eb.setFooter(user.getUser().getAsTag(), user.getUser().getAvatarUrl());

                if (showMessage) {
                    tc.sendMessage(eb.build()).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();
                }

                play(tc.getGuild(), musicManager, audioTrack, user, tc);

            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = audioPlaylist.getTracks().get(0);
                }

                if (showMessage) {

                    EmbedBuilder eb = new EmbedBuilder();

                    eb.setDescription("플레이리스트 첫 번째 곡을 " + firstTrack.getInfo().title + "(으)로 하겠습니다!");
                    eb.setColor(Color.CYAN);
                    tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                }

                setVolume(tc, 20, false);
                play(tc.getGuild(), musicManager, firstTrack, user, tc);
            }

            @Override
            public void noMatches() {
                EmbedBuilder eb = new EmbedBuilder();

                if (showMessage) {

                    eb.setDescription("제가 제대로 봤는지 모르겠지만, 해당하는 곡이 없었어요. 유튜브 검색 기능이 잘못 된 것이 아니라면, 잘못 입력 했을 수도 있겠죠.");
                    eb.setColor(Color.RED);

                    tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

                }
            }

            @Override
            public void loadFailed(FriendlyException e) {
                EmbedBuilder eb = new EmbedBuilder();

                if (showMessage) {
                    eb.setTitle("Track을 불러오는 데 실패했습니다.");
                    eb.addField("Exception Name", e.getClass().getName(), true);
                    eb.addBlankField(true);
                    eb.addField("Exception Caused By", e.getCause().getClass().getName(), true);
                    eb.addField("TextChannel Info", tc.getName() + ", " + tc.getId(), true);
                    eb.addField("Guild Info", tc.getGuild().getName() + ", " + tc.getGuild().getId(), true);
                    eb.addField("발생 시각", getTimeStamp(System.currentTimeMillis(), false), true);
                    eb.addField("Exception Message", "```" + e.getMessage() + "```", false);
                    eb.setDescription("곡을 불러올 수 없었습니다. 팀 데큐플에 해당 내용을 보고합니다.");
                    eb.setColor(Color.RED);

                    tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    DefaultListener.owner.openPrivateChannel().complete().sendMessage(eb.build()).queue();
                    DefaultListener.owner.openPrivateChannel().complete().sendMessage(tc.getGuild().retrieveInvites().complete().get(0).getUrl()).queue();
                }
            }
        });

    }

    public void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, Member member, TextChannel tc) {
        connectToFirstVoiceChannel(guild.getAudioManager());
        musicManager.scheduler.queue(track, member, tc);
    }

    public void skipTrack(TextChannel tc, boolean showMessage) {

        GuildMusicManager musicManager = getGuildAudioPlayer(tc.getGuild());
        musicManager.scheduler.nextTrack();

        if (showMessage) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setDescription("다음 곡으로 넘어갈까요?");
            eb.setColor(Color.CYAN);

            tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        }

    }

    public void skipTrack(TextChannel tc, int value, boolean showMessage) {

        GuildMusicManager musicManager = getGuildAudioPlayer(tc.getGuild());
        musicManager.scheduler.nextTrack(value);

        if (showMessage) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setDescription(value + "곡을 건너 뛸게요!");
            eb.setColor(Color.CYAN);

            tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        }

    }

    public void skipAllTrack(TextChannel tc, boolean showMessage) {
        GuildMusicManager musicManager = getGuildAudioPlayer(tc.getGuild());

        File serverDirectory = new File("D:/Database/Servers/" + tc.getGuild().getId());
        File topicFile = new File(serverDirectory.getPath() + "/Topic" + tc.getId() + ".txt");

        ReadFile r = new ReadFile();

        if (topicFile.exists())
            tc.getManager().setTopic(r.readString(topicFile)).queue();

        musicManager.pl.stopTrack();
        musicManager.scheduler.queue.clear();

        if (showMessage) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setDescription("플레이리스트를 전부 건너뛰었습니다.");

            tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        }
    }

    public void setVolume(TextChannel tc, int volume, boolean showMessage) {
        GuildMusicManager musicManager = getGuildAudioPlayer(tc.getGuild());
        musicManager.scheduler.pl.setVolume(volume);

        EmbedBuilder eb = new EmbedBuilder();

        eb.setDescription("볼륨을 `" + volume + "`(으)로 설정했습니다!");
        eb.setColor(Color.GREEN);

        if (!showMessage) return;
        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();

        defaultVolume = volume;
    }

    public void getNowPlaying(TextChannel tc) {
        try {
            GuildMusicManager musicManager = getGuildAudioPlayer(tc.getGuild());
            AudioTrack audioTrack = musicManager.scheduler.pl.getPlayingTrack();

            AudioTrackInfo af = audioTrack.getInfo();
            EmbedBuilder eb = new EmbedBuilder();

            int seconds = (int) (af.length / 1000) % 60;
            int minutes = (int) ((af.length / (1000 * 60)) % 60);
            int hours = (int) ((af.length / (1000 * 60 * 60)));

            long pos = audioTrack.getPosition();

            int ns = (int) (pos / 1000) % 60;
            int nm = (int) (pos / (1000 * 60)) % 60;
            int nh = (int) (pos / (1000 * 60 * 60));

            String author = af.author;

            double posPercentr = (double) pos / (double) af.length;
            double posPercent = posPercentr * 100;

            eb.setTitle("곡 : " + af.title);
            eb.addField("곡 길이", hours + "시간 " + minutes + "분 " + seconds + "초", true);
            eb.addField("업로더", author, true);
            eb.addField("현재 구간", nh + "시간 " + nm + "분 " + ns + "초(" + String.format("%.1f", posPercent) + "%)", true);

            eb.setDescription(af.uri);

            tc.sendMessage(eb.build()).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();
        } catch (NullPointerException e) {
            tc.sendMessage("현재 재생 중인 곡이 없어요.").queue();
        }
    }

    public String getTimeStamp(long pos, boolean korean) {

        int ns = (int) (pos / 1000) % 60;
        int nm = (int) (pos / (1000 * 60)) % 60;
        int nh = (int) (pos / (1000 * 60 * 60));

        if (korean) {
            return nh + "시간 " + nm + "분 " + ns + "초";
        } else {
            return nh + ":" + nm + ":" + ns + ":";
        }

    }

    public static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected()) { // 'audioManager.isAttemptingConnect()' was deprecated
            for (VoiceChannel vc : audioManager.getGuild().getVoiceChannels()) {
                audioManager.openAudioConnection(vc);
                return;
            }
        }
    }

    public void connectToVoiceChannel(VoiceChannel vc, TextChannel tc, User user, boolean showMessage) {
        AudioManager am = tc.getGuild().getAudioManager();
        EmbedBuilder eb = new EmbedBuilder();

        if (vc == null) {
            if (showMessage)
                tc.sendMessage("먼저 보이스 채널에 연결해 주세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        } else {
            if (!am.isConnected()) setVolume(tc, 20, false);

            am.setSendingHandler(new AudioSendHandler() {
                @Override
                public boolean canProvide() {
                    return false;
                }

                @Nullable
                @Override
                public ByteBuffer provide20MsAudio() {
                    return null;
                }
            });

            am.openAudioConnection(vc);
            setVolume(tc, 20, false);

            eb.setDescription("`" + vc.getName() + "` 보이스 채널에 연결했어요!");
            eb.setFooter(user.getAsTag(), user.getAvatarUrl());
            eb.setColor(Color.CYAN);
            if (showMessage) tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        }
    }

}
