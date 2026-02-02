package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId);
        Item item = itemRepository.findById(bookingDto.getItemId());

        if (booker == null || item == null) {
            log.error("User or Item not found");
            throw new NotFoundException("User or Item not found");
        }

        if (!item.getAvailable()) {
            log.error("Item is not available");
            throw new NotFoundException("Item is not available");
        }

        if (item.getOwner().getId().equals(userId)) {
            log.error("Owner cannot book own item");
            throw new NotFoundException("Owner cannot book own item");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId);

        chekBooking(booking);

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.error("Only owner can approve booking");
            throw new AccessDeniedException("Only owner can approve booking");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId);

        chekBooking(booking);

        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            log.error("Access denied for userId={} to bookingId={}", userId, bookingId);
            throw new AccessDeniedException("Access denied");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByBooker(Long userId) {
        return bookingRepository.findByBookerId(userId).stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long ownerId) {
        return bookingRepository.findByOwnerId(ownerId).stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    private void chekBooking(Booking booking) {
        if (booking == null) {
            log.error("Booking not found");
            throw new NotFoundException("Booking not found");
        }
    }
}
