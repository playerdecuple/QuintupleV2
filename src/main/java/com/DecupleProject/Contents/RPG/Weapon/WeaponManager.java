package com.DecupleProject.Contents.RPG.Weapon;

import com.DecupleProject.Core.DeleteFile;
import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.EasyEqual;
import com.DecupleProject.Core.Util.LinkUtility;
import com.DecupleProject.Core.WriteFile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.File;
import java.util.Random;

public class WeaponManager {

    public final File NAME_FILE;
    public final File REIN_FILE;
    public final File IMGE_FILE;
    public final File TIME_FILE;
    public final File STAT_FILE;
    public final File DESC_FILE;
    public final File DEAD_WEAPON_BASE_FILE;
    public final File DEAD_WEAPON_NAME_FILE;
    public final File DEAD_WEAPON_IMGE_FILE;
    public final File DEAD_WEAPON_REIN_FILE;
    public final File DEAD_WEAPON_STAT_FILE;
    public final File DEAD_WEAPON_DESC_FILE;
    private final User user;
    private final TextChannel tc;
    private final EmbedBuilder eb = new EmbedBuilder();
    private final File BASE_FILE;
    private final WriteFile w = new WriteFile();
    private final ReadFile r = new ReadFile();
    private final EasyEqual e = new EasyEqual();
    private final DeleteFile d = new DeleteFile();

    public WeaponManager(User u, TextChannel t) {
        this.user = u;
        this.tc = t;

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

        this.BASE_FILE = new File("D:/Database/Weapon/" + user.getId());

        String bPath = BASE_FILE.getPath();

        this.NAME_FILE = new File(bPath + "/WeaponName.txt");
        this.REIN_FILE = new File(bPath + "/Reinforce.txt");
        this.IMGE_FILE = new File(bPath + "/WeaponImage.txt");
        this.TIME_FILE = new File(bPath + "/LastReinforceTime.txt");
        this.STAT_FILE = new File(bPath + "/Status.txt");
        this.DESC_FILE = new File(bPath + "/Description.txt");

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

        this.DEAD_WEAPON_BASE_FILE = new File("D:/Database/DeadWeapon/" + user.getId());

        String dPath = DEAD_WEAPON_BASE_FILE.getPath();

        this.DEAD_WEAPON_NAME_FILE = new File(dPath + "/WeaponName.txt");
        this.DEAD_WEAPON_IMGE_FILE = new File(dPath + "/WeaponImage.txt");
        this.DEAD_WEAPON_REIN_FILE = new File(dPath + "/Reinforce.txt");
        this.DEAD_WEAPON_STAT_FILE = new File(dPath + "/Status.txt");
        this.DEAD_WEAPON_DESC_FILE = new File(dPath + "/Description.txt");

        if (basedFilesExists()) {
            backup();
        }

    }

    public void backup() {
        if (!DEAD_WEAPON_NAME_FILE.exists()) {
            w.writeString(DEAD_WEAPON_NAME_FILE, getWeaponName());
            w.writeString(DEAD_WEAPON_IMGE_FILE, getWeaponImage());
            w.writeString(DEAD_WEAPON_DESC_FILE, getDescription());
            w.writeInt(DEAD_WEAPON_REIN_FILE, getReinforce());
            w.writeInt(DEAD_WEAPON_STAT_FILE, getStatus());
        }
    }

    public boolean createWeapon(String weaponName) {
        if (!BASE_FILE.exists()) BASE_FILE.mkdir();
        if (!DEAD_WEAPON_BASE_FILE.exists()) DEAD_WEAPON_BASE_FILE.mkdir();

        if (!e.eq(weaponName, "") && !NAME_FILE.exists() && !REIN_FILE.exists() &&
                !IMGE_FILE.exists() && !TIME_FILE.exists()) {
            boolean nameSet = setWeaponName(weaponName, false);

            if (nameSet) {
                setWeaponReinforce(false, new Random().nextInt(2));
                setLastReinforcedTime(false, 0);
                setWeaponStatus(false, new Random().nextInt(6));
                setDescription("무기입니다.", false);

                eb.clear();

                eb.setTitle("대장장이가 무기를 만들어 주었습니다!");
                eb.addField("무기 이름", getWeaponName(), true);
                eb.addField("기본 강화", getReinforce() + "성", true);
                eb.addField("기본 스탯", "+ " + getStatus(), true);

                eb.setImage(getWeaponImage());
                eb.setColor(Color.GREEN);

                tc.sendMessage(eb.build()).queue();

                return true;
            }

            return false;

        }

        return false;
    }

