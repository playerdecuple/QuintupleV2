package com.DecupleProject.Core.ServerManager;

import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.LinkUtility;
import com.DecupleProject.Core.WriteFile;
import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServerManager {

    private final Guild guild;
    private final Member member;
    private final EmbedBuilder eb = new EmbedBuilder();

    private final Member bot;

    public ServerManager(Guild guild, Member member) {
        this.guild = guild;
        this.member = member;

        File serverDirectory = new File("D:/Database/Servers/");
        File guildDirectory = new File(serverDirectory.getPath() + "/" + guild.getId() + "/");

        if (!serverDirectory.exists()) {
            boolean serverDirectoryMade = serverDirectory.mkdir();

            if (!serverDirectoryMade) {
                System.out.println("Bot couldn't made a directory. Path : " + serverDirectory.getPath());
            }
        }

        if (!guildDirectory.exists()) {
            boolean guildDirectoryMade = guildDirectory.mkdir();

            if (!guildDirectoryMade) {
                System.out.println("Bot couldn't made a directory. Path : " + guildDirectory.getPath());
            }
        }

        this.bot = guild.getMember(DefaultListener.jda.getSelfUser());
    }

    /* Never used code yet.
    public User getServerOwner() {
        if (guild.getOwner() != null) return guild.getOwner().getUser();
        return null;
    }
     */

    public void deleteMessages(TextChannel tc, int value) {

        if (member.hasPermission(Permission.MESSAGE_MANAGE) && bot.hasPermission(Permission.MESSAGE_MANAGE)) {

            if (value < 2 || value > 100) {
                eb.setDescription("채팅은 한 번에 2개에서 100개까지만 삭제할 수 있어요.");
                tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                return;
            }

            MessageHistory messageHistory = tc.getHistory();
            List<Message> messagesList = messageHistory.retrievePast(value).complete();

            try {
                tc.deleteMessages(messagesList).queue();

                eb.setDescription(value + "개의 메시지를 삭제했어요.");
                tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
            } catch (IllegalArgumentException e) {
                tc.sendMessage("2주가 지난 메시지가 포함되어 있어 삭제할 수 없어요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
            } catch (Exception e) {
                // ignore
            }

        } else {

            eb.setDescription("권한이 없네요. `메시지 관리` 권한이 있어야 이 기능을 사용할 수 있어요.");
            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();

        }

    }

    public void setWelcomeMessage(TextChannel tc, String welcomeMessage) {

        WriteFile w = new WriteFile();
        File messageInfo = new File("D:/Database/Servers/" + guild.getId() + "/WelcomeMessage.txt");

        if (!member.hasPermission(Permission.ADMINISTRATOR)) return;

        if (welcomeMessage.replace(" ", "").equals("")) {
            if (messageInfo.exists()) {
                boolean deleted = messageInfo.delete();

                if (!deleted) {
                    eb.setDescription("서버 환영 메시지를 제거하는 데에 실패했습니다.");
                    tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    return;
                }
            }
            return;
        }

        w.writeString(messageInfo, welcomeMessage);

        tc.sendMessage("서버 환영 메시지를 설정했습니다.").delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();
        tc.sendMessage(makeEmbed(welcomeMessage)).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();

    }

    public void sendWelcomeMessage(User user) {

        File messageInfo = new File("D:/Database/Servers/" + guild.getId() + "/WelcomeMessage.txt");
        ReadFile r = new ReadFile();

        if (!messageInfo.exists()) return;

        String script = r.readString(messageInfo);

        assert script != null;
        user.openPrivateChannel().complete().sendMessage(makeEmbed(script)).queue();

    }

    public void kickMember(TextChannel tc, Member member, String reason) {

        if (member.hasPermission(Permission.KICK_MEMBERS) && bot.hasPermission(Permission.KICK_MEMBERS)) {
            member.kick(reason).queue();
            eb.setDescription(member.getUser().getAsTag() + "님을 추방했어요. 다시 초대할 경우 이 서버에 다시 들어올 수 있어요.");
            tc.sendMessage(eb.build()).delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();
        } else {
            eb.setDescription("권한이 없네요. `멤버 추방` 권한이 있어야 이 기능을 사용할 수 있어요.");
            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        }

    }

    public void banMember(TextChannel tc, Member member, String reason, int delDays) {

        if (member.hasPermission(Permission.BAN_MEMBERS) && bot.hasPermission(Permission.BAN_MEMBERS)) {
            member.ban(delDays, reason).queue();
            eb.setDescription(member.getUser().getAsTag() + "님을 차단했어요. 그리고 " + member.getUser().getAsTag() + "님이 " + delDays + "일간 보낸 메시지를 삭제했어요.");
            tc.sendMessage(eb.build()).delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();
        } else {
            eb.setDescription("권한이 없네요. `멤버 차단` 권한이 있어야 이 기능을 사용할 수 있어요.");
            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        }

    }

    public void attention(TextChannel tc, Member user, String attentionInfo) {

        if (member.hasPermission(Permission.MANAGE_CHANNEL)) {
            user.getUser().openPrivateChannel().complete().sendMessage(member.getUser().getAsTag() + "님이 " + user.getAsMention() + "님에게 다음과 같이 경고하였습니다." +
                    "\n```" + attentionInfo + "```").queue();

            File attentionCountFile = new File("D:/Database/Servers/" + user.getGuild().getId() + "/" + user.getUser().getId() + ".txt");
            int attentionCount = attentionCountFile.exists() ? new ReadFile().readInt(attentionCountFile) + 1 : 1;

            new WriteFile().writeInt(attentionCountFile, attentionCount);
        } else {
            eb.setDescription("권한이 없네요. `채널 관리` 권한이 있어야 이 기능을 사용할 수 있어요.");
            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        }

    }

    public MessageEmbed makeEmbed(String script) {

        String[] ln = script.split("\n");

        for (String s : ln) {

            if (s.charAt(0) == '>') {
                eb.setTitle(s.substring(1)); // Set title
            }

            if (s.charAt(0) == '*') {
                eb.setDescription(s.substring(1)); // Set Description
            }

            if (s.charAt(0) == '=') {
                s = s.substring(1);
                String[] fLn = s.split("-");

                if (fLn.length > 2) {

                    boolean inLine = false;

                    try {
                        if (fLn[2].contains(".t.")) inLine = true;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // Ignore
                    }

                    eb.addField(fLn[0], fLn[1], inLine); // Add Field

                }
            }

            if (s.charAt(0) == '#') {

                String url = s.substring(1);

                LinkUtility l = new LinkUtility();

                if (l.isURL(url)) eb.setImage(url); // Set Image

            }

            if (s.charAt(0) == '^') {

                String url = s.substring(1);

                LinkUtility l = new LinkUtility();

                if (l.isURL(url)) eb.setThumbnail(url); // Set Thumbnail

            }

            if (s.charAt(0) == '_') {

                s = s.substring(1);
                String[] fLn = s.split("-");

                eb.setFooter(fLn[0]);
                LinkUtility l = new LinkUtility();

                if (fLn.length == 2 && l.isURL(fLn[1])) {
                    eb.setFooter(fLn[0], fLn[1]);
                }

            }

            if (s.charAt(0) == 'R' | s.charAt(0) == 'r') {

                s = s.substring(1);
                String[] colors = s.replace(" ", "").split(",");

                if (colors.length == 3) {
                    int red = Integer.parseInt(colors[0]);
                    int green = Integer.parseInt(colors[1]);
                    int blue = Integer.parseInt(colors[2]);

                    eb.setColor(new Color(red, green, blue));
                }

            }

        }

        return eb.build();
    }

}
