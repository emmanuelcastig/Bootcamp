package co.com.pragma.consumer;

import co.com.pragma.model.bootcamp.consumer.CapacidadResponse;
import co.com.pragma.model.bootcamp.consumer.CapacidadRestConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestConsumer implements CapacidadRestConsumer {
    private final WebClient client;


    @Override
    public Flux<CapacidadResponse> listarCapacidades() {
        return client
                .get()
                .uri("/api/v1/capacidades")
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("Error servidor: " + response.statusCode())))
                .bodyToFlux(CapacidadResponse.class);
    }

    @Override
    public Mono<Void> eliminarCapacidadHuerfana(Long id) {
        log.info("eliminando capacidad Huerfana " + id);
        return client
                .delete()
                .uri("/api/v1/capacidades/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("Error consumiendo cliente: " + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("Error consumiendo servidor: " + response.statusCode())))
                .bodyToMono(Void.class);
    }
}
