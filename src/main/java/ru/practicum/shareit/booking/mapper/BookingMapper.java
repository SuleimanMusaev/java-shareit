package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto dto, Item item, User booker) {
        return new Booking(
                dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                item,
                booker,
                BookingStatus.WAITING
        );
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {

        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new ItemShortDto(
                        booking.getItem().getId(),
                        booking.getItem().getName()
                ),
                new UserShortDto(
                        booking.getBooker().getId(),
                        booking.getBooker().getName()
                )
        );
    }
}
