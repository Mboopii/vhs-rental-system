package com.vhs.rental.vhsrentalsystem.service;

import com.vhs.rental.vhsrentalsystem.dto.UserRequestDto;
import com.vhs.rental.vhsrentalsystem.dto.UserResponseDto;
import com.vhs.rental.vhsrentalsystem.exception.BusinessLogicException;
import com.vhs.rental.vhsrentalsystem.exception.ResourceNotFoundException;
import com.vhs.rental.vhsrentalsystem.mapper.UserMapper;
import com.vhs.rental.vhsrentalsystem.model.User;
import com.vhs.rental.vhsrentalsystem.repository.RentalRepository;
import com.vhs.rental.vhsrentalsystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Service layer handling business logic related to User operations.
 * Manages creation, retrieval, update, and deletion of users.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, RentalRepository rentalRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserResponseDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("user.notfound", new Object[]{id}));

        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()){
            throw new BusinessLogicException("user.email.exists", new Object[]{userRequestDto.getEmail()});
        }
        User user = userMapper.toEntity(userRequestDto);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound", new Object[]{id}));

        if(rentalRepository.existsByUserAndReturnDateIsNull(user)){
            throw new BusinessLogicException("user.hasActiveRentals", new Object[]{id});
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound", new Object[]{id}));

        userRepository.findByEmail(userRequestDto.getEmail()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(id)) {
                throw new BusinessLogicException("user.email.exists", new Object[]{userRequestDto.getEmail()});
            }
        });

        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}