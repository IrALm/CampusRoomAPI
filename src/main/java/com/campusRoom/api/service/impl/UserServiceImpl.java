package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.UserFormDto;
import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.entity.User;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.UserMapper;
import com.campusRoom.api.repository.UserRepository;
import com.campusRoom.api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public boolean verifyIfUserExist(String email){

        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDto getUserByEmail(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new CampusRoomBusinessException("Aucun utilisateur trouvé avec l'email : "
                                + email , HttpStatus.NOT_FOUND));

        return userMapper.toDTO(user);
    }

    @Override
    public boolean verifyIfRoleIsValid( String role){

        return Role.isValid(role);
    }

    @Override
    public void createUser(UserFormDto userFormDto){

        boolean userExist = verifyIfUserExist(userFormDto.email());

        if(userExist){
            throw new CampusRoomBusinessException("Un utilisateur existe déjà pour l'email : "
                    + userFormDto.email() , HttpStatus.CONFLICT);
        }

        boolean validRole = verifyIfRoleIsValid(userFormDto.role());

        if(!validRole){
            throw new CampusRoomBusinessException(" Le rôle de l'utilisateur : "
                    + userFormDto.firstName() + " est invalide." ,
                    HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(userFormDto.email())
                .firstName(userFormDto.firstName())
                .lastName(userFormDto.lastName())
                .role(Role.valueOf(userFormDto.role().toUpperCase()))
                .build();

        userRepository.save(user);
    }

    @Transactional
    public void updateFirstName(Long id, String firstName){

        userRepository.updateFirstName(id , firstName);
    }

    @Transactional
    public void updateLastName(Long id, String lastName){

        userRepository.updateLastName(id , lastName);
    }
}
