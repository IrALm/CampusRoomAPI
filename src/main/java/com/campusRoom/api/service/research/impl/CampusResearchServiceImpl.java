package com.campusRoom.api.service.research.impl;

import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.dto.researchDto.CampusPageDto;
import com.campusRoom.api.dto.researchDto.CampusSearchDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.mapper.CampusMapper;
import com.campusRoom.api.repository.CampusRepository;
import com.campusRoom.api.service.research.CampusResearchService;
import com.campusRoom.api.service.research.specification.CampusSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampusResearchServiceImpl implements CampusResearchService {

    private final CampusRepository campusRepository;
    private final CampusMapper campusMapper;

    @Override
    public CampusPageDto search(CampusSearchDto searchDto) {
        Pageable pageable = searchDto.toPagination().toPageable();
        Specification<Campus> spec = CampusSpecification.withFilters(searchDto);

        Page<CampusDto> page = campusRepository
                .findAll(spec, pageable)
                .map(campusMapper::toDTO);

        return CampusPageDto.from(page);
    }
}
