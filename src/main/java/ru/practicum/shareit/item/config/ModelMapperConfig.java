package ru.practicum.shareit.item.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        TypeMap<Comment, CommentDto> propertyMapper = mapper.createTypeMap(Comment.class, CommentDto.class);
        propertyMapper.addMappings(
                mapping -> mapping.map(src -> src.getUser().getName(), CommentDto::setAuthorName)
        );
        return mapper;

    }

}
