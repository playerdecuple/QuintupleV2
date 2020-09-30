package com.DecupleProject.Contents.RPG.Weapon;

import com.DecupleProject.Contents.RPG.Account;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Random;

public class WeaponReinforce {

    private final Account ac;
    private final WeaponManager wp;

    public WeaponReinforce(User u, TextChannel t) {
        wp = new WeaponManager(u, t);
        ac = new Account(u);
    }

    public int reinforceWeapon() {
        long requireMoney = (100L * (long) wp.getReinforce()) + (50L * (long) wp.getReinforce());

        if (requireMoney > ac.getNowMoneyForId()) {
            return 0; // Lack of money
        }

        if (wp.getReinforce() == 100) {
            return -1; // can't reinforce
        }

        if (System.currentTimeMillis() - wp.getLastReinforcedTime() < 3000) {
            return -2;
        }

        if (!wp.basedFilesExists()) {
            return -3;
        }

        Random r = new Random();
        float rnd = r.nextFloat() * 100;

        if (rnd <= wp.getReinforcePercentage(1)) {
            wp.setWeaponReinforce(true, 1);
            wp.setLastReinforcedTime(true, 0L);
            return 1; // success
        } else if (rnd > wp.getReinforcePercentage(1) && rnd <= wp.getReinforcePercentage(2)) {
            wp.removeWeapon(1);
            return 2; // destroy
        } else {
            int rnd2 = r.nextInt(10);

            if (rnd2 <= 7) {
                return 3; // failure
            } else {
                wp.setWeaponReinforce(true, -1);
                return 4; // decrease
            }
        }
    }

}
