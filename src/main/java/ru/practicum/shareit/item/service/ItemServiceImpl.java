package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found {}", userId);
                    return new NotFoundException("User not found");
                });

        Item item = ItemMapper.toItem(itemDto, owner);

        Item saved = itemRepository.save(item);

        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item not found {}", itemId);
                    return new NotFoundException("Item not found");
                });

        if (!item.getOwner().getId().equals(ownerId)) {
            log.error("Not owner with id={}", item.getOwner().getId());
            throw new AccessDeniedException("Only owner can update item");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item saved = itemRepository.save(item);

        return ItemMapper.toItemDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemWithBookingsDto getById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item not found {}", itemId);
                    return new NotFoundException("Item not found");
                });

        List<CommentDto> comments = commentRepository.findByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .toList();

        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = getLastBooking(itemId);
            nextBooking = getNextBooking(itemId);
        }

        return ItemMapper.toItemWithBookingsDto(
                item,
                lastBooking,
                nextBooking,
                comments
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemWithBookingsDto> getOwnerItems(Long ownerId) {

        List<Item> items = itemRepository.findByOwner_Id(ownerId);

        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = commentRepository
                            .findByItem_Id(item.getId())
                            .stream()
                            .map(CommentMapper::toDto)
                            .toList();

                    BookingShortDto lastBooking = getLastBooking(item.getId());
                    BookingShortDto nextBooking = getNextBooking(item.getId());

                    return ItemMapper.toItemWithBookingsDto(
                            item,
                            lastBooking,
                            nextBooking,
                            comments
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found {}", userId);
                    return new NotFoundException("User not found");
                });

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item not found {}", itemId);
                    return new NotFoundException("Item not found");
                });

        boolean hasBooking = bookingRepository
                .existsByItem_IdAndBooker_IdAndEndBeforeAndStatus(
                itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED
        );

        if (!hasBooking) {
            log.error("User '{}' has not booked this item '{}'", userId, itemId);
            throw new ValidationException("User has not booked this item");
        }

        Comment comment = CommentMapper.toComment(dto, item, author);

        Comment saved = commentRepository.save(comment);

        return CommentMapper.toDto(saved);
    }


    private BookingShortDto getLastBooking(Long itemId) {
        return bookingRepository.findFirstByItem_IdAndStatusAndStartIsBeforeOrderByStartDesc(
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()
                )
                .map(booking -> new BookingShortDto(
                        booking.getId(),
                        booking.getBooker().getId()
                ))
                .orElse(null);
    }

    private BookingShortDto getNextBooking(Long itemId) {
        return bookingRepository.findFirstByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()
                )
                .map(booking -> new BookingShortDto(
                        booking.getId(),
                        booking.getBooker().getId()
                ))
                .orElse(null);
    }
}
