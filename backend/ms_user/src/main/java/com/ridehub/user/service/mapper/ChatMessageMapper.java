package com.ridehub.user.service.mapper;

import com.ridehub.user.domain.ChatMessage;
import com.ridehub.user.domain.ChatSession;
import com.ridehub.user.service.dto.ChatMessageDTO;
import com.ridehub.user.service.dto.ChatSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChatMessage} and its DTO {@link ChatMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChatMessageMapper extends EntityMapper<ChatMessageDTO, ChatMessage> {
    @Mapping(target = "chatSession", source = "chatSession", qualifiedByName = "chatSessionId")
    ChatMessageDTO toDto(ChatMessage s);

    @Named("chatSessionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ChatSessionDTO toDtoChatSessionId(ChatSession chatSession);
}
