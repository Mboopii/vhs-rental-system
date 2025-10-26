package com.vhs.rental.vhsrentalsystem.controller;

import com.vhs.rental.vhsrentalsystem.dto.RentalResponseDto;
import com.vhs.rental.vhsrentalsystem.dto.UserRequestDto;
import com.vhs.rental.vhsrentalsystem.dto.UserResponseDto;
import com.vhs.rental.vhsrentalsystem.service.RentalService;
import com.vhs.rental.vhsrentalsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * REST Controller for managing User operations.
 * Exposes endpoints for CRUD operations on users and fetching user rental history.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RentalService rentalService;

    @Autowired
    public UserController(UserService userService, RentalService rentalService) {
        this.userService = userService;
        this.rentalService = rentalService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto savedUser = userService.saveUser(userRequestDto);

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequestDto) {

        UserResponseDto updatedUser = userService.updateUser(id, userRequestDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}/rentals")
    public ResponseEntity<List<RentalResponseDto>> getUserRentalHistory(@PathVariable Long id) {
        List<RentalResponseDto> rentalHistory = rentalService.findRentalsByUserId(id);
        return ResponseEntity.ok(rentalHistory);
    }
}