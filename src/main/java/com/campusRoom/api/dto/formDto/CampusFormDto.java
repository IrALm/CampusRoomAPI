package com.campusRoom.api.dto.formDto;

import jakarta.validation.constraints.NotBlank;

public record CampusFormDto(

        @NotBlank(message = "Le nom du Campus est obligatoire.")
        String name,

        @NotBlank(message = "Le nom de la ville est obligatoire.")
        String city
) {
}
