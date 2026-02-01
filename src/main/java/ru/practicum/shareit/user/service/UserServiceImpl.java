package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists");
        }
        User user = new User(null, userDto.getName(), userDto.getEmail());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {

        User existing = userRepository.findById(userId);
        if (existing == null) {
            throw new NotFoundException("User not found");
        }

        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            userRepository.findByEmail(userDto.getEmail())
                    .filter(u -> !u.getId().equals(userId))
                    .ifPresent(u -> {
                        throw new ConflictException("Email already exists");
                    });
            existing.setEmail(userDto.getEmail());
        }

        return UserMapper.toUserDto(userRepository.update(existing));
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }
}
