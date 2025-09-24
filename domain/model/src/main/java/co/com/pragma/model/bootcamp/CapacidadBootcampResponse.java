package co.com.pragma.model.bootcamp;

import co.com.pragma.model.bootcamp.consumer.TecnologiaResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacidadBootcampResponse {
    private Long id;
    private String nombre;
    private List<TecnologiaResponse> tecnologias;
}
