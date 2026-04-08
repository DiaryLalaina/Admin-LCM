package com.work.cashier.webSocket;

import lombok.Setter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.*;

import java.util.LinkedList;
import java.util.Queue;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Consumer;

public class WebSocketService {

    private StompSession stompSession;

    @Setter
    private Consumer<String> messageHandler;


    private Queue<String> pendingMessages = new LinkedList<>();

    // 🔌 Connexion WebSocket
    public void connect() {

        StandardWebSocketClient client = new StandardWebSocketClient();

        SockJsClient sockJsClient = new SockJsClient(
                Arrays.asList(new WebSocketTransport(client))
        );

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new StringMessageConverter());

        String url = "http://localhost:8080/ws";

        stompClient.connect(url, new StompSessionHandlerAdapter() {

            @Override
            public void afterConnected(StompSession session, StompHeaders headers) {
                System.out.println("✅ Connecté au WebSocket");

                stompSession = session;

                // 📡 Souscription
                session.subscribe("/topic/notifications", new StompFrameHandler() {

                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        if (messageHandler != null) {
                            messageHandler.accept((String) payload);
                        }
                    }
                });
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println("❌ Erreur WebSocket: " + exception.getMessage());
            }
        });
    }

    // 📤 Envoi message
    public void sendMessage(String message) {

        if (stompSession != null && stompSession.isConnected()) {
            stompSession.send("/app/sendMessage", message);
            System.out.println("📤 Envoyé: " + message);
        } else {
            System.out.println("⏳ Message en attente...");
            pendingMessages.add(message);
        }
    }
}