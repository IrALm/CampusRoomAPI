package com.campusRoom.api.service.research.specification;

import com.campusRoom.api.dto.researchDto.ReservationSearchDto;
import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.ReservationType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ReservationSpecification {

    public static Specification<Reservation> withFilters(ReservationSearchDto dto) {
        return Specification
                .where(hasType(dto.type()))
                .and(hasRoom(dto.roomId()))
                .and(hasUser(dto.userId()))
                .and(startTimeAfter(dto.startTime()))
                .and(endTimeBefore(dto.endTime()));
    }

    private static Specification<Reservation> hasType(ReservationType type) {
        return (root, query, cb) ->
                type == null ? null : cb.equal(root.get("type"), type);
    }

    private static Specification<Reservation> hasRoom(Long roomId) {
        return (root, query, cb) ->
                roomId == null ? null : cb.equal(root.get("room").get("id"), roomId);
    }

    private static Specification<Reservation> hasUser(Long userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<Reservation> startTimeAfter(LocalDateTime startTime) {
        return (root, query, cb) ->
                startTime == null ? null : cb.greaterThanOrEqualTo(root.get("startTime"), startTime);
    }

    private static Specification<Reservation> endTimeBefore(LocalDateTime endTime) {
        return (root, query, cb) ->
                endTime == null ? null : cb.lessThanOrEqualTo(root.get("endTime"), endTime);
    }
}
