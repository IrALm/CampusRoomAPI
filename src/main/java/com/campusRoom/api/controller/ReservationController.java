package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.ReservationFormDto;
import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.service.ReservationService;
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
}
