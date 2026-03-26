package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.RoomFormDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.dto.researchDto.RoomPageDto;
import com.campusRoom.api.dto.researchDto.RoomSearchDto;
import com.campusRoom.api.service.RoomService;
import com.campusRoom.api.service.research.RoomSearchService;
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
@Tag(name = "Room", description = "Endpoints pour gérer les salles universitaires : création, consultation, modification, recherche et suppression.")
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;
    private final RoomSearchService roomSearchService;

    // =========================================
    // CRÉER UNE SALLE
    // =========================================
    @Operation(
            summary = "Créer une nouvelle salle",
            description = """
                    Enregistre une nouvelle salle rattachée à un campus existant.

                    **Champs obligatoires :** `name`, `capacity`, `location`, `campusId`

                    **Champs optionnels :** `equipment` (chaîne libre décrivant les équipements disponibles : projecteur, PC, tableau blanc…)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Salle créée avec succès — aucun contenu retourné"
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
                                            "name":       "Le nom de la salle est obligatoire",
                                            "capacity":   "La capacité est obligatoire",
                                            "location":   "L'emplacement est obligatoire",
                                            "campusId": "L'Id du campus est obligatoire"
                                        },
                                        "timestamp": "2026-03-16T10:00:00"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Campus introuvable pour le nom fourni",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucun campus trouvé pour cet Id: Campus Inconnu"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflit : une salle portant ce nom existe déjà sur ce campus",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  409,
                                        "message": "Une salle avec ce nom existe déjà"
                                    }
                                    """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Informations de la salle à créer",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Salle équipée",
                                    value = """
                                            {
                                                "name":       "Salle A101",
                                                "capacity":   30,
                                                "location":   "Bâtiment A — 1er étage",
                                                "campusId": 1,
                                                "equipment":  "Projecteur, PC, Tableau blanc"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Salle simple sans équipement",
                                    value = """
                                            {
                                                "name":       "Salle B202",
                                                "capacity":   20,
                                                "location":   "Bâtiment B — 2ème étage",
                                                "campusId": 1,
                                                "equipment":  ""
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping
    ResponseEntity<Void> createRoom(@RequestBody @Valid RoomFormDto roomFormDto) {
        roomService.createRoom(roomFormDto);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // OBTENIR UNE SALLE PAR NOM
    // =========================================
    @Operation(
            summary = "Obtenir une salle par son nom exact",
            description = """
                    Retourne les informations d'une salle à partir de son nom **exact**.
                    La recherche est sensible à la casse.

                    Pour une recherche partielle (ex : `"sall"` pour retrouver `"Salle A101"`),
                    utilisez l'endpoint `POST /room/search`.

                    **Exemple :** `GET /room?name=Salle A101`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Salle trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "id":         1,
                                        "name":       "Salle A101",
                                        "capacity":   30,
                                        "location":   "Bâtiment A — 1er étage",
                                        "campusId": 1,
                                        "equipment":  "Projecteur, PC, Tableau blanc"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucune salle trouvée pour ce nom exact",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucune salle trouvée pour le nom : Salle A101"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping
    ResponseEntity<RoomDto> getRoomByName(
            @Parameter(
                    description = "Nom exact de la salle à rechercher (sensible à la casse)",
                    required = true,
                    example = "Salle A101"
            )
            @RequestParam String name) {
        return ResponseEntity.ok(roomService.getByRoomName(name));
    }

    // =========================================
    // METTRE À JOUR LA CAPACITÉ
    // =========================================
    @Operation(
            summary = "Mettre à jour la capacité d'une salle",
            description = """
                    Modifie le nombre maximum de personnes pouvant occuper la salle.

                    **Exemple :** `PATCH /room/1/capacity?capacity=40`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Capacité mise à jour avec succès — aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Capacité invalide (valeur négative ou nulle)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  400,
                                        "message": "La capacité doit être un entier positif"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Salle introuvable pour l'identifiant fourni",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucune salle trouvée pour l'id : 99"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{roomId}/capacity")
    ResponseEntity<Void> updateRoomCapacity(
            @Parameter(description = "Identifiant de la salle", required = true, example = "1")
            @PathVariable Long roomId,

            @Parameter(description = "Nouvelle capacité maximale de la salle", required = true, example = "40")
            @RequestParam int capacity) {
        roomService.updateRoomCapacity(roomId, capacity);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // METTRE À JOUR LE NOM
    // =========================================
    @Operation(
            summary = "Mettre à jour le nom d'une salle",
            description = """
                    Renomme une salle existante. Le nouveau nom est vérifié en unicité
                    sur l'ensemble du campus spécifié.

                    **Exemple :** `PATCH /room/1/name?campusId=2&name=Salle A102`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Nom mis à jour avec succès — aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Salle ou campus introuvable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucune salle trouvée pour l'id : 99"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflit : le nouveau nom est déjà utilisé sur ce campus",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  409,
                                        "message": "Une salle avec ce nom existe déjà sur ce campus"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{roomId}/name")
    ResponseEntity<Void> updateRoomName(
            @Parameter(description = "Identifiant de la salle à renommer", required = true, example = "1")
            @PathVariable Long roomId,

            @Parameter(description = "Identifiant du campus auquel appartient la salle", required = true, example = "2")
            @RequestParam Long campusId,

            @Parameter(description = "Nouveau nom de la salle", required = true, example = "Salle A102")
            @RequestParam String name) {
        roomService.updateRoomName(campusId, roomId, name);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // RECHERCHE PAGINÉE
    // =========================================
    @Operation(
            summary = "Rechercher des salles avec filtres et pagination",
            description = """
                    Recherche des salles selon des critères combinables avec pagination.

                    **`campusName` est le seul champ obligatoire** — tous les autres sont optionnels.

                    ---

                    **Filtres textuels — recherche partielle (LIKE, insensible à la casse)**

                    Les champs `name`, `location` et `equipment` supportent la **recherche partielle** :
                    il n'est pas nécessaire de saisir le nom complet.

                    | Champ       | Saisie      | Correspond à                        |
                    |-------------|-------------|-------------------------------------|
                    | `name`      | `"sall"`    | `"Salle A101"`, `"Salle B202"`, … |
                    | `name`      | `"salle a"` | `"Salle A101"`, `"Salle A102"`, … |
                    | `location`  | `"bât a"`   | `"Bâtiment A — 1er étage"`         |
                    | `location`  | `"1er"`     | toutes les salles au 1er étage      |
                    | `equipment` | `"proj"`    | `"Projecteur, PC, Tableau blanc"`   |
                    | `equipment` | `"pc"`      | toutes les salles avec PC           |

                    ---

                    **Filtre capacité — plage min/max**

                    `capacityMin` et `capacityMax` sont indépendants et combinables :
                    - `capacityMin: 20` → salles de 20 personnes et plus
                    - `capacityMax: 50` → salles de 50 personnes et moins
                    - Les deux combinés → plage exacte [20 … 50]

                    ---

                    **Tri disponible :** `id`, `name`, `capacity`, `location`, `campus`

                    **Direction :** `ASC` ou `DESC`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des salles correspondant aux critères",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "contenu": [
                                            {
                                                "id":         1,
                                                "name":       "Salle A101",
                                                "capacity":   30,
                                                "location":   "Bâtiment A — 1er étage",
                                                "campus": "Campus Central",
                                                "equipment":  "Projecteu"
                                            },
                                            {
                                                "id":         2,
                                                "name":       "Salle A102",
                                                "capacity":   25,
                                                "location":   "Bâtiment A — 1er étage",
                                                "campus": "Campus Central",
                                                "equipment":  "Tableau blanc"
                                            }
                                        ],
                                        "pageActuelle":   0,
                                        "totalPages":     2,
                                        "totalElements": 11,
                                        "premierePage":   true,
                                        "dernierePage":   false
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "campusName manquant ou paramètres de pagination invalides",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status": 400,
                                        "message": "Données invalides",
                                        "erreurs": {
                                            "campusName": "Le nom du campus est obligatoire"
                                        }
                                    }
                                    """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = """
                    Critères de recherche et paramètres de pagination.
                    `campusName` est obligatoire. Tous les autres champs sont optionnels.
                    Les champs textuels (`name`, `location`, `equipment`) acceptent des valeurs partielles.
                    """,
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Recherche partielle par nom — 'sall' trouve 'Salle A101'",
                                    value = """
                                            {
                                                "campusName":    "Campus Central",
                                                "name":          "sall",
                                                "location":      null,
                                                "capacityMin":   null,
                                                "capacityMax":   null,
                                                "equipment":     null,
                                                "page":          0,
                                                "size":          10,
                                                "sortBy":        "name",
                                                "sortDirection": "ASC"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Recherche par équipement partiel — 'proj' trouve 'Projecteur, PC'",
                                    value = """
                                            {
                                                "campusName":    "Campus Central",
                                                "name":          null,
                                                "location":      null,
                                                "capacityMin":   null,
                                                "capacityMax":   null,
                                                "equipment":     "proj",
                                                "page":          0,
                                                "size":          10,
                                                "sortBy":        "capacity",
                                                "sortDirection": "DESC"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Plage de capacité entre 20 et 50 personnes",
                                    value = """
                                            {
                                                "campusName":    "Campus Sud",
                                                "name":          null,
                                                "location":      "bât",
                                                "capacityMin":   20,
                                                "capacityMax":   50,
                                                "equipment":     null,
                                                "page":          0,
                                                "size":          10,
                                                "sortBy":        "capacity",
                                                "sortDirection": "ASC"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Tous les filtres combinés",
                                    value = """
                                            {
                                                "campusName":    "Campus Central",
                                                "name":          "salle a",
                                                "location":      "bâtiment a",
                                                "capacityMin":   20,
                                                "capacityMax":   40,
                                                "equipment":     "pc",
                                                "page":          0,
                                                "size":          5,
                                                "sortBy":        "name",
                                                "sortDirection": "ASC"
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping("/search")
    public ResponseEntity<RoomPageDto> search(
            @Valid @RequestBody RoomSearchDto searchDto) {
        return ResponseEntity.ok(roomSearchService.search(searchDto));
    }

    // =========================================
    // SUPPRIMER UNE SALLE
    // =========================================
    @Operation(
            summary = "Supprimer une salle",
            description = """
                    Supprime définitivement une salle et toutes ses réservations passées (cascade).

                    **Règle métier :** la suppression est bloquée si des réservations futures
                    existent sur cette salle. Il faut d'abord supprimer ou annuler
                    ces réservations avant de pouvoir supprimer la salle.

                    **Exemple :** `DELETE /room/1`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Salle supprimée avec succès — aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Salle introuvable pour l'identifiant fourni",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucune salle trouvée pour l'id : 99"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Suppression bloquée : des réservations futures sont rattachées à cette salle",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  409,
                                        "message": "Impossible de supprimer la salle \\"Salle A101\\" : des réservations futures sont rattachées."
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant de la salle à supprimer", required = true, example = "1")
            @PathVariable Long roomId) {
        roomService.deleteById(roomId);
        return ResponseEntity.noContent().build();
    }
}