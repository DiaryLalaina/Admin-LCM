package com.work.cashier.webSocket;

import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;

public class WebSocketFX {

    private StompSession session;

    public void connect(Runnable onMessageReceived, String topic) {

        WebSocketStompClient stompClient =
                new WebSocketStompClient(new StandardWebSocketClient());

        stompClient.setMessageConverter(new StringMessageConverter());
        stompClient.setInboundMessageSizeLimit(64 * 1024);

        String url = "ws://192.168.7.2:8080/ws";

        stompClient.connect(url, new StompSessionHandlerAdapter() {

            @Override
            public void afterConnected(@NotNull StompSession stompSession,
                                       @NotNull StompHeaders connectedHeaders) {

                System.out.println("🔗 WebSocket connecté avec succès");
                session = stompSession;

                session.subscribe(topic, new StompFrameHandler() {

                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        System.out.println("📩 Message reçu : " + payload);

                        javafx.application.Platform.runLater(onMessageReceived);
                    }
                });
            }

            @Override
            public void handleTransportError(@NotNull StompSession session,
                                             @NotNull Throwable exception) {

                System.err.println("❌ Erreur transport WebSocket");
                //exception.printStackTrace();
            }

            @Override
            public void handleException(@NotNull StompSession session,
                                        StompCommand command,
                                        @NotNull StompHeaders headers,
                                        @NotNull byte[] payload,
                                        @NotNull Throwable exception) {

                System.err.println("❌ Erreur STOMP");
                exception.printStackTrace();
            }
        });
    }

}
