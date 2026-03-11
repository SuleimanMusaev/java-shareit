package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {

        Long requestId = null;

        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId
        );
    }

    public static Item toItem(ItemDto dto, User owner) {
        Item item = new Item();

        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);

        return item;
    }

    public static ItemWithBookingsDto toItemWithBookingsDto(
            Item item,
            BookingShortDto lastBooking,
            BookingShortDto nextBooking,
            List<CommentDto> comments
    ) {
        if (item == null) return null;

        return ItemWithBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }
}
