package com.DecupleProject;

import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.LogWriter;
import com.DecupleProject.Core.WriteFile;
import com.DecupleProject.Listener.DefaultListener;
import com.DecupleProject.Listener.MusicListener;
import com.DecupleProject.Listener.OnlineGameListener;
import com.DecupleProject.Listener.ServerManagementListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Logger;

public class QuintupleMain {

    public static final String version = "Alpha 2.0";

    public static void main(String args[]) throws LoginException {

        // - 초기 설정 - //
        System.out.print("[QUINTUPLE] Input Quintuple Activate Mode : "); // GET QUINTUPLE ACTIVATE MODE;
        /*
         * If MODE = "test": Quintuple will be activated test mode.
         * If MODE = "real": Quintuple will be activated real mode.
         */

        Scanner sc = new Scanner(System.in);
        String v = sc.next();

        File f = new File("D:/Database/NowMode.txt");
        File t = new File("D:/Database/StartTime.txt");
        WriteFile w = new WriteFile();
        ReadFile r = new ReadFile();

        w.writeString(f, v); // Mode Setting
        w.writeLong(t, System.currentTimeMillis()); // save current time to calculate uptime.

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

        String testToken = "";
        String realToken = "";

        File testTokenFile = new File("D:/Database/TestToken.txt");
        File realTokenFile = new File("D:/Database/RealToken.txt");

        if (testTokenFile.exists() && realTokenFile.exists()) {
            testToken = r.readString(testTokenFile);
            realToken = r.readString(realTokenFile);
        } else {
            System.out.println("Bot couldn't find Token File. Please check database directory.");
        }

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

        String token = "";

        if (v.equalsIgnoreCase("real")) {
            token = realToken;

            System.out.println("[QUINTUPLE] Now Quintuple version : " + version);
            System.out.println("[QUINTUPLE] Quintuple, a part of PROJECT: DECUPLE, made by DECUPLE(데큐플#6056)");
        } else if (v.equalsIgnoreCase("test")) {
            token = testToken;

            System.out.println("[QUINTUPLE] Now Quintuple version : TEST " + version);
            System.out.println("[QUINTUPLE] Quintuple, a part of PROJECT: DECUPLE, made by DECUPLE(데큐플#6056#)");
        } else {
            main(args); // 재귀
        }

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ Build ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

        JDABuilder jdaBuilder = JDABuilder.createDefault(token)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_BANS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_EMOJIS)
                .setAutoReconnect(true)
                .setStatus(OnlineStatus.ONLINE)

                .addEventListeners(new DefaultListener(), new MusicListener(), new OnlineGameListener(), new ServerManagementListener());

        JDA jda = jdaBuilder.build();
        jda.getPresence().setActivity(Activity.playing(jda.getUsers().size() + " 분들과 함께"));

        LogWriter lW = new LogWriter(jda);
        lW.sendStartingLog();

    }

}
