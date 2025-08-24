package co.com.pragma.api.errorhandler;


import co.com.pragma.model.exceptions.EmailAlreadyRegisteredException;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import jakarta.validation.ConstraintViolationException;
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

@Component
@Order(-2)
public class GlobalErrorHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (ex instanceof ConstraintViolationException) {
            response.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY);
            return response.writeWith(
                    Mono.just(toBuffer(response, "Validation failed", HttpStatus.UNPROCESSABLE_ENTITY.value(),
                            ex.getMessage())));
        }

        if (ex instanceof WebExchangeBindException) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return response.writeWith(
                    Mono.just(toBuffer(response, "Validation failed", HttpStatus.BAD_REQUEST.value(),
                            ex.getMessage())));
        }

        if (ex instanceof EmailAlreadyRegisteredException) {
            response.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY);
            return response.writeWith(
                    Mono.just(toBuffer(response, "Business error", HttpStatus.UNPROCESSABLE_ENTITY.value(),
                            ex.getMessage())));
        }

        if (ex instanceof RoleNotFoundException) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return response.writeWith(
                    Mono.just(toBuffer(response, "Business error", HttpStatus.NOT_FOUND.value(),
                            ex.getMessage())));
        }

        // Validate errors from database.
        if (ex instanceof RuntimeException r) {
            String message = r.getMessage() != null ? r.getMessage() : "";

            if (message.contains("users_document_number_key")) {
                response.setStatusCode(HttpStatus.CONFLICT);
                return response.writeWith(
                        Mono.just(toBuffer(response, "Conflict", HttpStatus.CONFLICT.value(),
                                "document is already registered"))
                );
            }
        }

        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.writeWith(
                Mono.just(toBuffer(response, "Internal error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage())));
    }

    private DataBuffer toBuffer(ServerHttpResponse response, String error, int status, String detail) {
        String json = "{"
                + "\"error\":\"" + error + "\","
                + "\"status\":\"" + status + "\","
                + "\"detail\":\"" + detail + "\""
                + "}";
        return response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
    }
}
