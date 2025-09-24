package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.BootcampRequest;
import co.com.pragma.model.bootcamp.Bootcamp;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BootcampMapper {
    Bootcamp toDomain(BootcampRequest request);

}
