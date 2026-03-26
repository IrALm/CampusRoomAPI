package com.campusRoom.api.service.research.specification;

import com.campusRoom.api.dto.researchDto.ReservationSearchDto;
import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.ReservationType;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests unitaires pour ReservationSpecification")
class ReservationSpecificationTest {

    @Test
    @DisplayName("withFilters doit renvoyer null pour tous les filtres null")
    void shouldReturnNullForEmptyFilters() {
        ReservationSearchDto dto = ReservationSearchDto.builder().build();
        var spec = ReservationSpecification.withFilters(dto);

        Root<Reservation> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNull(predicate, "Predicate doit être null quand tous les filtres sont null");
    }

    @Test
    @DisplayName("withFilters doit créer une predicate pour type")
    void shouldCreatePredicateForType() {
        ReservationSearchDto dto = ReservationSearchDto.builder()
                .type(ReservationType.COURSE)
                .build();
        var spec = ReservationSpecification.withFilters(dto);

        Root<Reservation> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path<Object> path = mock(Path.class);
        when(root.get("type")).thenReturn(path);
        when(cb.equal(path, ReservationType.COURSE)).thenReturn(mock(Predicate.class));

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour type non null");
    }

    @Test
    @DisplayName("withFilters doit créer une predicate pour roomId")
    void shouldCreatePredicateForRoom() {
        ReservationSearchDto dto = ReservationSearchDto.builder()
                .roomId(10L)
                .build();
        var spec = ReservationSpecification.withFilters(dto);

        Root<Reservation> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path<Object> roomPath = mock(Path.class);
        Path<Object> idPath = mock(Path.class);

        when(root.get("room")).thenReturn(roomPath);
        when(roomPath.get("id")).thenReturn(idPath);
        when(cb.equal(idPath, 10L)).thenReturn(mock(Predicate.class));

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour roomId non null");
    }

    @Test
    @DisplayName("withFilters doit créer une predicate pour userId")
    void shouldCreatePredicateForUser() {
        ReservationSearchDto dto = ReservationSearchDto.builder()
                .userId(5L)
                .build();
        var spec = ReservationSpecification.withFilters(dto);

        Root<Reservation> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path<Object> userPath = mock(Path.class);
        Path<Object> idPath = mock(Path.class);

        when(root.get("user")).thenReturn(userPath);
        when(userPath.get("id")).thenReturn(idPath);
        when(cb.equal(idPath, 5L)).thenReturn(mock(Predicate.class));

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour userId non null");
    }

    @Test
    @DisplayName("withFilters doit créer une predicate pour startTime")
    void shouldCreatePredicateForStartTime() {
        LocalDateTime startTime = LocalDateTime.of(2026, 3, 25, 20, 0);
        ReservationSearchDto dto = ReservationSearchDto.builder()
                .startTime(startTime)
                .build();
        var spec = ReservationSpecification.withFilters(dto);

        Root<Reservation> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path path = mock(Path.class);
        when(root.get("startTime")).thenReturn(path);
        when(cb.greaterThanOrEqualTo((Expression<LocalDateTime>) path, startTime))
                .thenReturn(mock(Predicate.class));

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour startTime non null");
    }

    @Test
    @DisplayName("withFilters doit créer une predicate pour endTime")
    void shouldCreatePredicateForEndTime() {
        LocalDateTime endTime = LocalDateTime.of(2026, 3, 25, 22, 0);
        ReservationSearchDto dto = ReservationSearchDto.builder()
                .endTime(endTime)
                .build();
        var spec = ReservationSpecification.withFilters(dto);

        Root<Reservation> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path path = mock(Path.class);
        when(root.get("endTime")).thenReturn(path);
        when(cb.lessThanOrEqualTo((Expression<LocalDateTime>) path, endTime))
                .thenReturn(mock(Predicate.class));

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour endTime non null");
    }
}