package com.vhs.rental.vhsrentalsystem.service;

import com.vhs.rental.vhsrentalsystem.dto.RentalResponseDto;
import com.vhs.rental.vhsrentalsystem.exception.BusinessLogicException;
import com.vhs.rental.vhsrentalsystem.exception.ResourceNotFoundException;
import com.vhs.rental.vhsrentalsystem.mapper.RentalMapper;
import com.vhs.rental.vhsrentalsystem.model.Rental;
import com.vhs.rental.vhsrentalsystem.model.User;
import com.vhs.rental.vhsrentalsystem.model.Vhs;
import com.vhs.rental.vhsrentalsystem.repository.RentalRepository;
import com.vhs.rental.vhsrentalsystem.repository.UserRepository;
import com.vhs.rental.vhsrentalsystem.repository.VhsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
 * Unit tests for the RentalService class.
 * Uses Mockito to mock repository and mapper dependencies.
 */
@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VhsRepository vhsRepository;

    @Mock
    private RentalMapper rentalMapper;

    @InjectMocks
    private RentalService rentalService;

    @Test
    void testRentVHS_Success() {
        Long userId = 1L;
        Long vhsId = 1L;
        User user = new User(); user.setId(userId);
        Vhs vhs = new Vhs(); vhs.setId(vhsId);

        //manually inject value normally provided by @Value
        ReflectionTestUtils.setField(rentalService, "rentalDays", 10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(vhsRepository.findById(vhsId)).thenReturn(Optional.of(vhs));
        when(rentalRepository.findByVhsAndReturnDateIsNull(vhs)).thenReturn(Optional.empty());
        when(rentalRepository.save(any(Rental.class))).then(invocation -> invocation.getArgument(0));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(new RentalResponseDto());

        RentalResponseDto result = rentalService.rentVHS(userId, vhsId);

        assertNotNull(result);
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(rentalMapper, times(1)).toDto(any(Rental.class));
    }

    @Test
    void testRentVHS_Fails_WhenVhsAlreadyRented() {
        Long userId = 1L;
        Long vhsId = 1L;
        User user = new User(); user.setId(userId);
        Vhs vhs = new Vhs(); vhs.setId(vhsId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(vhsRepository.findById(vhsId)).thenReturn(Optional.of(vhs));
        when(rentalRepository.findByVhsAndReturnDateIsNull(vhs)).thenReturn(Optional.of(new Rental()));

        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            rentalService.rentVHS(userId, vhsId);
        });

        assertEquals("vhs.alreadyRented", exception.getMessage());
        verify(rentalRepository, never()).save(any());
    }

    @Test
    void testRentVHS_Fails_WhenVhsNotFound() {
        Long userId = 1L;
        Long vhsId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(vhsRepository.findById(vhsId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            rentalService.rentVHS(userId, vhsId);
        });

        assertEquals("vhs.notfound", exception.getMessage());
    }

    @Test
    void testReturnVHS_Success_WithLateFee() {
        Long rentalId = 1L;
        LocalDate dueDate = LocalDate.now().minusDays(10);
        Rental rental = new Rental();
        rental.setId(rentalId);
        rental.setDueDate(dueDate);
        rental.setVhs(new Vhs());
        rental.setUser(new User());

        //manually inject value normally provided by @Value
        ReflectionTestUtils.setField(rentalService, "rentalFeePerDay", 2.5);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).then(invocation -> invocation.getArgument(0));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(new RentalResponseDto());

        rentalService.returnVHS(rentalId);

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        verify(rentalRepository).save(rentalCaptor.capture());

        Rental savedRental = rentalCaptor.getValue();

        assertNotNull(savedRental.getReturnDate());
        assertNotNull(savedRental.getLateFee());
        assertEquals(25.0, savedRental.getLateFee());
    }

    @Test
    void testReturnVHS_Success_NoLateFee() {
        Long rentalId = 1L;
        LocalDate dueDate = LocalDate.now().plusDays(5);
        Rental rental = new Rental();
        rental.setId(rentalId);
        rental.setDueDate(dueDate);
        rental.setVhs(new Vhs());
        rental.setUser(new User());

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).then(invocation -> invocation.getArgument(0));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(new RentalResponseDto());

        rentalService.returnVHS(rentalId);

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        verify(rentalRepository).save(rentalCaptor.capture());

        Rental savedRental = rentalCaptor.getValue();

        assertNotNull(savedRental.getReturnDate());
        assertNull(savedRental.getLateFee());
    }

    @Test
    void testReturnVHS_Fails_WhenAlreadyReturned() {
        Long rentalId = 1L;
        Rental rental = new Rental();
        rental.setId(rentalId);
        rental.setReturnDate(LocalDate.now().minusDays(1));

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            rentalService.returnVHS(rentalId);
        });

        assertEquals("rental.alreadyReturned", exception.getMessage());
        verify(rentalRepository, never()).save(any());
    }
}