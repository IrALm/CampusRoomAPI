package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.UserFormDto;
import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User" , description = "Endpoints pour gérer les utilisateurs.")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping()
    ResponseEntity<Void> createUser(@RequestBody @Valid UserFormDto userFormDto){

        userService.createUser(userFormDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email")
    ResponseEntity<UserDto> getUserByEmail(@RequestParam String email){

        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

}
