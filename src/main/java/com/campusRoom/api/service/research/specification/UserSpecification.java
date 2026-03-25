package com.campusRoom.api.service.research.specification;

import com.campusRoom.api.dto.researchDto.UserSearchDto;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> withFilters(UserSearchDto dto) {
        return Specification
                .where(hasFirstName(dto.firstName()))
                .and(hasLastName(dto.lastName()))
                .and(hasEmail(dto.email()))
                .and(hasRole(dto.role()));
    }

    private static Specification<User> hasFirstName(String firstName) {
        return (root, query, cb) ->
                firstName == null || firstName.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("firstName")),
                        "%" + firstName.toLowerCase() + "%");
    }

    private static Specification<User> hasLastName(String lastName) {
        return (root, query, cb) ->
                lastName == null || lastName.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("lastName")),
                        "%" + lastName.toLowerCase() + "%");
    }

    private static Specification<User> hasEmail(String email) {
        return (root, query, cb) ->
                email == null || email.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%");
    }

    private static Specification<User> hasRole(Role role) {
        return (root, query, cb) ->
                role == null
                        ? null
                        : cb.equal(root.get("role"), role);
    }
}
