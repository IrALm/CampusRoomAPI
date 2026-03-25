package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.ReservationFormDto;
import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.dto.researchDto.ReservationPageDto;
import com.campusRoom.api.dto.researchDto.ReservationSearchDto;
import com.campusRoom.api.service.ReservationService;
import com.campusRoom.api.service.research.ReservationResearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name ="Reservations" , description = "Endpoints pour gérer les réservations")
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationResearchService reservationResearchService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody ReservationFormDto dto) {
        reservationService.create(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDto> getReservationWithAllProperties(@PathVariable Long reservationId) {
        ReservationDto dto = reservationService.getReservationWithAllProperties(reservationId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/search")
    public ResponseEntity<ReservationPageDto> search(
            @RequestBody ReservationSearchDto searchDto) {
        return ResponseEntity.ok(reservationResearchService.search(searchDto));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long reservationId) {
        reservationService.deleteById(reservationId);
        return ResponseEntity.noContent().build();
    }
}
