package com.vhs.rental.vhsrentalsystem.dto;

import lombok.Data;

/*
 * Data Transfer Object for representing User information in API responses.
 */
@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
}