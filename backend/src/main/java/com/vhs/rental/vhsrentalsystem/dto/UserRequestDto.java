package com.vhs.rental.vhsrentalsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 * Data Transfer Object for creating or updating a User.
 * Contains validated user details.
 */
@Data
public class UserRequestDto {

    @NotBlank(message = "{user.name.notblank}")
    private String name;

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.invalid}")
    private String email;
}
