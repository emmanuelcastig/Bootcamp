package co.com.pragma.usecase.bootcamp;

import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.bootcamp.BootcampResponse;
import co.com.pragma.model.bootcamp.CapacidadBootcampResponse;
import co.com.pragma.model.bootcamp.consumer.CapacidadResponse;
import co.com.pragma.model.bootcamp.consumer.CapacidadRestConsumer;
import co.com.pragma.model.bootcamp.gateways.BootcampRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
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

    public Flux<BootcampResponse> obtenerBootcampsPaginadas(int page, int size, String sortBy, String order) {
        return bootcampRepository.obtenerBootcampsPaginados(page, size, sortBy, order)
                .flatMap(bootcamp ->
                        capacidadRestConsumer.listarCapacidades().collectMap(CapacidadResponse::getId, c -> c)
                                .map(map -> {
                                    var capacidades = bootcamp.getCapacidades().stream()
                                            .map(map::get)
                                            .filter(c -> c != null)
                                            .map(c -> CapacidadBootcampResponse.builder()
                                                    .id(c.getId())
                                                    .nombre(c.getNombre())
                                                    .tecnologias(c.getTecnologias())
                                                    .build()
                                            )
                                            .toList();

                                    return BootcampResponse.builder()
                                            .id(bootcamp.getId())
                                            .nombre(bootcamp.getNombre())
                                            .descripcion(bootcamp.getDescripcion())
                                            .fechaLanzamiento(bootcamp.getFechaLanzamiento())
                                            .duracion(bootcamp.getDuracion())
                                            .capacidades(capacidades)
                                            .build();
                                })
                );
    }

    public Mono<Void> eliminarBootcamp(Long id) {
        return bootcampRepository.eliminarBootcamp(id)
                .flatMap(capacidadRestConsumer::eliminarCapacidadHuerfana
                )
                .then();
    }

    public Flux<BootcampResponse> obtenerTodosLosBootcamps() {
        return bootcampRepository.obtenerTodosLosBootcamps()
                .flatMap(bootcamp ->
                        capacidadRestConsumer.listarCapacidades().collectMap(CapacidadResponse::getId, c -> c)
                                .map(map -> {
                                    var capacidades = bootcamp.getCapacidades().stream()
                                            .map(map::get)
                                            .filter(c -> c != null)
                                            .map(c -> CapacidadBootcampResponse.builder()
                                                    .id(c.getId())
                                                    .nombre(c.getNombre())
                                                    .tecnologias(c.getTecnologias())
                                                    .build()
                                            )
                                            .toList();

                                    return BootcampResponse.builder()
                                            .id(bootcamp.getId())
                                            .nombre(bootcamp.getNombre())
                                            .descripcion(bootcamp.getDescripcion())
                                            .fechaLanzamiento(bootcamp.getFechaLanzamiento())
                                            .duracion(bootcamp.getDuracion())
                                            .capacidades(capacidades)
                                            .build();
                                })
                );
    }

}
