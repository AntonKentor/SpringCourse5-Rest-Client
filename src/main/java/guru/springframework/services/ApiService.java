package guru.springframework.services;

import guru.springframework.api.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ApiService {

    List<User> getUsers(int limit);

    Flux<User> getUsers(Mono<Integer> limit);
}
