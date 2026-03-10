package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
//    User save(User user);
//
//    User update(User user);
//
//    Optional<User> findById(Long id);
//
//    List<User> findAll();
//
    Optional<User> findByEmail(String email);
//
//    void delete(Long id);
}