    public boolean basedFilesExists() {
         return (BASE_FILE.exists() && DEAD_WEAPON_BASE_FILE.exists());
    }

    public String getWeaponName() {
        return basedFilesExists() ? r.readString(NAME_FILE) : "없음";
    }

    public int getReinforce() {
        return basedFilesExists() ? r.readInt(REIN_FILE) : 0;
    }

    public int getStatus() {
        return basedFilesExists() ? r.readInt(STAT_FILE) : 0;
    }

    public String getDescription() {
        if (!DESC_FILE.exists()) return "없음";
        return basedFilesExists() ? r.readString(DESC_FILE) : "없음";
    }

    public int getPlusStatusByWeaponReinforceValue() {
        return getReinforce() * 10000;
    }

    public long getRealStatus() {
        return getPlusStatusByWeaponReinforceValue() + getStatus();
    }

    public long getLastReinforcedTime() {
        if (TIME_FILE.exists()) {
            return r.readLong(TIME_FILE);
        } else {
            return 0L;
        }
    }

    public String getWeaponImage() {
        if (IMGE_FILE.exists()) {
            return r.readString(IMGE_FILE);
        } else {
            return "https://cdn.pixabay.com/photo/2019/10/25/20/59/dagger-4578137_960_720.png";
        }
    }

    public void setWeaponImage(String imageUrl) {
        if (!basedFilesExists()) return;

        LinkUtility l = new LinkUtility();
        if (l.isURL(imageUrl)) {
            w.writeString(IMGE_FILE, imageUrl);
        }
    }

    public boolean setWeaponName(String weaponName, boolean sendMessage) {
        eb.clear();

        if (!e.eq(weaponName, "")) {
            if (weaponName.length() > 24) {
                eb.setDescription("무기 이름은 24글자 이상이 될 수 없습니다.");
                tc.sendMessage(eb.build()).queue();
                return false;
            }

            w.writeString(NAME_FILE, weaponName);

            eb.setTitle("무기의 이름을 새로 깃들였습니다.");
            eb.setDescription("깃들였던 이름이 사라지고, 새로운 이름이 생겼습니다.");
            eb.setColor(Color.YELLOW);

            if (sendMessage) tc.sendMessage(eb.build()).queue();

            return true;
        }

        return false;
    }

    public boolean setDescription(String description, boolean sendMessage) {
        eb.clear();

        if (!e.eq(description, "") && DESC_FILE.exists()) {
            if (description.length() >= 1200) {
                eb.setDescription("무기 설명은 1200글자를 넘길 수 없습니다.");
                tc.sendMessage(eb.build()).queue();
            }

            w.writeString(DESC_FILE, description);

            eb.setDescription("무기 설명을 `" + description + "`으로 바꾸었습니다.");
            if (sendMessage) tc.sendMessage(eb.build()).queue();

            return true;
        }

        return false;
    }

    public void setWeaponReinforce(boolean add, int value) {
        if (!basedFilesExists()) return;

        if (add) {
            w.writeInt(REIN_FILE, r.readInt(REIN_FILE) + value);
        } else {
            w.writeInt(REIN_FILE, value);
        }
    }

    public void setWeaponStatus(boolean add, int value) {
        if (!basedFilesExists()) return;

        if (add) {
            w.writeInt(STAT_FILE, r.readInt(STAT_FILE) + value);
        } else {
            w.writeInt(STAT_FILE, value);
        }
    }

    public void setLastReinforcedTime(boolean now, long value) {
        if (!basedFilesExists()) return;

        if (now) {
            w.writeLong(TIME_FILE, System.currentTimeMillis());
        } else {
            w.writeLong(TIME_FILE, value);
        }
    }

