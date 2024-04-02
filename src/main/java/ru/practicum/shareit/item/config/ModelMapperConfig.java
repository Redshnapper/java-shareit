package ru.practicum.shareit.item.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.createTypeMap(Comment.class, CommentDto.class)
                .addMappings(mapping -> mapping.map(src -> src.getUser().getName(), CommentDto::setAuthorName));

        mapper.createTypeMap(Item.class, ItemRequestDto.class)
                .addMappings(mapping -> {
                    mapping.map(Item::getName, ItemRequestDto::setName);
                    mapping.map(Item::getDescription, ItemRequestDto::setDescription);
                    mapping.map(Item::getAvailable, ItemRequestDto::setAvailable);
                    mapping.map(src -> src.getRequest().getId(), ItemRequestDto::setRequestId);
                });

        mapper.createTypeMap(ItemRequestDto.class, Item.class)
                .addMappings(mapping -> {
                    mapping.map(ItemRequestDto::getName, Item::setName);
                    mapping.map(ItemRequestDto::getDescription, Item::setDescription);
                    mapping.map(ItemRequestDto::getAvailable, Item::setAvailable);
                });

        return mapper;
    }
}
