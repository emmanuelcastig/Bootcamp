package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.BootcampCapacidadEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BootcampCapacidadReactiveRepository extends ReactiveCrudRepository<BootcampCapacidadEntity, Long>, ReactiveQueryByExampleExecutor<BootcampCapacidadEntity> {

}
