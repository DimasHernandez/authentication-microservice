package co.com.pragma.api;

import co.com.pragma.api.dto.LoginRequest;
import co.com.pragma.api.mapper.AuthMapper;
import co.com.pragma.usecase.auth.AuthUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthUseCase authUseCase;

    private final Validator validator;

    private final AuthMapper authMapper;

    public Mono<ServerResponse> listenLogin(ServerRequest serverRequest) {
        URI uri = serverRequest.uri();
        return serverRequest.bodyToMono(LoginRequest.class)
                .flatMap(loginRequest -> {
                    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
                    if (!violations.isEmpty()) {
                        return Mono.error(new ConstraintViolationException(violations));
                    }
                    return Mono.just(loginRequest);
                })
                .map(authMapper::toDomain)
                .flatMap(authUseCase::login)
                .map(authMapper::toResponse)
                .flatMap(loginResponse -> ServerResponse
                        .created(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loginResponse));
    }
}
