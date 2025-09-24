package co.com.pragma.r2dbc;

import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.bootcamp.gateways.BootcampRepository;
import co.com.pragma.r2dbc.entity.BootcampCapacidadEntity;
import co.com.pragma.r2dbc.entity.BootcampEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
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

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository,
                                       BootcampCapacidadReactiveRepository bootcampCapacidadReactiveRepository,
                                       ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, Bootcamp.class));
        this.bootcampCapacidadReactiveRepository = bootcampCapacidadReactiveRepository;
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

}
