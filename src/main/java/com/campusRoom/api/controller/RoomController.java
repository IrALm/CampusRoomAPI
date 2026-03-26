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
@Tag(name = "Room", description = "Endpoints pour gérer les salles universitaires.")
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;
    private final RoomSearchService roomSearchService;

    // =========================================
    // CREATE
    // =========================================
    @Operation(summary = "Créer une salle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Salle créée"),
            @ApiResponse(responseCode = "400", description = "Erreur validation"),
            @ApiResponse(responseCode = "404", description = "Campus introuvable"),
            @ApiResponse(responseCode = "409", description = "Déjà existante")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Salle à créer",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Salle A101 (Postman)",
                            value = """
                                    {
                                        "name": "Salle A101",
                                        "capacity": 30,
                                        "location": "Bâtiment A - 1er étage",
                                        "campusId": 1,
                                        "equipment" :["projecteur" , "écran"]
                                    }
                                    """
                    )
            )
    )
    @PostMapping
    ResponseEntity<Void> createRoom(@RequestBody @Valid RoomFormDto roomFormDto) {
        roomService.createRoom(roomFormDto);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // GET BY NAME
    // =========================================
    @Operation(summary = "Obtenir une salle par nom exact")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Salle trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "id": 1,
                                        "name": "Salle A101",
                                        "capacity": 30,
                                        "location": "Bâtiment A - 1er étage",
                                        "campusId": 1,
                                        "equipment" :["projecteur" , "écran"]
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Non trouvée")
    })
    @GetMapping
    ResponseEntity<RoomDto> getRoomByName(
            @Parameter(example = "Salle A101")
            @RequestParam String name) {
        return ResponseEntity.ok(roomService.getByRoomName(name));
    }

    // =========================================
    // UPDATE CAPACITY
    // =========================================
    @Operation(
            summary = "Mettre à jour la capacité",
            description = "PATCH /room/1/capacity?capacity=45"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mis à jour"),
            @ApiResponse(responseCode = "400", description = "Capacité invalide"),
            @ApiResponse(responseCode = "404", description = "Salle introuvable")
    })
    @PatchMapping("/{roomId}/capacity")
    ResponseEntity<Void> updateRoomCapacity(
            @Parameter(example = "1")
            @PathVariable Long roomId,

            @Parameter(example = "45")
            @RequestParam int capacity) {

        roomService.updateRoomCapacity(roomId, capacity);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // UPDATE NAME
    // =========================================
    @Operation(
            summary = "Mettre à jour le nom",
            description = "PATCH /room/1/name?campusId=1&name=Salle A101"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mis à jour"),
            @ApiResponse(responseCode = "404", description = "Salle ou campus introuvable"),
            @ApiResponse(responseCode = "409", description = "Nom déjà utilisé")
    })
    @PatchMapping("/{roomId}/name")
    ResponseEntity<Void> updateRoomName(
            @Parameter(example = "1")
            @PathVariable Long roomId,

            @Parameter(example = "1")
            @RequestParam Long campusId,

            @Parameter(example = "Salle A101")
            @RequestParam String name) {

        roomService.updateRoomName(campusId, roomId, name);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // SEARCH
    // =========================================
    @Operation(
            summary = "Rechercher des salles avec filtres et pagination",
            description = """
                    Recherche des salles selon des critères combinables.

                    ─── FILTRES DISPONIBLES ─────────────────────────────

                    • campusName : obligatoire (recherche partielle)
                      → Exemple : "central"

                    • name : recherche partielle (LIKE)
                      → "salle" → "Salle A101"

                    • location : recherche partielle
                      → "bâtiment A"

                    • equipment : recherche partielle
                      → "projecteur"

                    • capacityMin : capacité minimale (>=)
                    • capacityMax : capacité maximale (<=)

                    Les filtres peuvent être combinés :
                    → campusName + capacityMin + equipment

                    ─── PAGINATION ──────────────────────────────────────
                    • page : numéro de page (0 par défaut)
                    • size : nombre d’éléments

                    ─── TRI ─────────────────────────────────────────────
                    • sortBy : id, name, capacity, location
                    • sortDirection : ASC ou DESC

                    ─── EXEMPLE COMPLET (Postman) ───────────────────────
                    {
                      "campusName": "central",
                      "name": null,
                      "location": "bâtiment A",
                      "capacityMin": 20,
                      "capacityMax": 50,
                      "equipment": "projecteur",
                      "page": 0,
                      "size": 10,
                      "sortBy": "capacity",
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
                                                "name": "Salle A101",
                                                "capacity": 30,
                                                "location": "Bâtiment A - 1er étage",
                                                "campus": "Campus Central"
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
            description = "Recherche salles",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Recherche Postman",
                            value = """
                                    {
                                      "campusName": "central",
                                      "name": null,
                                      "location": "bâtiment A",
                                      "capacityMin": 20,
                                      "capacityMax": 50,
                                      "equipment": "projecteur",
                                      "page": 0,
                                      "size": 10,
                                      "sortBy": "capacity",
                                      "sortDirection": "ASC"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/search")
    public ResponseEntity<RoomPageDto> search(
            @Valid @RequestBody RoomSearchDto searchDto) {
        return ResponseEntity.ok(roomSearchService.search(searchDto));
    }

    // =========================================
    // DELETE
    // =========================================
    @Operation(
            summary = "Supprimer une salle",
            description = "DELETE /room/1"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supprimée"),
            @ApiResponse(responseCode = "404", description = "Non trouvée"),
            @ApiResponse(responseCode = "409", description = "Réservations futures existantes")
    })
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteById(
            @Parameter(example = "1")
            @PathVariable Long roomId) {

        roomService.deleteById(roomId);
        return ResponseEntity.noContent().build();
    }
}