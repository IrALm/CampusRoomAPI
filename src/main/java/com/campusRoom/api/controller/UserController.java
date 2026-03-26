package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.UserFormDto;
import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.dto.researchDto.UserPageDto;
import com.campusRoom.api.dto.researchDto.UserSearchDto;
import com.campusRoom.api.service.UserService;
import com.campusRoom.api.service.research.UserResearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints pour gérer les utilisateurs.")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserResearchService userResearchService;

    // =========================================
    // CREATE
    // =========================================
    @Operation(summary = "Créer un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Créé"),
            @ApiResponse(responseCode = "400", description = "Erreur validation"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Utilisateur à créer",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Utilisateur Postman",
                            value = """
                                    {
                                        "firstName": "Moise",
                                        "lastName": "Aganze",
                                        "email": "moise.aganze@example.com",
                                        "role": "STUDENT"
                                    }
                                    """
                    )
            )
    )
    @PostMapping
    ResponseEntity<Void> createUser(@RequestBody @Valid UserFormDto userFormDto) {
        userService.createUser(userFormDto);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // GET BY EMAIL
    // =========================================
    @Operation(summary = "Obtenir un utilisateur par email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "id": 1,
                                        "firstName": "Moise",
                                        "lastName": "Aganze",
                                        "email": "moise.aganze@example.com",
                                        "role": "STUDENT"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Non trouvé")
    })
    @GetMapping
    ResponseEntity<UserDto> getUserByEmail(
            @Parameter(example = "moise.aganze@example.com")
            @RequestParam String email) {

        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // =========================================
    // UPDATE FIRSTNAME
    // =========================================
    @Operation(
            summary = "Mettre à jour le prénom",
            description = "PATCH /user/1/firstName?firstName=john"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mis à jour"),
            @ApiResponse(responseCode = "400", description = "Prénom invalide"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    @PatchMapping("/{userId}/firstName")
    ResponseEntity<Void> updateFirstName(
            @Parameter(example = "1")
            @PathVariable Long userId,

            @Parameter(example = "john")
            @RequestParam String firstName) {

        userService.updateFirstName(userId, firstName);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // UPDATE LASTNAME
    // =========================================
    @Operation(
            summary = "Mettre à jour le nom",
            description = "PATCH /user/1/lastName?lastName=Doe"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mis à jour"),
            @ApiResponse(responseCode = "400", description = "Nom invalide"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    @PatchMapping("/{userId}/lastName")
    ResponseEntity<Void> updateLastName(
            @Parameter(example = "1")
            @PathVariable Long userId,

            @Parameter(example = "Doe")
            @RequestParam String lastName) {

        userService.updateLastName(userId, lastName);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // SEARCH
    // =========================================
    @Operation(
            summary = "Rechercher des utilisateurs avec filtres et pagination",
            description = """
                    Recherche des utilisateurs selon des critères combinables.

                    ─── FILTRES DISPONIBLES ─────────────────────────────

                    • firstName : recherche partielle (LIKE)
                      → "jo" → "John", "Jordan"

                    • lastName : recherche partielle
                      → "do" → "Doe", "Dorian"

                    • email : recherche partielle
                      → "example.com"

                    • role : égalité exacte
                      → STUDENT ou TEACHER

                    Les filtres peuvent être combinés :
                    → firstName + role

                    ─── PAGINATION ──────────────────────────────────────
                    • page : numéro de page (0 par défaut)
                    • size : nombre d’éléments

                    ─── TRI ─────────────────────────────────────────────
                    • sortBy : id, firstName, lastName, email, role
                    • sortDirection : ASC ou DESC

                    ─── EXEMPLE COMPLET ─────────────────────────────────
                    {
                      "firstName": null,
                      "lastName": null,
                      "email": null,
                      "role": null,
                      "page": 0,
                      "size": 10,
                      "sortBy": "firstName",
                      "sortDirection": "ASC"
                    }
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Résultat paginé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "contenu": [
                                            {
                                                "id": 1,
                                                "firstName": "John",
                                                "lastName": "Doe",
                                                "email": "john.doe@example.com",
                                                "role": "STUDENT"
                                            }
                                        ],
                                        "pageActuelle": 0,
                                        "totalPages": 1,
                                        "totalElements": 1,
                                        "premierePage": true,
                                        "dernierePage": true
                                    }
                                    """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Recherche utilisateurs",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Recherche Postman",
                            value = """
                                    {
                                      "firstName": null,
                                      "lastName": null,
                                      "email": null,
                                      "role": null,
                                      "page": 0,
                                      "size": 10,
                                      "sortBy": "firstName",
                                      "sortDirection": "ASC"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/search")
    public ResponseEntity<UserPageDto> search(
            @RequestBody UserSearchDto searchDto) {
        return ResponseEntity.ok(userResearchService.search(searchDto));
    }

    // =========================================
    // DELETE
    // =========================================
    @Operation(
            summary = "Supprimer un utilisateur",
            description = "DELETE /user/1"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supprimé"),
            @ApiResponse(responseCode = "404", description = "Non trouvé")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteById(
            @Parameter(example = "1")
            @PathVariable Long userId) {

        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }
}