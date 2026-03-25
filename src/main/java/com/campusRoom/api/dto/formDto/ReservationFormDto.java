package com.campusRoom.api.dto.formDto;

import com.campusRoom.api.entity.ReservationType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReservationFormDto(

        @NotNull(message = "Le type de réservation est obligatoire")
        ReservationType type,

        @NotNull(message = "La date de début est obligatoire")
        @Future(message = "La date de début doit être dans le futur")
        LocalDateTime startTime,

        @NotNull(message = "La date de fin est obligatoire")
        LocalDateTime endTime,

        @NotNull(message = "La salle est obligatoire")
        Long roomId,

        @NotNull(message = "L'utilisateur est obligatoire")
        Long userId
) {
}
