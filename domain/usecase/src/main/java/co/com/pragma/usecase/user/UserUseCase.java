package co.com.pragma.usecase.user;

import co.com.pragma.model.exceptions.EmailAlreadyRegisteredException;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import co.com.pragma.model.exceptions.UserNotFoundException;
import co.com.pragma.model.rol.enums.RoleType;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.LoggerRepository;
import co.com.pragma.model.user.gateways.TransactionalWrapper;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    private final RolRepository rolRepository;

    private final TransactionalWrapper transactionalWrapper;

    private final LoggerRepository logger;

    public Mono<User> registerUser(User user) {

        return transactionalWrapper.transactional(
                userRepository.existsUserEmail(user.getEmail())
                        .flatMap(emailExists -> {
                            if (Boolean.TRUE.equals(emailExists)) {
                                return Mono.error(
                                        new EmailAlreadyRegisteredException("La direccion del correo electronico ya esta registrada."));
                            }
                            return assignApplicationRoleAndSave(user)
                                    .doOnSuccess(userSaved ->
                                            logger.info("User with id {} registered successfully", userSaved.getId()));
                        }));
    }

    private Mono<User> assignApplicationRoleAndSave(User user) {
        user.activate();
        user.markCreatedNow();

        return rolRepository
                .findRoleByName(RoleType.APPLICANT)
                .switchIfEmpty(Mono.error(new RoleNotFoundException("Rol no encontrado")))
                .flatMap(role -> {
                    user.setRoleId(role.getId());
                    return userRepository.registerUser(user);
                });
    }

    public Mono<User> getUserByDocumentIdentity(String documentNumber) {
        return userRepository.getUserByDocumentIdentity(documentNumber)
                .doOnNext(user -> logger.info("User with id {} found successfully", user.getId()))
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario no encontrado")));
    }
}