    public void removeWeapon(int removeReason) {
        eb.clear();

        if (!basedFilesExists()) return;

        switch (removeReason) {
            case -1: // 무기 삭제가 보호되어 있을 경우
                eb.setTitle("무기 삭제 불가능!");
                eb.setDescription("이 무기는 `무기 삭제`가 봉인되어 있습니다.");
                eb.setColor(Color.RED);

                tc.sendMessage(eb.build()).queue();
                return;
            case 0: // 무기를 사용자가 삭제했을 경우.
                d.deleteFile(BASE_FILE);
                d.deleteFile(DEAD_WEAPON_BASE_FILE);

                eb.setTitle("무기를 파괴했습니다..");
                eb.setDescription("무기를 다시 만드려면, `.무기 생성 [이름]`을 입력해 주세요.");
                eb.setColor(Color.RED);

                tc.sendMessage(eb.build()).queue();
                return;
            case 1: // 무기가 강화 실패로 인해 파괴되었을 경우.
                d.deleteFile(BASE_FILE);

                eb.setTitle("무기가 파괴되었습니다..");
                eb.setDescription("하지만 걱정 마세요! 일부 금액을 낸다면, 복구할 수 있어요. 무기를 다시 만드려면 `.무기 생성 [이름]`을 입력해 주세요. (이래도 무기 복구는 할 수 있어요.)");
                eb.setImage(getWeaponImage());
                eb.setColor(Color.RED);
                eb.setFooter(user.getAsTag(), user.getAvatarUrl());

                tc.sendMessage(eb.build()).queue();
                return;
        }

        eb.clear();
    }

    public void restoreWeapon() {
        if (!BASE_FILE.exists()) BASE_FILE.mkdir();

        String weaponName = r.readString(DEAD_WEAPON_NAME_FILE);
        int reinforce = r.readInt(DEAD_WEAPON_REIN_FILE);
        String image = r.readString(DEAD_WEAPON_IMGE_FILE);
        int status = r.readInt(DEAD_WEAPON_STAT_FILE);
        String description = r.readString(DEAD_WEAPON_DESC_FILE);

        setWeaponName(weaponName, false);
        setWeaponStatus(false, status);
        setWeaponImage(image);
        setWeaponReinforce(false, reinforce);
        setDescription(description, false);
    }

    public String getWeaponRank() {

        if (!BASE_FILE.exists()) return "없음";

        if (getReinforce() == 0) return "일반(F)";
        if (getReinforce() > 0 && getReinforce() < 6) return "일반(E)";
        if (getReinforce() > 5 && getReinforce() < 11) return "레어(D)";
        if (getReinforce() > 10 && getReinforce() < 16) return "레어(C)";
        if (getReinforce() > 15 && getReinforce() < 21) return "레어(B)";
        if (getReinforce() > 20 && getReinforce() < 26) return "에픽(A)";
        if (getReinforce() > 25 && getReinforce() < 31) return "에픽(AA)";
        if (getReinforce() > 30 && getReinforce() < 36) return "에픽(AAA)";
        if (getReinforce() > 35 && getReinforce() < 41) return "유니크(S-)";
        if (getReinforce() > 40 && getReinforce() < 46) return "유니크(S)";
        if (getReinforce() > 45 && getReinforce() < 51) return "유니크(SS)";
        if (getReinforce() > 50 && getReinforce() < 56) return "유니크(SSS)";
        if (getReinforce() > 55 && getReinforce() < 61) return "슈페리얼(I)";
        if (getReinforce() > 60 && getReinforce() < 66) return "슈페리얼(II)";
        if (getReinforce() > 65 && getReinforce() < 71) return "엑설런트(V)";
        if (getReinforce() > 70 && getReinforce() < 76) return "엑설런트(W)";
        if (getReinforce() > 75 && getReinforce() < 81) return "마블러스(X)";
        if (getReinforce() > 80 && getReinforce() < 86) return "마블러스(RX)";
        if (getReinforce() > 85 && getReinforce() < 91) return "엔드리스(ES)";
        if (getReinforce() > 90 && getReinforce() < 96) return "엔드리스(EX)";
        if (getReinforce() > 95 && getReinforce() < 100) return "레전더리(L)";

        return "데큐플(DX)";

    }

