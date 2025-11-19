package com.ridehub.user.service.mapper;

import com.ridehub.user.domain.AppUser;
import com.ridehub.user.domain.ChatSession;
import com.ridehub.user.service.dto.AppUserDTO;
import com.ridehub.user.service.dto.ChatSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChatSession} and its DTO {@link ChatSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChatSessionMapper extends EntityMapper<ChatSessionDTO, ChatSession> {
    @Mapping(target = "user", source = "user", qualifiedByName = "appUserId")
    ChatSessionDTO toDto(ChatSession s);

    @Named("appUserId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppUserDTO toDtoAppUserId(AppUser appUser);
}
