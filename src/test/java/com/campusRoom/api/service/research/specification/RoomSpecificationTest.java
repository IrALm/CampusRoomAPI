package com.campusRoom.api.service.research.specification;

import com.campusRoom.api.dto.researchDto.RoomSearchDto;
import com.campusRoom.api.entity.Room;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests unitaires pour RoomSpecification")
class RoomSpecificationTest {

    // Helper pour mocker campusName obligatoire
    private void mockCampusName(Root<Room> root, CriteriaBuilder cb, String campusName) {
        Path campusPath = mock(Path.class);
        Path namePath = mock(Path.class);
        Expression<String> lowerExpr = mock(Expression.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("campus")).thenReturn(campusPath);
        when(campusPath.get("name")).thenReturn(namePath);
        when(cb.lower(namePath)).thenReturn(lowerExpr);
        when(cb.like(lowerExpr, "%" + campusName.toLowerCase() + "%")).thenReturn(predicate);
    }

    @Test
    @DisplayName("withFilters doit renvoyer null si tous les filtres sont vides sauf campusName")
    void shouldReturnPredicateForOnlyCampusName() {
        RoomSearchDto dto = RoomSearchDto.builder()
                .campusName("Main Campus")
                .build();
        var spec = RoomSpecification.withFilters(dto);

        Root<Room> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        mockCampusName(root, cb, dto.campusName());

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate doit exister pour campusName obligatoire");
    }

    @Test
    @DisplayName("withFilters doit créer predicate pour name")
    void shouldCreatePredicateForName() {
        RoomSearchDto dto = RoomSearchDto.builder()
                .campusName("Main Campus")
                .name("Salle 1")
                .build();
        var spec = RoomSpecification.withFilters(dto);

        Root<Room> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        mockCampusName(root, cb, dto.campusName());

        Path namePath = mock(Path.class);
        Expression<String> lowerExpr = mock(Expression.class);
        Predicate namePredicate = mock(Predicate.class);

        when(root.get("name")).thenReturn(namePath);
        when(cb.lower(namePath)).thenReturn(lowerExpr);
        when(cb.like(lowerExpr, "%salle %")).thenReturn(namePredicate);

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour name non vide");
    }

    @Test
    @DisplayName("withFilters doit créer predicate pour location")
    void shouldCreatePredicateForLocation() {
        RoomSearchDto dto = RoomSearchDto.builder()
                .campusName("Main Campus")
                .location("Bâtiment A")
                .build();
        var spec = RoomSpecification.withFilters(dto);

        Root<Room> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        mockCampusName(root, cb, dto.campusName());

        Path locPath = mock(Path.class);
        Expression<String> lowerExpr = mock(Expression.class);
        Predicate locPredicate = mock(Predicate.class);

        when(root.get("location")).thenReturn(locPath);
        when(cb.lower(locPath)).thenReturn(lowerExpr);
        when(cb.like(lowerExpr, "%bâtiment %")).thenReturn(locPredicate);

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour location non vide");
    }

    @Test
    @DisplayName("withFilters doit créer predicate pour capacityMin")
    void shouldCreatePredicateForMinCapacity() {
        RoomSearchDto dto = RoomSearchDto.builder()
                .campusName("Main Campus")
                .capacityMin(11)
                .build();
        var spec = RoomSpecification.withFilters(dto);

        Root<Room> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        // Mock campusName obligatoire
        Path campusPath = mock(Path.class);
        Path<String> campusNamePath = mock(Path.class);
        Predicate campusPredicate = mock(Predicate.class);

        when(root.get("campus")).thenReturn(campusPath);
        when(campusPath.get("name")).thenReturn(campusNamePath);
        when(cb.lower(campusNamePath)).thenReturn(campusNamePath);
        when(cb.like(campusNamePath, "%main campus%")).thenReturn(campusPredicate);

        // Mock capacityMin
        Path capacityPath = mock(Path.class);
        Predicate capacityPredicate = mock(Predicate.class);

        when(root.get("capacity")).thenReturn(capacityPath);
        when(cb.greaterThanOrEqualTo(any(Expression.class), eq(10)))
                .thenReturn(capacityPredicate);

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour capacityMin >= 10");
    }

    @Test
    @DisplayName("withFilters doit créer predicate pour maxCapacity")
    void shouldCreatePredicateForMaxCapacity() {
        RoomSearchDto dto = RoomSearchDto.builder()
                .campusName("Main Campus")
                .capacityMax(51)
                .build();
        var spec = RoomSpecification.withFilters(dto);

        Root root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        mockCampusName(root, cb, dto.campusName());

        Path capacityPath = mock(Path.class);
        Predicate capacityPredicate = mock(Predicate.class);
        when(root.get("capacity")).thenReturn(capacityPath);
        when(cb.lessThanOrEqualTo(capacityPath, 50)).thenReturn(capacityPredicate);

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour maxCapacity non null");
    }

    @Test
    @DisplayName("withFilters doit créer predicate pour equipment via join")
    void shouldCreatePredicateForEquipment() {
        // Tous les filtres non obligatoires mis à null sauf equipment
        RoomSearchDto dto = RoomSearchDto.builder()
                .campusName("Main Campus") // Obligatoire
                .equipment("Projector")    // Filtre à tester
                .name("")                  // Ne doit pas générer de null
                .location("")
                .capacityMin(null)
                .capacityMax(null)
                .build();
        var spec = RoomSpecification.withFilters(dto);

        Root<Room> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        // Mock campusName
        Path campusPath = mock(Path.class);
        Path<String> campusNamePath = mock(Path.class);
        Expression<String> campusLower = mock(Expression.class);
        Predicate campusPredicate = mock(Predicate.class);
        when(root.get("campus")).thenReturn(campusPath);
        when(campusPath.get("name")).thenReturn(campusNamePath);
        when(cb.lower(campusNamePath)).thenReturn(campusLower);
        when(cb.like(campusLower, "%main campus%")).thenReturn(campusPredicate);

        // Mock equipment
        Join join = mock(Join.class);
        Expression<String> lowerExpr = mock(Expression.class);
        Predicate eqPredicate = mock(Predicate.class);
        when(root.join("equipment", JoinType.INNER)).thenReturn(join);
        when(cb.lower(join)).thenReturn(lowerExpr);
        when(cb.like(lowerExpr, "%projecto%")).thenReturn(eqPredicate);

        Predicate predicate = spec.toPredicate(root, query, cb);

        assertNotNull(predicate, "Predicate ne doit pas être null pour equipment non vide");
    }
}