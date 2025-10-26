package com.vhs.rental.vhsrentalsystem.controller;

import com.vhs.rental.vhsrentalsystem.dto.RentalRequestDto;
import com.vhs.rental.vhsrentalsystem.dto.RentalResponseDto;
import com.vhs.rental.vhsrentalsystem.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * REST Controller for managing Rental operations.
 * Exposes endpoints for getting, creating (renting), and updating (returning) rentals.
 */
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public ResponseEntity<List<RentalResponseDto>> getAllRentals() {
        List<RentalResponseDto> rentals = rentalService.findAllRentals();
        return ResponseEntity.ok(rentals);
    }

    @PostMapping
    public ResponseEntity<RentalResponseDto> rentVhs(@Valid @RequestBody RentalRequestDto rentalRequestDto){
        RentalResponseDto newRental = rentalService.rentVHS(
                rentalRequestDto.getUserId(),
                rentalRequestDto.getVhsId()
        );

        return new ResponseEntity<>(newRental, HttpStatus.CREATED);
    }

    @PostMapping("/return/{rentalId}")
    public ResponseEntity<RentalResponseDto> returnVhs(@PathVariable Long rentalId){
        RentalResponseDto updatedRental = rentalService.returnVHS(rentalId);

        return ResponseEntity.ok(updatedRental);
    }
}