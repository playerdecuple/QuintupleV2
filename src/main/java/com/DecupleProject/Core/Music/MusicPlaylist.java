package com.DecupleProject.Core.Music;

import com.DecupleProject.Core.DeleteFile;
import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.WriteFile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MusicPlaylist {

    private final ReadFile r = new ReadFile();
    private final WriteFile w = new WriteFile();
    private final DeleteFile d = new DeleteFile();

    public MusicPlaylist() {
    }

    public String getNextMusicUrl(String id, int musicId) {

        File f = new File("D:/Database/MusicPlayList/" + id + "/" + musicId + ".txt");

        if (f.exists()) {
            String musicUrl = r.readString(f);
            return musicUrl;
        } else {
            return null;
        }

    }

    public boolean getNextMusicPlaylistExists(String id, int musicId) {

        File f = new File("D:/Database/MusicPlayList/" + id + "/" + musicId + ".txt");

        if (f.exists()) {
            return true;
        } else {
            return false;
        }

    }

    public boolean addMusic(String id, String... urls) {

        int musicId = getUserPlaylistLength(id) + 1;

        for (int i = 0; i < urls.length; i++) {

            File musicFolder = new File("D:/Database/MusicPlayList/" + id);
            File musicFile = new File("D:/Database/MusicPlayList/" + id + "/" + musicId + ".txt");

            if (musicFile.exists() && musicFolder.exists()) {
                return false;
            } else {
                if (!musicFolder.exists()) {
                    musicFolder.mkdir();
                }

                if (urls[i] != null) {
                    w.writeString(musicFile.getPath(), urls[i]);
                    return true;
                }
            }

        }

        return false;

    }

    public void addMusic(String id, int musicId, String... urls) {

        for (int i = 0; i < urls.length; i++) {

            File musicFolder = new File("D:/Database/MusicPlayList/" + id);
            File musicFile = new File("D:/Database/MusicPlayList/" + id + "/" + musicId + ".txt");

            if (musicFile.exists() && musicFolder.exists()) {
                return;
            } else {
                if (!musicFolder.exists()) {
                    musicFolder.mkdir();
                }

                if (urls[i] != null) {
                    w.writeString(musicFile.getPath(), urls[i]);
                    musicId++;
                }
            }

        }

    }

    public void addMusicAr(String id, String[] urls) {

        int musicId = getUserPlaylistLength(id) + 1;

        for (int i = 0; i < urls.length; i++) {

            File musicFolder = new File("D:/Database/MusicPlayList/" + id);
            File musicFile = new File("D:/Database/MusicPlayList/" + id + "/" + musicId + ".txt");

            if (musicFile.exists() && musicFolder.exists()) {
                return;
            } else {
                if (!musicFolder.exists()) {
                    musicFolder.mkdir();
                }

                if (urls[i] != null) {
                    w.writeString(musicFile.getPath(), urls[i]);
                    musicId++;
                }
            }

        }

    }

    public void resetMusicPlaylist(String id) {
        File musicPlaylistFile = new File("D:/Database/MusicPlayList/" + id);

        if (musicPlaylistFile.exists()) {
            File[] musicPlaylistFolderLists = musicPlaylistFile.listFiles();

            for (int i = 0; i < musicPlaylistFolderLists.length; i++) {
                d.deleteFile(musicPlaylistFolderLists[i]);
            }

            d.deleteFile(musicPlaylistFile);
        }
    }

    public boolean playlistExists(String id) {

        File f = new File("D:/Database/MusicPlayList/" + id);

        if (f.exists()) {
            return true;
        } else {
            return false;
        }

    }

    public int getUserPlaylistLength(String id) {
        File f = new File("D:/Database/MusicPlayList/" + id);
        File[] fs = f.listFiles();

        int count = 0;

        if (f.exists()) {
            for (int i = 0; i < fs.length; i++) {
                count++;
            }

            return count;
        }
        return 0;
    }

    public void replaceMusic(String id, int code1, int code2) {

        if (code1 == code2) return;

        File fr = new File("D:/Database/MusicPlayList/" + id + "/" + code1 + ".txt");
        File fs = new File("D:/Database/MusicPlayList/" + id + "/" + code2 + ".txt");

        String rfr = r.readString(fr);
        String rfs = r.readString(fs);

        w.writeString(fs, rfr);
        w.writeString(fr, rfs);

    }

    public void sendAllMusicPlaylistFromId(String id, TextChannel tc) {
        String allMusicPlaylistURL = "";
        String oneLineMusicURL = "";

        for (int i = 1; i <= getUserPlaylistLength(id); i++) {
            oneLineMusicURL = i + ". " + getTitle(getNextMusicUrl(id, i)) + "<" + getNextMusicUrl(id, i) + ">\n";
            if (allMusicPlaylistURL.length() + oneLineMusicURL.length() >= 2000) {
                tc.sendMessage("```md\n" + allMusicPlaylistURL + "```").delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();
                allMusicPlaylistURL = "";
            }
            allMusicPlaylistURL = allMusicPlaylistURL + oneLineMusicURL;
        }

        tc.sendMessage("```md\n" + allMusicPlaylistURL + "```").delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();
    }

    public String getTitle(String youtubeUrl) {
        try {
            if (youtubeUrl != null) {
                URL url = new URL("http://www.youtube.com/oembed?url=" + youtubeUrl + "&format=json");

                String result = IOUtils.toString(url, "UTF-8");

                JsonParser jp = new JsonParser();
                JsonObject object = (JsonObject) jp.parse(result);

                String title = new JSONObject(IOUtils.toString(url)).getString("title");
                return title;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteOneMusic(String id, int deleteMusic) {
        try {
            File f = new File("D:/Database/MusicPlayList/" + id);
            File[] fs = f.listFiles();

            for (int i = deleteMusic; i < fs.length; i++) {
                boolean musicExists = getNextMusicPlaylistExists(id, i);

                if (musicExists) {
                    String nextMusicUrl = getNextMusicUrl(id, i + 1);

                    File musicFile = new File(f.getPath() + "/" + i + ".txt");
                    d.deleteFile(musicFile);

                    addMusic(id, i, nextMusicUrl);
                }

                if (getNextMusicPlaylistExists(id, i + 1) == false) {
                    break;
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void editPlayCount(String id, String url) {

        if (isCoolTime(id)) return;
        File f = new File("D:/Database/MusicPlayCount/");

        if (!f.exists()) {
            f.mkdir();
            editPlayCount(id, url);
        }

        String code = url.replace("https://www.youtube.com/watch?v=", "");

        File f2 = new File("D:/Database/MusicPlayCount/" + code + ".txt");

        if (!f2.exists()) {
            w.writeInt(f2.getPath(), 0);
        }

        w.writeInt(f2.getPath(), getPlayCount(url) + 1);
        setCoolTime(id);

    }

    public int getPlayCount(String url) {

        File f = new File("D:/Database/MusicPlayCount/");

        if (!f.exists()) {
            f.mkdir();
            getPlayCount(url);
        }

        String code = url.replace("https://www.youtube.com/watch?v=", "");

        File f2 = new File("D:/Database/MusicPlayCount/" + code + ".txt");

        if (!f.exists()) {
            w.writeInt(f2.getPath(), 0);
        }

        return r.readInt(f2.getPath());

    }

    public boolean isCoolTime(String id) {
        File f = new File("D:/Database/Time/" + id + "M.txt");

        if (f.exists()) {
            long coolTimeL = r.readLong(f.getPath());
            long realTime = System.currentTimeMillis();

            if (realTime - coolTimeL < 30000) {
                return true;
            } else {
                return false;
            }
        } else {
            w.writeInt(f.getPath(), 0);
            return false;
        }
    }

    public void setCoolTime(String id) {
        File f = new File("D:/Database/Time/" + id + "M.txt");
        w.writeLong(f.getPath(), System.currentTimeMillis());
    }

    public void resetPlayCounts() {
        File f = new File("D:/Database/MusicPlayCount/");
        File td = new File("D:/Database/DayOfToday.txt");

        if (r.readInt(td.getPath()) - 1 != 0) {
            return;
        }

        if (r.readInt(td.getPath()) == 1) return;

        File[] fs = f.listFiles();

        for (int i = 0; i < fs.length; i++) {
            if (!fs[i].isDirectory()) {
                d.deleteFile(fs[i]);
            }
        }
    }

    public void saveDay() {
        File td = new File("D:/Database/DayOfToday.txt");
        SimpleDateFormat format1 = new SimpleDateFormat("dd");
        Date date = new Date();

        String m1 = format1.format(date);

        w.writeString(td.getPath(), m1);
    }

}
