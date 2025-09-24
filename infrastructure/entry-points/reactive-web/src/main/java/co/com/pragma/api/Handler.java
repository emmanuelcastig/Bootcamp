package co.com.pragma.api;

import co.com.pragma.api.dto.BootcampRequest;
import co.com.pragma.api.mapper.BootcampMapper;
import co.com.pragma.model.bootcamp.BootcampResponse;
import co.com.pragma.usecase.bootcamp.BootcampUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Handler {
    private final BootcampUseCase bootcampUseCase;
    private final BootcampMapper bootcampMapper;
    private final Validator validator;
    private final TransactionalOperator transactionalOperator;

    public Mono<ServerResponse> crearBootcamp(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BootcampRequest.class)
                .flatMap(this::validacion)
                .map(bootcampMapper::toDomain)
                .as(transactionalOperator::transactional)
                .flatMap(bootcampUseCase::crearBootcamp)
                .then(ServerResponse.status(201).build())
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getMessage())
                );
    }

    public Mono<ServerResponse> obtenerBootcampsPaginados(ServerRequest serverRequest) {
        int page = Integer.parseInt(serverRequest.queryParam("page").orElse("0"));
        int size = Integer.parseInt(serverRequest.queryParam("size").orElse("10"));
        String sortBy = serverRequest.queryParam("sortBy").orElse("nombre");
        String order = serverRequest.queryParam("order").orElse("asc");
        return ServerResponse.ok()
                .body(
                        bootcampUseCase.obtenerBootcampsPaginadas(page, size, sortBy, order),
                        BootcampResponse.class
                );
    }

    public Mono<BootcampRequest> validacion(BootcampRequest request) {
        Set<ConstraintViolation<BootcampRequest>> violaciones = validator.validate(request);
        if (!violaciones.isEmpty()) {
            String errorMessage = violaciones.stream()
                    .map(violation -> violation.getPropertyPath() + ": " +
                            violation.getMessage())
                    .collect(Collectors.joining(", "));
            return Mono.error(new ValidationException(errorMessage));
        }
        return Mono.just(request);
    }

}
