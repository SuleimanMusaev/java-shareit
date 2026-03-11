package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByItem_Owner_Id(Long ownerId, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort
    );

    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime time, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime time, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end,
            Sort sort
    );

    Optional<Booking> findFirstByItem_IdAndStatusAndStartIsBeforeOrderByStartDesc(
            Long itemId,
            BookingStatus status,
            LocalDateTime now
    );

    Optional<Booking> findFirstByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(
            Long itemId,
            BookingStatus status,
            LocalDateTime now
    );

    boolean existsByItem_IdAndBooker_IdAndEndBeforeAndStatus(
            Long itemId,
            Long userId,
            LocalDateTime time,
            BookingStatus status
    );
}
