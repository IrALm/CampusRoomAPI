package com.campusRoom.api.service.research.specification;

import com.campusRoom.api.dto.researchDto.RoomSearchDto;
import com.campusRoom.api.entity.Room;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class RoomSpecification {

    public static Specification<Room> withFilters(RoomSearchDto dto) {
        return Specification
                .where(hasCampusName(dto.campusName()))
                .and(hasName(dto.name()))
                .and(hasLocation(dto.location()))
                .and(hasMinCapacity(dto.capacityMin()))
                .and(hasMaxCapacity(dto.capacityMax()))
                .and(hasEquipment(dto.equipment()));
    }

    // Obligatoire — equal sur campus.name
    private static Specification<Room> hasCampusName(String campusName) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("campus").get("name")),
                        "%" + campusName.toLowerCase() + "%");
    }

    private static Specification<Room> hasName(String name) {
        return (root, query, cb) ->
                name == null || name.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%");
    }

    private static Specification<Room> hasLocation(String location) {
        return (root, query, cb) ->
                location == null || location.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("location")),
                        "%" + location.toLowerCase() + "%");
    }

    private static Specification<Room> hasMinCapacity(Integer min) {
        return (root, query, cb) ->
                min == null
                        ? null
                        : cb.greaterThanOrEqualTo(root.get("capacity"), min);
    }

    private static Specification<Room> hasMaxCapacity(Integer max) {
        return (root, query, cb) ->
                max == null
                        ? null
                        : cb.lessThanOrEqualTo(root.get("capacity"), max);
    }

    // Vérifie si l'équipement est présent dans la liste
    private static Specification<Room> hasEquipment(String equipment) {
        return (root, query, cb) -> {
            if (equipment == null || equipment.isBlank()) return null;
            Join<Room, String> equipmentJoin = root.join("equipment", JoinType.INNER);
            return cb.like(cb.lower(equipmentJoin),
                    "%" + equipment.toLowerCase() + "%");
        };
    }
}
