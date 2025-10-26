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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/*
 * Service layer handling business logic related to Rental operations.
 * Manages renting, returning, and calculating late fees.
 */
@Service
public class RentalService {

    @Value("${vhs.rental.duration-days}")
    private int rentalDays;

    @Value("${vhs.rental.late-fee-per-day}")
    private double rentalFeePerDay;

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VhsRepository vhsRepository;
    private final RentalMapper rentalMapper;
    private static final Logger logger = LoggerFactory.getLogger(RentalService.class);

    @Autowired
    public RentalService(RentalRepository rentalRepository, UserRepository userRepository,
                         VhsRepository vhsRepository, RentalMapper rentalMapper) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.vhsRepository = vhsRepository;
        this.rentalMapper = rentalMapper;
    }

    @Transactional(readOnly = true)
    public List<RentalResponseDto> findAllRentals() {
        return rentalMapper.toDtoList(rentalRepository.findAll());
    }

    @Transactional
    public RentalResponseDto rentVHS(Long userId, Long vhsId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound", new Object[]{userId}));
        Vhs vhs = vhsRepository.findById(vhsId)
                .orElseThrow(() -> new ResourceNotFoundException("vhs.notfound", new Object[]{vhsId}));

        if(rentalRepository.findByVhsAndReturnDateIsNull(vhs).isPresent()){
            throw new BusinessLogicException("vhs.alreadyRented", new Object[]{vhs.getTitle()});
        }

        LocalDate rentalDate = LocalDate.now();
        LocalDate dueDate = LocalDate.now().plusDays(rentalDays);

        Rental newRental = new Rental();
        newRental.setUser(user);
        newRental.setVhs(vhs);
        newRental.setRentalDate(rentalDate);
        newRental.setReturnDate(null);
        newRental.setDueDate(dueDate);

        Rental savedRental = rentalRepository.save(newRental);

        return rentalMapper.toDto(savedRental);
    }

    @Transactional
    public RentalResponseDto returnVHS(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("rental.notfound",  new Object[]{rentalId}));

        if(rental.getReturnDate() != null){
            throw new BusinessLogicException("rental.alreadyReturned",  new Object[]{rentalId});
        }

        LocalDate returnDate = LocalDate.now();

        if(returnDate.isAfter(rental.getDueDate())){
            long daysLate = ChronoUnit.DAYS.between(rental.getDueDate(), returnDate);
            double lateFee = rentalFeePerDay * daysLate;
            rental.setLateFee(lateFee);

            logger.info("The fee for returning the rental {} days later with rental id: {} is {}", daysLate, rental.getId(), lateFee);
        }

        rental.setReturnDate(returnDate);

        Rental updatedRental = rentalRepository.save(rental);

        return rentalMapper.toDto(updatedRental);
    }

    @Transactional(readOnly = true)
    public List<RentalResponseDto> findRentalsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound", new Object[]{userId}));

        List<Rental> rentals = rentalRepository.findByUser(user);

        return rentalMapper.toDtoList(rentals);
    }
}