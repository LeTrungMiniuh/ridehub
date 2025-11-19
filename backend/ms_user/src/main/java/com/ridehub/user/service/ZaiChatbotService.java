package com.ridehub.user.service;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.*;
import com.ridehub.user.config.ZaiChatbotProperties;
import com.ridehub.user.domain.AppUser;
import com.ridehub.user.domain.ChatMessage;
import com.ridehub.user.domain.ChatSession;
import com.ridehub.user.repository.AppUserRepository;
import com.ridehub.user.security.SecurityUtils;
import com.ridehub.user.service.dto.AppUserDTO;
import com.ridehub.user.service.dto.ChatbotRequestDTO;
import com.ridehub.user.service.dto.ChatbotResponseDTO;
import com.ridehub.user.service.dto.TripRecommendationDTO;
import com.ridehub.user.service.dto.TripStatisticsDTO;
import com.ridehub.user.service.mapper.ChatSessionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;

@Service
public class ZaiChatbotService {

    private static final Logger log = LoggerFactory.getLogger(ZaiChatbotService.class);

    private final ZaiClient zaiClient;
    private final ZaiChatbotProperties properties;
    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;
    private final ChatSessionMapper chatSessionMapper;
    private final AppUserRepository appUserRepository;
    private final TripRecommendationService tripRecommendationService;
    private final TripStatisticsService tripStatisticsService;

    public ZaiChatbotService(
            ZaiClient zaiClient,
            ZaiChatbotProperties properties,
            ChatSessionService chatSessionService,
            ChatMessageService chatMessageService,
            ChatSessionMapper chatSessionMapper, 
            AppUserRepository appUserRepository,
            TripRecommendationService tripRecommendationService,
            TripStatisticsService tripStatisticsService) {
        this.zaiClient = zaiClient;
        this.properties = properties;
        this.chatSessionService = chatSessionService;
        this.chatMessageService = chatMessageService;
        this.chatSessionMapper = chatSessionMapper;
        this.appUserRepository = appUserRepository;
        this.tripRecommendationService = tripRecommendationService;
        this.tripStatisticsService = tripStatisticsService;
    }

