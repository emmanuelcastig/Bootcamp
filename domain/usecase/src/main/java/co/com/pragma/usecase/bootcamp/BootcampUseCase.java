package co.com.pragma.usecase.bootcamp;

import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.bootcamp.consumer.CapacidadResponse;
import co.com.pragma.model.bootcamp.consumer.CapacidadRestConsumer;
import co.com.pragma.model.bootcamp.gateways.BootcampRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BootcampUseCase {

    private final BootcampRepository bootcampRepository;
    private final CapacidadRestConsumer capacidadRestConsumer;

    public Mono<Void> crearBootcamp(Bootcamp bootcamp) {
        return capacidadRestConsumer.listarCapacidades()
                .map(CapacidadResponse::getId)
                .collectList()
                .flatMap(idsExistentes -> {
                    if (!idsExistentes.containsAll(bootcamp.getCapacidades())) {
                        return Mono.error(new IllegalArgumentException("Existen capacidades que no están registradas en el sistema"));
                    }
                    return bootcampRepository.crearBootcamp(bootcamp);
                });
    }
}
