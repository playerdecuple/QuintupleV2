package com.DecupleProject.Core;

import com.DecupleProject.Core.Util.EasyEqual;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDatabaseService {

    private final User user;
    private final String baseFilePath = "D:/Database/";

    private final ReadFile r = new ReadFile();
    private final EasyEqual e = new EasyEqual();

    public UserDatabaseService(User user) {
        this.user = user;
    }

    public String getUserId() {
        return user.getId();
    }

    public String getUserTextFile() {
        return user.getId() + ".txt";
    }

    public String getUserTag() {
        return user.getAsTag();
    }

    public boolean fileExists(String path) {
        File f = new File(path);
        return f.exists();
    }

    public int getLastAttendanceCheckTime() {
        String path = baseFilePath + "/AttendanceCheck/" + getUserTextFile();
        return fileExists(path) ? r.readInt(path) : 20051105;
    }

    public int getAttendanceRank() {
        String path = baseFilePath + "/AttendanceCheck/Rank/" + getUserTextFile();
        return fileExists(path) ? r.readInt(path) : 20051105;
    }

    public int getAuthority() {
        String path = baseFilePath + "/Authority/" + getUserTextFile();
        return fileExists(path) ? r.readInt(path) : 0;
    }

    public int getEXP() {
        String path = baseFilePath + "/EXP/" + getUserTextFile();
        return fileExists(path) ? r.readInt(path) : 0;
    }

    public int getLevel() {
        String path = baseFilePath + "/Level/" + getUserTextFile();
        return fileExists(path) ? r.readInt(path) : 0;
    }

    public String[] getInventory() {
        String path = baseFilePath + "/Inventory/" + getUserTextFile();

        if (!fileExists(path)) return null;
        String inventory = r.readString(path);

        return inventory != null ? inventory.split(",") : null;
    }

    public long getMoney() {
        String path = baseFilePath + "/Money/" + getUserTextFile();
        return fileExists(path) ? r.readLong(path) : 0L;
    }

    public String[] getUserPlayLists() {
        List<String> playlists = new ArrayList<>();

        String path = baseFilePath + "/MusicPlayList/" + getUserId();
        File file = new File(path);

        if (fileExists(path)) {

            for (File f : file.listFiles()) {
                if (!e.eq(f.getName(), "owner.txt", "share.txt", "title.txt")) {
                    playlists.add(f.getName().replace(".txt", ""));
                }
            }

            return playlists.toArray(new String[playlists.size()]);

        }

        return null;
    }

    public Map<Integer, Integer> getProficiency() {

        Map<Integer, Integer> map = new HashMap<>();

        String path = baseFilePath + "/Proficiency/" + getUserId();
        File file = new File(path);

        if (fileExists(path)) {
            for (File f : file.listFiles()) {
                map.put(Integer.parseInt(f.getName().replace(".txt", "")), r.readInt(f));
            }

            return map;
        }

        return null;

    }

    public String getWeaponName() {
        String path = baseFilePath + "/Weapon/" + getUserId() + "/WeaponName.txt";
        return fileExists(path) ? r.readString(path) : "None";
    }

    public long getLastReinforceTime() {
        String path = baseFilePath + "/Weapon/" + getUserId() + "/LastReinforceTime.txt";
        return fileExists(path) ? r.readLong(path) : 0L;
    }

    public String getWeaponImage() {
        String path = baseFilePath + "/Weapon/" + getUserId() + "/WeaponImage.txt";
        return fileExists(path) ? r.readString(path) : "https://cdn.pixabay.com/photo/2019/10/25/20/59/dagger-4578137_960_720.png";
    }

    public int getWeaponReinforce() {
        String path = baseFilePath + "/Weapon/" + getUserId() + "/Reinforce.txt";
        return fileExists(path) ? r.readInt(path) : 0;
    }

    public int getRealWeaponStatus() {
        String path = baseFilePath + "/Weapon/" + getUserId() + "/Status.txt";
        return fileExists(path) ? r.readInt(path) : 0;
    }

}
