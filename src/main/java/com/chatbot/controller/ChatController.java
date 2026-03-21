package com.chatbot.controller;

import com.chatbot.model.ChatMessage;
import com.chatbot.model.ChatRequest;
import com.chatbot.model.ChatResponse;
import com.chatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * REST controller exposing chatbot endpoints.
 *
 * Endpoints:
 * POST /api/chat              — Send a message, get a response
 * POST /api/chat/stream       — Streaming response via SSE
 * GET  /api/chat/{id}/history — Get conversation history
 * DELETE /api/chat/{id}       — Clear conversation history
 * GET  /api/chat/health       — Health check
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    /**
     * Standard chat endpoint.
     *
     * Request:
     * {
     *   "message": "What is Spring AI?",
     *   "conversationId": "abc-123"   // optional, omit for stateless
     * }
     *
     * Response:
     * {
     *   "reply": "Spring AI is...",
     *   "conversationId": "abc-123",
     *   "timestamp": "2025-01-01T10:00:00"
     * }
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("POST /api/chat | conversationId={}", request.getConversationId());

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        ChatResponse response = chatService.chat(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Streaming chat via Server-Sent Events (SSE).
     * Tokens stream in real-time as the AI generates them.
     *
     * Usage: EventSource or fetch with ReadableStream on frontend.
     *
     * POST /api/chat/stream
     * Body: { "message": "Tell me a joke" }
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        log.info("POST /api/chat/stream | message={}", message);

        if (message == null || message.isBlank()) {
            return Flux.error(new IllegalArgumentException("Message cannot be empty"));
        }

        return chatService.streamChat(message);
    }

    /**
     * Returns the full conversation history for a session.
     * GET /api/chat/{conversationId}/history
     */
    @GetMapping("/{conversationId}/history")
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable String conversationId) {
        log.info("GET /api/chat/{}/history", conversationId);
        List<ChatMessage> history = chatService.getHistory(conversationId);
        return ResponseEntity.ok(history);
    }

    /**
     * Clears the conversation memory for a session.
     * DELETE /api/chat/{conversationId}
     */
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Map<String, String>> clearHistory(@PathVariable String conversationId) {
        log.info("DELETE /api/chat/{}", conversationId);
        chatService.clearHistory(conversationId);
        return ResponseEntity.ok(Map.of(
                "message", "Conversation cleared",
                "conversationId", conversationId
        ));
    }

    /**
     * Simple health check endpoint.
     * GET /api/chat/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "Spring AI Chatbot"));
    }
}
