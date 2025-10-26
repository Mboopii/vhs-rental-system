package com.vhs.rental.vhsrentalsystem.service;

import com.vhs.rental.vhsrentalsystem.dto.VhsRequestDto;
import com.vhs.rental.vhsrentalsystem.dto.VhsResponseDto;
import com.vhs.rental.vhsrentalsystem.exception.BusinessLogicException;
import com.vhs.rental.vhsrentalsystem.exception.ResourceNotFoundException;
import com.vhs.rental.vhsrentalsystem.mapper.VhsMapper;
import com.vhs.rental.vhsrentalsystem.model.Vhs;
import com.vhs.rental.vhsrentalsystem.repository.RentalRepository;
import com.vhs.rental.vhsrentalsystem.repository.VhsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Service layer handling business logic related to Vhs operations.
 * Manages creation, retrieval, filtering, and deletion of VHS tapes.
 */
@Service
public class VhsService {
    private final VhsRepository vhsRepository;
    private final RentalRepository rentalRepository;
    private final VhsMapper vhsMapper;

    @Autowired
    public VhsService(VhsRepository vhsRepository, RentalRepository rentalRepository, VhsMapper vhsMapper) {
        this.vhsRepository = vhsRepository;
        this.rentalRepository = rentalRepository;
        this.vhsMapper = vhsMapper;
    }

    @Transactional(readOnly = true)
    public List<VhsResponseDto> findAllVhs(String genre, Integer releaseYear) {
        boolean hasGenre = (genre != null && !genre.isEmpty());
        boolean hasYear = (releaseYear != null);

        List<Vhs> vhsList;

        if (hasGenre && hasYear) {
            vhsList = vhsRepository.findByGenreAndReleaseYear(genre, releaseYear);
        } else if (hasGenre) {
            vhsList = vhsRepository.findByGenre(genre);
        } else if (hasYear) {
            vhsList = vhsRepository.findByReleaseYear(releaseYear);
        } else {
            vhsList = vhsRepository.findAll();
        }

        return vhsMapper.toDtoList(vhsList);
    }

    @Transactional(readOnly = true)
    public VhsResponseDto findVhsById(Long id) {
        Vhs vhs = vhsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vhs.notfound", new Object[]{id}));

        return vhsMapper.toDto(vhs);
    }

    @Transactional
    public VhsResponseDto saveVhs(VhsRequestDto vhsRequestDto) {
        Vhs vhs = vhsMapper.toEntity(vhsRequestDto);

        Vhs savedVhs = vhsRepository.save(vhs);

        return vhsMapper.toDto(savedVhs);
    }

    @Transactional
    public void deleteVhs(Long id) {
        Vhs vhs = vhsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("vhs.notfound", new Object[]{id}));

        if (rentalRepository.existsByVhsAndReturnDateIsNull(vhs)) {
            throw new BusinessLogicException("vhs.isRented", new Object[]{vhs.getTitle()});
        }

        vhsRepository.delete(vhs);
    }

    @Transactional(readOnly = true)
    public List<VhsResponseDto> findNewReleases() {
        List<Vhs> vhsList = vhsRepository.findLatestTenByOrderByReleaseYearDesc();
        return vhsMapper.toDtoList(vhsList);
    }
}