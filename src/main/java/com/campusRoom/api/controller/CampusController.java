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
@Tag(name = "Campus", description = "Endpoints pour gérer les campus universitaires : création, consultation, modification et suppression.")
@RequestMapping("/campus")
public class CampusController {

    private final CampusService campusService;
    private final CampusResearchService campusResearchService;

    // =========================================
    // CRÉER UN CAMPUS
    // =========================================
    @Operation(
            summary = "Créer un nouveau campus",
            description = """
                    Enregistre un nouveau campus universitaire dans le système.
                    Un campus est le conteneur principal de l'application : il regroupe
                    des salles, elles-mêmes liées à des réservations.

                    **Champs obligatoires :** `name`, `city`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Campus créé avec succès — aucun contenu retourné"
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
                                            "name": "Le nom du campus est obligatoire",
                                            "city": "La ville est obligatoire"
                                        },
                                        "timestamp": "2026-03-16T10:00:00"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflit : un campus portant ce nom existe déjà",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status": 409,
                                        "message": "Un campus avec ce nom existe déjà"
                                    }
                                    """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Informations du campus à créer",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Exemple",
                            value = """
                                    {
                                        "name": "Campus Saint-Nazaire",
                                        "city": "Saint-Nazaire"
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
    // OBTENIR UN CAMPUS PAR NOM
    // =========================================
    @Operation(
            summary = "Obtenir un campus par son nom",
            description = """
                    Retourne les informations d'un campus à partir de son nom exact.
                    La recherche est sensible à la casse.

                    **Exemple :** `GET /campus?name=Campus Central`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Campus trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "id":   1,
                                        "name": "Campus Central",
                                        "city": "Nantes"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucun campus trouvé pour ce nom",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucun campus trouvé pour le nom : Campus Central"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping
    ResponseEntity<CampusDto> getCampusByName(
            @Parameter(
                    description = "Nom exact du campus à rechercher",
                    required = true,
                    example = "Campus Central"
            )
            @RequestParam String name) {
        return ResponseEntity.ok(campusService.getCampusByName(name));
    }

    // =========================================
    // METTRE À JOUR UN CAMPUS
    // =========================================
    @Operation(
            summary = "Mettre à jour le nom et la ville d'un campus",
            description = """
                    Modifie partiellement un campus existant (PATCH).
                    Seuls le `name` et la `city` sont modifiables via cet endpoint.

                    **Exemple :** `PATCH /campus/1/update?name=Nouveau Nom&city=Paris`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Campus mis à jour avec succès — aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Campus introuvable pour l'identifiant fourni",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucun campus trouvé pour l'id : 99"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflit : le nouveau nom est déjà utilisé par un autre campus",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  409,
                                        "message": "Un campus avec ce nom existe déjà"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{campusId}/update")
    ResponseEntity<Void> updateName(
            @Parameter(description = "Identifiant du campus à modifier", required = true, example = "1")
            @PathVariable Long campusId,

            @Parameter(description = "Nouveau nom du campus", required = true, example = "Campus Sud")
            @RequestParam String name,

            @Parameter(description = "Nouvelle ville du campus", required = true, example = "Lyon")
            @RequestParam String city) {

        campusService.updateNameAndCity(campusId, name, city);
        return ResponseEntity.noContent().build();
    }

    // =========================================
    // RECHERCHE PAGINÉE
    // =========================================
    @Operation(
            summary = "Rechercher des campus avec filtres et pagination",
            description = """
                    Recherche des campus selon des critères combinables.
                    Tous les champs sont optionnels — sans filtre, retourne tous les campus paginés.

                    **Filtres disponibles :** `name` (LIKE), `city` (LIKE)

                    **Tri disponible :** `id`, `name`, `city`

                    **Direction :** `ASC` ou `DESC`
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des campus correspondant aux critères",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "contenu": [
                                            { "id": 1, "name": "Campus Central", "city": "Nantes" },
                                            { "id": 2, "name": "Campus Sud",     "city": "Lyon"   }
                                        ],
                                        "pageActuelle":   0,
                                        "totalPages":     3,
                                        "totalElements": 12,
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
            description = "Critères de recherche et paramètres de pagination — tous les champs sont optionnels",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Recherche par ville avec tri",
                                    value = """
                                            {
                                                "name":          null,
                                                "city":          "Nantes",
                                                "page":          0,
                                                "size":         10,
                                                "sortBy":        "name",
                                                "sortDirection": "ASC"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Tous les campus — page 2",
                                    value = """
                                            {
                                                "page":          1,
                                                "size":         10,
                                                "sortBy":        "id",
                                                "sortDirection": "DESC"
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping("/search")
    public ResponseEntity<CampusPageDto> search(
            @RequestBody CampusSearchDto searchDto) {
        return ResponseEntity.ok(campusResearchService.search(searchDto));
    }

    // =========================================
    // SUPPRIMER UN CAMPUS
    // =========================================
    @Operation(
            summary = "Supprimer un campus",
            description = """
                    Supprime définitivement un campus et toutes ses salles associées (cascade).

                    **Règle métier :** la suppression est bloquée si des réservations futures
                    existent sur l'une des salles du campus. Il faut d'abord annuler
                    ces réservations avant de pouvoir supprimer le campus.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Campus supprimé avec succès — aucun contenu retourné"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Campus introuvable pour l'identifiant fourni",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  404,
                                        "message": "Aucun campus trouvé pour l'id : 99"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Suppression bloquée : des réservations futures sont rattachées à ce campus",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "status":  409,
                                        "message": "Impossible de supprimer le campus \\"Campus Central\\" : des réservations futures sont rattachées à ses salles."
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{campusId}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant du campus à supprimer", required = true, example = "1")
            @PathVariable Long campusId) {
        campusService.deleteById(campusId);
        return ResponseEntity.noContent().build();
    }
}