    public ChatbotResponseDTO chat(ChatbotRequestDTO request) {
        try {
            ChatSession chatSession = getOrCreateChatSession(request.getSessionId(), request.getUserId());

            saveUserMessage(chatSession, request.getMessage());

            List<ChatMessage> conversationHistory = chatMessageService.findByChatSessionId(chatSession.getId());

            ChatCompletionCreateParams chatRequest = buildChatRequest(request.getMessage(), conversationHistory);

            ChatCompletionResponse response = zaiClient.chat().createChatCompletion(chatRequest);

            if (response.isSuccess()) {
                ai.z.openapi.service.model.ChatMessage aiAssistantMessage = response.getData().getChoices().get(0).getMessage();
                
                // Handle function calling
                if (aiAssistantMessage.getToolCalls() != null && !aiAssistantMessage.getToolCalls().isEmpty()) {
                    String functionResult = handleFunctionCalls(aiAssistantMessage.getToolCalls(), chatSession);
                    
                    // Continue conversation with function results
                    List<ai.z.openapi.service.model.ChatMessage> messagesWithFunction = buildMessagesWithFunctionResults(
                        request.getMessage(), conversationHistory, aiAssistantMessage, functionResult);
                    
                    ChatCompletionCreateParams followUpRequest = ChatCompletionCreateParams.builder()
                            .model(properties.getModel())
                            .messages(messagesWithFunction)
                            .stream(false)
                            .temperature(properties.getTemperature())
                            .maxTokens(properties.getMaxTokens())
                            .build();
                    
                    ChatCompletionResponse followUpResponse = zaiClient.chat().createChatCompletion(followUpRequest);
                    
                    if (followUpResponse.isSuccess()) {
                        String aiResponse = followUpResponse.getData().getChoices().get(0).getMessage().getContent().toString();
                        saveAIMessage(chatSession, aiResponse);
                        
                        return ChatbotResponseDTO.builder()
                                .sessionId(chatSession.getId().toString())
                                .message(aiResponse)
                                .timestamp(Instant.now())
                                .success(true)
                                .build();
                    }
                }
                
                String aiResponse = aiAssistantMessage.getContent().toString();
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
                        "You are a helpful AI assistant for RideHub platform. You help users with transportation, ride-sharing, and mobility-related questions. " +
                        "You can suggest trips and provide trip statistics using the available functions.")
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

        // Build function tools
        List<ChatTool> tools = buildFunctionTools();

        return ChatCompletionCreateParams.builder()
                .model(properties.getModel())
                .messages(messages)
                .tools(tools)
                .toolChoice("auto")
                .stream(false)
                .temperature(properties.getTemperature())
                .maxTokens(properties.getMaxTokens())
                .build();
    }

    private List<ChatTool> buildFunctionTools() {
        List<ChatTool> tools = new ArrayList<>();

        // Trip Suggestion Function
        Map<String, ChatFunctionParameterProperty> tripParams = new HashMap<>();
        tripParams.put("origin", ChatFunctionParameterProperty.builder()
                .type("string")
                .description("Starting location for the trip")
                .build());
        tripParams.put("destination", ChatFunctionParameterProperty.builder()
                .type("string")
                .description("Destination for the trip")
                .build());
        tripParams.put("travelDate", ChatFunctionParameterProperty.builder()
                .type("string")
                .description("Travel date in YYYY-MM-DD format")
                .build());
        tripParams.put("budgetRange", ChatFunctionParameterProperty.builder()
                .type("string")
                .description("Budget range for the trip (e.g., 'low', 'medium', 'high')")
                .build());

        ChatTool tripTool = ChatTool.builder()
                .type(ChatToolType.FUNCTION.value())
                .function(ChatFunction.builder()
                        .name("suggest_trip")
                        .description("Suggest trip recommendations based on origin, destination, and preferences")
                        .parameters(ChatFunctionParameters.builder()
                                .type("object")
                                .properties(tripParams)
                                .required(List.of("origin", "destination"))
                                .build())
                        .build())
                .build();

        // Trip Statistics Function
        Map<String, ChatFunctionParameterProperty> statsParams = new HashMap<>();
        statsParams.put("routeId", ChatFunctionParameterProperty.builder()
                .type("integer")
                .description("Route ID to get statistics for")
                .build());
        statsParams.put("vehicleType", ChatFunctionParameterProperty.builder()
                .type("string")
                .description("Vehicle type (CAR, BUS, TRAIN, MOTORCYCLE, BICYCLE)")
                .build());
        statsParams.put("period", ChatFunctionParameterProperty.builder()
                .type("string")
                .description("Time period for statistics (daily, weekly, monthly)")
                .build());

        ChatTool statsTool = ChatTool.builder()
                .type(ChatToolType.FUNCTION.value())
                .function(ChatFunction.builder()
                        .name("get_trip_statistics")
                        .description("Get trip statistics for a specific route and vehicle type")
                        .parameters(ChatFunctionParameters.builder()
                                .type("object")
                                .properties(statsParams)
                                .required(List.of("routeId"))
                                .build())
                        .build())
                .build();

        tools.add(tripTool);
        tools.add(statsTool);

        return tools;
    }

    private String handleFunctionCalls(List<ToolCalls> toolCalls, ChatSession chatSession) {
        StringBuilder result = new StringBuilder();
        
        for (ToolCalls toolCall : toolCalls) {
            String functionName = toolCall.getFunction().getName();
            String argumentsJson = toolCall.getFunction().getArguments() != null ? 
                toolCall.getFunction().getArguments().toString() : "{}";
            Map<String, Object> arguments = parseFunctionArguments(argumentsJson);
            
            try {
                switch (functionName) {
                    case "suggest_trip":
                        result.append(handleSuggestTrip(arguments, chatSession));
                        break;
                    case "get_trip_statistics":
                        result.append(handleGetTripStatistics(arguments));
                        break;
                    default:
                        result.append("Unknown function: ").append(functionName);
                }
            } catch (Exception e) {
                log.error("Error executing function: {}", functionName, e);
                result.append("Error executing ").append(functionName).append(": ").append(e.getMessage());
            }
        }
        
        return result.toString();
    }

    private Map<String, Object> parseFunctionArguments(String argumentsJson) {
        Map<String, Object> args = new HashMap<>();
        if (argumentsJson != null && !argumentsJson.trim().isEmpty()) {
            try {
                // Use Jackson for proper JSON parsing
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                return mapper.readValue(argumentsJson, Map.class);
            } catch (Exception e) {
                log.warn("Failed to parse function arguments: {}", argumentsJson, e);
                // Fallback to simple parsing
                try {
                    String clean = argumentsJson.replaceAll("[{}\"]", "").trim();
                    String[] pairs = clean.split(",");
                    
                    for (String pair : pairs) {
                        String[] keyValue = pair.split(":");
                        if (keyValue.length == 2) {
                            args.put(keyValue[0].trim(), keyValue[1].trim());
                        }
                    }
                } catch (Exception ex) {
                    log.error("Fallback parsing also failed", ex);
                }
            }
        }
        return args;
    }

    private String handleSuggestTrip(Map<String, Object> arguments, ChatSession chatSession) {
        try {
            TripRecommendationDTO recommendation = new TripRecommendationDTO();
            recommendation.setOrigin((String) arguments.get("origin"));
            recommendation.setDestination((String) arguments.get("destination"));
            
            String travelDateStr = (String) arguments.get("travelDate");
            if (travelDateStr != null) {
                recommendation.setTravelDate(LocalDate.parse(travelDateStr));
            } else {
                recommendation.setTravelDate(LocalDate.now().plusDays(1));
            }
            
            recommendation.setBudgetRange((String) arguments.get("budgetRange"));
            recommendation.setCreatedAt(Instant.now());
            recommendation.setIsDeleted(false);
            
            // Set user from chat session
            AppUserDTO userDTO = new AppUserDTO();
            userDTO.setId(chatSession.getUser().getId());
            recommendation.setUser(userDTO);
            
            // Generate AI-powered trip suggestions
            String aiSuggestions = generateTripSuggestions(
                recommendation.getOrigin(), 
                recommendation.getDestination(), 
                recommendation.getBudgetRange()
            );
            recommendation.setRecommendedTrips(aiSuggestions);
            recommendation.setConfidenceScore(new java.math.BigDecimal("0.85"));
            
            TripRecommendationDTO saved = tripRecommendationService.save(recommendation);
            
            return String.format("Trip suggestion created with ID %d: %s", saved.getId(), aiSuggestions);
            
        } catch (Exception e) {
            log.error("Error creating trip suggestion", e);
            return "Failed to create trip suggestion: " + e.getMessage();
        }
    }

    private String handleGetTripStatistics(Map<String, Object> arguments) {
        try {
            Long routeId = arguments.containsKey("routeId") ? 
                Long.valueOf(arguments.get("routeId").toString()) : 1L;
            
            // Try to find existing statistics
            Optional<TripStatisticsDTO> existingStats = tripStatisticsService.findOne(routeId);
            
            if (existingStats.isPresent()) {
                TripStatisticsDTO stats = existingStats.get();
                return formatTripStatistics(stats);
            } else {
                // Generate mock statistics for demonstration
                TripStatisticsDTO mockStats = generateMockStatistics(routeId, arguments);
                return formatTripStatistics(mockStats);
            }
            
        } catch (Exception e) {
            log.error("Error getting trip statistics", e);
            return "Failed to get trip statistics: " + e.getMessage();
        }
    }

    private String generateTripSuggestions(String origin, String destination, String budgetRange) {
        StringBuilder suggestions = new StringBuilder();
        suggestions.append("AI-Powered Trip Suggestions for ").append(origin).append(" to ").append(destination).append(":\n\n");
        
        if ("low".equalsIgnoreCase(budgetRange)) {
            suggestions.append("1. Economy Bus - $15-25, 2.5 hours\n");
            suggestions.append("2. Shared Ride - $20-30, 2 hours\n");
            suggestions.append("3. Public Transport - $10-15, 3 hours\n");
        } else if ("high".equalsIgnoreCase(budgetRange)) {
            suggestions.append("1. Premium Car - $80-120, 1.5 hours\n");
            suggestions.append("2. Private Taxi - $100-150, 1.5 hours\n");
            suggestions.append("3. Flight - $200-300, 0.5 hours\n");
        } else {
            suggestions.append("1. Standard Car - $40-60, 2 hours\n");
            suggestions.append("2. Train - $35-50, 2.5 hours\n");
            suggestions.append("3. Premium Bus - $30-45, 2.5 hours\n");
        }
        
        suggestions.append("\nRecommendation: Book in advance for better prices!");
        return suggestions.toString();
    }

    private TripStatisticsDTO generateMockStatistics(Long routeId, Map<String, Object> arguments) {
        TripStatisticsDTO stats = new TripStatisticsDTO();
        stats.setId(routeId);
        stats.setRouteId(routeId);
        stats.setTotalBookings(150);
        stats.setTotalRevenue(new java.math.BigDecimal("12500.50"));
        stats.setAveragePrice(new java.math.BigDecimal("83.37"));
        stats.setOccupancyRate(new java.math.BigDecimal("0.75"));
        stats.setCancellationRate(new java.math.BigDecimal("0.05"));
        stats.setCustomerSatisfactionScore(new java.math.BigDecimal("4.2"));
        stats.setPopularSeatTypes("Window, Economy");
        stats.setPeakTravelTimes("8-10 AM, 5-7 PM");
        stats.setMonthlyTrend("Increasing trend: +15% this month");
        stats.setValidFrom(LocalDate.now().minusMonths(1));
        stats.setValidTo(LocalDate.now());
        stats.setCreatedAt(Instant.now());
        stats.setIsDeleted(false);
        
        return stats;
    }

    private String formatTripStatistics(TripStatisticsDTO stats) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("Trip Statistics for Route ").append(stats.getRouteId()).append(":\n\n");
        formatted.append("📊 Total Bookings: ").append(stats.getTotalBookings()).append("\n");
        formatted.append("💰 Total Revenue: $").append(stats.getTotalRevenue()).append("\n");
        formatted.append("📈 Average Price: $").append(stats.getAveragePrice()).append("\n");
        formatted.append("🪑 Occupancy Rate: ").append(stats.getOccupancyRate().multiply(new java.math.BigDecimal("100"))).append("%\n");
        formatted.append("❌ Cancellation Rate: ").append(stats.getCancellationRate().multiply(new java.math.BigDecimal("100"))).append("%\n");
        formatted.append("⭐ Customer Satisfaction: ").append(stats.getCustomerSatisfactionScore()).append("/5.0\n");
        formatted.append("🎯 Popular Seat Types: ").append(stats.getPopularSeatTypes()).append("\n");
        formatted.append("⏰ Peak Travel Times: ").append(stats.getPeakTravelTimes()).append("\n");
        formatted.append("📅 Valid Period: ").append(stats.getValidFrom()).append(" to ").append(stats.getValidTo()).append("\n");
        formatted.append("📈 Trend: ").append(stats.getMonthlyTrend()).append("\n");
        
        return formatted.toString();
    }

