package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;

    @NotNull(message = "Имя не может быть пустым")
    private String name;

    @NotNull(message = "Email не может быть пустым")
    @Email(message = "Неправильная валидация Email")
    private String email;
}
