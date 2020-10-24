package com.DecupleProject.API.Game;

import com.DecupleProject.Core.ExceptionReport;
import com.DecupleProject.Core.Util.GetJSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

// D

public class LeagueOfLegends {

    private final String apiKey = "RGAPI-968258bf-9891-49e0-a09e-93d8a08fa5fc";
    private final GetJSON g = new GetJSON();
    private final String summonerName;
    private final String summonerId;

    private final String LOL_VERSION = "10.21.1";

    public LeagueOfLegends(String summonerName) throws Exception {

        this.summonerName = summonerName;
        this.summonerId = getSummonerId(summonerName);

    }

    public void sendInfo(TextChannel tc, boolean nowPlaying) throws Exception {

        if (!nowPlaying) {
            EmbedBuilder eb = new EmbedBuilder();

            String encodedSummonerName = URLEncoder.encode(summonerName, "UTF-8");

            String summonerInfoUrl = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + encodedSummonerName + "?api_key=" + apiKey;
            String summonerInfoBody = g.getJsonByUrl(summonerInfoUrl);

            JsonParser j = new JsonParser();
            JsonObject body = (JsonObject) j.parse(summonerInfoBody);

            String summonerName = body.get("name").getAsString();
            String summonerNameUTF = URLEncoder.encode(summonerName, "UTF-8");
            int summonerLevel = body.get("summonerLevel").getAsInt();
            int profileNumber = body.get("profileIconId").getAsInt();

            eb.setTitle("리그 오브 레전드 : " + summonerName, "https://www.op.gg/summoner/userName=" + summonerNameUTF);

            // Ln 1

            eb.addField("소환사 레벨", String.valueOf(summonerLevel), false);

            // Ln 2

            eb.addField("모스트 챔피언", "```" + getMostChampInfo(summonerId) + "```", false);

            // Ln 3

            eb.addField("솔로 랭크", "```" + getTier(summonerId, 0) + "```", false);
            eb.addField("자유 랭크", "```" + getTier(summonerId, 1) + "```", false);
            eb.addField("TFT 랭크", "```" + getTier(summonerId, 2) + "```", false);

            // Image and other information

            eb.setThumbnail(getThumbnail(summonerId));
            eb.setColor(Color.CYAN);

            eb.setFooter("아이콘", "http://ddragon.leagueoflegends.com/cdn/" + LOL_VERSION + "/img/profileicon/" + profileNumber + ".png");

            tc.sendMessage(eb.build()).delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();
        } else {
            sendNowPlayInfo(tc, summonerId);
        }

    }

