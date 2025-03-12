/*package com.spring.guidely.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This endpoint is used by clients to connect using SockJS fallback.
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*") // Adjust allowed origins as needed.
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // All messages with destination prefix /app will be routed to your controllers.
        registry.setApplicationDestinationPrefixes("/app");

        // Enable a STOMP broker relay using RabbitMQ.
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("localhost")
                .setRelayPort(61613)   // Default STOMP port on RabbitMQ.
                .setClientLogin("guest")
                .setClientPasscode("guest");
    }
}
*/