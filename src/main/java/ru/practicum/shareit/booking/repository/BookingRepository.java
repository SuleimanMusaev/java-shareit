package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingRepository {
    Booking save(Booking booking);

    Booking findById(Long id);

    List<Booking> findAll();

    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByOwnerId(Long ownerId);
}
