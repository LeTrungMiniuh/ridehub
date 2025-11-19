package com.ridehub.user.service.mapper;

import static com.ridehub.user.domain.UserStatisticsAsserts.*;
import static com.ridehub.user.domain.UserStatisticsTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserStatisticsMapperTest {

    private UserStatisticsMapper userStatisticsMapper;

    @BeforeEach
    void setUp() {
        userStatisticsMapper = new UserStatisticsMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserStatisticsSample1();
        var actual = userStatisticsMapper.toEntity(userStatisticsMapper.toDto(expected));
        assertUserStatisticsAllPropertiesEquals(expected, actual);
    }
}
