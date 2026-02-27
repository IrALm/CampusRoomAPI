package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.service.CampusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name ="Campus" , description = "Endpoints pour gérer Le campus.")
@RequestMapping("/campus")
public class CampusController {

    private final CampusService campusService;

    @PostMapping()
    ResponseEntity<CampusDto> createCampus(@RequestBody @Valid CampusFormDto campusFormDto){

        return ResponseEntity.ok(campusService.createCampus(campusFormDto));
    }
}
