package co.com.pragma.usecase.user;

import co.com.pragma.model.exceptions.EmailAlreadyRegisteredException;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import co.com.pragma.model.rol.enums.RoleType;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.TransactionalWrapper;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    private final RolRepository rolRepository;

    private final TransactionalWrapper transactionalWrapper;

    public Mono<User> registerUser(User user) {

        return transactionalWrapper.transactional(
                userRepository.existsUserEmail(user.getEmail())
                        .flatMap(emailExists -> {
                            if (Boolean.TRUE.equals(emailExists)) {
                                return Mono.error(new EmailAlreadyRegisteredException("The email address is already registered."));
                            }
                            return assignApplicationRoleAndSave(user);
                        }));
    }

    private Mono<User> assignApplicationRoleAndSave(User user) {
        user.activate();
        user.markCreatedNow();

        return rolRepository
                .findRoleByName(RoleType.APPLICANT)
                .switchIfEmpty(Mono.error(new RoleNotFoundException("Role not found")))
                .flatMap(role -> {
                    user.setRoleId(role.getId());
                    return userRepository.registerUser(user);
                });
    }
}
