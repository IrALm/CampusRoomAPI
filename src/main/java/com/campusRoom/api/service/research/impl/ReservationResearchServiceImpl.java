package com.campusRoom.api.service.research.impl;

import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.dto.researchDto.ReservationPageDto;
import com.campusRoom.api.dto.researchDto.ReservationSearchDto;
import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.mapper.ReservationMapper;
import com.campusRoom.api.repository.ReservationRepository;
import com.campusRoom.api.service.research.ReservationResearchService;
import com.campusRoom.api.service.research.specification.ReservationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationResearchServiceImpl implements ReservationResearchService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationPageDto search(ReservationSearchDto searchDto) {
        Pageable pageable = searchDto.toPagination().toPageable();
        Specification<Reservation> spec = ReservationSpecification.withFilters(searchDto);

        Page<ReservationDto> page = reservationRepository
                .findAll(spec, pageable)
                .map(reservationMapper::toDTO);

        return ReservationPageDto.from(page);
    }
}
