package com.ridehub.user.domain;

import static com.ridehub.user.domain.AppUserTestSamples.*;
import static com.ridehub.user.domain.ChatSessionTestSamples.*;
import static com.ridehub.user.domain.ProfileTestSamples.*;
import static com.ridehub.user.domain.TripRecommendationTestSamples.*;
import static com.ridehub.user.domain.UserStatisticsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ridehub.user.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AppUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppUser.class);
        AppUser appUser1 = getAppUserSample1();
        AppUser appUser2 = new AppUser();
        assertThat(appUser1).isNotEqualTo(appUser2);

        appUser2.setId(appUser1.getId());
        assertThat(appUser1).isEqualTo(appUser2);

        appUser2 = getAppUserSample2();
        assertThat(appUser1).isNotEqualTo(appUser2);
    }

    @Test
    void profileTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        appUser.setProfile(profileBack);
        assertThat(appUser.getProfile()).isEqualTo(profileBack);

        appUser.profile(null);
        assertThat(appUser.getProfile()).isNull();
    }

    @Test
    void statisticsTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        UserStatistics userStatisticsBack = getUserStatisticsRandomSampleGenerator();

        appUser.setStatistics(userStatisticsBack);
        assertThat(appUser.getStatistics()).isEqualTo(userStatisticsBack);

        appUser.statistics(null);
        assertThat(appUser.getStatistics()).isNull();
    }

    @Test
    void chatSessionsTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        ChatSession chatSessionBack = getChatSessionRandomSampleGenerator();

        appUser.addChatSessions(chatSessionBack);
        assertThat(appUser.getChatSessions()).containsOnly(chatSessionBack);
        assertThat(chatSessionBack.getUser()).isEqualTo(appUser);

        appUser.removeChatSessions(chatSessionBack);
        assertThat(appUser.getChatSessions()).doesNotContain(chatSessionBack);
        assertThat(chatSessionBack.getUser()).isNull();

        appUser.chatSessions(new HashSet<>(Set.of(chatSessionBack)));
        assertThat(appUser.getChatSessions()).containsOnly(chatSessionBack);
        assertThat(chatSessionBack.getUser()).isEqualTo(appUser);

        appUser.setChatSessions(new HashSet<>());
        assertThat(appUser.getChatSessions()).doesNotContain(chatSessionBack);
        assertThat(chatSessionBack.getUser()).isNull();
    }

    @Test
    void recommendationsTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        TripRecommendation tripRecommendationBack = getTripRecommendationRandomSampleGenerator();

        appUser.addRecommendations(tripRecommendationBack);
        assertThat(appUser.getRecommendations()).containsOnly(tripRecommendationBack);
        assertThat(tripRecommendationBack.getUser()).isEqualTo(appUser);

        appUser.removeRecommendations(tripRecommendationBack);
        assertThat(appUser.getRecommendations()).doesNotContain(tripRecommendationBack);
        assertThat(tripRecommendationBack.getUser()).isNull();

        appUser.recommendations(new HashSet<>(Set.of(tripRecommendationBack)));
        assertThat(appUser.getRecommendations()).containsOnly(tripRecommendationBack);
        assertThat(tripRecommendationBack.getUser()).isEqualTo(appUser);

        appUser.setRecommendations(new HashSet<>());
        assertThat(appUser.getRecommendations()).doesNotContain(tripRecommendationBack);
        assertThat(tripRecommendationBack.getUser()).isNull();
    }
}
