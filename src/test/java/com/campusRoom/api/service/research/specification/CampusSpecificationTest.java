package com.campusRoom.api.service.research.specification;

import com.campusRoom.api.dto.researchDto.CampusSearchDto;
import com.campusRoom.api.entity.Campus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests unitaires pour CampusSpecification")
class CampusSpecificationTest {

    @Test
    @DisplayName("withFilters doit renvoyer null pour les champs vides")
    void shouldReturnNullForEmptyFilters() {
        CampusSearchDto dto = CampusSearchDto.builder()
                .name("")
                .city("")
                .build();
        var spec = CampusSpecification.withFilters(dto);

        Root<Campus> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNull(predicate, "Predicate doit être null pour filters vides");
    }

    @Test
    @DisplayName("withFilters doit créer une predicate pour name")
    void shouldCreatePredicateForName() {
        CampusSearchDto dto = CampusSearchDto.builder()
                .name("Nantes")
                .city(null)
                .build();
        var spec = CampusSpecification.withFilters(dto);

        Root root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path path = mock(Path.class);
        when(root.get("name")).thenReturn(path);
        when(cb.lower(path)).thenReturn(path);
        when(cb.like(path, "%nantes%")).thenReturn(mock(Predicate.class));

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour un name non vide");
    }

    @Test
    @DisplayName("withFilters doit créer une predicate pour city")
    void shouldCreatePredicateForCity() {
        CampusSearchDto dto = CampusSearchDto.builder()
                .name(null)
                .city("Paris")
                .build();
        var spec = CampusSpecification.withFilters(dto);

        Root root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path path = mock(Path.class);
        when(root.get("city")).thenReturn(path);
        when(cb.lower(path)).thenReturn(path);
        when(cb.like(path, "%paris%")).thenReturn(mock(Predicate.class));

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour city non vide");
    }
}