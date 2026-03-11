package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found");
                    return new NotFoundException("User not found");
                });

        ItemRequest request = ItemRequestMapper.toItemRequest(dto, user);

        return ItemRequestMapper.toItemRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getUserRequest(Long userId) {
        return requestRepository.findByRequestorId(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        return requestRepository.findAll().stream()
                .filter(request -> !request.getRequestor().getId().equals(userId))
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        Optional<ItemRequest> request = requestRepository.findById(requestId);

        return ItemRequestMapper.toItemRequestDto(request.orElse(null));
    }
}
