package com.vhs.rental.vhsrentalsystem.service;

import com.vhs.rental.vhsrentalsystem.dto.VhsRequestDto;
import com.vhs.rental.vhsrentalsystem.dto.VhsResponseDto;
import com.vhs.rental.vhsrentalsystem.exception.BusinessLogicException;
import com.vhs.rental.vhsrentalsystem.exception.ResourceNotFoundException;
import com.vhs.rental.vhsrentalsystem.mapper.VhsMapper;
import com.vhs.rental.vhsrentalsystem.model.Vhs;
import com.vhs.rental.vhsrentalsystem.repository.RentalRepository;
import com.vhs.rental.vhsrentalsystem.repository.VhsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the VhsService class.
 * Uses Mockito to mock repository and mapper dependencies.
 */
@ExtendWith(MockitoExtension.class)
class VhsServiceTest {

    @Mock
    private VhsRepository vhsRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private VhsMapper vhsMapper;

    @InjectMocks
    private VhsService vhsService;

    @Test
    void testSaveVhs_Success() {
        VhsRequestDto vhsRequestDto = new VhsRequestDto();
        vhsRequestDto.setTitle("Terminator");
        vhsRequestDto.setGenre("Sci-Fi");
        vhsRequestDto.setReleaseYear(1984);

        Vhs vhsToSave = new Vhs();
        vhsToSave.setTitle("Terminator");

        Vhs savedVhs = new Vhs();
        savedVhs.setId(1L);
        savedVhs.setTitle("Terminator");

        when(vhsMapper.toEntity(vhsRequestDto)).thenReturn(vhsToSave);
        when(vhsRepository.save(vhsToSave)).thenReturn(savedVhs);
        when(vhsMapper.toDto(savedVhs)).thenReturn(new VhsResponseDto());

        VhsResponseDto result = vhsService.saveVhs(vhsRequestDto);

        assertNotNull(result);
        verify(vhsMapper, times(1)).toEntity(vhsRequestDto);
        verify(vhsRepository, times(1)).save(vhsToSave);
        verify(vhsMapper, times(1)).toDto(savedVhs);
    }

    @Test
    void testFindAllVhs_NoFilters() {
        String genre = null;
        Integer releaseYear = null;
        when(vhsRepository.findAll()).thenReturn(Collections.singletonList(new Vhs()));
        when(vhsMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(new VhsResponseDto()));

        vhsService.findAllVhs(genre, releaseYear);

        verify(vhsRepository, times(1)).findAll();
        verify(vhsRepository, never()).findByGenre(any());
        verify(vhsRepository, never()).findByReleaseYear(anyInt());
        verify(vhsRepository, never()).findByGenreAndReleaseYear(any(), anyInt());
        verify(vhsMapper, times(1)).toDtoList(anyList());
    }

    @Test
    void testFindAllVhs_WithGenreFilterOnly() {
        String genre = "Sci-Fi";
        Integer releaseYear = null;
        when(vhsRepository.findByGenre(genre)).thenReturn(Collections.singletonList(new Vhs()));
        when(vhsMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(new VhsResponseDto()));

        vhsService.findAllVhs(genre, releaseYear);

        verify(vhsRepository, never()).findAll();
        verify(vhsRepository, times(1)).findByGenre(genre);
        verify(vhsRepository, never()).findByReleaseYear(anyInt());
        verify(vhsRepository, never()).findByGenreAndReleaseYear(any(), anyInt());
        verify(vhsMapper, times(1)).toDtoList(anyList());
    }

    @Test
    void testFindAllVhs_WithYearFilterOnly() {
        String genre = null;
        Integer releaseYear = 1984;
        when(vhsRepository.findByReleaseYear(releaseYear)).thenReturn(Collections.singletonList(new Vhs()));
        when(vhsMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(new VhsResponseDto()));

        vhsService.findAllVhs(genre, releaseYear);

        verify(vhsRepository, never()).findAll();
        verify(vhsRepository, never()).findByGenre(any());
        verify(vhsRepository, times(1)).findByReleaseYear(releaseYear);
        verify(vhsRepository, never()).findByGenreAndReleaseYear(any(), anyInt());
        verify(vhsMapper, times(1)).toDtoList(anyList());
    }

