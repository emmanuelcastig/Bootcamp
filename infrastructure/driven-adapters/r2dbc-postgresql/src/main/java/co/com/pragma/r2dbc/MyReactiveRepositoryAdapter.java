package co.com.pragma.r2dbc;

import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.bootcamp.gateways.BootcampRepository;
import co.com.pragma.r2dbc.entity.BootcampCapacidadEntity;
import co.com.pragma.r2dbc.entity.BootcampEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.utils.BootcampCustomRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Bootcamp,
        BootcampEntity,
        Long,
        MyReactiveRepository
        > implements BootcampRepository {

    private final BootcampCapacidadReactiveRepository bootcampCapacidadReactiveRepository;
    private final BootcampCustomRepository bootcampCustomRepository;

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository,
                                       BootcampCapacidadReactiveRepository bootcampCapacidadReactiveRepository,
                                       ObjectMapper mapper, BootcampCustomRepository bootcampCustomRepository) {

        super(repository, mapper, d -> mapper.map(d, Bootcamp.class));
        this.bootcampCapacidadReactiveRepository = bootcampCapacidadReactiveRepository;
        this.bootcampCustomRepository = bootcampCustomRepository;
    }

    @Override
    public Mono<Void> crearBootcamp(Bootcamp bootcamp) {

        BootcampEntity entity = new BootcampEntity(null,bootcamp.getNombre(), bootcamp.getDescripcion(),
                bootcamp.getFechaLanzamiento(), bootcamp.getDuracion());

        return repository.save(entity)
                .flatMap(savedEntity -> Flux.fromIterable(bootcamp.getCapacidades())
                        .map(idCapacidad -> BootcampCapacidadEntity.builder()
                                .idBootcamp(savedEntity.getId())
                                .idCapacidad(idCapacidad)
                                .build())
                        .collectList()
                        .flatMapMany(bootcampCapacidad ->
                                bootcampCapacidadReactiveRepository.saveAll(bootcampCapacidad)
                        )
                        .then());
    }

    @Override
    public Flux<Bootcamp> obtenerBootcampsPaginados(int page, int size, String sortBy, String order) {
        int offset = page * size;

        return bootcampCustomRepository.findBootcampsPaged(sortBy, order, size, offset)
                .concatMap(entity ->
                        repository.findCapacidadByBootcamp(entity.getId())
                                .collectList()
                                .map(capacidades -> Bootcamp.builder()
                                        .id(entity.getId())
                                        .nombre(entity.getNombre())
                                        .descripcion(entity.getDescripcion())
                                        .fechaLanzamiento(entity.getFechaLanzamiento())
                                        .duracion(entity.getDuracion())
                                        .capacidades(capacidades)
                                        .build()
                                )
                );
    }

    @Override
    public Flux<Long> eliminarBootcamp(Long id) {
        return repository.findCapacidadByBootcamp(id)
                .collectList()
                .flatMapMany(capacidades ->
                        // Eliminar primero el bootcamp
                        repository.deleteById(id)
                                .thenMany(Flux.fromIterable(capacidades))
                )
                .flatMap(capacidadId ->
                        bootcampCapacidadReactiveRepository.findAllByIdCapacidad(capacidadId)
                                .count()
                                .flatMapMany(count -> {
                                    if (count == 0) {
                                        log.info("Se envia la capacidad: " + capacidadId);
                                        return Flux.just(capacidadId); // huérfana → devolverla
                                    } else {
                                        return Flux.empty(); // sigue asociada
                                    }
                                })
                );
    }

    @Override
    public Flux<Bootcamp> obtenerTodosLosBootcamps() {
        return repository.findAll()
                .concatMap(entity ->
                        repository.findCapacidadByBootcamp(entity.getId())
                                .collectList()
                                .map(capacidades -> Bootcamp.builder()
                                        .id(entity.getId())
                                        .nombre(entity.getNombre())
                                        .descripcion(entity.getDescripcion())
                                        .fechaLanzamiento(entity.getFechaLanzamiento())
                                        .duracion(entity.getDuracion())
                                        .capacidades(capacidades)
                                        .build()
                                )
                );
    }

}
