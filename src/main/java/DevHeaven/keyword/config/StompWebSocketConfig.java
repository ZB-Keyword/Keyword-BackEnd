package DevHeaven.keyword.config;

import DevHeaven.keyword.common.service.chat.CustomPrincipalHandshakeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Configuration
    @EnableWebSocketMessageBroker
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/stomp/chat")
                    .setAllowedOriginPatterns("*")
                    .setHandshakeHandler(new CustomPrincipalHandshakeHandler());
//                .withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            //클라 -> 서버 발행 메세지 endpoint
            // Client의 SEND 요청을 처리
            registry.setApplicationDestinationPrefixes("/pub");

            //서버 -> 클라 메세지 endpoint'
            // 해당 경로를 SUBSCRIBE하는 Client에세 메세지 전달하는 작업 수행
            registry.enableSimpleBroker("/sub");
        }
    }
}
