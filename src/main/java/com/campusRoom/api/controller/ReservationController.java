package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.ReservationFormDto;
import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.dto.researchDto.ReservationPageDto;
import com.campusRoom.api.dto.researchDto.ReservationSearchDto;
import com.campusRoom.api.service.ReservationService;
import com.campusRoom.api.service.research.ReservationResearchService;
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
@Tag(name = "Reservations", description = "Endpoints pour gérer les réservations de salles universitaires.")
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationResearchService reservationResearchService;

    // =========================================
    // CRÉER UNE RÉSERVATION
    // =========================================
    @Operation(
            summary = "Créer une nouvelle réservation",
            description = """
                    Crée une réservation de salle universitaire pour un utilisateur donné.

                    **Règles métier appliquées automatiquement :**
                    - `ConflictStrategy` — vérifie qu'aucune réservation n'existe déjà sur ce créneau
                    - `PriorityStrategy` — bloque un étudiant si un enseignant a réservé ce créneau
                    - `QuotaStrategy`    — bloque un étudiant ayant atteint son quota mensuel

                    **Types disponibles :** `COURSE`, `MEETING`, `EXAM`

                    **Durées maximales par type :**
                    | Type    | Durée max |
                    |---------|-----------|
                    | COURSE  | 3h        |
                    | MEETING | 2h        |
                    | EXAM    | 4h        |

                    **Champs obligatoires :** `type`, `startTime`, `endTime`, `roomId`, `userId`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Réservation créée avec succès — aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requête invalide : champs manquants, date passée ou durée dépassée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status": 400,
                                        "message": "Données invalides",
                                        "erreurs": {
                                            "type":      "Le type est obligatoire",
                                            "startTime": "La date de début doit être dans le futur",
                                            "roomId":    "L'id de la salle est obligatoire",
                                            "userId":    "L'id de l'utilisateur est obligatoire"
                                        },
                                        "timestamp": "2026-03-16T10:00:00"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Salle ou utilisateur introuvable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Salle introuvable : 99"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflit métier : créneau déjà réservé, durée dépassée ou priorité refusée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Conflit horaire",
                                            value = """
                                                    {
                                                        "status":  409,
                                                        "message": "Ce créneau est déjà réservé pour cette salle."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Durée dépassée",
                                            value = """
                                                    {
                                                        "status":  409,
                                                        "message": "Durée max pour ce type : 3h"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Priorité enseignant",
                                            value = """
                                                    {
                                                        "status":  403,
                                                        "message": "Ce créneau est réservé à un enseignant. Les étudiants ont une priorité inférieure."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Quota mensuel dépassé",
                                            value = """
                                                    {
                                                        "status":  409,
                                                        "message": "Quota mensuel atteint (5 réservations max)."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Informations complètes de la réservation à créer",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Réservation de cours (enseignant)",
                                    value = """
                                            {
                                                "type":      "COURSE",
                                                "startTime": "2026-06-01T10:00:00",
                                                "endTime":   "2026-06-01T12:00:00",
                                                "roomId":    1,
                                                "userId":    3
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Réservation d'examen (durée max 4h)",
                                    value = """
                                            {
                                                "type":      "EXAM",
                                                "startTime": "2026-06-10T08:00:00",
                                                "endTime":   "2026-06-10T12:00:00",
                                                "roomId":    2,
                                                "userId":    5
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Réunion courte (durée max 2h)",
                                    value = """
                                            {
                                                "type":      "MEETING",
                                                "startTime": "2026-05-20T14:00:00",
                                                "endTime":   "2026-05-20T15:30:00",
                                                "roomId":    3,
                                                "userId":    7
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody ReservationFormDto dto) {
        reservationService.create(dto);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // OBTENIR UNE RÉSERVATION PAR ID
    // =========================================
    @Operation(
            summary = "Obtenir une réservation avec toutes ses propriétés",
            description = """
                    Retourne le détail complet d'une réservation : informations de la salle,
                    du campus, de l'utilisateur, type, horaires et description métier.

                    **Exemple :** `GET /reservations/1`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Réservation trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "id":          1,
                                        "type":        "COURSE",
                                        "startTime":   "2026-06-01T10:00:00",
                                        "endTime":     "2026-06-01T12:00:00",
                                        "description": "Réservation de cours",
                                        "roomName":    "Salle A101",
                                        "campusName":  "Campus Central",
                                        "userName":    "Jean Dupont",
                                        "userRole":    "TEACHER"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucune réservation trouvée pour cet identifiant",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucune reservation n'existe pour cet id"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDto> getReservationWithAllProperties(
            @Parameter(description = "Identifiant de la réservation", required = true, example = "1")
            @PathVariable Long reservationId) {
        ReservationDto dto = reservationService.getReservationWithAllProperties(reservationId);
        return ResponseEntity.ok(dto);
    }

    // =========================================
    // RECHERCHE PAGINÉE
    // =========================================
    @Operation(
            summary = "Rechercher des réservations avec filtres et pagination",
            description = """
                    Recherche des réservations selon des critères combinables.
                    Tous les champs sont optionnels — sans filtre, retourne toutes les réservations paginées.

                    **Filtres disponibles :**
                    | Champ         | Type              | Comportement          |
                    |---------------|-------------------|-----------------------|
                    | `type`        | enum              | Égalité exacte        |
                    | `roomId`      | Long              | Égalité exacte        |
                    | `userId`      | Long              | Égalité exacte        |
                    | `startTime`   | ISO date-time     | Supérieur ou égal     |
                    | `endTime`     | ISO date-time     | Inférieur ou égal     |

                    **Tri disponible :** `id`, `type`, `startTime`, `endTime`, `description`, `maxDurationHours`

                    **Direction :** `ASC` ou `DESC`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des réservations correspondant aux critères",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "contenu": [
                                            {
                                                "id":          1,
                                                "type":        "COURSE",
                                                "startTime":   "2026-06-01T10:00:00",
                                                "endTime":     "2026-06-01T12:00:00",
                                                "description": "Réservation de cours",
                                                "roomName":    "Salle A101",
                                                "userName":    "Jean Dupont"
                                            }
                                        ],
                                        "pageActuelle":   0,
                                        "totalPages":     5,
                                        "totalElements": 48,
                                        "premierePage":   true,
                                        "dernierePage":   false
                                    }
                                    """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Critères de recherche et paramètres de pagination — tous les champs sont optionnels",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Filtrer par type EXAM",
                                    value = """
                                            {
                                                "type":          "EXAM",
                                                "roomId":        null,
                                                "userId":        null,
                                                "startTime":     null,
                                                "endTime":       null,
                                                "page":          0,
                                                "size":         10,
                                                "sortBy":        "startTime",
                                                "sortDirection": "ASC"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Réservations d'une salle sur une plage de dates",
                                    value = """
                                            {
                                                "type":          null,
                                                "roomId":        3,
                                                "userId":        null,
                                                "startTime":     "2026-06-01T00:00:00",
                                                "endTime":       "2026-06-30T23:59:59",
                                                "page":          0,
                                                "size":         10,
                                                "sortBy":        "startTime",
                                                "sortDirection": "ASC"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Toutes les réservations d'un utilisateur",
                                    value = """
                                            {
                                                "userId":        5,
                                                "page":          0,
                                                "size":         20,
                                                "sortBy":        "startTime",
                                                "sortDirection": "DESC"
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping("/search")
    public ResponseEntity<ReservationPageDto> search(
            @RequestBody ReservationSearchDto searchDto) {
        return ResponseEntity.ok(reservationResearchService.search(searchDto));
    }

    // =========================================
    // SUPPRIMER UNE RÉSERVATION
    // =========================================
    @Operation(
            summary = "Supprimer une réservation",
            description = """
                    Supprime définitivement une réservation existante.
                    Contrairement aux campus et aux salles, la suppression d'une réservation
                    n'est jamais bloquée — il n'y a pas d'entité enfant liée.

                    **Exemple :** `DELETE /reservations/1`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Réservation supprimée avec succès — aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucune réservation trouvée pour cet identifiant",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucune réservation trouvée pour l'id : 99"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant de la réservation à supprimer", required = true, example = "1")
            @PathVariable Long reservationId) {
        reservationService.deleteById(reservationId);
        return ResponseEntity.noContent().build();
    }
}