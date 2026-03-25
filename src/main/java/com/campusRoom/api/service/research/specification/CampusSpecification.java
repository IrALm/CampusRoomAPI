package com.campusRoom.api.service.research.specification;

import com.campusRoom.api.dto.researchDto.CampusSearchDto;
import com.campusRoom.api.entity.Campus;
import org.springframework.data.jpa.domain.Specification;

public class CampusSpecification {

    public static Specification<Campus> withFilters(CampusSearchDto dto) {
        return Specification
                .where(hasName(dto.name()))
                .and(hasCity(dto.city()));
    }

    private static Specification<Campus> hasName(String name) {
        return (root, query, cb) ->
                name == null || name.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%");
    }

    private static Specification<Campus> hasCity(String city) {
        return (root, query, cb) ->
                city == null || city.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("city")),
                        "%" + city.toLowerCase() + "%");
    }
}
