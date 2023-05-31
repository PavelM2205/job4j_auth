package ru.job4j.auth.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class PersonDto {
    @NotBlank(message = "Password must be not empty")
    @Length(min = 3, message = "Password length must be min 3 character")
    private String password;
}
