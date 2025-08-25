package co.com.pragma.api;

import co.com.pragma.api.dto.UserRequest;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;

    private final Validator validator;

    private final UserMapper userMapper;

    public Mono<ServerResponse> listenRegisterUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequest.class)
                .flatMap(this::validate)
                .map(userMapper::toDomain)
                .flatMap(userUseCase::registerUser)
                .map(userMapper::toResponse)
                .flatMap(userResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userResponse));
    }

    private <T> Mono<T> validate(T object) {
        var errors = validator.validate(object);
        if (!errors.isEmpty()) {
            return Mono.error(new ConstraintViolationException(errors));
        }
        return Mono.just(object);
    }
}