    /* Never used code yet.
    public String getWeaponRank(int weaponReinforce) {

        if (!BASE_FILE.exists()) return "없음";

        if (weaponReinforce == 0) return "일반(F)";
        if (weaponReinforce > 0 && weaponReinforce < 6) return "일반(E)";
        if (weaponReinforce > 5 && weaponReinforce < 11) return "레어(D)";
        if (weaponReinforce > 10 && weaponReinforce < 16) return "레어(C)";
        if (weaponReinforce > 15 && weaponReinforce < 21) return "레어(B)";
        if (weaponReinforce > 20 && weaponReinforce < 26) return "에픽(A)";
        if (weaponReinforce > 25 && weaponReinforce < 31) return "에픽(AA)";
        if (weaponReinforce > 30 && weaponReinforce < 36) return "에픽(AAA)";
        if (weaponReinforce > 35 && weaponReinforce < 41) return "유니크(S-)";
        if (weaponReinforce > 40 && weaponReinforce < 46) return "유니크(S)";
        if (weaponReinforce > 45 && weaponReinforce < 51) return "유니크(SS)";
        if (weaponReinforce > 50 && weaponReinforce < 56) return "유니크(SSS)";
        if (weaponReinforce > 55 && weaponReinforce < 61) return "슈페리얼(I)";
        if (weaponReinforce > 60 && weaponReinforce < 66) return "슈페리얼(II)";
        if (weaponReinforce > 65 && weaponReinforce < 71) return "엑설런트(V)";
        if (weaponReinforce > 70 && weaponReinforce < 76) return "엑설런트(W)";
        if (weaponReinforce > 75 && weaponReinforce < 81) return "마블러스(X)";
        if (weaponReinforce > 80 && weaponReinforce < 86) return "마블러스(RX)";
        if (weaponReinforce > 85 && weaponReinforce < 91) return "엔드리스(ES)";
        if (weaponReinforce > 90 && weaponReinforce < 96) return "엔드리스(EX)";
        if (weaponReinforce > 95 && weaponReinforce < 100) return "레전더리(L)";

        return "데큐플(DX)";

    }
     */

    public double getReinforcePercentage(int type) {

        if (!BASE_FILE.exists()) return 0D;

        // Weapon reinforce success percentage
        // <R> = Reinforce, <S> = Success percentage.
        // <S> = 1 / <R> * 7

        double successPercentage = (1D / (double) getReinforce()) * (double) 7 * 100D;
        double decreasePercentage = (double) getReinforce() / 10D;
        double destroyPercentage = (double) getReinforce() / 20D;

        if (successPercentage > 100D) successPercentage = 100D;
        if (100D - successPercentage < decreasePercentage) decreasePercentage = 0D;
        if (100D - successPercentage - decreasePercentage < destroyPercentage) destroyPercentage = 0D;

        switch(type) {
            case 1: // Type 1 = Get success percentage.
                return successPercentage;
            case 2:
                return destroyPercentage;
            default:
                return 0D;
        }

    }

    public Color getWeaponColor(int weaponReinforce) {
        if (weaponReinforce > 0 && weaponReinforce < 6) return Color.GRAY;
        if (weaponReinforce > 5 && weaponReinforce < 21) return Color.BLUE;
        if (weaponReinforce > 20 && weaponReinforce < 36) return Color.MAGENTA;
        if (weaponReinforce > 35 && weaponReinforce < 56) return Color.YELLOW;
        if (weaponReinforce > 55 && weaponReinforce < 66) return Color.RED;
        if (weaponReinforce > 65 && weaponReinforce < 76) return Color.ORANGE;
        if (weaponReinforce > 75 && weaponReinforce < 86) return Color.PINK;
        if (weaponReinforce > 85 && weaponReinforce < 96) return Color.BLACK;
        if (weaponReinforce > 95 && weaponReinforce < 100) return Color.GREEN;

        return Color.LIGHT_GRAY;
    }


