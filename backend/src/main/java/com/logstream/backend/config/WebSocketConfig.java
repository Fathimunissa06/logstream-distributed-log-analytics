package com.logstream.backend.config;

import com.logstream.backend.websocket.LiveTailHandler;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Week 4: turns on raw WebSocket support and says "any connection to
 * ws://localhost:8080/ws/livetail should be handled by LiveTailHandler".
 *
 * setAllowedOrigins("*") is used here purely to keep local development
 * simple (React dev server on a different port). In a real production
 * app you would lock this down to your actual frontend's domain.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final LiveTailHandler liveTailHandler;

    public WebSocketConfig(LiveTailHandler liveTailHandler) {
        this.liveTailHandler = liveTailHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry
                .addHandler(liveTailHandler, "/ws/livetail")
                .setAllowedOrigins("*");
    }
}
