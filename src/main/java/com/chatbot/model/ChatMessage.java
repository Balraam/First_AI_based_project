package com.chatbot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single message in a conversation history.
 * Role: "user" or "assistant"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private String role;
    private String content;
}
