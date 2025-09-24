package co.com.pragma.r2dbc;

import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.bootcamp.gateways.BootcampRepository;
import co.com.pragma.r2dbc.entity.BootcampCapacidadEntity;
import co.com.pragma.r2dbc.entity.BootcampEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.utils.BootcampCustomRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

}
