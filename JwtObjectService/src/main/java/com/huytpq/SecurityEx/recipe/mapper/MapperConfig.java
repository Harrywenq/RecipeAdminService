package com.huytpq.SecurityEx.recipe.mapper;

import com.huytpq.SecurityEx.base.mapper.BaseModelMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MapperConfig {

    @Bean
    public BaseModelMapper baseModelMapper() {
        return Mappers.getMapper(BaseModelMapper.class);
    }

    @Bean
    @Primary
    public ModelMapper modelMapper() {
        return Mappers.getMapper(ModelMapper.class);
    }

}
