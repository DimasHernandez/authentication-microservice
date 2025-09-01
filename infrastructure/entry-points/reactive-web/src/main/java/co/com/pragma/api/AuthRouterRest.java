package co.com.pragma.api;

import co.com.pragma.api.config.AuthPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AuthRouterRest {

    private final AuthPath authPath;

    private final AuthHandler authHandler;


    @Bean
    public RouterFunction<ServerResponse> authRouterFunction(AuthHandler authHandler) {
        return route(POST(authPath.getLogin()), this.authHandler::listenLogin);
    }
}
