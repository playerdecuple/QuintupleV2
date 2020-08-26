package com.DecupleProject.Contents.RPG;

import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.LogWriter;
import com.DecupleProject.Core.WriteFile;
import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import sun.rmi.runtime.Log;

import java.awt.*;
import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Account {

    ReadFile r = new ReadFile();
    WriteFile w = new WriteFile();

    String id;
    String name;

    private TextChannel tc;

    public Account(String id, String name, TextChannel tc) {
        this.id = id;
        this.name = name;
        this.tc = tc;
    }

    private void createAccount() {
        File accountFile = new File("D:/Database/Money/" + id + ".txt");
        File timerFile = new File("D:/Database/Time/" + id + "T.txt");
        File investFile1 = new File("D:/Database/Invest/Count/1/" + id + ".txt");
        File investFile2 = new File("D:/Database/Invest/Count/2/" + id + ".txt");
        File investFile3 = new File("D:/Database/Invest/Count/3/" + id + ".txt");
        File investFile4 = new File("D:/Database/Invest/Count/4/" + id + ".txt");
        File investFile5 = new File("D:/Database/Invest/Count/5/" + id + ".txt");

        if (accountFile.exists()) {
            return;
        } else {
            System.out.println(id + ": 새로운 계좌 만듬.");

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("새로운 계좌를 만들었습니다.");
            eb.addField("TargetId", id, true);

            LogWriter lW = new LogWriter(DefaultListener.jda);
            lW.sendEmbed(eb.build());

            try {
                w.writeInt(accountFile.getPath(), 0);
                w.writeInt(timerFile.getPath(), 0);
                w.writeInt(investFile1.getPath(), 0);
                w.writeInt(investFile2.getPath(), 0);
                w.writeInt(investFile3.getPath(), 0);
                w.writeInt(investFile4.getPath(), 0);
                w.writeInt(investFile5.getPath(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }
    }

    public boolean accountExists() {
        File accountFile = new File("D:/Database/Money/" + id + ".txt");

        return accountFile.exists();
    }

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

        if (accountExists()) {
            long nowMoney = getNowMoneyForId();
            String result = "";

            String value = String.valueOf(nowMoney);

            char c[] = new char[value.length()];
            for (int i = value.length() - 1; i >= 0; i--) {
                c[i] = value.charAt(i);
                result = c[i] + result;

                if (i == value.length() - 4 && i != 0) {
                    result = "만 " + result;
                } else if (i == value.length() - 8 && i != 0) {
                    result = "억 " + result;
                } else if (i == value.length() - 12 && i != 0) {
                    result = "조 " + result;
                } else if (i == value.length() - 16 && i != 0) {
                    result = "경 " + result;
                }
            }

            result = result.replace("0000경 ", "").replace("0000조 ", "").replace("0000억 ", "").replace( "0000만 ", "").replace(" 0000", "");

            eb.setTitle(name + "님의 계좌!");
            eb.setDescription(name + "님의 계좌에는 " + result + "플이 있어요!");

            tc.sendMessage(eb.build()).queue();
        } else {
            createAccount();

            if (accountExists()) {
                eb.setDescription("성공적으로 " + name + "님의 계좌를 만들었습니다!");

                tc.sendMessage(eb.build()).queue();
                return;
            } else {
                eb.setDescription("성공적으로 " + name + "님의 계좌를 만들었습니다!");

                tc.sendMessage(eb.build()).queue();
                return;
            }
        }
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
                        case 1: moneyAmount = moneyAmount + 1000;
                            break;
                        case 11: moneyAmount = moneyAmount + (int) (moneyAmount * 0.1);
                            break;
                        case 21: moneyAmount = moneyAmount + (int) (moneyAmount * 0.25);
                            break;
                        case 31: moneyAmount = moneyAmount * 2;
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
    
}
