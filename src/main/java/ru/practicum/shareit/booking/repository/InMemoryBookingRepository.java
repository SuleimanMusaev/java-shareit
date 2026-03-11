package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryBookingRepository implements BookingRepository {
    private final Map<Long, Booking> bookings = new HashMap<>();
    private long idCounter = 1;

    @Override
    public Booking save(Booking booking) {
        booking.setId(idCounter++);
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Booking findById(Long id) {
        return bookings.get(id);
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getBooker().getId().equals(bookerId))
                .toList();
    }

    @Override
    public List<Booking> findByOwnerId(Long ownerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getItem().getOwner().getId().equals(ownerId))
                .toList();
    }
}
