package com.chatbot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Incoming chat request from the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {

    /** The user's message */
    private String message;

    /**
     * Optional conversationId for multi-turn memory.
     * If null, a stateless single-turn response is returned.
     */
    private String conversationId;
}
