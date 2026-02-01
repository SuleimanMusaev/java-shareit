package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemRequestRepository implements ItemRequestRepository {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private long idCounter = 1;

    @Override
    public ItemRequest save(ItemRequest request) {
        request.setId(idCounter++);
        requests.put(request.getId(), new ItemRequest());
        return request;
    }

    @Override
    public ItemRequest findById(Long id) {
        return requests.get(id);
    }

    @Override
    public List<ItemRequest> findByRequestorId(Long requestorId) {
        return requests.values().stream()
                .filter(request -> request.getId().equals(requestorId))
                .sorted(Comparator.comparing(ItemRequest::getCreated))
                .toList();
    }

    @Override
    public List<ItemRequest> findAll() {
        return requests.values().stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated))
                .toList();
    }
}
