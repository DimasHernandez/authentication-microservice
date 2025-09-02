package co.com.pragma.api;

import co.com.pragma.api.config.UserPath;
import co.com.pragma.api.dto.UserInfoResponse;
import co.com.pragma.api.dto.UserRequest;
import co.com.pragma.api.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class UserRouterRest {

    private final UserPath userPath;

    private final UserHandler userHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    produces = {"application/json"},
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "listenRegisterUser",
                    operation = @Operation(
                            operationId = "registerUser",
                            summary = "Register a new user",
                            description = "Create a user in the system with the information provided.",
                            tags = {"User"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "User registration request",
                                    content = @Content(schema = @Schema(implementation = UserRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "User registered successfully",
                                            content = @Content(schema = @Schema(implementation = UserResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users/{documentNumber}",
                    produces = {"application/json"},
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "listenGetUserByDocumentIdentity",
                    operation = @Operation(
                            operationId = "getUserByDocumentNumber",
                            summary = "Get user by document number",
                            description = "Check user via personal identification document",
                            parameters = {
                                    @Parameter(
                                            name = "documentNumber",
                                            description = "User document number to be consulted",
                                            required = true,
                                            in = ParameterIn.PATH
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User found",
                                            content = @Content(schema = @Schema(implementation = UserInfoResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "User not found",
                                            content = @Content(schema = @Schema(
                                                    example = "{ \"error\": \"Error de negocio\", \"code\": \"USR_004\", \"detail\": \"Usuario no encontrado\" }"
                                            ))
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(schema = @Schema(
                                                    example = "{ \"error\": \"Error interno del servidor\", \"code\": \"GEN_500\", \"detail\": \"Failed r2dbc connection\" }"
                                            ))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users/email/{email}",
                    produces = {"application/json"},
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "listenGetUserByEmail",
                    operation = @Operation(
                            operationId = "getUserByEmail",
                            summary = "Get user by email",
                            description = "Check user via personal email",
                            parameters = {
                                    @Parameter(
                                            name = "email",
                                            description = "User email to be consulted",
                                            required = true,
                                            in = ParameterIn.PATH
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User found",
                                            content = @Content(schema = @Schema(implementation = UserInfoResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "User not found",
                                            content = @Content(schema = @Schema(
                                                    example = "{ \"error\": \"Error de negocio\", \"code\": \"USR_004\", \"detail\": \"Usuario no encontrado\" }"
                                            ))
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(schema = @Schema(
                                                    example = "{ \"error\": \"Error interno del servidor\", \"code\": \"GEN_500\", \"detail\": \"Failed r2dbc connection\" }"
                                            ))
                                    )
                            }
                    )
            )

    })
    public RouterFunction<ServerResponse> userRouterFunction(UserHandler userHandler) {
        return route(POST(userPath.getUsers()), this.userHandler::listenRegisterUser)
                .andRoute(GET(userPath.getUserByDocumentNumber()), this.userHandler::listenGetUserByDocumentIdentity)
                .andRoute(GET(userPath.getUserByEmail()), this.userHandler::listenGetUserByEmail);
    }
}
