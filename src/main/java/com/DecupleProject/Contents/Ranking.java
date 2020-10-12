package com.DecupleProject.Contents;

import com.DecupleProject.Contents.RPG.Account;
import com.DecupleProject.Contents.RPG.Weapon.WeaponManager;
import com.DecupleProject.Core.DatabaseManager;
import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.python.indexer.Def;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Ranking {

    private static final String[] ADMIN_ID = {"419116887469981708", "356716238056980482", "495820475579105281", "612290725886820366",
            "470152608225689602", "276574658789244928", "540014609910726676", "609592230986121228", "735737363942342708",
            "555249779047923712"};

    public static List<String> sortByValue(final Map map, boolean reverse) {
        List<String> list = new ArrayList<>(map.keySet());

        list.sort((Comparator) (o1, o2) -> {
            Object v1 = map.get(o1);
            Object v2 = map.get(o2);

            return ((Comparable) v2).compareTo(v1);
        });

        if (reverse) Collections.reverse(list);
        return list;
    }

    public boolean isTeamDecuple(User user) {
        String userId = user.getId();

        for (String ID : ADMIN_ID) {
            if (ID.equals(userId)) return true;
        }

        return false;
    }

    public void sendMoneyRanking(TextChannel tc, boolean admin) {

        Map<String, Long> moneyInfo = new HashMap<>();
        File f = new File("D:/Database/Money");
        File[] accounts = f.listFiles();

        for (File account : accounts) {
            if (admin && isTeamDecuple(DefaultListener.jda.retrieveUserById(account.getName().replace(".txt", "")).complete())) {
                moneyInfo.put(account.getName().replace(".txt", ""), new ReadFile().readLong(account));
            } else if (!admin && !isTeamDecuple(DefaultListener.jda.retrieveUserById(account.getName().replace(".txt", "")).complete())) {
                moneyInfo.put(account.getName().replace(".txt", ""), new ReadFile().readLong(account));
            }
        }

        Iterator it = sortByValue(moneyInfo, false).iterator();
        StringBuilder rank = new StringBuilder("```md\n# 자금 랭킹\n\n");

        int count = 1;

        do {

            if (moneyInfo.isEmpty()) {
                rank.append("* 랭킹 정보가 없습니다.");
                break;
            }

            String temp = (String) it.next();
            User user = DefaultListener.jda.retrieveUserById(temp).complete();
            Account ac = new Account(user);

            if (ac.getNowMoneyForId() == 0L) break;

            rank.append(count)
                    .append(". ")
                    .append(user.getAsTag().replace("*", "(별)").replace("_", "(언더바)"))
                    .append(" [자금](")
                    .append(ac.getMoneyForHangeul(ac.getNowMoneyForId()))
                    .append(" 플)\n");

            count++;

            if (it.hasNext()) {
                String nextTemp = (String) it.next();
                User nextUser = DefaultListener.jda.retrieveUserById(nextTemp).complete();
                Account nextAc = new Account(nextUser);

                if (ac.getNowMoneyForId() == nextAc.getNowMoneyForId()) {
                    count--;
                }
            }

            if (count > 10) break;

        } while (it.hasNext());

        rank.append("```");
        tc.sendMessage(rank.toString()).delay(2, TimeUnit.MINUTES).flatMap(Message::delete).queue();

    }

    public void sendMoneyRanking(TextChannel tc, Guild guild) {

        Map<String, Long> moneyInfo = new HashMap<>();
        File f = new File("D:/Database/Money");
        File[] accounts = f.listFiles();

        for (File account : accounts) {
            if (!isTeamDecuple(DefaultListener.jda.retrieveUserById(account.getName().replace(".txt", "")).complete())) {
                moneyInfo.put(account.getName().replace(".txt", ""), new ReadFile().readLong(account));
            }
        }

        Iterator it = sortByValue(moneyInfo, false).iterator();
        StringBuilder rank = new StringBuilder("```md\n# 자금 랭킹\n\n");

        int count = 1;

        do {

            if (moneyInfo.isEmpty()) {
                rank.append("* 랭킹 정보가 없습니다.");
                break;
            }

            String temp = (String) it.next();
            User user = DefaultListener.jda.retrieveUserById(temp).complete();
            Account ac = new Account(user);

            if (ac.getNowMoneyForId() == 0L) break;

            rank.append(count)
                    .append(". ")
                    .append(user.getAsTag().replace("*", "(별)").replace("_", "(언더바)"))
                    .append(" [자금](")
                    .append(ac.getMoneyForHangeul(ac.getNowMoneyForId()))
                    .append(" 플)\n");

            count++;

            if (it.hasNext()) {
                String nextTemp = (String) it.next();
                User nextUser = DefaultListener.jda.retrieveUserById(nextTemp).complete();
                Account nextAc = new Account(nextUser);

                if (ac.getNowMoneyForId() == nextAc.getNowMoneyForId()) {
                    count--;
                }
            }

            if (count > 10) break;

        } while (it.hasNext());

        rank.append("```");
        tc.sendMessage(rank.toString()).delay(2, TimeUnit.MINUTES).flatMap(Message::delete).queue();

    }

    public void sendWeaponRanking(TextChannel tc, boolean admin) {

        Map<String, Integer> weaponInfo = new HashMap<>();
        File f = new File("D:/Database/Weapon/");
        File[] weapons = f.listFiles();

        for (File weapon : weapons) {
            if (admin && isTeamDecuple(DefaultListener.jda.retrieveUserById(weapon.getName()).complete())) {
                weaponInfo.put(weapon.getName(), new ReadFile().readInt(weapon.getPath() + "/Reinforce.txt"));
            } else {
                if (!admin && !isTeamDecuple(DefaultListener.jda.retrieveUserById(weapon.getName()).complete())) {
                    weaponInfo.put(weapon.getName(), new ReadFile().readInt(weapon.getPath() + "/Reinforce.txt"));
                }
            }
        }

        Iterator it = sortByValue(weaponInfo, false).iterator();
        StringBuilder rank = new StringBuilder("```md\n# 무기 랭킹\n\n");

        int count = 0;

        do {

            count++;

            if (weaponInfo.isEmpty()) {
                rank.append("* 랭킹 정보가 없습니다.");
                break;
            }

            String temp = (String) it.next();
            User user = DefaultListener.jda.retrieveUserById(temp).complete();
            WeaponManager wp = new WeaponManager(user, tc);

            if (wp.getReinforce() == 0) break;

            rank.append(count)
                    .append(". ")
                    .append(user.getAsTag().replace("*", "(별)").replace("_", "(언더바)"))
                    .append(" [")
                    .append(wp.getWeaponName())
                    .append("](★ ")
                    .append(wp.getReinforce())
                    .append(")\n");

            if (count > 10) break;

        } while (it.hasNext());

        rank.append("```");
        tc.sendMessage(rank.toString()).delay(2, TimeUnit.MINUTES).flatMap(Message::delete).queue();

    }

    public void sendWeaponRanking(TextChannel tc, Guild guild) {

        Map<String, Integer> weaponInfo = new HashMap<>();
        File f = new File("D:/Database/Weapon/");
        File[] weapons = f.listFiles();

        for (File weapon : weapons) {
            if (!isTeamDecuple(DefaultListener.jda.retrieveUserById(weapon.getName()).complete())) {
                weaponInfo.put(weapon.getName(), new ReadFile().readInt(weapon.getPath() + "/Reinforce.txt"));
            }
        }

        Iterator it = sortByValue(weaponInfo, false).iterator();
        StringBuilder rank = new StringBuilder("```md\n# 무기 랭킹\n\n");

        int count = 0;

        do {

            count++;

            if (weaponInfo.isEmpty()) {
                rank.append("* 랭킹 정보가 없습니다.");
                break;
            }

            String temp = (String) it.next();
            User user = DefaultListener.jda.retrieveUserById(temp).complete();
            WeaponManager wp = new WeaponManager(user, tc);

            if (guild == null || guild.isMember(user)) {

                if (wp.getReinforce() == 0) break;

                rank.append(count)
                        .append(". ")
                        .append(user.getAsTag().replace("*", "(별)").replace("_", "(언더바)"))
                        .append(" [")
                        .append(wp.getWeaponName())
                        .append("](★ ")
                        .append(wp.getReinforce())
                        .append(")\n");

            } else {
                count--;
            }

            if (count > 10) break;

        } while (it.hasNext());

        rank.append("```");
        tc.sendMessage(rank.toString()).delay(2, TimeUnit.MINUTES).flatMap(Message::delete).queue();

    }

}