    public void sendNowPlayInfo(TextChannel tc, String summonerId) throws Exception {

        try {
            String spectateUrl = "https://kr.api.riotgames.com/lol/spectator/v4/active-games/by-summoner/" + summonerId + "?api_key=" + apiKey;
            String spectateBody = g.getJsonByUrl(spectateUrl);

            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(spectateBody);

            JsonArray participants = object.getAsJsonArray("participants");
            JsonArray bannedChampions = object.getAsJsonArray("bannedChampions");

            for (int i = 0; i < participants.size(); i++) {
                JsonElement element = participants.get(i);

                String summonerName = element.getAsJsonObject().get("summonerName").getAsString();

                int summonerChamp = element.getAsJsonObject().get("championId").getAsInt();
                int summonerTeam = element.getAsJsonObject().get("teamId").getAsInt() / 100;

                Color teamColor = Color.WHITE;

                if (summonerTeam == 1) teamColor = Color.CYAN;
                if (summonerTeam == 2) teamColor = Color.RED;

                String summonerSpell1 = getSpellById(element.getAsJsonObject().get("spell1Id").getAsInt());
                String summonerSpell2 = getSpellById(element.getAsJsonObject().get("spell2Id").getAsInt());

                JsonObject perks = element.getAsJsonObject().get("perks").getAsJsonObject();
                JsonArray perksId = perks.getAsJsonArray("perkIds");

                StringBuilder perksStr = new StringBuilder("<주 룬> : ");

                for (int j = 0; j <= 7; j++) {
                    JsonElement perksIdElement = perksId.get(j);
                    perksStr.append(getRuneNameById(perksIdElement.getAsInt())).append(" / ");

                    if (j == 2) {
                        perksStr.append("\n<보조 룬> : ");
                    }

                    if (j == 4) {
                        perksStr.append("\n<보너스 룬> : ");
                    }
                }

                String perksResult = perksStr.toString();

                sendInfo(tc, teamColor, summonerName, summonerSpell1, summonerSpell2, perksResult, summonerChamp);

                Thread.sleep(1000);


            }

            StringBuilder bannedStr = new StringBuilder(":no_entry_sign: **밴 당한 챔피언들**\n");

            for (int i = 0; i < bannedChampions.size(); i++) {
                JsonElement e = bannedChampions.get(i);

                try {
                    bannedStr.append(getChampionNameById(e.getAsJsonObject().get("championId").getAsInt()));
                } catch (IndexOutOfBoundsException ex) {
                    new ExceptionReport(ex);
                    bannedStr.append("없음");
                }

                if (i != bannedChampions.size() - 1) {
                    bannedStr.append(", ");
                }

                if (i == 4) bannedStr.append("\n");
            }

            int playTime = object.get("gameLength").getAsInt();

            int playSec = playTime % 60;
            int playMin = playTime / 60;

            bannedStr.append("\n\n:watch: **게임 진행 시간**\n").append(playMin).append("분 ").append(playSec).append("초");
            tc.sendMessage(bannedStr.toString()).delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();
        } catch (FileNotFoundException e) {
            new ExceptionReport(e);
            EmbedBuilder eb = new EmbedBuilder();

            eb.setDescription(summonerName + "님은 게임을 하고 계시지 않은 것 같네요. 챔피언 선택 화면일 수도 있어요.");
            tc.sendMessage(eb.build()).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        }
    }

    public void sendInfo(TextChannel tc, Color color, String summonerName, String spell1, String spell2, String runes, int championId) throws Exception {

        EmbedBuilder eb = new EmbedBuilder();
        String encodedSummonerName = URLEncoder.encode(summonerName, "UTF-8");

        String summonerInfoUrl = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + encodedSummonerName + "?api_key=" + apiKey;
        String summonerInfoBody = g.getJsonByUrl(summonerInfoUrl);

        JsonParser j = new JsonParser();
        JsonObject body = (JsonObject) j.parse(summonerInfoBody);

        String summonerNameR = body.get("name").getAsString();
        String summonerNameUTF = URLEncoder.encode(summonerNameR, "UTF-8");
        int summonerLevel = body.get("summonerLevel").getAsInt();
        int profileNumber = body.get("profileIconId").getAsInt();

        String summonerId = getSummonerId(summonerNameR);

        eb.setTitle("현재 플레이 중 : " + summonerNameR, "https://www.op.gg/summoner/userName=" + summonerNameUTF);

        // Ln 1

        eb.addField("소환사 레벨", String.valueOf(summonerLevel), false);

        // Ln 2

        eb.addField("현재 챔피언", "```" + getPlayedChampInfo(summonerId, championId) + "```", false);

        // Ln 4

        String leagueURL = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + summonerId + "?api_key=" + apiKey;
        String jsonMain = g.getJsonByUrl(leagueURL);

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(jsonMain);

        JsonElement lE_Solo = null;
        JsonElement lE_Flex = null;

        try {
            if (jsonArray.get(0).getAsJsonObject().get("queueType").getAsString().equals("RANKED_SOLO_5x5")) {
                lE_Solo = jsonArray.get(0);
            } else if (jsonArray.get(1).getAsJsonObject().get("queueType").getAsString().equalsIgnoreCase("RANKED_SOLO_5x5")) {
                lE_Solo = jsonArray.get(1);
            }
        } catch (IndexOutOfBoundsException e) {
            new ExceptionReport(e);
        }

        try {
            if (jsonArray.get(0).getAsJsonObject().get("queueType").getAsString().equals("RANKED_FLEX_SR")) {
                lE_Flex = jsonArray.get(0);
            } else if (jsonArray.get(1).getAsJsonObject().get("queueType").getAsString().equalsIgnoreCase("RANKED_FLEX_SR")) {
                lE_Flex = jsonArray.get(1);
            }
        } catch (IndexOutOfBoundsException e) {
            // ignore
        }

        String soloRankTier = "UNRANKED";
        String flexRankTier = "UNRANKED";

        if (lE_Solo != null) {
            String sTier = lE_Solo.getAsJsonObject().get("tier").getAsString();
            String sDivs = lE_Solo.getAsJsonObject().get("rank").getAsString();
            String sLPts = lE_Solo.getAsJsonObject().get("leaguePoints").getAsString();

            soloRankTier = sTier + " " + sDivs + " " + sLPts + " LP";
        }

        if (lE_Flex != null) {
            String fTier = lE_Flex.getAsJsonObject().get("tier").getAsString();
            String fDivs = lE_Flex.getAsJsonObject().get("rank").getAsString();
            String fLPts = lE_Flex.getAsJsonObject().get("leaguePoints").getAsString();

            flexRankTier = fTier + " " + fDivs + " " + fLPts + " LP";
        }

        eb.addField("솔로 랭크", "```" + soloRankTier + "```", false);
        eb.addField("자유 랭크", "```" + flexRankTier + "```", false);

        // Ln 5

        eb.addField("스펠(D)", spell1, true);
        eb.addBlankField(true);
        eb.addField("스펠(F)", spell2, true);

        // Ln 6

        eb.addField("룬", "```md\n" + runes + "```", false);

        // Image and other information

        eb.setThumbnail(getThumbnail(summonerId));
        eb.setColor(color);

        eb.setFooter("아이콘", "http://ddragon.leagueoflegends.com/cdn/" + LOL_VERSION + "/img/profileicon/" + profileNumber + ".png");

        tc.sendMessage(eb.build()).delay(3, TimeUnit.MINUTES).flatMap(Message::delete).queue();

    }

