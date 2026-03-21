package com.chatbot.service;

import com.chatbot.model.ChatMessage;
import com.chatbot.model.ChatRequest;
import com.chatbot.model.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core chatbot service.
 *
 * Features:
 * - Single-turn chat (no conversationId)
 * - Multi-turn chat with in-memory conversation history (with conversationId)
 * - Streaming support (see streamChat method)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatClient chatClient;

    /**
     * In-memory store for conversation histories.
     * Key: conversationId, Value: list of messages
     *
     * NOTE: For production, replace this with Redis or a database.
     */
    private final Map<String, List<Message>> conversationStore = new ConcurrentHashMap<>();

    /**
     * Handles a chat request.
     * If conversationId is provided, maintains conversation history.
     * If not provided, sends a single stateless message.
     */
    public ChatResponse chat(ChatRequest request) {
        String userMessage = request.getMessage();
        String conversationId = request.getConversationId();

        log.debug("Received chat request | conversationId={} | message={}", conversationId, userMessage);

        String reply;

        if (conversationId != null && !conversationId.isBlank()) {
            // ── Multi-turn: retrieve history, add new message, send full history ──
            reply = chatWithHistory(conversationId, userMessage);
        } else {
            // ── Single-turn: stateless, no memory ──
            conversationId = UUID.randomUUID().toString();
            reply = chatStateless(userMessage);
        }

        log.debug("AI reply | conversationId={} | reply={}", conversationId, reply);

        return ChatResponse.builder()
                .reply(reply)
                .conversationId(conversationId)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Single-turn stateless chat — no history.
     */
    private String chatStateless(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }

    /**
     * Multi-turn chat — maintains full conversation history per conversationId.
     */
    private String chatWithHistory(String conversationId, String userMessage) {
        // Get or create history list
        List<Message> history = conversationStore.computeIfAbsent(
                conversationId, k -> new ArrayList<>()
        );

        // Add current user message to history
        history.add(new UserMessage(userMessage));

        // Build prompt with full history
        Prompt prompt = new Prompt(history);

        // Call AI with full conversation context
        String reply = chatClient.prompt(prompt)
                .call()
                .content();

        // Save assistant reply to history for next turn
        history.add(new AssistantMessage(reply));

        log.debug("Conversation history size for {} = {}", conversationId, history.size());

        return reply;
    }

    /**
     * Returns the full conversation history for a given conversationId.
     * Useful for debug or displaying chat history.
     */
    public List<ChatMessage> getHistory(String conversationId) {
        List<Message> history = conversationStore.getOrDefault(conversationId, List.of());
        return history.stream()
                .map(msg -> ChatMessage.builder()
                        .role(msg instanceof UserMessage ? "user" : "assistant")
                        .content(msg.getText())
                        .build())
                .toList();
    }

    /**
     * Clears conversation history for a given conversationId.
     */
    public void clearHistory(String conversationId) {
        conversationStore.remove(conversationId);
        log.info("Cleared conversation history for conversationId={}", conversationId);
    }

    /**
     * Streaming chat — returns a Flux<String> of tokens as they arrive.
     * Connect this to a Server-Sent Events (SSE) endpoint for real-time streaming UI.
     */
    public reactor.core.publisher.Flux<String> streamChat(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .stream()
                .content();
    }
}
