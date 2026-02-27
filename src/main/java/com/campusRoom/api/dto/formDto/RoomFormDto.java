package com.campusRoom.api.dto.formDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomFormDto(

        @NotBlank(message = "Le nom est obligatoire.")
        String name,

        @NotNull(message = "La capacité doit être renseignée.")
        Integer capacity,

        @NotBlank(message = "Emplacement Obligatoire.")
        String location ,

        @NotNull(message = " Obligation d'avoir un campus associé pour une salle.")
        Long campusId
) {
}
