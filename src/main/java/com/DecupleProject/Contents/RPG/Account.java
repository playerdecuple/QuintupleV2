package com.DecupleProject.Contents.RPG;

import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.LogWriter;
import com.DecupleProject.Core.WriteFile;
import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Account {

    ReadFile r = new ReadFile();
    WriteFile w = new WriteFile();

    String id;
    String name;

    private final TextChannel tc;

    public Account(String id, String name, TextChannel tc) {
        this.id = id;
        this.name = name;
        this.tc = tc;
    }

    /* Never used code yet.

    public boolean accountExists() {
        File accountFile = new File("D:/Database/Money/" + id + ".txt");

        return accountFile.exists();
    }

     */

    public long getNowMoneyForId() {
        File accountFile = new File("D:/Database/Money/" + id + ".txt");

        if (accountFile.exists()) {
            return r.readLong(accountFile.getPath());
        } else {
            return 0L;
        }
    }

    public void sendAccountMessage(TextChannel tc) {
        EmbedBuilder eb = new EmbedBuilder();

        long nowMoney = getNowMoneyForId();
        StringBuilder result = new StringBuilder();

        String moneyValue = String.valueOf(nowMoney);

        char[] c = new char[moneyValue.length()];
        for (int i = moneyValue.length() - 1; i >= 0; i--) {
            c[i] = moneyValue.charAt(i);
            result.insert(0, c[i]);

            if (i == moneyValue.length() - 4 && i != 0) {
                result.insert(0, "만 ");
            } else if (i == moneyValue.length() - 8 && i != 0) {
                result.insert(0, "억 ");
            } else if (i == moneyValue.length() - 12 && i != 0) {
                result.insert(0, "조 ");
            } else if (i == moneyValue.length() - 16 && i != 0) {
                result.insert(0, "경 ");
            }
        }

        result = new StringBuilder(result.toString().replace("0000경 ", "").replace("0000조 ", "").replace("0000억 ", "").replace("0000만 ", "").replace(" 0000", ""));

        eb.setTitle(name + "님의 계좌!");
        eb.setDescription(name + "님의 계좌에는 " + result + "플이 있어요!");

        tc.sendMessage(eb.build()).queue();
    }

    public void giveMoney(String targetId, long moneyAmount, boolean randomMode, boolean sendText) {

        EmbedBuilder eb = new EmbedBuilder();

        try {
            File moneyFile = new File("D:/Database/Money/" + targetId + ".txt");
            File timeFile = new File("D:/Database/Time/" + targetId + "T.txt");
            File abilityFile = new File("D:/Database/Item/Accessories/Ability/" + targetId + ".txt");

            if (moneyFile.exists() && timeFile.exists()) {
                long nowMoney = r.readLong(moneyFile);
                long lastTime = r.readLong(timeFile);
                long nowTime = System.currentTimeMillis();

                if (randomMode) {
                    if (nowTime - lastTime < 10000) {
                        int dTime = ((int) nowTime - (int) lastTime) / 1000;

                        eb.setDescription("돈을 더 받으려면, " + (10 - dTime) + "초만 기다려 주세요.");

                        tc.sendMessage(eb.build()).delay(5, TimeUnit.SECONDS)
                                .flatMap(Message::delete).queue();

                        return;
                    } else {
                        Random random = new Random();
                        moneyAmount = random.nextInt(14500) + 500;
                    }

                    w.writeLong(timeFile, nowTime);
                }

                if (abilityFile.exists()) {
                    int ability = r.readInt(abilityFile);

                    switch (ability) {
                        case 1:
                            moneyAmount = moneyAmount + 1000;
                            break;
                        case 11:
                            moneyAmount = moneyAmount + (int) (moneyAmount * 0.1);
                            break;
                        case 21:
                            moneyAmount = moneyAmount + (int) (moneyAmount * 0.25);
                            break;
                        case 31:
                            moneyAmount = moneyAmount * 2;
                            break;
                        default:
                            break;
                    }
                }

                long finalMoney = nowMoney + moneyAmount;

                if (finalMoney < 0) {
                    finalMoney = 0;
                }

                w.writeLong(moneyFile, finalMoney);

                if (randomMode) {
                    System.out.println(targetId + "(" + name + "): 자신의 계좌에 " + moneyAmount + "플 입금받음.");

                    eb.setTitle("계좌에 입금하였습니다.");
                    eb.addField("TargetName", name, true);
                    eb.addField("TargetId", targetId, true);
                    eb.addField("GaveMoney", moneyAmount + "", true);
                    eb.addField("FinalMoney", finalMoney + "", true);

                    LogWriter lW = new LogWriter(DefaultListener.jda);
                    lW.sendEmbed(eb.build());

                    eb = new EmbedBuilder();
                }

                eb.setTitle("소량의 금액을 지급해 드렸어요!");
                eb.addField("받으신 분", "<@" + targetId + ">", true);
                eb.addField("입금받은 금액", String.format("%,d", moneyAmount) + "플", true);
                eb.addField("현재 잔액", String.format("%,d", finalMoney) + "플", true);
                eb.setColor(Color.CYAN);

                if (sendText) {
                    tc.sendMessage(eb.build()).queue();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            LogWriter lW = new LogWriter(DefaultListener.jda);

            lW.sendMessage("```" + e.getMessage() + "```");
        }
    }

    public void sendMoneyRanking(Guild guild) {

        EmbedBuilder eb = new EmbedBuilder();
        Map<String, Long> ranking = new HashMap<>();
        JDA jda = DefaultListener.jda;

        File moneyFile = new File("D:/Database/Money/");
        File[] accountList = moneyFile.listFiles();

        for (File account : accountList) {

            String rankId = account.getName().replace(".txt", "");
            long money = r.readLong(account);

            if (guild == null) {
                ranking.put(rankId, money);
            } else {
                if (guild.isMember(jda.retrieveUserById(rankId).complete()))
                    ranking.put(rankId, money);
            }

        }

        if (ranking.size() == 0) {
            eb.setDescription("서버 내에 계좌를 생성하신 유저분이 하나도 없나 보네요.");
            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        }

        List<String> keySetList = new ArrayList<>(ranking.keySet());
        keySetList.sort((r1, r2) -> (ranking.get(r2).compareTo(ranking.get(r1))));

        StringBuilder rankingInfo = new StringBuilder("```md\n# 돈 랭킹\n\n");
        int count = 0;

        for (String id : keySetList) {

            long moneyValue = ranking.get(id);
            String tag = jda.retrieveUserById(id).complete().getAsTag().replace("_", "").replace("*", "");

            if (moneyValue == 0L || count > 9) break;

            StringBuilder result = new StringBuilder();

            char[] c = new char[String.valueOf(moneyValue).length()];
            for (int i = String.valueOf(moneyValue).length() - 1; i >= 0; i--) {
                c[i] = String.valueOf(moneyValue).charAt(i);
                result.insert(0, c[i]);

                if (i == String.valueOf(moneyValue).length() - 4 && i != 0) {
                    result.insert(0, "만 ");
                } else if (i == String.valueOf(moneyValue).length() - 8 && i != 0) {
                    result.insert(0, "억 ");
                } else if (i == String.valueOf(moneyValue).length() - 12 && i != 0) {
                    result.insert(0, "조 ");
                } else if (i == String.valueOf(moneyValue).length() - 16 && i != 0) {
                    result.insert(0, "경 ");
                }
            }

            String moneyFormat = result.toString()
                    .replace("0000경 ", "")
                    .replace("0000조 ", "")
                    .replace("0000억 ", "")
                    .replace("0000만 ", "")
                    .replace(" 0000", "");

            rankingInfo.append(count + 1).append(". [").append(tag).append("](").append(moneyFormat).append("플)\n");
            count++;

        }

        rankingInfo.append("```");
        tc.sendMessage(rankingInfo.toString()).delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();

    }

}
