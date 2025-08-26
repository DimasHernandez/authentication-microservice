package co.com.pragma.api;

import co.com.pragma.api.dto.UserRequest;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.usecase.user.UserUseCase;
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
public class Handler {

    private final UserUseCase userUseCase;

    private final Validator validator;

    private final UserMapper userMapper;

    public Mono<ServerResponse> listenRegisterUser(ServerRequest serverRequest) {
        URI location = serverRequest.uri();
        return serverRequest.bodyToMono(UserRequest.class)
                .flatMap(userRequest -> {
                    Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
                    if (!violations.isEmpty()) {
                        return Mono.error(new ConstraintViolationException(violations));
                    }
                    return Mono.just(userRequest);
                })
                .map(userMapper::toDomain)
                .flatMap(userUseCase::registerUser)
                .map(userMapper::toResponse)
                .flatMap(userResponse -> ServerResponse
                        .created(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userResponse));
    }

    public Mono<ServerResponse> listenGetUserByDocumentIdentity(ServerRequest serverRequest) {
        String documentNumber = serverRequest.pathVariable("documentNumber");
        return userUseCase.getUserByDocumentIdentity(documentNumber)
                .map(userMapper::toInfoResponse)
                .flatMap(userInfoResponse ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userInfoResponse));
    }
}
