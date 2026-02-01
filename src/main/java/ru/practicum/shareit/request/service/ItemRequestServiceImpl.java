package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        ItemRequest request = new ItemRequest(
                null,
                dto.getDescription(),
                user,
                LocalDateTime.now()
        );

        return ItemRequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getUserRequest(Long userId) {
        return requestRepository.findByRequestorId(userId).stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        return requestRepository.findAll().stream()
                .filter(request -> !request.getRequestor().getId().equals(userId))
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        ItemRequest request = requestRepository.findById(requestId);
        if (request == null) {
            throw new NotFoundException("Request not found");
        }

        return ItemRequestMapper.toDto(request);
    }
}
