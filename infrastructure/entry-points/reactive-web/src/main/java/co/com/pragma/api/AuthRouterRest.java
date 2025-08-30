package co.com.pragma.api;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class AuthRouterRest {

    @Bean
    public RouterFunction<ServerResponse> authRouterFunction() {
        return null;
    }
}
