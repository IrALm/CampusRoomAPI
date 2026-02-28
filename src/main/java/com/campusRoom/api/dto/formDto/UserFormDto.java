package com.campusRoom.api.dto.formDto;

import com.campusRoom.api.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserFormDto(

        @NotBlank(message = "Le prénom est obligatoire.")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire.")
        String lastName,

        @NotBlank(message = "L'email est obligatoire.")
        String email,

        @NotNull(message = "Le rôle est également obligatoire.")
        String role
) {
}
