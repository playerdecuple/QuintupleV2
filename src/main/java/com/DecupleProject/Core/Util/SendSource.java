package com.DecupleProject.Core.Util;

import com.DecupleProject.Core.ExceptionReport;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class SendSource {

    private final User user;

    public SendSource(User user) {
        this.user = user;
    }

    public void sendSource(String path, int startLine, int endLine) {
        String BASE_PATH = "D:/QuintupleV2/src/main/java/com/DecupleProject";
        File readFile = new File(BASE_PATH + "/" + path);

        if (!readFile.exists()) {
            return;
        }

        List<String> list = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(readFile);

            while (scanner.hasNextLine()) {
                list.add(scanner.nextLine());
            }
        } catch (IOException e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }

        StringBuilder message = new StringBuilder();

        for (int l = 0; l < list.size(); l++) {
            if (l + 1 >= startLine && l + 1 <= endLine) {
                message.append(String.format("%03d", l + 1)).append("|").append(list.get(l)).append("\n");
            }
        }

        try {
            if (message.toString().length() >= 1850) {
                user.openPrivateChannel().complete().sendMessage("The code cannot be transmitted because the number of characters exceeds 1850 characters.").queue();
                return;
            }
            user.openPrivateChannel().complete().sendMessage("***" + "com.DecupleProject." + path.replace("/", ".") +
                    "***, __Ln " + startLine + " ~ " + endLine + "__\n```java\n" + message.toString() + "```").queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String returnSource(String path, int startLine, int endLine) {
        String BASE_PATH = "D:/QuintupleV2/src/main/java/com/DecupleProject";
        File readFile = new File(BASE_PATH + "/" + path);

        if (!readFile.exists()) {
            return "Bot couldn't sent source code(s).";
        }

        List<String> list = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(readFile);

            while (scanner.hasNextLine()) {
                list.add(scanner.nextLine());
            }
        } catch (IOException e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }

        StringBuilder message = new StringBuilder();

        for (int l = 0; l < list.size(); l++) {
            if (l + 1 >= startLine && l + 1 <= endLine) {
                message.append(String.format("%03d", l + 1)).append("|").append(list.get(l)).append("\n");
            }
        }

        try {
            if (message.toString().length() >= 1850) {
                return "The code cannot be transmitted because the number of characters exceeds 1850 characters.";
            }

            return "***" + "com.DecupleProject." + path.replace("/", ".") +
                    "***, __Ln " + startLine + " ~ " + endLine + "__\n```java\n" + message.toString() + "```";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

}
