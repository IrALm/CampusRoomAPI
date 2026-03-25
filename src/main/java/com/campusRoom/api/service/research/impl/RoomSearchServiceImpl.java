package com.campusRoom.api.service.research.impl;

import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.dto.researchDto.RoomPageDto;
import com.campusRoom.api.dto.researchDto.RoomSearchDto;
import com.campusRoom.api.entity.Room;
import com.campusRoom.api.mapper.RoomMapper;
import com.campusRoom.api.repository.RoomRepository;
import com.campusRoom.api.service.research.RoomSearchService;
import com.campusRoom.api.service.research.specification.RoomSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomSearchServiceImpl implements RoomSearchService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    public RoomPageDto search(RoomSearchDto searchDto) {
        Pageable pageable = searchDto.toPagination().toPageable();
        Specification<Room> spec = RoomSpecification.withFilters(searchDto);

        Page<RoomDto> page = roomRepository
                .findAll(spec, pageable)
                .map(roomMapper::toDTO);

        return RoomPageDto.from(page);
    }
}
