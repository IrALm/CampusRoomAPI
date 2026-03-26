package com.campusRoom.api.service.research.specification;

import com.campusRoom.api.dto.researchDto.UserSearchDto;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.entity.User;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests unitaires pour UserSpecification")
class UserSpecificationTest {

    @Test
    @DisplayName("withFilters doit renvoyer null pour tous les filtres null ou vides")
    void shouldReturnNullForEmptyFilters() {
        UserSearchDto dto = UserSearchDto.builder().build();
        var spec = UserSpecification.withFilters(dto);

        Root<User> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNull(predicate, "Predicate doit être null quand tous les filtres sont null");
    }

    @Test
    @DisplayName("withFilters doit créer une predicate pour firstName")
    void shouldCreatePredicateForFirstName() {
        UserSearchDto dto = UserSearchDto.builder()
                .firstName("John")
                .build();
        var spec = UserSpecification.withFilters(dto);

        Root<User> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path firstNamePath = mock(Path.class);
        Expression<String> lowerExpr = mock(Expression.class);
        Predicate firstNamePredicate = mock(Predicate.class);

        when(root.get("firstName")).thenReturn(firstNamePath);
        when(cb.lower(firstNamePath)).thenReturn(lowerExpr);
        when(cb.like(lowerExpr, "%john%")).thenReturn(firstNamePredicate);

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour firstName non vide");
    }

    @Test
    @DisplayName("withFilters doit créer une predicate pour lastName")
    void shouldCreatePredicateForLastName() {
        UserSearchDto dto = UserSearchDto.builder()
                .lastName("Doe")
                .build();
        var spec = UserSpecification.withFilters(dto);

        Root<User> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path lastNamePath = mock(Path.class);
        Expression<String> lowerExpr = mock(Expression.class);
        Predicate lastNamePredicate = mock(Predicate.class);

        when(root.get("lastName")).thenReturn(lastNamePath);
        when(cb.lower(lastNamePath)).thenReturn(lowerExpr);
        when(cb.like(lowerExpr, "%doe%")).thenReturn(lastNamePredicate);

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour lastName non vide");
    }

    @Test
    @DisplayName("withFilters doit créer une predicate pour email")
    void shouldCreatePredicateForEmail() {
        UserSearchDto dto = UserSearchDto.builder()
                .email("john@example.com")
                .build();
        var spec = UserSpecification.withFilters(dto);

        Root<User> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path emailPath = mock(Path.class);
        Expression<String> lowerExpr = mock(Expression.class);
        Predicate emailPredicate = mock(Predicate.class);

        when(root.get("email")).thenReturn(emailPath);
        when(cb.lower(emailPath)).thenReturn(lowerExpr);
        when(cb.like(lowerExpr, "%john@example.com%")).thenReturn(emailPredicate);

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour email non vide");
    }

    @Test
    @DisplayName("withFilters doit créer une predicate pour role")
    void shouldCreatePredicateForRole() {
        Role role = Role.STUDENT;
        UserSearchDto dto = UserSearchDto.builder()
                .role(role)
                .build();
        var spec = UserSpecification.withFilters(dto);

        Root<User> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path rolePath = mock(Path.class);
        Predicate rolePredicate = mock(Predicate.class);

        when(root.get("role")).thenReturn(rolePath);
        when(cb.equal(rolePath, role)).thenReturn(rolePredicate);

        Predicate predicate = spec.toPredicate(root, query, cb);
        assertNotNull(predicate, "Predicate ne doit pas être null pour role non null");
    }
}