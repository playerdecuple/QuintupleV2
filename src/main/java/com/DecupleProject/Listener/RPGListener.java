package com.DecupleProject.Listener;

import com.DecupleProject.Contents.RPG.*;
import com.DecupleProject.Contents.RPG.Weapon.WeaponManager;
import com.DecupleProject.Contents.Ranking;
import com.DecupleProject.Core.CustomCommand;
import com.DecupleProject.Core.ExceptionReport;
import com.DecupleProject.Core.Util.EasyEqual;
import com.DecupleProject.Core.Util.LinkUtility;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RPGListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        try {

            // TODO : If playing game, return.

            EasyEqual e = new EasyEqual();

            User user = event.getAuthor();
            TextChannel tc = event.getTextChannel();
            Guild guild = event.getGuild();
            Message msg = event.getMessage();

            EmbedBuilder eb = new EmbedBuilder();

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            CustomCommand CC = new CustomCommand(user);
            String prefix = CC.getPrefixStr();

            String[] args;
            boolean prefixCheck = false;

            if (user.isBot()) return;

            if (msg.getContentRaw().substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
                args = msg.getContentRaw().substring(prefix.length()).split(" ");
                prefixCheck = true;
            } else {
                args = msg.getContentRaw().substring(1).split(" ");
            }

            Work work = new Work(tc, user);

            // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

            if (msg.getContentRaw().charAt(0) == 'Q' | msg.getContentRaw().charAt(0) == 'q' | msg.getContentRaw().charAt(0) == '.' | prefixCheck) {

                if (args.length < 1) return;

                Account ac = new Account(user.getId(), user.getName(), tc);

                if (e.eq(args[0], "아르바이트", "알바", "돈")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (args.length == 1) {
                        ac.giveMoney(user.getId(), 0, true, true);
                    } else if (e.eq(args[1], "랭킹", "랭크", "ranking", "rank")) {
                        if (args.length == 2) {
                            ac.sendMoneyRanking(null);
                        } else if (args.length == 3 && e.eq(args[2], "서버", "server", "우리", "이곳")) {
                            ac.sendMoneyRanking(guild);
                        }
                    }

                }

                if (e.eq(args[0], "도박", "배팅", "베팅", "Betting")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    BettingGame bt = new BettingGame(user);

                    if (args.length == 1) {
                        tc.sendMessage("배팅 금액을 정해 주세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        return;
                    }

                    long betValue = Long.parseLong(args[1]);
                    float bettingPercentage = 1f / (float) betValue * 500000f;

                    if (bettingPercentage > 1f) {
                        bettingPercentage = 0.5f;
                    }

                    if (ac.getNowMoneyForId() < betValue) {
                        tc.sendMessage("금액이 부족합니다.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        return;
                    }

                    boolean bettingResult = bt.normalBetting(betValue, bettingPercentage);
                    eb.addField("배팅 금액", ac.getMoneyForHangeul(betValue) + "플", true);
                    eb.addField("성공 확률", (bettingPercentage * 100f) + "%", true);

                    if (bettingResult) {
                        eb.setTitle("운이 좋군요.");
                        eb.setColor(Color.GREEN);

                        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    } else {
                        eb.setTitle("잃어버렸습니다.");
                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    }

                }

                if (e.eq(args[0], "올인", "전부", "allin")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    BettingGame bt = new BettingGame(user);

                    if (args.length != 1) {
                        return;
                    }

                    long betValue = ac.getNowMoneyForId();
                    float bettingPercentage = 1f / (float) betValue * 50000000f;

                    if (bettingPercentage > 1f) {
                        bettingPercentage = new Random().nextFloat();
                    }

                    boolean bettingResult = bt.normalBetting(betValue, bettingPercentage);
                    eb.addField("전 재산", ac.getMoneyForHangeul(betValue) + "플", true);
                    eb.addField("성공 확률", (bettingPercentage * 100f) + "%", true);

                    if (bettingResult) {
                        eb.setTitle("대박!");
                        eb.setColor(Color.GREEN);

                        tc.sendMessage(eb.build()).delay(30, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    } else {
                        eb.setTitle("처음부터 다시..");
                        tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                    }

                }

                if (e.eq(args[0], "파산", "삭제", "초기화")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (args.length != 1) {
                        return;
                    }

                    ac.giveMoney(user.getId(), ac.getNowMoneyForId() * -1L, false, false);

                    eb.setDescription("성공적으로 파산했어요.");
                    tc.sendMessage(eb.build()).delay(1, TimeUnit.MINUTES).flatMap(Message::delete).queue();

                }

                if (e.eq(args[0], "계좌")) {

                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();

                    if (args.length == 1) {
                        ac.sendAccountMessage(tc);
                    } else {

                        if (e.eq(args[1], "랭킹", "순위")) {
                            Ranking ranking = new Ranking();

                            if (e.eq(args[2], "길드")) {
                                ranking.sendMoneyRanking(tc, guild);
                            } else ranking.sendMoneyRanking(tc, e.eq(args[2], "관리자", "어드민", "팀뎈", "팀"));
                        }

                    }

                }

                if (e.eq(args[0], "벌목", "나무", "woodcut", "woodcutting")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    work.woodCutting();
                }

                if (e.eq(args[0], "낚시", "물고기", "fishing", "낚싯대", "횟감수획")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    work.fishing();
                }

                if (e.eq(args[0], "농사", "농작물", "수확물", "farming", "farm")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    work.farming();
                }

                if (e.eq(args[0], "제작", "제조", "produce")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    work.producing();
                }

                if (e.eq(args[0], "사냥", "사냥감", "hunt", "hunting")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    work.hunting();
                }

                if (e.eq(args[0], "숙련도", "숙련", "일")) {
                    if (Objects.requireNonNull(guild.getMember(DefaultListener.jda.getSelfUser())).hasPermission(Permission.MESSAGE_MANAGE))
                        tc.deleteMessageById(msg.getId()).queue();
                    Proficiency p = new Proficiency(user);

                    p.sendProficiencyInformation(tc);
                }

                if (e.eq(args[0], "무기")) {

                    WeaponManager wp = new WeaponManager(user, tc);

                    if (args.length == 1) {
                        wp.sendWeaponInfo();
                        return;
                    }

                    if (e.eq(args[1], "생성", "만들기", "추가")) {
                        if (args.length == 2) {
                            eb.setDescription("`.무기 생성 [이름]` 형식으로 작성해 주세요.");
                            eb.setColor(Color.red);
                            tc.sendMessage(eb.build()).queue();
                            return;
                        }

                        String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                        boolean created = wp.createWeapon(name);

                        if (!created) {
                            eb.setDescription("무기를 만들지 못했습니다.");
                            tc.sendMessage(eb.build()).queue();
                        }
                    }

                    if (e.eq(args[1], "파괴", "제거", "삭제")) {
                        wp.removeWeapon(0);
                    }

                    if (e.eq(args[1], "복원", "복구")) {

                        if (ac.getNowMoneyForId() >= 1000000000) {
                            ac.giveMoney(user.getId(), -1000000000, false, false);
                            wp.restoreWeapon();

                            tc.sendMessage("무기를 복구했습니다.").queue();
                        }

                    }

                    if (e.eq(args[1], "강화")) {

                        if (args.length == 2) {
                            wp.reinforceWeapon(false);
                        } else if (e.eq(args[2], "파괴방지", "파방", "실드")) {
                            wp.reinforceWeapon(true);
                        }

                    }


                    if (e.eq(args[1], "이미지", "Image", "이미지설정")) {

                        if (args.length == 3) {
                            boolean isLink = new LinkUtility().isURL(args[2]);

                            if (isLink) {
                                wp.setWeaponImage(args[2]);
                                eb.setDescription("무기의 [이미지](" + args[2] + ")를 바꾸었습니다.");
                                tc.sendMessage(eb.build()).queue();
                            }
                        }

                    }

                    if (e.eq(args[1], "랭킹", "랭크", "순위", "Ranking", "무기랭킹")) {

                        if (args.length == 2) {

                            Ranking r = new Ranking();
                            r.sendWeaponRanking(tc, false);

                        } else {

                            if (e.eq(args[2], "서버", "길드")) {

                                Ranking r = new Ranking();
                                r.sendWeaponRanking(tc, guild);

                            }

                            if (e.eq(args[2], "관리자", "어드민", "팀", "팀뎈")) {

                                Ranking r = new Ranking();
                                r.sendWeaponRanking(tc, true);

                            }

                        }

                    }

                }

                if (e.eq(args[0], "강화")) {

                    WeaponManager wp = new WeaponManager(user, tc);

                    if (args.length == 1) {
                        wp.reinforceWeapon(false);
                    } else if (e.eq(args[1], "파괴방지", "파방", "실드")) {
                        wp.reinforceWeapon(true);
                    }

                }

                if (e.eq(args[0], "레이드", "월드보스", "이벤트보스", "보스")) {
                    WorldBossRaid boss = new WorldBossRaid(user);
                    boss.attackWorldBoss(tc);
                }

                if (e.eq(args[0], "길드", "Guild")) {

                    GuildSystem gld = new GuildSystem(event.getJDA(), tc, user.getId());

                    if (args.length == 1) {
                        if (gld.hasGuild()) {
                            eb.setTitle("길드 : " + gld.getGuildName(gld.getGuildId(user.getId())));
                            eb.addField("길드장", event.getJDA().retrieveUserById(gld.getGuildId(user.getId())).complete().getAsTag(), true);
                            eb.addField("길드 레벨", gld.getGuildLevel(gld.getGuildId(user.getId())) + " 레벨 (티어 " + gld.getGuildTierFromLevel(gld.getGuildLevel(gld.getGuildId(user.getId()))) + ")", true);
                            eb.addField("길드 자금", new Account(user.getId(), "", tc).getMoneyForHangeul(gld.getGuildMoney()) + "플", true);
                            eb.setImage(gld.getGuildImage());
                            eb.setColor(Color.GREEN);

                            tc.sendMessage(eb.build()).queue();
                        } else {
                            tc.sendMessage("아직 가입한 길드가 없네요. `.길드 가입 [유저 태그]`를 이용해서 길드에 가입해 보세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                        }
                    } else {

                        if (e.eq(args[1], "초대")) {

                            String tag = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                            try {
                                gld.sendInviteRequest(Objects.requireNonNull(event.getJDA().getUserByTag(tag)).getId());
                            } catch (NullPointerException ex) {
                                tc.sendMessage("해당 유저를 찾을 수 없었어요. 태그를 다시 입력해 보세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            }

                        }

                        if (e.eq(args[1], "가입")) {

                            String tag = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                            try {
                                gld.sendJoinRequest(Objects.requireNonNull(event.getJDA().getUserByTag(tag)).getId());
                            } catch (NullPointerException ex) {
                                tc.sendMessage("해당 유저를 찾을 수 없었어요. 태그를 다시 입력해 보세요.").delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
                            }

                        }

                    }

                }

            }

        } catch (StringIndexOutOfBoundsException | IllegalStateException e) {
            // ignore
        } catch (Exception e) {
            new ExceptionReport(e, event.getAuthor(), event.getTextChannel());
            e.printStackTrace();
        }

    }

}
