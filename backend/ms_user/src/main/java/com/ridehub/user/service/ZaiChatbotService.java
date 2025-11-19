package com.ridehub.user.service;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.*;
import com.ridehub.user.config.ZaiChatbotProperties;
import com.ridehub.user.domain.AppUser;
import com.ridehub.user.domain.ChatMessage;
import com.ridehub.user.domain.ChatSession;
import com.ridehub.user.repository.AppUserRepository;
import com.ridehub.user.security.SecurityUtils;
import com.ridehub.user.service.dto.ChatbotRequestDTO;
import com.ridehub.user.service.dto.ChatbotResponseDTO;
import com.ridehub.user.service.mapper.ChatSessionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ZaiChatbotService {

    private static final Logger log = LoggerFactory.getLogger(ZaiChatbotService.class);

    private final ZaiClient zaiClient;
    private final ZaiChatbotProperties properties;
    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;
    private final ChatSessionMapper chatSessionMapper;
    private final AppUserRepository appUserRepository;

    public ZaiChatbotService(
            ZaiClient zaiClient,
            ZaiChatbotProperties properties,
            ChatSessionService chatSessionService,
            ChatMessageService chatMessageService,
            ChatSessionMapper chatSessionMapper, AppUserRepository appUserRepository) {
        this.zaiClient = zaiClient;
        this.properties = properties;
        this.chatSessionService = chatSessionService;
        this.chatMessageService = chatMessageService;
        this.chatSessionMapper = chatSessionMapper;
        this.appUserRepository = appUserRepository;
    }

    public ChatbotResponseDTO chat(ChatbotRequestDTO request) {
        try {
            ChatSession chatSession = getOrCreateChatSession(request.getSessionId(), request.getUserId());

            saveUserMessage(chatSession, request.getMessage());

            List<ChatMessage> conversationHistory = chatMessageService.findByChatSessionId(chatSession.getId());

            ChatCompletionCreateParams chatRequest = buildChatRequest(request.getMessage(), conversationHistory);

            ChatCompletionResponse response = zaiClient.chat().createChatCompletion(chatRequest);

            if (response.isSuccess()) {
                String aiResponse = response.getData().getChoices().get(0).getMessage().getContent().toString();

                saveAIMessage(chatSession, aiResponse);

                return ChatbotResponseDTO.builder()
                        .sessionId(chatSession.getId().toString())
                        .message(aiResponse)
                        .timestamp(Instant.now())
                        .success(true)
                        .build();
            } else {
                log.error("Z AI API error: {}", response.getMsg());
                return ChatbotResponseDTO.builder()
                        .sessionId(chatSession.getId().toString())
                        .error("AI service temporarily unavailable")
                        .timestamp(Instant.now())
                        .success(false)
                        .build();
            }
        } catch (Exception e) {
            log.error("Error processing chat request", e);
            return ChatbotResponseDTO.builder()
                    .error("Internal server error")
                    .timestamp(Instant.now())
                    .success(false)
                    .build();
        }
    }

    private ChatSession getOrCreateChatSession(String sessionId, String userId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            try {
                Long sessionLong = Long.parseLong(sessionId);
                return chatSessionService.findOne(sessionLong)
                        .map(chatSessionMapper::toEntity)
                        .orElseGet(() -> createNewChatSession(userId));
            } catch (NumberFormatException e) {
                log.warn("Invalid session ID format: {}", sessionId);
                return createNewChatSession(userId);
            }
        } else {
            return createNewChatSession(userId);
        }
    }

    private ChatSession createNewChatSession(String userId) {
        ChatSession chatSession = new ChatSession();
        chatSession.setSessionId("session_" + System.currentTimeMillis());
        chatSession.setStartedAt(Instant.now());
        chatSession.setIsActive(true);
        chatSession.setCreatedAt(Instant.now()); // ✅ required

        // if you have soft delete logic
        chatSession.setIsDeleted(false); // optional, but good

        // set the user (JHipster-style example)
        // AppUser currentUser = appUserRepository
        // .findById(SecurityUtils.getCurrentUserLogin().orElseThrow())
        // .orElseThrow();
        AppUser currentUser = new AppUser();
        currentUser.setId(1L);
        chatSession.setUser(currentUser); // ✅ required

        ChatSession savedSession = chatSessionService.save(chatSession);

        log.info("Created new chat session: {}", savedSession.getId());
        return savedSession;
    }

    private void saveUserMessage(ChatSession chatSession, String message) {
        ChatMessage userMessage = new ChatMessage();
        userMessage.setMessageText(message);
        userMessage.setMessageType("USER");
        userMessage.setTimestamp(Instant.now());
        userMessage.setCreatedAt(Instant.now());
        userMessage.setUpdatedAt(Instant.now());
        userMessage.setIsDeleted(false);
        userMessage.setChatSession(chatSession);

        chatMessageService.save(userMessage);
    }

    private void saveAIMessage(ChatSession chatSession, String message) {
        ChatMessage aiMessage = new ChatMessage();
        aiMessage.setMessageText(message);
        aiMessage.setMessageType("AI");
        aiMessage.setTimestamp(Instant.now());
        aiMessage.setCreatedAt(Instant.now());
        aiMessage.setUpdatedAt(Instant.now());
        aiMessage.setIsDeleted(false);
        aiMessage.setChatSession(chatSession);

        chatMessageService.save(aiMessage);
    }

    private ChatCompletionCreateParams buildChatRequest(String userMessage, List<ChatMessage> conversationHistory) {
        List<ai.z.openapi.service.model.ChatMessage> messages = new ArrayList<>();

        messages.add(ai.z.openapi.service.model.ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM.value())
                .content(
                        "You are a helpful AI assistant for RideHub platform. You help users with transportation, ride-sharing, and mobility-related questions.")
                .build());

        for (ChatMessage msg : conversationHistory) {
            String role = "USER".equals(msg.getMessageType()) ? ChatMessageRole.USER.value()
                    : ChatMessageRole.ASSISTANT.value();

            messages.add(ai.z.openapi.service.model.ChatMessage.builder()
                    .role(role)
                    .content(msg.getMessageText())
                    .build());
        }

        messages.add(ai.z.openapi.service.model.ChatMessage.builder()
                .role(ChatMessageRole.USER.value())
                .content(userMessage)
                .build());

        return ChatCompletionCreateParams.builder()
                .model(properties.getModel())
                .messages(messages)
                .stream(false)
                .temperature(properties.getTemperature())
                .maxTokens(properties.getMaxTokens())
                .build();
    }
}