    @Test
    void testFindAllVhs_WithGenreAndYearFilters() {
        String genre = "Sci-Fi";
        Integer releaseYear = 1984;
        when(vhsRepository.findByGenreAndReleaseYear(genre, releaseYear)).thenReturn(Collections.singletonList(new Vhs()));
        when(vhsMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(new VhsResponseDto()));

        vhsService.findAllVhs(genre, releaseYear);

        verify(vhsRepository, never()).findAll();
        verify(vhsRepository, never()).findByGenre(any());
        verify(vhsRepository, never()).findByReleaseYear(anyInt());
        verify(vhsRepository, times(1)).findByGenreAndReleaseYear(genre, releaseYear);
        verify(vhsMapper, times(1)).toDtoList(anyList());
    }

    @Test
    void testFindVhsById_Success() {
        Long vhsId = 1L;
        Vhs vhs = new Vhs();
        vhs.setId(vhsId);

        when(vhsRepository.findById(vhsId)).thenReturn(Optional.of(vhs));
        when(vhsMapper.toDto(vhs)).thenReturn(new VhsResponseDto());

        VhsResponseDto result = vhsService.findVhsById(vhsId);

        assertNotNull(result);
        verify(vhsRepository, times(1)).findById(vhsId);
        verify(vhsMapper, times(1)).toDto(vhs);
    }

    @Test
    void testFindVhsById_Fails_WhenNotFound() {
        Long vhsId = 99L;
        when(vhsRepository.findById(vhsId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            vhsService.findVhsById(vhsId);
        });

        assertEquals("vhs.notfound", exception.getMessage());
        verify(vhsMapper, never()).toDto(any());
    }

    @Test
    void testDeleteVhs_Success() {
        Long vhsId = 1L;
        Vhs vhs = new Vhs(); vhs.setId(vhsId);

        when(vhsRepository.findById(vhsId)).thenReturn(Optional.of(vhs));
        when(rentalRepository.existsByVhsAndReturnDateIsNull(vhs)).thenReturn(false);
        doNothing().when(vhsRepository).delete(vhs);

        vhsService.deleteVhs(vhsId);

        verify(vhsRepository, times(1)).findById(vhsId);
        verify(rentalRepository, times(1)).existsByVhsAndReturnDateIsNull(vhs);
        verify(vhsRepository, times(1)).delete(vhs);
    }


    @Test
    void testDeleteVhs_Fails_WhenVhsNotFound() {
        Long vhsId = 99L;
        when(vhsRepository.findById(vhsId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            vhsService.deleteVhs(vhsId);
        });

        assertEquals("vhs.notfound", exception.getMessage());
        verify(rentalRepository, never()).existsByVhsAndReturnDateIsNull(any());
        verify(vhsRepository, never()).delete(any());
    }

    @Test
    void testDeleteVhs_Fails_WhenVhsIsRented() {
        Long vhsId = 1L;
        Vhs vhs = new Vhs(); vhs.setId(vhsId); vhs.setTitle("Rented Movie");

        when(vhsRepository.findById(vhsId)).thenReturn(Optional.of(vhs));
        when(rentalRepository.existsByVhsAndReturnDateIsNull(vhs)).thenReturn(true);

        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            vhsService.deleteVhs(vhsId);
        });

        assertEquals("vhs.isRented", exception.getMessage());
        verify(vhsRepository, never()).delete(any());
    }

    @Test
    void testFindNewReleases() {
        Vhs vhs1 = new Vhs(); vhs1.setId(1L);
        Vhs vhs2 = new Vhs(); vhs2.setId(2L);
        List<Vhs> vhsList = List.of(vhs1, vhs2);
        List<VhsResponseDto> dtoList = List.of(new VhsResponseDto(), new VhsResponseDto());

        when(vhsRepository.findLatestTenByOrderByReleaseYearDesc()).thenReturn(vhsList);
        when(vhsMapper.toDtoList(vhsList)).thenReturn(dtoList);

        List<VhsResponseDto> result = vhsService.findNewReleases();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(vhsRepository, times(1)).findLatestTenByOrderByReleaseYearDesc();
        verify(vhsMapper, times(1)).toDtoList(vhsList);
    }
}