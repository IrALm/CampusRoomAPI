package com.campusRoom.api.service.research.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserSortEnum {

    ID("id"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),
    ROLE("role");

    private final String fieldName;

    public static boolean isValidField(String value) {
        if (value == null || value.isBlank()) return false;
        return Arrays.stream(values())
                .anyMatch(field -> field.getFieldName().equalsIgnoreCase(value));
    }

    public static String resolveField(String value) {
        if (value == null || value.isBlank()) return ID.fieldName;
        return Arrays.stream(values())
                .map(UserSortEnum::getFieldName)
                .filter(name -> name.equalsIgnoreCase(value))
                .findFirst()
                .orElse(ID.fieldName);
    }
}
