package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.dto.researchDto.CampusPageDto;
import com.campusRoom.api.dto.researchDto.CampusSearchDto;
import com.campusRoom.api.service.CampusService;
import com.campusRoom.api.service.research.CampusResearchService;
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
@Tag(name = "Campus", description = "Endpoints pour gérer les campus universitaires.")
@RequestMapping("/campus")
public class CampusController {

    private final CampusService campusService;
    private final CampusResearchService campusResearchService;

    // =========================================
    // CREATE
    // =========================================
    @Operation(summary = "Créer un campus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Campus créé"),
            @ApiResponse(responseCode = "400", description = "Erreur validation"),
            @ApiResponse(responseCode = "409", description = "Déjà existant")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Campus à créer",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Création ESGI Paris",
                            value = """
                                    {
                                        "name": "ESGI - Campus Paris",
                                        "city": "Paris"
                                    }
                                    """
                    )
            )
    )
    @PostMapping
    ResponseEntity<Void> createCampus(@RequestBody @Valid CampusFormDto campusFormDto) {
        campusService.createCampus(campusFormDto);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // GET BY NAME
    // =========================================
    @Operation(summary = "Récupérer un campus par nom")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Campus trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "id": 1,
                                        "name": "ESGI - Campus Paris",
                                        "city": "Paris"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Non trouvé")
    })
    @GetMapping
    ResponseEntity<CampusDto> getCampusByName(
            @Parameter(
                    description = "Nom du campus",
                    required = true,
                    example = "ESGI - Campus Paris"
            )
            @RequestParam String name) {

        return ResponseEntity.ok(campusService.getCampusByName(name));
    }

    // =========================================
    // UPDATE
    // =========================================
    @Operation(
            summary = "Mettre à jour un campus",
            description = "PATCH /campus/1/update?name=ESGI - Campus de Nantes&city=Nantes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mis à jour"),
            @ApiResponse(responseCode = "404", description = "Non trouvé"),
            @ApiResponse(responseCode = "409", description = "Conflit")
    })
    @PatchMapping("/{campusId}/update")
    ResponseEntity<Void> updateName(
            @Parameter(example = "1")
            @PathVariable Long campusId,

            @Parameter(example = "ESGI - Campus de Nantes")
            @RequestParam String name,

            @Parameter(example = "Nantes")
            @RequestParam String city) {

        campusService.updateNameAndCity(campusId, name, city);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // SEARCH
    // =========================================
    @Operation(
            summary = "Rechercher des campus avec filtres et pagination",
            description = """
                    Recherche des campus selon des critères combinables.
                    Tous les champs sont optionnels — sans filtre, retourne tous les campus paginés.

                    ─── FILTRES DISPONIBLES ─────────────────────────────
                    • name : recherche partielle (LIKE)
                      → Exemple : "ESGI" trouvera "ESGI - Campus Paris"

                    • city : recherche partielle insensible à la casse
                      → Exemple : "paris" trouvera "Paris"

                    Les filtres peuvent être combinés :
                    → name = "ESGI" ET city = "Paris"

                    ─── PAGINATION ──────────────────────────────────────
                    • page : numéro de page (commence à 0)
                    • size : nombre d’éléments par page

                    ─── TRI ─────────────────────────────────────────────
                    • sortBy : champ de tri possible → id, name, city
                    • sortDirection : ASC (croissant) ou DESC (décroissant)

                    ─── EXEMPLE COMPLET ─────────────────────────────────
                    {
                      "name": "ESGI",
                      "city": "paris",
                      "page": 0,
                      "size": 10,
                      "sortBy": "name",
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
                                                "name": "ESGI - Campus Paris",
                                                "city": "Paris"
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
            description = "Recherche campus",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Recherche Paris",
                            value = """
                                    {
                                        "name": null,
                                        "city": "paris",
                                        "page": 0,
                                        "size": 10,
                                        "sortBy": "name",
                                        "sortDirection": "ASC"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/search")
    public ResponseEntity<CampusPageDto> search(
            @RequestBody CampusSearchDto searchDto) {
        return ResponseEntity.ok(campusResearchService.search(searchDto));
    }

    // =========================================
    // DELETE
    // =========================================
    @Operation(summary = "Supprimer un campus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supprimé"),
            @ApiResponse(responseCode = "404", description = "Non trouvé"),
            @ApiResponse(responseCode = "409", description = "Bloqué")
    })
    @DeleteMapping("/{campusId}")
    public ResponseEntity<Void> deleteById(
            @Parameter(example = "1")
            @PathVariable Long campusId) {

        campusService.deleteById(campusId);
        return ResponseEntity.noContent().build();
    }
}