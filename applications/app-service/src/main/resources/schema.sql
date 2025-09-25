CREATE TABLE IF NOT EXISTS bootcamps (
                                         id BIGSERIAL PRIMARY KEY,
                                         nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(90) NOT NULL,
    fecha_lanzamiento DATE NOT NULL,
    duracion INT NOT NULL
    );

CREATE TABLE IF NOT EXISTS bootcamp_capacidad (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  id_bootcamp BIGINT NOT NULL,
                                                  id_capacidad BIGINT NOT NULL,
                                                  CONSTRAINT fk_bootcamp FOREIGN KEY (id_bootcamp) REFERENCES bootcamps(id) ON DELETE CASCADE
    );