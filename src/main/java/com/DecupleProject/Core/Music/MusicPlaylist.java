package com.DecupleProject.Core.Music;

import com.DecupleProject.Core.*;
import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MusicPlaylist {

    private final ReadFile r = new ReadFile();
    private final WriteFile w = new WriteFile();
    private final DeleteFile d = new DeleteFile();

    public MusicPlaylist() {
    }

    public String getMusicUrl(String id, int musicId) {

        File f = new File("D:/Database/MusicPlayList/" + id + "/" + musicId + ".txt");

        if (f.exists()) {
            return r.readString(f);
        } else {
            return null;
        }

    }


    public boolean exists(String id, int musicId) {

        File f = new File("D:/Database/MusicPlayList/" + id + "/" + musicId + ".txt");

        return f.exists();

    }

    public void addMusic(String id, String... urls) {

        int musicId = getUserPlaylistLength(id) + 1;

        for (String url : urls) {

            File musicFolder = new File("D:/Database/MusicPlayList/" + id);
            File musicFile = new File("D:/Database/MusicPlayList/" + id + "/" + musicId + ".txt");

            if (musicFile.exists() && musicFolder.exists()) {
                return;
            } else {
                if (!musicFolder.exists()) {
                    boolean directoryMade = musicFolder.mkdir();

                    if (!directoryMade) {
                        return;
                    }
                }

                if (url != null) {
                    w.writeString(musicFile.getPath(), url);
                    return;
                }
            }

        }

    }

    public void addMusic(String id, int musicId, String... urls) {

        for (String url : urls) {

            File musicFolder = new File("D:/Database/MusicPlayList/" + id);
            File musicFile = new File("D:/Database/MusicPlayList/" + id + "/" + musicId + ".txt");

            if (musicFile.exists() && musicFolder.exists()) {
                return;
            } else {
                if (!musicFolder.exists()) {
                    boolean directoryMade = musicFolder.mkdir();

                    if (!directoryMade) {
                        return;
                    }
                }

                if (url != null) {
                    w.writeString(musicFile.getPath(), url);
                    musicId++;
                }
            }

        }

    }

    /* Never used code yet.

    public void addMusicAr(String id, String[] urls) {

        int musicId = getUserPlaylistLength(id) + 1;

        for (String url : urls) {

            File musicFolder = new File("D:/Database/MusicPlayList/" + id);
            File musicFile = new File("D:/Database/MusicPlayList/" + id + "/" + musicId + ".txt");

            if (musicFile.exists() && musicFolder.exists()) {
                return;
            } else {
                if (!musicFolder.exists()) {
                    boolean directoryMade = musicFolder.mkdir();

                    if (!directoryMade) {
                        return;
                    }
                }

                if (url != null) {
                    w.writeString(musicFile.getPath(), url);
                    musicId++;
                }
            }

        }

    }

     */

    public void resetMusicPlaylist(String id) {
        File musicPlaylistFile = new File("D:/Database/MusicPlayList/" + id);

        if (musicPlaylistFile.exists()) {
            File[] musicPlaylistFolderLists = musicPlaylistFile.listFiles();

            for (File musicPlaylistFolderList : musicPlaylistFolderLists) {
                d.deleteFile(musicPlaylistFolderList);
            }

            d.deleteFile(musicPlaylistFile);
        }
    }

    public boolean playlistExists(String id) {

        File f = new File("D:/Database/MusicPlayList/" + id);

        return f.exists();

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

    public void sendAllPlaylist(String id, TextChannel tc) {
        StringBuilder allMusicPlaylistURL = new StringBuilder();
        String oneLineMusicURL;

        int i = 1;

        while (exists(id, i)) {

            oneLineMusicURL = i + ". " + getTitle(getMusicUrl(id, i)) + "<" + getMusicUrl(id, i) + ">\n";
            allMusicPlaylistURL.append(oneLineMusicURL);

            if (allMusicPlaylistURL.toString().length() >= 1900) {
                tc.sendMessage("```md\n" + allMusicPlaylistURL + "```").delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();
                allMusicPlaylistURL = new StringBuilder();
            }

            i++;

        }

        tc.sendMessage("```md\n" + allMusicPlaylistURL.toString() + "```").delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();
    }

    public String getTitle(String youtubeUrl) {
        try {
            if (youtubeUrl != null) {
                URL url = new URL("http://www.youtube.com/oembed?url=" + youtubeUrl + "&format=json");
                return new JSONObject(IOUtils.toString(url, StandardCharsets.UTF_8)).getString("title");
            } else {
                return null;
            }
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteOneMusic(String id, int deleteMusic) {
        try {
            File f = new File("D:/Database/MusicPlayList/" + id);
            File[] fs = f.listFiles();

            for (int i = deleteMusic; i < fs.length; i++) {
                boolean musicExists = exists(id, i);

                if (musicExists) {
                    String nextMusicUrl = getMusicUrl(id, i + 1);

                    File musicFile = new File(f.getPath() + "/" + i + ".txt");
                    d.deleteFile(musicFile);

                    addMusic(id, i, nextMusicUrl);
                }

                if (!exists(id, i + 1)) {
                    break;
                }
            }

            return true;
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
            return false;
        }
    }

    public void setTitle(String id, String title) {
        try {
            File f = new File("D:/Database/MusicPlayList/" + id + "/title.txt");
            File g = new File("D:/Database/MusicPlayList/" + id + "/share.txt");
            File h = new File("D:/Database/MusicPlayList/" + id + "/owner.txt");

            if (title.equals("")) {
                System.gc();
                System.runFinalization();

                f.delete();
                g.delete();
                h.delete();

                setShare(id, false);
                return;
            }

            setShare(id, true);

            w.writeString(f, title);
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }
    }

    public void setShare(String id, boolean share) {
        try {
            File f = new File("D:/Database/MusicPlayList/" + id + "/share.txt");
            File g = new File("D:/Database/MusicPlayList/" + id + "/owner.txt");

            if (share) {
                w.writeString(f, "true");
                w.writeString(g, id);
            } else {
                if (f.exists()) f.delete();
            }
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }
    }

    public String getPlaylistTitle(String id) {

        File baseFile = new File("D:/Database/MusicPlayList/" + id + "/title.txt");

        if (baseFile.exists()) {
            return r.readString(baseFile);
        }

        return null;

    }

    public User getOwner(String id) {
        File g = new File("D:/Database/MusicPlayList/" + id + "/owner.txt");

        if (g.exists()) {
            return DefaultListener.jda.retrieveUserById(Objects.requireNonNull(r.readString(g))).complete();
        }

        return null;
    }

    public String searchPlayLists(String word) {

        File baseFile = new File("D:/Database/MusicPlayList/");
        File[] playlistFiles = baseFile.listFiles();

        List<String> playlists = new ArrayList<>();

        for (File playlistFile : playlistFiles) {

            if (playlistFile.isDirectory()) {

                File shareFile = new File(playlistFile.getPath() + "/share.txt");
                File titleFile = new File(playlistFile.getPath() + "/title.txt");

                if (shareFile.exists() && titleFile.exists()) {

                    if (Objects.equals(r.readString(shareFile), "true")
                            && Objects.requireNonNull(r.readString(titleFile)).contains(word.replace(" ", ""))) {

                        playlists.add(playlistFile.getName());

                    }

                }

            }

        }

        if (playlists.size() == 0) {

            return "검색 결과가 없습니다.";

        }

        StringBuilder returnValue = new StringBuilder();

        for (int i = 0; i < playlists.size(); i++) {

            returnValue.append(i + 1)
                    .append(". ")
                    .append(getPlaylistTitle(playlists.get(i)).replace("*", "").replace("_", ""))
                    .append(" - ")
                    .append(getOwner(playlists.get(i)).getAsTag())
                    .append("(코드 : ")
                    .append(playlists.get(i))
                    .append(")")
                    .append("\n");

        }

        return returnValue.toString();

    }

    public boolean exportPlaylist(String id) {

        try {

            CopyFile cf = new CopyFile();

            File fromFile = new File("D:/Database/MusicPlayList/" + id);
            File[] fs = fromFile.listFiles();

            File toFile = new File("D:/Database/MusicPlayList/" + getRandomCode());

            cf.copyTo(toFile, fs);
            return true;

        } catch (Exception e) {
            new ExceptionReport(e);
            // ignore
        }

        return false;

    }

    public String getRandomCode() {

        Random r = new Random();
        String[] charset = {
                "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z",
                "a", "b", "c", "d", "e", "f", "g", "h", "i",
                "j", "k", "l", "m", "n", "o", "p", "q", "r",
                "s", "t", "u", "v", "w", "x", "y", "z"
        };

        StringBuilder returnValue = new StringBuilder();

        for (int i = 0; i < 5; i++) {

            int v = r.nextInt(charset.length);
            returnValue.append(charset[v]);

        }

        File f = new File("D:/Database/MusicPlayList/" + returnValue.toString());

        if (f.exists()) {
            return getRandomCode();
        }

        return returnValue.toString();

    }

    /* Never used codes yet.
    public void editPlayCount(String id, String url) {

        if (isCoolTime(id)) return;
        File f = new File("D:/Database/MusicPlayCount/");

        if (!f.exists()) {
            boolean couldMadeDirectory = f.mkdir();

            if (!couldMadeDirectory) return;
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
            boolean couldMadeDirectory = f.mkdir();

            if (!couldMadeDirectory) return 0;
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

            return realTime - coolTimeL < 30000;
        } else {
            w.writeInt(f.getPath(), 0);
            return false;
        }
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

     */

    public void setCoolTime(String id) {
        File f = new File("D:/Database/Time/" + id + "M.txt");
        w.writeLong(f.getPath(), System.currentTimeMillis());
    }

}
