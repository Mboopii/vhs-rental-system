package com.vhs.rental.vhsrentalsystem;

import com.vhs.rental.vhsrentalsystem.exception.BusinessLogicException;
import com.vhs.rental.vhsrentalsystem.service.RentalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * Integration tests for the VhsRentalSystemApplication.
 * Verifies that the Spring application context loads correctly and
 * includes tests for service layer interactions within a transaction context.
 */
@SpringBootTest
@Transactional
class VhsRentalSystemApplicationTests {

    @Autowired
    private RentalService rentalService;

	@Test
	void contextLoads() { //basic test to ensure the application context starts
	}

    @Test
    void testRentVhs_WhenVhsIsAlreadyRented() { //this test relies on data.sql having rental for VHS ID 1
        Long userId = 2L;
        Long vhsId = 1L;
        String expectedErrorMessageKey = "vhs.alreadyRented";

        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> {
                    rentalService.rentVHS(userId, vhsId);
                }
        );

        assertEquals(expectedErrorMessageKey, exception.getMessage());
    }

}
