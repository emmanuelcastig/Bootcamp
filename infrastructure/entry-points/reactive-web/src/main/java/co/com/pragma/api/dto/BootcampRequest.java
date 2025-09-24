package co.com.pragma.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Data
public class BootcampRequest {

    @NotBlank(message = "El nombre es obligatorio y no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria y no puede estar vacía")
    private String descripcion;

    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    @FutureOrPresent(message = "La fecha de lanzamiento no puede estar en el pasado")
    private LocalDate fechaLanzamiento;

    @NotBlank(message = "La duración es obligatoria")
    private String duracion;

    @NotEmpty(message = "Debe seleccionar al menos  tecnologías")
    @Size(min = 1, max = 4, message = "Debe seleccionar entre 1 y 4 capacidades")
    private List<Long> capacidades;
}
