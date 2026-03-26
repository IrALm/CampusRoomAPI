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
@Tag(name = "User", description = "Endpoints pour gérer les utilisateurs : création, consultation, modification, recherche et suppression.")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserResearchService userResearchService;

    // =========================================
    // CRÉER UN UTILISATEUR
    // =========================================
    @Operation(
            summary = "Créer un nouvel utilisateur",
            description = """
                    Enregistre un nouvel utilisateur dans le système.

                    **Champs obligatoires :** `firstName`, `lastName`, `email`, `role`

                    **Rôles disponibles :** `STUDENT`, `TEACHER`

                    > Les étudiants sont soumis à un quota mensuel de réservations
                    > et ont une priorité inférieure aux enseignants sur les créneaux.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Utilisateur créé avec succès — aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requête invalide : champs manquants ou incorrects",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status": 400,
                                        "message": "Données invalides",
                                        "erreurs": {
                                            "firstName": "Le prénom est obligatoire",
                                            "lastName":  "Le nom est obligatoire",
                                            "email":     "L'email est obligatoire",
                                            "role":      "Le rôle est obligatoire"
                                        },
                                        "timestamp": "2026-03-16T10:00:00"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflit : un utilisateur avec cet email existe déjà",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  409,
                                        "message": "Un utilisateur avec cet email existe déjà"
                                    }
                                    """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Informations de l'utilisateur à créer",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Enseignant",
                                    value = """
                                            {
                                                "firstName": "Jean",
                                                "lastName":  "Dupont",
                                                "email":     "jean.dupont@univ.fr",
                                                "role":      "TEACHER"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Étudiant",
                                    value = """
                                            {
                                                "firstName": "Marie",
                                                "lastName":  "Martin",
                                                "email":     "marie.martin@etud.univ.fr",
                                                "role":      "STUDENT"
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping
    ResponseEntity<Void> createUser(@RequestBody @Valid UserFormDto userFormDto) {
        userService.createUser(userFormDto);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // OBTENIR UN UTILISATEUR PAR EMAIL
    // =========================================
    @Operation(
            summary = "Obtenir un utilisateur par son email exact",
            description = """
                    Retourne les informations d'un utilisateur à partir de son adresse email **exacte**.
                    La recherche est sensible à la casse.

                    Pour une recherche partielle sur le prénom ou le nom,
                    utilisez l'endpoint `POST /user/search`.

                    **Exemple :** `GET /user?email=jean.dupont@univ.fr`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "id":        1,
                                        "firstName": "Jean",
                                        "lastName":  "Dupont",
                                        "email":     "jean.dupont@univ.fr",
                                        "role":      "TEACHER"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucun utilisateur trouvé pour cet email",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucun utilisateur trouvé pour l'email : jean.dupont@univ.fr"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping
    ResponseEntity<UserDto> getUserByEmail(
            @Parameter(
                    description = "Adresse email exacte de l'utilisateur (sensible à la casse)",
                    required = true,
                    example = "jean.dupont@univ.fr"
            )
            @RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // =========================================
    // METTRE À JOUR LE PRÉNOM
    // =========================================
    @Operation(
            summary = "Mettre à jour le prénom d'un utilisateur",
            description = """
                    Modifie uniquement le prénom d'un utilisateur existant.

                    **Exemple :** `PATCH /user/1/firstName?firstName=Pierre`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Prénom mis à jour avec succès — aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Prénom invalide (vide ou null)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  400,
                                        "message": "Le prénom ne peut pas être vide"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur introuvable pour l'identifiant fourni",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucun utilisateur trouvé pour l'id : 99"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{userId}/firstName")
    ResponseEntity<Void> updateFirstName(
            @Parameter(description = "Identifiant de l'utilisateur", required = true, example = "1")
            @PathVariable Long userId,

            @Parameter(description = "Nouveau prénom", required = true, example = "Pierre")
            @RequestParam String firstName) {
        userService.updateFirstName(userId, firstName);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // METTRE À JOUR LE NOM
    // =========================================
    @Operation(
            summary = "Mettre à jour le nom d'un utilisateur",
            description = """
                    Modifie uniquement le nom de famille d'un utilisateur existant.

                    **Exemple :** `PATCH /user/1/lastName?lastName=Bernard`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Nom mis à jour avec succès — aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nom invalide (vide ou null)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  400,
                                        "message": "Le nom ne peut pas être vide"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur introuvable pour l'identifiant fourni",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucun utilisateur trouvé pour l'id : 99"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{userId}/lastName")
    ResponseEntity<Void> updateLastName(
            @Parameter(description = "Identifiant de l'utilisateur", required = true, example = "1")
            @PathVariable Long userId,

            @Parameter(description = "Nouveau nom de famille", required = true, example = "Bernard")
            @RequestParam String lastName) {
        userService.updateLastName(userId, lastName);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // RECHERCHE PAGINÉE
    // =========================================
    @Operation(
            summary = "Rechercher des utilisateurs avec filtres et pagination",
            description = """
                    Recherche des utilisateurs selon des critères combinables avec pagination.
                    Tous les champs sont optionnels — sans filtre, retourne tous les utilisateurs paginés.

                    ---

                    **Filtres textuels — recherche partielle (LIKE, insensible à la casse)**

                    Les champs `firstName`, `lastName` et `email` supportent la **recherche partielle** :
                    il n'est pas nécessaire de saisir la valeur complète.

                    | Champ       | Saisie      | Correspond à                             |
                    |-------------|-------------|------------------------------------------|
                    | `firstName` | `"jea"`     | `"Jean"`, `"Jeanne"`, …                 |
                    | `firstName` | `"mar"`     | `"Marie"`, `"Marc"`, `"Martin"`, …      |
                    | `lastName`  | `"dup"`     | `"Dupont"`, `"Dupuis"`, …               |
                    | `lastName`  | `"mart"`    | `"Martin"`, `"Martinez"`, …             |
                    | `email`     | `"univ.fr"` | tous les emails en `@univ.fr`            |
                    | `email`     | `"jean"`    | `"jean.dupont@univ.fr"`, …              |

                    ---

                    **Filtre rôle — égalité exacte**

                    `role` accepte uniquement `STUDENT` ou `TEACHER`.

                    ---

                    **Tri disponible :** `id`, `firstName`, `lastName`, `email`, `role`

                    **Direction :** `ASC` ou `DESC`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des utilisateurs correspondant aux critères",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "contenu": [
                                            {
                                                "id":        1,
                                                "firstName": "Jean",
                                                "lastName":  "Dupont",
                                                "email":     "jean.dupont@univ.fr",
                                                "role":      "TEACHER"
                                            },
                                            {
                                                "id":        2,
                                                "firstName": "Jeanne",
                                                "lastName":  "Durand",
                                                "email":     "jeanne.durand@univ.fr",
                                                "role":      "TEACHER"
                                            }
                                        ],
                                        "pageActuelle":   0,
                                        "totalPages":     3,
                                        "totalElements": 24,
                                        "premierePage":   true,
                                        "dernierePage":   false
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paramètres de pagination invalides",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  400,
                                        "message": "Le numéro de page ne peut pas être négatif"
                                    }
                                    """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = """
                    Critères de recherche et paramètres de pagination — tous les champs sont optionnels.
                    Les champs textuels (`firstName`, `lastName`, `email`) acceptent des valeurs partielles.
                    """,
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Recherche partielle par prénom — 'jea' trouve 'Jean' et 'Jeanne'",
                                    value = """
                                            {
                                                "firstName":     "jea",
                                                "lastName":      null,
                                                "email":         null,
                                                "role":          null,
                                                "page":          0,
                                                "size":          10,
                                                "sortBy":        "lastName",
                                                "sortDirection": "ASC"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Tous les enseignants triés par nom",
                                    value = """
                                            {
                                                "firstName":     null,
                                                "lastName":      null,
                                                "email":         null,
                                                "role":          "TEACHER",
                                                "page":          0,
                                                "size":          10,
                                                "sortBy":        "lastName",
                                                "sortDirection": "ASC"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Recherche par domaine email — 'univ.fr' trouve tous les profs",
                                    value = """
                                            {
                                                "firstName":     null,
                                                "lastName":      null,
                                                "email":         "univ.fr",
                                                "role":          null,
                                                "page":          0,
                                                "size":          20,
                                                "sortBy":        "email",
                                                "sortDirection": "ASC"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Tous les filtres combinés",
                                    value = """
                                            {
                                                "firstName":     "mar",
                                                "lastName":      "mart",
                                                "email":         null,
                                                "role":          "STUDENT",
                                                "page":          0,
                                                "size":          10,
                                                "sortBy":        "firstName",
                                                "sortDirection": "ASC"
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping("/search")
    public ResponseEntity<UserPageDto> search(
            @RequestBody UserSearchDto searchDto) {
        return ResponseEntity.ok(userResearchService.search(searchDto));
    }

    // =========================================
    // SUPPRIMER UN UTILISATEUR
    // =========================================
    @Operation(
            summary = "Supprimer un utilisateur",
            description = """
                    Supprime définitivement un utilisateur et toutes ses réservations associées (cascade).

                    > **Note :** contrairement aux campus et aux salles, la suppression d'un utilisateur
                    > n'est pas bloquée par ses réservations futures — elles seront supprimées en cascade.
                    > Assurez-vous que cette action est intentionnelle.

                    **Exemple :** `DELETE /user/1`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Utilisateur supprimé avec succès — aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur introuvable pour l'identifiant fourni",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucun utilisateur trouvé pour l'id : 99"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{UserId}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant de l'utilisateur à supprimer", required = true, example = "1")
            @PathVariable Long UserId) {
        userService.deleteById(UserId);
        return ResponseEntity.noContent().build();
    }
}