package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.AccessToken;
import co.com.pragma.model.auth.UserCredential;
import co.com.pragma.model.auth.gateways.JwtGateway;
import co.com.pragma.model.exceptions.InvalidCredentialsException;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.user.gateways.LoggerRepository;
import co.com.pragma.model.user.gateways.PasswordEncoderGateway;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUseCase {

    private final UserRepository userRepository;
    private final RolRepository roleRepository;
    private final PasswordEncoderGateway passwordEncoder;
    private final JwtGateway jwtGateway;
    private final LoggerRepository logger;

    public Mono<AccessToken> login(UserCredential userCredential) {
        return userRepository.getUserByEmail(userCredential.getEmail())
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Correo electronico o contraseña incorrectos")))
                .filter(user -> passwordEncoder.matchesPassword(userCredential.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Correo electronico o contraseña incorrectos")))
                .flatMap(user ->
                        roleRepository.findRoleById(user.getRoleId())
                                .switchIfEmpty(Mono.error(new RoleNotFoundException("Role no encontrado")))
                                .map(role -> {
                                    String accessToken = jwtGateway.generateToken(user, role.getRoleType().getEnglishName());
                                    return new AccessToken(accessToken);
                                })
                                .doOnSuccess(token ->
                                        logger.info("Usuario autenticado: {} (documento: {}) - " +
                                                "Token generado correctamente", user.getEmail(), user.getDocumentNumber()))
                );
    }


}
