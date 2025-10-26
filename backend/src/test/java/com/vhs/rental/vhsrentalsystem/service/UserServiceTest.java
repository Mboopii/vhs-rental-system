package com.vhs.rental.vhsrentalsystem.service;

import com.vhs.rental.vhsrentalsystem.dto.UserRequestDto;
import com.vhs.rental.vhsrentalsystem.dto.UserResponseDto;
import com.vhs.rental.vhsrentalsystem.exception.BusinessLogicException;
import com.vhs.rental.vhsrentalsystem.exception.ResourceNotFoundException;
import com.vhs.rental.vhsrentalsystem.mapper.UserMapper;
import com.vhs.rental.vhsrentalsystem.model.User;
import com.vhs.rental.vhsrentalsystem.repository.RentalRepository;
import com.vhs.rental.vhsrentalsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
 * Unit tests for the UserService class.
 * Uses Mockito to mock repository and mapper dependencies.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testSaveUser_Success() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("test@email.com");
        userRequestDto.setName("Test Ime");

        User userToSave = new User();
        userToSave.setEmail("test@email.com");
        userToSave.setName("Test Ime");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@email.com");
        savedUser.setName("Test Ime");

        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toEntity(userRequestDto)).thenReturn(userToSave);
        when(userRepository.save(userToSave)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(new UserResponseDto());

        UserResponseDto result = userService.saveUser(userRequestDto);

        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail(userRequestDto.getEmail());
        verify(userMapper, times(1)).toEntity(userRequestDto);
        verify(userRepository, times(1)).save(userToSave);
        verify(userMapper, times(1)).toDto(savedUser);
    }

    @Test
    void testSaveUser_Fails_WhenEmailAlreadyExists() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("taken@email.com");
        userRequestDto.setName("Test User");

        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.of(new User()));

        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            userService.saveUser(userRequestDto);
        });

        assertEquals("user.email.exists", exception.getMessage());
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUserById_Success() {
        Long userId = 1L;
        User user = new User(); user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(rentalRepository.existsByUserAndReturnDateIsNull(user)).thenReturn(false); //no active rentals
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(rentalRepository, times(1)).existsByUserAndReturnDateIsNull(user);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUserById_Fails_WhenUserNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUserById(userId);
        });

        assertEquals("user.notfound", exception.getMessage());
        verify(rentalRepository, never()).existsByUserAndReturnDateIsNull(any());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteUserById_Fails_WhenUserHasActiveRentals() {
        Long userId = 1L;
        User user = new User(); user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(rentalRepository.existsByUserAndReturnDateIsNull(user)).thenReturn(true); //user has active rentals

        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            userService.deleteUserById(userId);
        });

        assertEquals("user.hasActiveRentals", exception.getMessage());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void testFindUserById_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(new UserResponseDto());

        UserResponseDto result = userService.findUserById(userId);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testFindUserById_Fails_WhenNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.findUserById(userId);
        });

        assertEquals("user.notfound", exception.getMessage());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testFindAllUsers() {
        List<User> userList = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toDtoList(userList)).thenReturn(List.of(new UserResponseDto(), new UserResponseDto()));

        List<UserResponseDto> results = userService.findAllUsers();

        assertNotNull(results);
        assertEquals(2, results.size());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDtoList(userList);
    }

    @Test
    void testUpdateUser_Success() {
        Long userId = 1L;
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("Updated Name");
        userRequestDto.setEmail("updated@email.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@email.com");

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setName("Updated Name");
        savedUser.setEmail("updated@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(new UserResponseDto());

        UserResponseDto result = userService.updateUser(userId, userRequestDto);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail(userRequestDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("Updated Name", userCaptor.getValue().getName());
        assertEquals("updated@email.com", userCaptor.getValue().getEmail());
    }

    @Test
    void testUpdateUser_Fails_WhenUserNotFound() {
        Long userId = 99L;
        UserRequestDto userRequestDto = new UserRequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(userId, userRequestDto);
        });

        assertEquals("user.notfound", exception.getMessage());
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_Fails_WhenEmailExistsForAnotherUser() {
        Long userId = 1L;
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("taken@email.com");

        User currentUser = new User(); currentUser.setId(userId);
        User otherUser = new User(); otherUser.setId(2L); otherUser.setEmail("taken@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.of(otherUser)); //email is taken by user 2

        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            userService.updateUser(userId, userRequestDto);
        });

        assertEquals("user.email.exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_Success_WhenEmailIsSameUser() {
        Long userId = 1L;
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("Updated Name");
        userRequestDto.setEmail("my.own@email.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("my.own@email.com"); //same email

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        //simulate finding the user itself when checking the email
        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser); //return same user after save
        when(userMapper.toDto(existingUser)).thenReturn(new UserResponseDto());

        UserResponseDto result = userService.updateUser(userId, userRequestDto);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class)); //ensure save was called
    }

}