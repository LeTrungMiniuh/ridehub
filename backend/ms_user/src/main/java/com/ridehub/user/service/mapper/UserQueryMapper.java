package com.ridehub.user.service.mapper;

import com.ridehub.user.domain.ChatSession;
import com.ridehub.user.domain.UserQuery;
import com.ridehub.user.service.dto.ChatSessionDTO;
import com.ridehub.user.service.dto.UserQueryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserQuery} and its DTO {@link UserQueryDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserQueryMapper extends EntityMapper<UserQueryDTO, UserQuery> {
    @Mapping(target = "chatSession", source = "chatSession", qualifiedByName = "chatSessionId")
    UserQueryDTO toDto(UserQuery s);

    @Named("chatSessionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ChatSessionDTO toDtoChatSessionId(ChatSession chatSession);
}
