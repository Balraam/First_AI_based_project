package com.chatbot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response sent back to the client after AI processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {

    /** The AI-generated reply */
    private String reply;

    /** Echo back the conversationId so client can track sessions */
    private String conversationId;

    /** Timestamp of the response */
    private LocalDateTime timestamp;

    /** Token usage info (optional, useful for monitoring) */
    private Integer promptTokens;
    private Integer completionTokens;
}
