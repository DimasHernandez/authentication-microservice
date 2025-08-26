package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> registerUser(User user);

    Mono<Boolean> existsUserEmail(String email);
}
