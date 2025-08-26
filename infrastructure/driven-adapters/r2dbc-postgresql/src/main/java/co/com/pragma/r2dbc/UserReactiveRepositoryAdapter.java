package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entities.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        UUID,
        UserReactiveRepository
        > implements UserRepository {
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, userEntity -> mapper.map(userEntity, User.class));
    }

    @Override
    public Mono<User> registerUser(User user) {
        return super.save(user);
    }

    @Override
    public Mono<Boolean> existsUserEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<User> getUserByDocumentIdentity(String documentNumber) {
        return repository.findByDocumentNumber(documentNumber);
    }
}
