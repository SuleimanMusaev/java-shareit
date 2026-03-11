package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            log.error("Email already exists");
            throw new ConflictException("Email already exists");
        }

        User user = UserMapper.toUser(userDto);

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        checkUser(user);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            userRepository.findByEmail(userDto.getEmail())
                    .filter(u -> !u.getId().equals(userId))
                    .ifPresent(u -> {
                        log.error("Email already exists");
                        throw new ConflictException("Email already exists");
                    });

            user.setEmail(userDto.getEmail());
        }

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", userId);
                    return new NotFoundException("User not found");
                });

        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    void checkUser(User user) {
        if (user == null) {
            log.error("User not found");
            throw new NotFoundException("User not found");
        }
    }
}
