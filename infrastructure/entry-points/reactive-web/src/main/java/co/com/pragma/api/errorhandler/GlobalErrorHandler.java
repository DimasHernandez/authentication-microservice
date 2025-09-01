package co.com.pragma.api.errorhandler;


import co.com.pragma.model.exceptions.EmailAlreadyRegisteredException;
import co.com.pragma.model.exceptions.InvalidCredentialsException;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import co.com.pragma.model.exceptions.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(-2)
public class GlobalErrorHandler implements WebExceptionHandler {

    private static final String VALIDATION_FAILED = "Fallo validacion";
    private static final String BUSINESS_ERROR = "Error de negocio";
    private static final String UNIQUE_DOCUMENT_NUMBER_DB = "users_document_number_key";

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (ex instanceof ConstraintViolationException) {
            response.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY);
            return response.writeWith(
                    Mono.just(toBuffer(response, VALIDATION_FAILED, HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                    ex.getMessage()))
                            .doOnNext(buffer -> logException(exchange, ex, HttpStatus.UNPROCESSABLE_ENTITY,
                                    false, "ConstraintViolationException")));
        }

        if (ex instanceof WebExchangeBindException) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return response.writeWith(
                    Mono.just(toBuffer(response, VALIDATION_FAILED, HttpStatus.BAD_REQUEST.value(),
                                    ex.getMessage()))
                            .doOnNext(buffer -> logException(exchange, ex, HttpStatus.BAD_REQUEST,
                                    false, "WebExchangeBindException")));
        }

        if (ex instanceof EmailAlreadyRegisteredException) {
            response.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY);
            return response.writeWith(
                    Mono.just(toBuffer(response, BUSINESS_ERROR, HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                    ex.getMessage()))
                            .doOnNext(buffer -> logException(exchange, ex, HttpStatus.UNPROCESSABLE_ENTITY,
                                    false, "EmailAlreadyRegisteredException")));
        }

        if (ex instanceof UserNotFoundException) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return response.writeWith(
                    Mono.just(toBuffer(response, BUSINESS_ERROR, HttpStatus.NOT_FOUND.value(), ex.getMessage()))
                            .doOnNext(buffer -> logException(exchange, ex, HttpStatus.NOT_FOUND,
                                    false, "UserNotFoundException")));
        }

        if (ex instanceof RoleNotFoundException) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return response.writeWith(
                    Mono.just(toBuffer(response, BUSINESS_ERROR, HttpStatus.NOT_FOUND.value(),
                                    ex.getMessage()))
                            .doOnNext(buffer -> logException(exchange, ex, HttpStatus.NOT_FOUND,
                                    false, "RoleNotFoundException")));
        }

        if (ex instanceof IllegalArgumentException) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return response.writeWith(
                    Mono.just(toBuffer(response, VALIDATION_FAILED, HttpStatus.BAD_REQUEST.value(),
                                    ex.getMessage()))
                            .doOnNext(buffer -> logException(exchange, ex, HttpStatus.BAD_REQUEST,
                                    false, "IllegalArgumentException")));
        }

        if (ex instanceof InvalidCredentialsException) {
            response.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY);
            return response.writeWith(
                    Mono.just(toBuffer(response, VALIDATION_FAILED, HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                    ex.getMessage()))
                            .doOnNext(buffer -> logException(exchange, ex, HttpStatus.UNPROCESSABLE_ENTITY,
                                    false, "InvalidCredentialsException")));
        }


        if (ex instanceof RuntimeException r) {
            String message = r.getMessage() != null ? r.getMessage() : "";

            // Validate errors from database.
            if (message.contains(UNIQUE_DOCUMENT_NUMBER_DB)) {
                response.setStatusCode(HttpStatus.CONFLICT);
                return response.writeWith(
                        Mono.just(toBuffer(response, "Conflict", HttpStatus.CONFLICT.value(),
                                        "Documento de identidad ya esta registrado"))
                                .doOnNext(buffer ->
                                        log.warn("DuplicateKeyException Constrain violation detected: " +
                                                        "field document_number duplicated [{} {}]: {}",
                                                exchange.getRequest().getMethod(),
                                                exchange.getRequest().getPath(),
                                                HttpStatus.CONFLICT.value())));
            }
        }

        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.writeWith(
                Mono.just(toBuffer(response, "Internal error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                ex.getMessage()))
                        .doOnNext(buffer -> logException(exchange, ex, HttpStatus.INTERNAL_SERVER_ERROR,
                                true, "Internal Server Error")));
    }

    private DataBuffer toBuffer(ServerHttpResponse response, String error, int status, String detail) {
        String json = "{"
                + "\"error\":\"" + error + "\","
                + "\"status\":\"" + status + "\","
                + "\"detail\":\"" + detail + "\""
                + "}";
        return response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
    }

    private void logException(ServerWebExchange exchange, Throwable ex, HttpStatus status, boolean isError, String customLabel) {
        String message = String.format(
                "%s [%s %s]: %s - Returning HTTP %d",
                customLabel,
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath(),
                ex.getMessage(),
                status.value()
        );

        if (isError) {
            log.error(message, ex);
        } else {
            log.warn(message);
        }
    }
}
