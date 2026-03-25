package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.RoomFormDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.dto.researchDto.RoomPageDto;
import com.campusRoom.api.dto.researchDto.RoomSearchDto;
import com.campusRoom.api.service.RoomService;
import com.campusRoom.api.service.research.RoomSearchService;
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
    private final RoomSearchService roomSearchService;

    @PostMapping()
    ResponseEntity<Void> createRoom(@RequestBody @Valid RoomFormDto roomFormDto){

        roomService.createRoom(roomFormDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    ResponseEntity<RoomDto> getRoomByName(@RequestParam String name){

        return ResponseEntity.ok(roomService.getByRoomName(name));
    }

    @PatchMapping("/{roomId}/capacity")
    ResponseEntity<Void> updateRoomCapacity(@PathVariable Long roomId , @RequestParam int capacity){

        roomService.updateRoomCapacity(roomId , capacity);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{roomId}/name")
    ResponseEntity<Void> updateRoomName(@PathVariable Long roomId , @RequestParam Long campusId , @RequestParam String name){

        roomService.updateRoomName(campusId , roomId , name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<RoomPageDto> search(
            @Valid @RequestBody RoomSearchDto searchDto) {
        return ResponseEntity.ok(roomSearchService.search(searchDto));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long roomId) {
        roomService.deleteById(roomId);
        return ResponseEntity.noContent().build();
    }
}
