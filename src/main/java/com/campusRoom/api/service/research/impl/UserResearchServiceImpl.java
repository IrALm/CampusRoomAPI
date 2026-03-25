package com.campusRoom.api.service.research.impl;

import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.dto.researchDto.UserPageDto;
import com.campusRoom.api.dto.researchDto.UserSearchDto;
import com.campusRoom.api.entity.User;
import com.campusRoom.api.mapper.UserMapper;
import com.campusRoom.api.repository.UserRepository;
import com.campusRoom.api.service.research.UserResearchService;
import com.campusRoom.api.service.research.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserResearchServiceImpl implements UserResearchService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserPageDto search(UserSearchDto searchDto) {
        Pageable pageable = searchDto.toPagination().toPageable();
        Specification<User> spec = UserSpecification.withFilters(searchDto);

        Page<UserDto> page = userRepository
                .findAll(spec, pageable)
                .map(userMapper::toDTO);

        return UserPageDto.from(page);
    }
}