    public String getSummonerId(String name) throws Exception {

        try {
            name = URLEncoder.encode(name, "UTF-8");

            String summonerInfoUrl = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + name + "?api_key=" + apiKey;
            String summonerInfoBody = g.getJsonByUrlForUserMode(summonerInfoUrl);

            JsonParser j = new JsonParser();
            JsonObject body = (JsonObject) j.parse(summonerInfoBody);

            return body.get("id").getAsString();
        } catch (NullPointerException e) {
            return null;
        }

    }

    public String getTier(String summonerId, int mode) throws Exception {

        String leagueURL = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + summonerId + "?api_key=" + apiKey;
        String jsonMain = g.getJsonByUrl(leagueURL);

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(jsonMain);

        JsonElement lE_Solo = null;
        JsonElement lE_Flex = null;

        try {
            if (jsonArray.get(0).getAsJsonObject().get("queueType").getAsString().equals("RANKED_SOLO_5x5")) {
                lE_Solo = jsonArray.get(0);
            } else if (jsonArray.get(1).getAsJsonObject().get("queueType").getAsString().equalsIgnoreCase("RANKED_SOLO_5x5")) {
                lE_Solo = jsonArray.get(1);
            }
        } catch (IndexOutOfBoundsException e) {
            // ignore
        }

        try {
            if (jsonArray.get(0).getAsJsonObject().get("queueType").getAsString().equals("RANKED_FLEX_SR")) {
                lE_Flex = jsonArray.get(0);
            } else if (jsonArray.get(1).getAsJsonObject().get("queueType").getAsString().equalsIgnoreCase("RANKED_FLEX_SR")) {
                lE_Flex = jsonArray.get(1);
            }
        } catch (IndexOutOfBoundsException e) {
            // ignore
        }

        String soloRankTier = "UNRANKED";
        String flexRankTier = "UNRANKED";
        String tft_RankTier = "UNRANKED";

        if (lE_Solo != null) {
            String sTier = lE_Solo.getAsJsonObject().get("tier").getAsString();
            String sDivs = lE_Solo.getAsJsonObject().get("rank").getAsString();
            String sLPts = lE_Solo.getAsJsonObject().get("leaguePoints").getAsString();
            int sWins = lE_Solo.getAsJsonObject().get("wins").getAsInt();
            int sLoses = lE_Solo.getAsJsonObject().get("losses").getAsInt();

            soloRankTier = sTier + " " + sDivs + " " + sLPts + " LP, 승률 " + String.format("%.2f", ((double) sWins / ((double) sWins + (double) sLoses)) * 100D) + "%(" + sWins + "승 " + sLoses + "패)";
        }

        if (lE_Flex != null) {
            String fTier = lE_Flex.getAsJsonObject().get("tier").getAsString();
            String fDivs = lE_Flex.getAsJsonObject().get("rank").getAsString();
            String fLPts = lE_Flex.getAsJsonObject().get("leaguePoints").getAsString();

            int fWins = lE_Flex.getAsJsonObject().get("wins").getAsInt();
            int fLoses = lE_Flex.getAsJsonObject().get("losses").getAsInt();

            flexRankTier = fTier + " " + fDivs + " " + fLPts + " LP, 승률 " + String.format("%.2f", ((double) fWins / ((double) fWins + (double) fLoses)) * 100D) + "%(" + fWins + "승 " + fLoses + "패)";
        }

        try {

            String apiKey = "RGAPI-0c9074f3-2e70-43f2-a92c-b13f5f24c51a";
            URL url = new URL("https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-name/" + summonerName + "?api_key=" + apiKey);

            BufferedReader br;

            HttpURLConnection uC = (HttpURLConnection) url.openConnection();
            uC.setRequestProperty("User-Agent", "Mozilla");
            uC.setReadTimeout(5000);
            uC.setConnectTimeout(5000);
            br = new BufferedReader(new InputStreamReader(uC.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            JsonParser jp = new JsonParser();
            JsonObject obj = (JsonObject) jp.parse(result.toString());

            String e_sId = obj.get("id").getAsString();

            url = new URL("https://kr.api.riotgames.com/tft/league/v1/entries/by-summoner/" + e_sId + "?api_key=" + apiKey);
            HttpURLConnection uC2 = (HttpURLConnection) url.openConnection();
            uC2.setRequestProperty("X-Riot-Token", apiKey);
            uC2.setReadTimeout(5000);
            uC2.setConnectTimeout(5000);
            br = new BufferedReader(new InputStreamReader(uC2.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder result_L = new StringBuilder();
            String line_L;

            while ((line_L = br.readLine()) != null) {
                result_L.append(line_L);
            }

            jsonArray = (JsonArray) jp.parse(result_L.toString());

            JsonObject lg_obj = (JsonObject) jsonArray.get(0);
            String tftTir = lg_obj.get("tier").getAsString();
            String tftDiv = lg_obj.get("rank").getAsString();
            int tftLP = lg_obj.get("leaguePoints").getAsInt();

            int tftWin = lg_obj.get("wins").getAsInt();
            int tftLose = lg_obj.get("losses").getAsInt();

            tft_RankTier = tftTir + " " + tftDiv + " " + tftLP + " LP, 승률 " + String.format("%.2f", ((double) tftWin / ((double) tftWin + (double) tftLose)) * 100D) + "%(" + tftWin + "승 " + tftLose + "패)";

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            new ExceptionReport(e);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("IndexOutOfBoundsException occurred.");
        }

        switch (mode) {
            case 2:
                return tft_RankTier;
            case 1:
                return flexRankTier;
            default:
                return soloRankTier;
        }

    }

    public String getThumbnail(String summonerId) throws Exception {
        String leagueURL = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + summonerId + "?api_key=" + apiKey;
        String jsonMain = g.getJsonByUrl(leagueURL);

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(jsonMain);

        JsonElement lE_Solo = null;

        try {
            if (jsonArray.get(0).getAsJsonObject().get("queueType").getAsString().equals("RANKED_SOLO_5x5")) {
                lE_Solo = jsonArray.get(0);
            } else if (jsonArray.get(1).getAsJsonObject().get("queueType").getAsString().equalsIgnoreCase("RANKED_SOLO_5x5")) {
                lE_Solo = jsonArray.get(1);
            }
        } catch (IndexOutOfBoundsException e) {
            // Ignore
        }

        String imageTier = (lE_Solo != null) ? lE_Solo.getAsJsonObject().get("tier").getAsString() : null;

        return imageTier != null ? "https://opgg-static.akamaized.net/images/medals/" + imageTier + "_1.png" : "https://opgg-static.akamaized.net/images/medals/default.png?image=q_auto&v=1";
    }

    public String getMostChampInfo(String id) throws Exception {

        String mostChampUrl = "https://kr.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-summoner/" + id + "?api_key=" + apiKey;
        String mostChampBody = g.getJsonByUrl(mostChampUrl);

        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(mostChampBody);
        JsonElement element = array.get(0);

        String championName = getChampionNameById(element.getAsJsonObject().get("championId").getAsInt());
        int masteryLevel = element.getAsJsonObject().get("championLevel").getAsInt();
        int masteryPoint = element.getAsJsonObject().get("championPoints").getAsInt();
        long lastPlayed = element.getAsJsonObject().get("lastPlayTime").getAsLong();

        long calDate = System.currentTimeMillis() - lastPlayed;
        long calDateDays = calDate / (24 * 60 * 60 * 1000);

        calDateDays = Math.abs(calDateDays);

        SimpleDateFormat df = new SimpleDateFormat("yyyy. MM. dd. HH:mm:ss");

        return championName + ", " + masteryLevel + "레벨 (" + String.format("%,d", masteryPoint) + "점)\n" +
                "최근 플레이 : " + df.format(lastPlayed) + "(" + calDateDays + "일 지남)";

    }

    public String getPlayedChampInfo(String id, int championId) throws Exception {

        try {

            String mostChampUrl = "https://kr.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-summoner/" + id + "?api_key=" + apiKey;
            String mostChampBody = g.getJsonByUrl(mostChampUrl);

            JsonParser parser = new JsonParser();
            JsonArray array = (JsonArray) parser.parse(mostChampBody);
            JsonElement element = null;

            for (int i = 0; i < array.size(); i++) {
                element = array.get(i);

                if (element.getAsJsonObject().get("championId").getAsInt() == championId) {
                    break;
                }
            }

            try {
                if (element != null) {
                    String championName = getChampionNameById(championId);
                    int masteryLevel = element.getAsJsonObject().get("championLevel").getAsInt();
                    int masteryPoint = element.getAsJsonObject().get("championPoints").getAsInt();
                    long lastPlayed = element.getAsJsonObject().get("lastPlayTime").getAsLong();

                    long calDate = System.currentTimeMillis() - lastPlayed;
                    long calDateDays = calDate / (24 * 60 * 60 * 1000);

                    calDateDays = Math.abs(calDateDays);

                    SimpleDateFormat df = new SimpleDateFormat("yyyy. MM. dd. HH:mm:ss");

                    return championName + ", " + masteryLevel + "레벨 (" + String.format("%,d", masteryPoint) + "점)\n" +
                            "최근 플레이 : " + df.format(lastPlayed) + "(" + calDateDays + "일 지남)";
                } else {
                    return getChampionNameById(championId) + ", 첫 판입니다.";
                }
            } catch (NullPointerException ex) {
                return getChampionNameById(championId) + ", 첫 판입니다.";
            }

        } catch (IOException e) {
            return getChampionNameById(championId) + ", 첫 판입니다.";
        }

    }

    private String getChampionNameById(int championId) {
        switch (championId) {
            case 266:
                return "아트록스";
            case 103:
                return "아리";
            case 84:
                return "아칼리";
            case 12:
                return "알리스타";
            case 32:
                return "아무무";
            case 34:
                return "애니비아";
            case 1:
                return "애니";
            case 523:
                return "아펠리오스";
            case 22:
                return "애쉬";
            case 136:
                return "아우렐리온 솔";
            case 268:
                return "아지르";
            case 432:
                return "바드";
            case 53:
                return "블리츠크랭크";
            case 63:
                return "브랜드";
            case 201:
                return "브라움";
            case 51:
                return "케이틀린";
            case 164:
                return "카밀";
            case 69:
                return "카시오페아";
            case 31:
                return "초가스";
            case 42:
                return "코르키";
            case 122:
                return "다리우스";
            case 131:
                return "다이애나";
            case 36:
                return "문도 박사";
            case 119:
                return "드레이븐";
            case 245:
                return "에코";
            case 60:
                return "엘리스";
            case 28:
                return "이블린";
            case 81:
                return "이즈리얼";
            case 9:
                return "피들스틱";
            case 114:
                return "피오라";
            case 105:
                return "피즈";
            case 3:
                return "갈리오";
            case 41:
                return "갱플랭크";
            case 86:
                return "가렌";
            case 150:
                return "나르";
            case 79:
                return "그라가스";
            case 104:
                return "그레이브즈";
            case 120:
                return "헤카림";
            case 74:
                return "하이머딩거";
            case 420:
                return "일라오이";
            case 39:
                return "이렐리아";
            case 427:
                return "아이번";
            case 40:
                return "잔나";
            case 59:
                return "자르반 4세";
            case 24:
                return "잭스";
            case 126:
                return "제이스";
            case 202:
                return "진";
            case 222:
                return "징크스";
            case 145:
                return "카이사";
            case 429:
                return "칼리스타";
            case 43:
                return "카르마";
            case 30:
                return "카서스";
            case 38:
                return "카사딘";
            case 55:
                return "카타리나";
            case 10:
                return "케일";
            case 141:
                return "케인";
            case 85:
                return "케넨";
            case 121:
                return "카직스";
            case 203:
                return "킨드레드";
            case 240:
                return "클레드";
            case 96:
                return "코그모";
            case 7:
                return "르블랑";
            case 64:
                return "리 신";
            case 89:
                return "레오나";
            case 127:
                return "리산드라";
            case 236:
                return "루시안";
            case 117:
                return "룰루";
            case 99:
                return "럭스";
            case 54:
                return "말파이트";
            case 90:
                return "말자하";
            case 57:
                return "마오카이";
            case 11:
                return "마스터 이";
            case 21:
                return "미스 포츈";
            case 82:
                return "모데카이저";
            case 25:
                return "모르가나";
            case 267:
                return "나미";
            case 75:
                return "나서스";
            case 111:
                return "노틸러스";
            case 518:
                return "니코";
            case 76:
                return "니달리";
            case 56:
                return "녹턴";
            case 20:
                return "누누와 윌럼프";
            case 2:
                return "올라프";
            case 61:
                return "오리아나";
            case 516:
                return "오른";
            case 80:
                return "판테온";
            case 78:
                return "뽀삐";
            case 555:
                return "파이크";
            case 246:
                return "키아나";
            case 133:
                return "퀸";
            case 497:
                return "라칸";
            case 33:
                return "람머스";
            case 421:
                return "렉사이";
            case 58:
                return "레넥톤";
            case 92:
                return "리븐";
            case 68:
                return "럼블";
            case 13:
                return "라이즈";
            case 113:
                return "세주아니";
            case 235:
                return "세나";
            case 875:
                return "세트";
            case 35:
                return "샤코";
            case 98:
                return "쉔";
            case 102:
                return "쉬바나";
            case 27:
                return "신지드";
            case 14:
                return "사이온";
            case 15:
                return "시비르";
            case 72:
                return "스카너";
            case 37:
                return "소나";
            case 16:
                return "소라카";
            case 50:
                return "스웨인";
            case 517:
                return "사일러스";
            case 134:
                return "신드라";
            case 223:
                return "탐 켄치";
            case 163:
                return "탈리야";
            case 91:
                return "탈론";
            case 44:
                return "요릭";
            case 17:
                return "티모";
            case 412:
                return "쓰레쉬";
            case 18:
                return "트리스타나";
            case 48:
                return "트런들";
            case 23:
                return "트린다미어";
            case 4:
                return "트위스티드 페이트";
            case 29:
                return "트위치";
            case 77:
                return "우디르";
            case 6:
                return "우르곳";
            case 110:
                return "바루스";
            case 67:
                return "베인";
            case 45:
                return "베이가";
            case 161:
                return "벨코즈";
            case 254:
                return "바이";
            case 112:
                return "빅토르";
            case 8:
                return "블라디미르";
            case 106:
                return "볼리베어";
            case 19:
                return "워윅";
            case 62:
                return "오공";
            case 498:
                return "자야";
            case 101:
                return "제라스";
            case 5:
                return "신 짜오";
            case 157:
                return "야스오";
            case 83:
                return "요릭";
            case 350:
                return "유미";
            case 154:
                return "자크";
            case 238:
                return "제드";
            case 115:
                return "직스";
            case 26:
                return "질리언";
            case 142:
                return "조이";
            case 143:
                return "자이라";
            case 107:
                return "렝가";
            case 876:
                return "릴리아";
            case 777:
                return "요네";

            default:
                return null;
        }
    }

    private String getRuneNameById(int perkId) {
        switch (perkId) {

            case 8100:
                return "지배";

            case 8112:
                return "감전";
            case 8124:
                return "포식자";
            case 8128:
                return "어둠의 수확";
            case 9923:
                return "칼날비";
            case 8126:
                return "비열한 한 방";
            case 8139:
                return "피의 맛";
            case 8143:
                return "돌발 일격";
            case 8136:
                return "좀비 와드";
            case 8120:
                return "유령 포로";
            case 8138:
                return "시야 장악";
            case 8135:
                return "굶주린 사냥꾼";
            case 8134:
                return "영리한 사냥꾼";
            case 8105:
                return "끈질긴 사냥꾼";
            case 8106:
                return "궁극의 사냥꾼";

            case 8300:
                return "영감";

            case 8351:
                return "빙결 강화";
            case 8360:
                return "봉인 풀린 주문서";
            case 8358:
                return "프로토타입 : 만능의 돌";
            case 8306:
                return "마법공학 점멸";
            case 8304:
                return "마법의 신발";
            case 8313:
                return "완벽한 타이밍";
            case 8321:
                return "외상";
            case 8316:
                return "미니언 해체분석기";
            case 8345:
                return "비스킷 배달";
            case 8347:
                return "우주적 통찰력";
            case 8410:
                return "쾌속 접근";
            case 8352:
                return "시간 왜곡 물약";

            case 8000:
                return "정밀";

            case 8005:
                return "집중 공격";
            case 8008:
                return "치명적 속도";
            case 8021:
                return "기민한 발놀림";
            case 8010:
                return "정복자";
            case 9101:
                return "과다 치유";
            case 9111:
                return "승전보";
            case 8009:
                return "침착";
            case 9104:
                return "전설: 민첩함";
            case 9105:
                return "전설: 강인함";
            case 9103:
                return "전설: 핏빛 길";
            case 8014:
                return "최후의 일격";
            case 8017:
                return "체력차 극복";
            case 8299:
                return "최후의 저항";

            case 8400:
                return "결의";

            case 8437:
                return "착취의 손아귀";
            case 8439:
                return "여진";
            case 8465:
                return "수호자";
            case 8446:
                return "철거";
            case 8463:
                return "생명의 샘";
            case 8401:
                return "보호막 강타";
            case 8429:
                return "사전 준비";
            case 8444:
                return "재생의 바람";
            case 8473:
                return "뼈 방패";
            case 8451:
                return "과잉성장";
            case 8453:
                return "소생";
            case 8242:
                return "불굴의 의지";

            case 8200:
                return "마법";

            case 8214:
                return "콩콩이 소환";
            case 8229:
                return "신비로운 유성";
            case 8230:
                return "난입";
            case 8224:
                return "무효화 구체";
            case 8226:
                return "마나순환 팔찌";
            case 8275:
                return "빛의 망토";
            case 8210:
                return "깨달음";
            case 8234:
                return "기민함";
            case 8233:
                return "절대 집중";
            case 8237:
                return "주문 작열";
            case 8232:
                return "물 위를 걷는 자";
            case 8236:
                return "폭풍 결집";

            case 5005:
                return "공격 속도 10%";
            case 5008:
                return "적응형 능력치 9";
            case 5002:
                return "방어력 6";
            case 5007:
                return "쿨타임 감소 1~10%";
            case 5001:
                return "체력 15~90";
            case 5003:
                return "마법 저항력 8";

            default:
                return String.valueOf(perkId);
        }
    }

    private String getSpellById(int spellID) {
        switch (spellID) {
            case 21:
                return "방어막";
            case 1:
                return "정화";
            case 14:
                return "점화";
            case 3:
                return "탈진";
            case 4:
                return "점멸";
            case 6:
                return "유체화";
            case 7:
                return "회복";
            case 13:
                return "총명";
            case 11:
                return "강타";
            case 32:
                return "표식";
            case 12:
                return "순간이동";

            default:
                return null;
        }
    }

}
