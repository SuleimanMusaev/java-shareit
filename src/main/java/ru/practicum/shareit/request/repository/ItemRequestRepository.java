package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestRepository {
    ItemRequest save(ItemRequest request);

    ItemRequest findById(Long id);

    List<ItemRequest> findByRequestorId(Long requestorId);

    List<ItemRequest> findAll();
}
