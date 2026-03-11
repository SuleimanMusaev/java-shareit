package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    User update(User user);

    User findById(Long id);

    List<User> findAll();

    Optional<User> findByEmail(String email);

    void delete(Long id);
}
