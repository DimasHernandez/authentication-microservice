package co.com.pragma.api;

import co.com.pragma.api.config.AuthPath;
import co.com.pragma.api.dto.LoginRequest;
import co.com.pragma.api.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/login",
                    produces = {"application/json"},
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "listenLogin",
                    operation = @Operation(
                            operationId = "Create JWT token",
                            summary = "Create token",
                            description = "Create a token to verify the user's authenticity.",
                            tags = {"Auth"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Create Token",
                                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Token generated successfully",
                                            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                                    @ApiResponse(
                                            responseCode = "422",
                                            description = "Invalid credentials",
                                            content = @Content(schema = @Schema(
                                                    example = "{ \"error\": \"Error de validación\", \"code\": \"AUTH_001\", \"detail\": \"Credenciales invalidas\" }"
                                            ))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> authRouterFunction(AuthHandler authHandler) {
        return route(POST(authPath.getLogin()), this.authHandler::listenLogin);
    }
}
