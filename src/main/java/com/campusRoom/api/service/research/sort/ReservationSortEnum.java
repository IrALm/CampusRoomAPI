package com.campusRoom.api.service.research.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ReservationSortEnum {

    ID("id"),
    TYPE("type"),
    START_TIME("startTime"),
    END_TIME("endTime"),
    ROOM("room.name"),
    USER("user.lastName"),
    DESCRIPTION("description"),
    MAX_DURATION_HOURS("maxDurationHours");

    private final String fieldName;

    public static boolean isValidField(String value) {
        if (value == null || value.isBlank()) return false;
        return Arrays.stream(values())
                .anyMatch(field -> field.getFieldName().equalsIgnoreCase(value));
    }

    public static String resolveField(String value) {
        if (value == null || value.isBlank()) return ID.fieldName;
        return Arrays.stream(values())
                .map(ReservationSortEnum::getFieldName)
                .filter(name -> name.equalsIgnoreCase(value))
                .findFirst()
                .orElse(ID.fieldName);
    }
}
