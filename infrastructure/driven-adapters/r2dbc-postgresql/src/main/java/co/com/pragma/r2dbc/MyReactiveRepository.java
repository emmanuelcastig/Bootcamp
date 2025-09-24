package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.BootcampEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MyReactiveRepository extends ReactiveCrudRepository<BootcampEntity, Long>, ReactiveQueryByExampleExecutor<BootcampEntity> {
    @Query("""
        SELECT bc.id_capacidad 
        FROM bootcamp_capacidad bc
        WHERE bc.id_bootcamp = :bootcampId
        """)
    Flux<Long> findCapacidadByBootcamp(Long bootcampId);
}
