package com.DecupleProject.Contents.RPG;

import com.DecupleProject.Contents.RPG.Weapon.WeaponManager;
import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.TextTool;
import com.DecupleProject.Core.WriteFile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class WorldBossRaid {

    private final User user;
    private final File bossInfo = new File("D:/Database/WorldBoss.txt");

    private final ReadFile r = new ReadFile();
    private final WriteFile w = new WriteFile();

    public WorldBossRaid(User user) {
        this.user = user;

        if (!bossInfo.exists()) w.writeLong(bossInfo, getMaxHP());
    }

    public long getWorldBossHP() {
        return r.readLong(bossInfo);
    }

    public long getMaxHP() {
        return 100000000000000L;
    }

    public boolean between(long a, long b, long c) {
        // this method returns true if a ≤ b < c.
        return b >= a && b < c;
    }

    public int getPhase() {
        long HP = getWorldBossHP();

        // 99999999999999 - 1 Phase
        // 6250000000000 - 2 Phase
        // 195312500000 - 3 Phase
        // 6103515625 - 4 Phase
        // 190734863 - 5 Phase
        // 95367431 - Final Phase

        if (between(6250000000000L, HP, 99999999999999L)) {
            return 1;
        }

        if (between(195312500000L, HP, 6250000000000L)) {
            return 2;
        }

        if (between(6103515625L, HP, 195312500000L)) {
            return 3;
        }

        if (between(190734863L, HP, 6103515625L)) {
            return 4;
        }

        if (between(95367431L, HP, 190734863L)) {
            return 5;
        }

        if (between(0L, HP, 95367431L)) {
            return 6;
        }

        return 0;
    }

    public String getPhaseMessage() {
        int phase = getPhase();

        switch (phase) {
            case 0:
                return "월드 보스를 물리쳤습니다. 곧 새로운 괴물이 등장할 것입니다.";
            case 1:
                return "월드 보스가 아직도 매우 강력한 상태에 놓여 있습니다.";
            case 2:
                return "월드 보스의 강력한 피부가 사라지고, 내부에 있는 물질이 보이기 시작합니다.";
            case 3:
                return "월드 보스의 조금 약해진 모습이 여러분들을 더욱더 강하게 만듭니다.";
            case 4:
                return "월드 보스가 많이 약해졌습니다. 곧 물리칠 수 있을지도 모릅니다.";
            case 5:
                return "월드 보스의 심부가 모습을 드러냈습니다! 월드 보스는 곧 물러날 것입니다!";
            default:
                return "이건 개발 상의 오류거나, 무능한 데큐플이 이걸 염두해 두지 못한 탓입니다. 지금 가서 문의로 혼내 주세요.";
        }
    }

    public void setBossHealth(boolean setMode, long value) {

        if (setMode) {
            w.writeLong(bossInfo, value);
        } else {
            w.writeLong(bossInfo, r.readLong(bossInfo) + value);
        }

    }

    public void attackWorldBoss(TextChannel tc) {

        final WeaponManager weapon = new WeaponManager(user, tc);
        final EmbedBuilder eb = new EmbedBuilder();
        final File timeFile = new File("D:/Database/Time/" + user.getId() + "M.txt");

        long nowTime = System.currentTimeMillis();
        long lastTime = r.readLong(timeFile);

        if (nowTime - lastTime < 75000) {
            int remainingSec = 75 - ((int) (nowTime - lastTime) / 1000);
            eb.setDescription("월드 보스를 공격할 수 있을 때까지 " + remainingSec + "초 남았습니다.");
            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
            return;
        }

        long givingMoney;

        if (!weapon.basedFilesExists()) {
            eb.setDescription("무기가 없으므로, 이벤트 보스 토벌을 할 수 없습니다.");
            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
            return;
        }

        long dmg = weapon.getRandomDamage();
        givingMoney = dmg + new TextTool().nextLong(getPhase() * 10000000L);

        setBossHealth(false, dmg * -1);

        // TODO : Gives the item for the phase.

        eb.setTitle("이벤트 보스를 공격했습니다!");
        eb.setDescription(getPhase() + " 페이즈, " + getPhaseMessage());
        String bossName = "이학코라티스 습트레아 터미너그";
        eb.addField("이벤트 보스 이름", bossName, true);
        eb.addField("남은 체력", new TextTool().addKoreanUnitsToNumber(getWorldBossHP()) +
                "(" + String.format("%.2f", ((double) getWorldBossHP() / (double) getMaxHP() * 100D)) + "%)", true);
        eb.addField("입힌 피해(얻은 P)", new TextTool().addKoreanUnitsToNumber(dmg), true);
        eb.setImage("https://cdn.discordapp.com/attachments/678777011610714143/732045270656876595/426734.jpg");
        eb.setColor(Color.GREEN);

        Account ac = new Account(user);
        ac.giveMoney(user.getId(), givingMoney, false, false);

        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        w.writeLong(timeFile, nowTime);

    }

}
