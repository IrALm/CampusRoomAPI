package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.service.CampusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name ="Campus" , description = "Endpoints pour gérer Le campus.")
@RequestMapping("/campus")
public class CampusController {

    private final CampusService campusService;

    @PostMapping()
    ResponseEntity<Void> createCampus(@RequestBody @Valid CampusFormDto campusFormDto){

        campusService.createCampus(campusFormDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    ResponseEntity<CampusDto> getCampusByName(@RequestParam String name){

        return ResponseEntity.ok(campusService.getCampusByName(name));
    }

    @PatchMapping("/{campusId}/update")
    ResponseEntity<Void> updateName(@PathVariable Long campusId , @RequestParam String name, @RequestParam String city){

        campusService.updateNameAndCity(campusId , name , city);
        return ResponseEntity.noContent().build();
    }

}
