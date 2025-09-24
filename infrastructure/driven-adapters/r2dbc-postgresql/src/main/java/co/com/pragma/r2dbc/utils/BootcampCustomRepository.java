package co.com.pragma.r2dbc.utils;

import co.com.pragma.r2dbc.entity.BootcampEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BootcampCustomRepository {

    private final DatabaseClient databaseClient;

    public Flux<BootcampEntity> findBootcampsPaged(String sortBy, String order, int size, int offset) {
        String orderColumn;
        if ("nombre".equalsIgnoreCase(sortBy)) {
            orderColumn = "b.nombre";
        } else if ("capacidades".equalsIgnoreCase(sortBy)) {
            orderColumn = "(SELECT COUNT(*) FROM bootcamp_capacidad bc WHERE bc.id_bootcamp = b.id)";
        } else {
            orderColumn = "b.id";
        }

        String sql = String.format("""
            SELECT b.id, b.nombre, b.descripcion, b.fecha_lanzamiento, b.duracion
            FROM bootcamps b
            ORDER BY %s %s
            LIMIT %d OFFSET %d
        """, orderColumn, order.equalsIgnoreCase("desc") ? "DESC" : "ASC", size, offset);

        log.info("SQL generado: {}", sql);

        return databaseClient.sql(sql)
                .map((row, metadata) -> BootcampEntity.builder()
                        .id(row.get("id", Long.class))
                        .nombre(row.get("nombre", String.class))
                        .descripcion(row.get("descripcion", String.class))
                        .fechaLanzamiento(row.get("fecha_lanzamiento", LocalDate.class))
                        .duracion(row.get("duracion", String.class))
                        .build()
                )
                .all();
    }
}