    private List<ai.z.openapi.service.model.ChatMessage> buildMessagesWithFunctionResults(
            String userMessage, 
            List<ChatMessage> conversationHistory, 
            ai.z.openapi.service.model.ChatMessage aiAssistantMessage, 
            String functionResult) {
        
        List<ai.z.openapi.service.model.ChatMessage> messages = new ArrayList<>();

        // System message
        messages.add(ai.z.openapi.service.model.ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM.value())
                .content("You are a helpful AI assistant for RideHub platform.")
                .build());

        // Conversation history
        for (ChatMessage msg : conversationHistory) {
            String role = "USER".equals(msg.getMessageType()) ? ChatMessageRole.USER.value()
                    : ChatMessageRole.ASSISTANT.value();

            messages.add(ai.z.openapi.service.model.ChatMessage.builder()
                    .role(role)
                    .content(msg.getMessageText())
                    .build());
        }

        // Current user message
        messages.add(ai.z.openapi.service.model.ChatMessage.builder()
                .role(ChatMessageRole.USER.value())
                .content(userMessage)
                .build());

        // Assistant message with function calls
        messages.add(ai.z.openapi.service.model.ChatMessage.builder()
                .role(ChatMessageRole.ASSISTANT.value())
                .content(aiAssistantMessage.getContent().toString())
                .toolCalls(aiAssistantMessage.getToolCalls())
                .build());

        // Function result - use FUNCTION role instead of TOOL
        messages.add(ai.z.openapi.service.model.ChatMessage.builder()
                .role(ChatMessageRole.FUNCTION.value())
                .content(functionResult)
                .build());

        return messages;
    }
}