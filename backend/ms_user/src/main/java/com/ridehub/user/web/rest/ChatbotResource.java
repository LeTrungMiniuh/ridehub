package com.ridehub.user.web.rest;

import com.ridehub.user.service.ChatbotService;
import com.ridehub.user.service.dto.ChatbotRequestDTO;
import com.ridehub.user.service.dto.ChatbotResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "Chatbot", description = "AI Chatbot API for RideHub")
public class ChatbotResource {

    private static final Logger log = LoggerFactory.getLogger(ChatbotResource.class);

    private final ChatbotService zaiChatbotService;

    public ChatbotResource(ChatbotService zaiChatbotService) {
        this.zaiChatbotService = zaiChatbotService;
    }

    @PostMapping("/chat")
    @Operation(
        summary = "Send a message to the AI chatbot",
        description = "Send a message and receive an AI response. Supports conversation history through session management."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful chat response",
            content = @Content(schema = @Schema(implementation = ChatbotResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public ResponseEntity<ChatbotResponseDTO> chat(@Valid @RequestBody ChatbotRequestDTO request) {
        log.debug("REST request to chat with AI: {}", request.getMessage());
        
        try {
            ChatbotResponseDTO response = zaiChatbotService.chat(request);
            
            if (Boolean.TRUE.equals(response.getSuccess())) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }
        } catch (Exception e) {
            log.error("Error in chat endpoint", e);
            ChatbotResponseDTO errorResponse = ChatbotResponseDTO.builder()
                    .error("Internal server error")
                    .timestamp(java.time.Instant.now())
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/health")
    @Operation(
        summary = "Check chatbot service health",
        description = "Simple health check endpoint for the chatbot service"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Service is healthy"
        )
    })
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot service is running");
    }
}