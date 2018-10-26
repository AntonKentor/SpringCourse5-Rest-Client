package guru.springframework.services;

        import guru.springframework.api.domain.User;
        import guru.springframework.api.domain.UserData;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.http.MediaType;
        import org.springframework.stereotype.Service;
        import org.springframework.web.client.RestTemplate;
        import org.springframework.web.reactive.function.client.WebClient;
        import org.springframework.web.util.UriComponentsBuilder;
        import reactor.core.publisher.Flux;
        import reactor.core.publisher.Mono;

        import java.util.List;

@Service
public class ApiServiceImpl implements ApiService {

    private RestTemplate restTemplate;

    private final String api_url;

    public ApiServiceImpl(RestTemplate restTemplate, @Value("${api.uri}") String api_url) {
        this.restTemplate = restTemplate;
        this.api_url = api_url;
    }

    @Override
    public List<User> getUsers(int limit) {

        //Non reactive way

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromUriString(api_url)
                .queryParam("limit", limit);

        //Jackson is taking care of binding pojos to our datamodel.
        UserData userData = restTemplate.getForObject(uriComponentsBuilder.toUriString(), UserData.class);
        return userData.getData();
    }

    @Override
    public Flux<User> getUsers(Mono<Integer> limit) {

        //Reactive way.

        //This will not be executed until its back on the thymeleaf template. When, for example, you will iterate
        //the data in userlist.html.

        return WebClient    //Webclient is the new reactive way.
                .create(api_url)
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("limit", limit.block()).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(resp -> resp.bodyToMono(UserData.class)) // Same as row 36. mapping to UserData.class.
                .flatMapIterable(UserData::getData);
    }
}
