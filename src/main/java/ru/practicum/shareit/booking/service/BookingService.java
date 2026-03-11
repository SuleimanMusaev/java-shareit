package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getBookingsByBooker(Long userId);

    List<BookingDto> getBookingsByOwner(Long ownerId);
}
