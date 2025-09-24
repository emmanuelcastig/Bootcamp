package co.com.pragma.model.bootcamp.consumer;

import reactor.core.publisher.Flux;

public interface CapacidadRestConsumer {
    Flux<CapacidadResponse>listarCapacidades();
}
