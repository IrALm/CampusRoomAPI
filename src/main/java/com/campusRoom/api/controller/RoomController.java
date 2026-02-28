package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.RoomFormDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name= "Room" , description = "Endpoints pour gérer les salles du campus")
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping()
    ResponseEntity<Void> createRoom(@RequestBody @Valid RoomFormDto roomFormDto){

        roomService.createRoom(roomFormDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    ResponseEntity<RoomDto> getRoomByName(@RequestParam String name){

        return ResponseEntity.ok(roomService.getByRoomName(name));
    }
}
