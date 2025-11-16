package com.ridehub.user.service.mapper;

import static com.ridehub.user.domain.UserQueryAsserts.*;
import static com.ridehub.user.domain.UserQueryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserQueryMapperTest {

    private UserQueryMapper userQueryMapper;

    @BeforeEach
    void setUp() {
        userQueryMapper = new UserQueryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserQuerySample1();
        var actual = userQueryMapper.toEntity(userQueryMapper.toDto(expected));
        assertUserQueryAllPropertiesEquals(expected, actual);
    }
}
