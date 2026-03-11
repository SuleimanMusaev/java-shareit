package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingState.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponseDto create(Long userId, BookingCreateDto bookingCreateDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found {}", userId);
                    return new NotFoundException("User not found");
                });

        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> {
                    log.error("Item not found");
                    return new NotFoundException("Item not found");
                });

        if (!item.getAvailable()) {
            log.error("Item is not available");
            throw new ValidationException("Item is not available");
        }

        if (item.getOwner().getId().equals(userId)) {
            log.error("Owner cannot book own item");
            throw new NotFoundException("Owner cannot book own item");
        }

        if (bookingCreateDto.getStart().isAfter(bookingCreateDto.getEnd()) ||
                bookingCreateDto.getStart().isEqual(bookingCreateDto.getEnd())) {
            throw new ValidationException("Invalid booking time");
        }

        Booking booking = Booking.builder()
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto approve(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Booking not found");
                    return new NotFoundException("Booking not found");
                });


        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.error("Only owner can approve booking");
            throw new AccessDeniedException("Only owner can approve booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            log.error("Booking already processed");
            throw new ValidationException("Booking already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);



        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Booking not found");
                    return new NotFoundException("Booking not found");
                });

        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            log.error("Access denied for userId={} to bookingId={}", userId, bookingId);
            throw new AccessDeniedException("Access denied");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState = from(state);

        switch (bookingState) {
            case ALL:
                return bookingRepository.findByBooker_Id(userId, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();

            case CURRENT:
                return bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                        userId, now, now, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();

            case PAST:
                return bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();

            case FUTURE:
                return bookingRepository.findByBooker_IdAndStartIsAfter(userId, now, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();

            case WAITING:
                return bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();

            case REJECTED:
                return bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();
            default:
                throw new ValidationException("Unexpected value: " + bookingState);
        }
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state) {

        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.error("User Not found '{}'", ownerId);
                    return new NotFoundException("User not found");
                });

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState = from(state);

        switch (bookingState) {
            case ALL:
                return bookingRepository.findByItem_Owner_Id(ownerId, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();

            case CURRENT:
                return bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                        ownerId, now, now, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();

            case PAST:
                return bookingRepository.findByItem_Owner_IdAndEndIsBefore(ownerId, now, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();

            case FUTURE:
                return bookingRepository.findByItem_Owner_IdAndStartIsAfter(ownerId, now, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();

            case WAITING:
                return bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();

            case REJECTED:
                return bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED, sort)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .toList();
            default:
                throw new ValidationException("Unexpected value: " + bookingState);
        }
    }
}