    public void sendWeaponInfo() {

        if (basedFilesExists()) {
            eb.clear();

            eb.setTitle("무기 : " + getWeaponName());
            eb.setDescription(getDescription());
            eb.addField("강화", getWeaponRank() + " " + getReinforce() + "성, " + String.format("%.2f", getReinforcePercentage(1)) + "%", true);
            eb.addField("스테이터스", "공격력 + " + getRealStatus(), true);
            // TODO : Add 'EXP'.
            eb.setImage(getWeaponImage());
            eb.setFooter(user.getAsTag(), user.getAvatarUrl());
            eb.setColor(getWeaponColor(getReinforce()));
        } else {
            eb.setDescription(user.getAsTag() + "님은 무기가 없네요. `.무기 생성 [이름]`으로 무기를 생성해 보시겠어요?");
        }

        tc.sendMessage(eb.build()).queue();

    }

    public void reinforceWeapon() {
        WeaponReinforce wr = new WeaponReinforce(user, tc);
        eb.clear();

        switch (wr.reinforceWeapon()) {
            case -3:
                eb.setDescription("무기가 없네요. `.무기 생성 [이름]`으로 무기를 만들어 보세요.");
                eb.setFooter(user.getAsTag(), user.getAvatarUrl());
                tc.sendMessage(eb.build()).queue();
                break;
            case -2:
                eb.setDescription("강화를 할 수 있기까지 " + (int) ((3000L - (System.currentTimeMillis() - getLastReinforcedTime())) / 1000) + "초 남았습니다.");
                eb.setFooter(user.getAsTag(), user.getAvatarUrl());
                tc.sendMessage(eb.build()).queue();
                break;
            case -1:
                eb.setTitle("전설적인 무기가 강화를 거부합니다.");
                eb.setDescription("이미 100성 강화가 완료된 무기입니다.");
                eb.setColor(Color.YELLOW);
                eb.setImage(getWeaponImage());

                tc.sendMessage(eb.build()).queue();
                break;
            case 0:
                eb.setDescription("돈이 부족하여 대장장이가 강화를 거절했습니다.");
                eb.setFooter(user.getAsTag(), user.getAvatarUrl());
                tc.sendMessage(eb.build());
                break;
            case 1:
                eb.setTitle("『 최고의 결과로군! 』");
                eb.setFooter(user.getAsTag(), user.getAvatarUrl());
                eb.addField("무기 이름", getWeaponName(), true);
                eb.addField("현재 강화 정보", getWeaponRank() + " " + getReinforce() + "성 (확률 " + String.format("%.2f", getReinforcePercentage(1)) + "%)", true);
                eb.addField("현재 스테이터스", "+ " + getRealStatus(), true);
                eb.setImage(getWeaponImage());
                eb.setColor(getWeaponColor(getReinforce()));
                tc.sendMessage(eb.build()).queue();
                break;
            case 2:
                break;
            case 3:
                eb.setTitle("『 안타깝군 그래. 』");
                eb.setFooter(user.getAsTag(), user.getAvatarUrl());
                eb.addField("무기 이름", getWeaponName(), true);
                eb.addField("현재 강화 정보", getWeaponRank() + " " + getReinforce() + "성 (확률 " + String.format("%.2f", getReinforcePercentage(1)) + "%)", true);
                eb.addField("현재 스테이터스", "+ " + getRealStatus(), true);
                eb.setImage(getWeaponImage());
                eb.setColor(Color.YELLOW);
                tc.sendMessage(eb.build()).queue();
                break;
            case 4:
                eb.setTitle("『 조금 금이 간 것 같네만. 』");
                eb.setDescription("강화에 실패하여, 강화 성공 횟수가 차감되었습니다.");
                eb.setFooter(user.getAsTag(), user.getAvatarUrl());
                eb.addField("무기 이름", getWeaponName(), true);
                eb.addField("현재 강화 정보", getWeaponRank() + " " + getReinforce() + "성 (확률 " + String.format("%.2f", getReinforcePercentage(1)) + "%)", true);
                eb.addField("현재 스테이터스", "+ " + getRealStatus(), true);
                eb.setImage(getWeaponImage());
                eb.setColor(Color.ORANGE);
                tc.sendMessage(eb.build()).queue();
                break;
        }
    }


}
