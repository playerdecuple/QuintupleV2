package com.DecupleProject.Core.Util;

import com.DecupleProject.Listener.DefaultListener;

import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TopicServerInfo {

    final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    int sleepSec = 600; // Sending log cooldown.

    public TopicServerInfo() {
        ModeChecker mc = new ModeChecker();
        if (mc.isTestMode())
            return;

        int serverNow = Objects.requireNonNull(DefaultListener.jda.getGuildById("615469086616584193")).getMemberCount();
        int serverChannel = Objects.requireNonNull(DefaultListener.jda.getGuildById("615469086616584193")).getTextChannels().size();
        int serverBoostCount = Objects.requireNonNull(DefaultListener.jda.getGuildById("615469086616584193")).getBoostCount();
        int serverBoostTier = Objects.requireNonNull(DefaultListener.jda.getGuildById("615469086616584193")).getBoostTier().getKey();

        exec.scheduleAtFixedRate(() -> {
            Objects.requireNonNull(DefaultListener.jda.getTextChannelById("699439025479745556"))
                    .getManager().setTopic("아무 주제에 관한 대화방이에요. (:person_gesturing_ok: 서버 인원 | " + serverNow +
                    "명 :thought_balloon: 채널 갯수 | " + serverChannel + "개 :moneybag: 부스트 횟수 | " + serverBoostCount + "번[" + serverBoostTier + " 단계])").queue();
        }, 0, sleepSec, TimeUnit.SECONDS);
    }

}
