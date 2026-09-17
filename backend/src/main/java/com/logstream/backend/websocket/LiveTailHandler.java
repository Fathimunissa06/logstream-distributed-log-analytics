package com.logstream.backend.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Week 4: "Live Tail" over WebSockets.
 *
 * WHAT A WEBSOCKET IS, IN ONE SENTENCE: unlike a normal REST request
 * (browser asks once, server answers once, connection closes), a
 * WebSocket is a connection that STAYS OPEN, so the server can keep
 * pushing new messages to the browser the instant they happen -
 * perfect for "show me new logs the moment they arrive" instead of
 * the browser having to repeatedly ask "any new logs? any new logs?".
 *
 * This class is registered as the handler for the /ws/livetail
 * endpoint (see WebSocketConfig). Every browser tab that opens that
 * connection becomes one "session" tracked in the `sessions` set
 * below. Whenever a new log is ingested, LogIngestionService calls
 * broadcast(...) here, which forwards the log as JSON text to every
 * currently-open session at once - bypassing Lucene/search entirely,
 * exactly as the project plan describes for Live Tail.
 */
@Component
public class LiveTailHandler extends TextWebSocketHandler {

    // CopyOnWriteArraySet is a Set that's safe to read/write from many
    // threads at once - important because ingestion happens on gRPC
    // threads while sessions open/close on their own WebSocket threads.
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println(
                "[LiveTail] Client connected: " + session.getId()
                        + " (total: " + sessions.size() + ")"
        );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println(
                "[LiveTail] Client disconnected: " + session.getId()
                        + " (total: " + sessions.size() + ")"
        );
    }

    /**
     * Sends one text message (we send small JSON strings) to every
     * currently-connected browser tab. Called by LogIngestionService
     * right after a log is accepted.
     */
    public void broadcast(String json) {

        TextMessage message = new TextMessage(json);

        for (WebSocketSession session : sessions) {

            try {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                // If sending to one broken/slow client fails, don't
                // let it stop the log from reaching everyone else.
                System.err.println(
                        "[LiveTail] Failed to send to " + session.getId()
                                + ": " + e.getMessage()
                );
            }
        }
    }
}
