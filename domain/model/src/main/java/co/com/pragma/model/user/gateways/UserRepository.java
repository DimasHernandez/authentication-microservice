package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {

    Mono<User> registerUser(User user);

    Mono<Boolean> existsUserEmail(String email);

    Mono<User> getUserByDocumentIdentity(String documentNumber);

    Mono<User> getUserByEmail(String email);

    Flux<User> getUsersByEmails(List<String> emails);
}
