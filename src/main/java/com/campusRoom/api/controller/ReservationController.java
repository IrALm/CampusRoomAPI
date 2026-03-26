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
                    - ConflictStrategy — vérifie qu'aucune réservation n'existe déjà sur ce créneau
                    - PriorityStrategy — bloque un étudiant si un enseignant a réservé ce créneau
                    - QuotaStrategy    — bloque un étudiant ayant atteint son quota mensuel

                    **Types disponibles :** COURSE, MEETING, EXAM

                    **Durées maximales :**
                    COURSE → 3h
                    MEETING → 2h
                    EXAM → 4h

                    **Champs obligatoires :** type, startTime, endTime, roomId, userId
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Réservation créée"),
            @ApiResponse(responseCode = "400", description = "Erreur validation"),
            @ApiResponse(responseCode = "404", description = "Salle ou utilisateur introuvable"),
            @ApiResponse(responseCode = "409", description = "Conflit métier")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Réservation à créer",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "MEETING simple (Postman)",
                            value = """
                                    {
                                        "type": "MEETING",
                                        "startTime": "2026-03-30T10:00:00",
                                        "endTime": "2026-03-30T11:00:00",
                                        "roomId": 1,
                                        "userId": 1
                                    }
                                    """
                    )
            )
    )
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody ReservationFormDto dto) {
        reservationService.create(dto);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // GET BY ID
    // =========================================
    @Operation(
            summary = "Obtenir une réservation",
            description = "GET /reservations/1"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Réservation trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "id": 1,
                                        "type": "MEETING",
                                        "startTime": "2026-03-30T10:00:00",
                                        "endTime": "2026-03-30T11:00:00",
                                        "description": "Réunion",
                                        "roomName": "Salle A101",
                                        "campusName": "ESGI - Campus Paris",
                                        "userName": "John Doe",
                                        "userRole": "STUDENT"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Non trouvé")
    })
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDto> getReservationWithAllProperties(
            @Parameter(example = "1")
            @PathVariable Long reservationId) {

        return ResponseEntity.ok(
                reservationService.getReservationWithAllProperties(reservationId)
        );
    }

    // =========================================
    // SEARCH
    // =========================================
    @Operation(
            summary = "Rechercher des réservations avec filtres et pagination",
            description = """
                    Recherche des réservations selon des critères combinables.
                    Tous les champs sont optionnels — sans filtre, retourne toutes les réservations paginées.

                    ─── FILTRES DISPONIBLES ─────────────────────────────
                    • type : égalité exacte
                      → Exemple : "MEETING"

                    • roomId : égalité exacte
                      → Exemple : 1

                    • userId : égalité exacte
                      → Exemple : 1

                    • startTime : date minimale (>=)
                      → Exemple : "2026-03-01T00:00:00"

                    • endTime : date maximale (<=)
                      → Exemple : "2026-03-31T23:59:59"

                    Les filtres peuvent être combinés :
                    → userId = 1 ET roomId = 1
                    → période de temps (startTime + endTime)

                    ─── PAGINATION ──────────────────────────────────────
                    • page : numéro de page (commence à 0)
                    • size : nombre d’éléments

                    ─── TRI ─────────────────────────────────────────────
                    • sortBy : id, type, startTime, endTime
                    • sortDirection : ASC ou DESC

                    ─── EXEMPLE COMPLET ─────────────────────────────────
                    {
                      "type": null,
                      "roomId": null,
                      "userId": null,
                      "startTime": null,
                      "endTime": null,
                      "page": 0,
                      "size": 10,
                      "sortBy": "startTime",
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
                                                "type": "MEETING",
                                                "startTime": "2026-03-30T10:00:00",
                                                "endTime": "2026-03-30T11:00:00",
                                                "roomName": "Salle A101",
                                                "userName": "John Doe"
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
            description = "Recherche réservations",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Recherche vide (Postman)",
                            value = """
                                    {
                                        "type": null,
                                        "roomId": null,
                                        "userId": null,
                                        "startTime": null,
                                        "endTime": null,
                                        "page": 0,
                                        "size": 10,
                                        "sortBy": "startTime",
                                        "sortDirection": "ASC"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/search")
    public ResponseEntity<ReservationPageDto> search(
            @RequestBody ReservationSearchDto searchDto) {
        return ResponseEntity.ok(reservationResearchService.search(searchDto));
    }

    // =========================================
    // DELETE
    // =========================================
    @Operation(
            summary = "Supprimer une réservation",
            description = "DELETE /reservations/1"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supprimée"),
            @ApiResponse(responseCode = "404", description = "Non trouvée")
    })
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteById(
            @Parameter(example = "1")
            @PathVariable Long reservationId) {

        reservationService.deleteById(reservationId);
        return ResponseEntity.noContent().build();
    }
}