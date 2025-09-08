package co.com.pragma.api;

import co.com.pragma.api.dto.EmailRequest;
import co.com.pragma.api.dto.UserBasicInfo;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private static final String USER_DOCUMENT_NUMBER = "documentNumber";

    private static final String USER_EMAIL = "email";

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
        String documentNumber = serverRequest.pathVariable(USER_DOCUMENT_NUMBER);
        return userUseCase.getUserByDocumentIdentity(documentNumber)
                .map(userMapper::toInfoResponse)
                .flatMap(userInfoResponse ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userInfoResponse));
    }

    public Mono<ServerResponse> listenGetUserByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable(USER_EMAIL);
        return userUseCase.getUserByEmail(email)
                .map(userMapper::toInfoResponse)
                .flatMap(userInfoResponse ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userInfoResponse));
    }

    public Mono<ServerResponse> listenGetUsersByEmailsBatch(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(EmailRequest.class)
                .flatMap(emailRequest -> {
                    if (emailRequest.getEmails().size() > 1000) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("error", "Demasiados correos electronicos");
                        error.put("detail", "El limite máximo es 1000 correos electrónicos por request");

                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(error);
                    }
                    Flux<UserBasicInfo> usersFlux = userUseCase.getUsersByEmails(emailRequest.getEmails())
                            .map(userMapper::toBasicInfo);

                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(usersFlux, UserBasicInfo.class);
                });
    }

}
