package co.com.pragma.model.bootcamp.consumer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadRestConsumer {
    Flux<CapacidadResponse>listarCapacidades();
    Mono<Void> eliminarCapacidadHuerfana(Long id);
}
