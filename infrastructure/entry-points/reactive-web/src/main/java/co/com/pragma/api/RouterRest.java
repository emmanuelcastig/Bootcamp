package co.com.pragma.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/bootcamp"), handler::crearBootcamp)
                .andRoute(GET("/api/v1/bootcamp/paginado"), handler::obtenerBootcampsPaginados)
                .andRoute(DELETE("/api/v1/bootcamp/{id}"), handler::eliminarBootcamp)
                .andRoute(GET("/api/v1/bootcamp"), handler::obtenerTodosLosBootcamps);
    }
}
