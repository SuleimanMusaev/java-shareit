package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor().getId(),
                request.getCreated()
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto dto, User user) {
        return new ItemRequest(
                null,
                dto.getDescription(),
                user,
                LocalDateTime.now());
    }
}
