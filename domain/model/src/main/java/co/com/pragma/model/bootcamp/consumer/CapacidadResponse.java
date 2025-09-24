package co.com.pragma.model.bootcamp.consumer;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacidadResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private List<TecnologiaResponse> tecnologias;
}
