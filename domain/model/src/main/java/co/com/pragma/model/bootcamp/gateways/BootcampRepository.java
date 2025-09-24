package co.com.pragma.model.bootcamp.gateways;

import co.com.pragma.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

public interface BootcampRepository {
    Mono<Void> crearBootcamp(Bootcamp bootcamp);
}
