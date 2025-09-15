package co.com.pragma.usecase.user;

import co.com.pragma.model.exceptions.EmailAlreadyRegisteredException;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import co.com.pragma.model.exceptions.UserNotFoundException;
import co.com.pragma.model.exceptions.enums.ErrorMessages;
import co.com.pragma.model.rol.enums.RoleType;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.LoggerRepository;
import co.com.pragma.model.user.gateways.PasswordEncoderGateway;
import co.com.pragma.model.user.gateways.TransactionalWrapper;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    private final RolRepository rolRepository;

    private final TransactionalWrapper transactionalWrapper;

    private final PasswordEncoderGateway passwordEncoder;

    private final LoggerRepository logger;

    public Mono<User> registerUser(User user) {

        return transactionalWrapper.transactional(
                userRepository.existsUserEmail(user.getEmail())
                        .flatMap(emailExists -> {
                            if (Boolean.TRUE.equals(emailExists)) {
                                return Mono.error(
                                        new EmailAlreadyRegisteredException(ErrorMessages.EMAIL_ALREADY_REGISTERED.getMessage()));
                            }
                            return assignApplicationRoleHashPasswordAndSave(user)
                                    .doOnSuccess(userSaved ->
                                            logger.info("User with id {} registered successfully", userSaved.getId()));
                        }));
    }

    private Mono<User> assignApplicationRoleHashPasswordAndSave(User user) {
        String hashPassword = passwordEncoder.hashPassword(user.getPassword());
        user.activate();
        user.setPassword(hashPassword);
        user.markCreatedNow();
        user.markLastLoginNow();

        return rolRepository
                .findRoleByName(RoleType.APPLICANT)
                .switchIfEmpty(Mono.error(new RoleNotFoundException(ErrorMessages.ROLE_NOT_FOUND.getMessage())))
                .flatMap(role -> {
                    user.setRoleId(role.getId());
                    return userRepository.registerUser(user);
                });
    }

    public Mono<User> getUserByDocumentIdentity(String documentNumber) {
        return userRepository.getUserByDocumentIdentity(documentNumber)
                .doOnNext(user -> logger.info("User with id {} found successfully", user.getId()))
                .switchIfEmpty(Mono.error(new UserNotFoundException(ErrorMessages.USER_NOT_FOUND.getMessage())));
    }

    public Mono<User> getUserByEmail(String email) {
        return userRepository.getUserByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException(ErrorMessages.USER_NOT_FOUND.getMessage())))
                .doOnSuccess(user -> logger.info("User with email {} found successfully", user.getEmail()));
    }

    public Flux<User> getUsersByEmails(List<String> emails) {
        return userRepository.getUsersByEmails(emails);
    }
}
