package com.campusRoom.api.dto.formDto;

import com.campusRoom.api.entity.ReservationType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationFormDto(

        @NotNull(message = "Date de début obligatoire.")
        LocalDateTime startTime,

        @NotNull(message = "Date de fin obligatoire.")
        LocalDateTime endTime,

        @NotNull(message = "Un type de réservation est réquis.")
        ReservationType type
) {
}
