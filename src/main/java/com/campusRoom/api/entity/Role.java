package com.campusRoom.api.entity;

public enum Role {
    STUDENT,
    TEACHER;

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        try {
            Role.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
