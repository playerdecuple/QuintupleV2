package com.DecupleProject.Contents;

import com.DecupleProject.Contents.RPG.Account;
import com.DecupleProject.Contents.RPG.UserStatus;
import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.WriteFile;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ResumedEvent;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;

public class AttendanceCheck {
    
    private String id;
    private String name;
    private TextChannel tc;
    private final ReadFile r = new ReadFile();
    private final WriteFile w = new WriteFile();
    
    public AttendanceCheck(String id, TextChannel tc, String name) {
        this.id = id;
        this.name = name;
        this.tc = tc;
    }
    
    public void attendance() {
        Account a = new Account(id, name, tc);

        File lastCheckFile = new File("D:/Database/AttendanceCheck/" + id + ".txt");
        String lastDate = r.readString(lastCheckFile.getPath());

        String yS = "0";
        String mS = "0";
        String dS = "0";

        long giveMoney = 100000;

        try {
            yS = lastDate.substring(0, 4);
            mS = lastDate.substring(4, 6);
            dS = lastDate.substring(6, 8);
        } catch (NullPointerException e) {

        }

        int year = Integer.parseInt(yS);
        int month = Integer.parseInt(mS);
        int day = Integer.parseInt(dS);

        long nowTime = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String today = dateFormat.format(nowTime);

        int yearN = Integer.parseInt(today.substring(0, 4));
        int monthN = Integer.parseInt(today.substring(4, 6));
        int dayN = Integer.parseInt(today.substring(6, 8));

        EmbedBuilder eb = new EmbedBuilder();

        File rankFolder = new File("D:/Database/AttendanceCheck/Rank");
        if (!rankFolder.exists()) rankFolder.mkdir();

        File rankFile = new File("D:/Database/AttendanceCheck/Rank/" + id + ".txt");

        /* bug occurred:
         * if yesterday's month different for today's month, Quintuple set Rank to 0(reset Error).
         * Beta v1.2.286r2
         */

        // ㅡㅡㅡㅡㅡㅡㅡㅡ Added in Beta v1.2.286r4 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

        boolean isDayYesterday = false;
        boolean isMonthYesterday = false;
        boolean isYearYesterday = false;
        boolean isLeapYear = false;

        boolean result = false;

        if ((yearN % 4 == 0 && yearN % 100 != 0) || year % 400 == 0) {
            isLeapYear = true;
        }

        if (year == yearN - 1 && month == 12 && day == 31 && monthN == 1 && dayN == 1) // when year added
            isYearYesterday = true;

        // Month 1, 3, 5, 7, 8, 10 (when month added)
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10) {
            if (monthN == month + 1) {
                isMonthYesterday = true;

                if (day == 31 && dayN == 1) {
                    isDayYesterday = true;
                }
            }
        }

        // Month 4, 6, 9, 11 (when month added)
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            if (monthN == month + 1) {
                isMonthYesterday = true;

                if (day == 30 && dayN == 1) {
                    isDayYesterday = true;
                }
            }
        }

        // Month 2 (when month added)
        if (month == 2) {
            if (monthN == month + 1) {
                isMonthYesterday = true;

                if (isLeapYear) {
                    if (day == 29 && dayN == 1) {
                        isDayYesterday = true;
                    }
                } else {
                    if (day == 28 && dayN == 1) {
                        isDayYesterday = true;
                    }
                }
            }
        }

        // Month 12 (when month added)
        if (month == 12) {
            if (monthN == 1) {
                isMonthYesterday = true;

                if (day == 31 && dayN == 1) {
                    isDayYesterday = true;
                }
            }
        }

        // Final Setting
        if (yearN == year) isYearYesterday = true;
        if (monthN == month) isMonthYesterday = true;
        if (dayN == day + 1) isDayYesterday = true;

        if (isYearYesterday && isMonthYesterday && isDayYesterday) result = true;


        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

        if (year == yearN && month == monthN && day == dayN) {
            eb.setDescription(name + "님, 오늘은 이미 출석하셨어요.");
            giveMoney = 0;
            tc.sendMessage(eb.build()).queue();
        } else {
            giveMoney = 100000;

            if (rankFile.exists()) {
                if ((yearN == year && monthN == month && dayN == day + 1) || result) { // 'result == true' added in 'Beta v1.2.286r4'.
                    w.writeInt(rankFile.getPath(), r.readInt(rankFile.getPath()) + 1);
                    giveMoney = giveMoney + r.readInt(rankFile.getPath()) * 10000;
                    eb.setTitle(r.readInt(rankFile.getPath()) + "일째 출석 중!");
                    eb.addField("보너스 금액",  r.readInt(rankFile.getPath()) * 10000 + "플", false);
                } else if (year == yearN && month == monthN && day == dayN) {
                    return;
                } else {
                    w.writeInt(rankFile.getPath(), 0);
                }
            } else {
                w.writeInt(rankFile.getPath(), 0);
            }

            eb.setColor(Color.CYAN);
            eb.setDescription(name + "님, 오늘 출석해 주셔서 감사합니다! 10만 플을 드릴게요.\n" +
                    "오늘은 " + monthN + "월 " + dayN + "일입니다!");
            tc.sendMessage(eb.build()).queue();

            UserStatus us = new UserStatus(id, tc);
            us.setEXP(id, 50, true, true);

            w.writeString(lastCheckFile.getPath(), today);
        }

        if (giveMoney == 0) return;
        a.giveMoney(id, giveMoney, false, false);
    }
    
}
