package com.DecupleProject.Contents;

import com.DecupleProject.Contents.RPG.Weapon.WeaponManager;
import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Ranking {

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

    public void sendWeaponRanking(TextChannel tc) {

        Map<String, Integer> weaponInfo = new HashMap<>();
        File f = new File("D:/Database/Weapon/");
        File[] weapons = f.listFiles();

        for (File weapon : weapons) {
            weaponInfo.put(weapon.getName(), new ReadFile().readInt(weapon.getPath() + "/Reinforce.txt"));
        }

        Iterator it = sortByValue(weaponInfo, true).iterator();
        StringBuilder rank = new StringBuilder("```md\n# 무기 랭킹\n\n");

        int count = 1;

        do {
            String temp = (String) it.next();
            User user = DefaultListener.jda.retrieveUserById(temp).complete();
            WeaponManager wp = new WeaponManager(user, tc);

            rank.append(count)
                    .append(". ")
                    .append(user.getAsTag().replace("*", "(별)").replace("_", "(언더바)"))
                    .append(" [")
                    .append(wp.getWeaponName())
                    .append("](★ ")
                    .append(wp.getReinforce())
                    .append(")\n");

            count++;

            if (it.hasNext()) {
                String nextTemp = (String) it.next();
                User nextUser = DefaultListener.jda.retrieveUserById(nextTemp).complete();
                WeaponManager nextWp = new WeaponManager(nextUser, tc);

                if (nextWp.getReinforce() == wp.getReinforce()) {
                    count--;
                }
            }
        } while (it.hasNext());

        rank.append("```");
        tc.sendMessage(rank.toString()).delay(2, TimeUnit.MINUTES).flatMap(Message::delete).queue();

   }

}
