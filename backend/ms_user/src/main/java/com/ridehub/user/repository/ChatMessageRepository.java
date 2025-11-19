package com.ridehub.user.repository;

import com.ridehub.user.domain.ChatMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for ChatMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, JpaSpecificationExecutor<ChatMessage> {

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.id = :sessionId AND cm.isDeleted = false ORDER BY cm.timestamp ASC")
    List<ChatMessage> findByChatSessionIdOrderByTimestampAsc(@Param("sessionId") Long sessionId);
}
