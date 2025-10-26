package com.vhs.rental.vhsrentalsystem.controller;

import com.vhs.rental.vhsrentalsystem.dto.VhsRequestDto;
import com.vhs.rental.vhsrentalsystem.dto.VhsResponseDto;
import com.vhs.rental.vhsrentalsystem.service.VhsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * REST Controller for managing Vhs operations.
 * Exposes endpoints for CRUD operations, filtering, and fetching new releases.
 */
@RestController
@RequestMapping("/api/vhs")
public class VhsController {

    private final VhsService vhsService;

    @Autowired
    public VhsController(VhsService vhsService) {
        this.vhsService = vhsService;
    }

    @GetMapping
    public ResponseEntity<List<VhsResponseDto>> getAllVhs(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false, name = "year") Integer releaseYear
    ){
        List<VhsResponseDto> vhsList = vhsService.findAllVhs(genre, releaseYear);
        return ResponseEntity.ok(vhsList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VhsResponseDto> getVhsById(@PathVariable Long id){
        VhsResponseDto vhs = vhsService.findVhsById(id);
        return ResponseEntity.ok(vhs);
    }

    @PostMapping
    public ResponseEntity<VhsResponseDto> createVhs(@Valid @RequestBody VhsRequestDto vhsRequestDto){
        VhsResponseDto savedVhs = vhsService.saveVhs(vhsRequestDto);

        return new ResponseEntity<>(savedVhs, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVhs(@PathVariable Long id){
        vhsService.deleteVhs(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/new-releases")
    public ResponseEntity<List<VhsResponseDto>> getNewReleases() {
        List<VhsResponseDto> newReleases = vhsService.findNewReleases();
        return ResponseEntity.ok(newReleases);
    }
}