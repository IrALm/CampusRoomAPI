package com.campusRoom.api.service;

import com.campusRoom.api.dto.formDto.UserFormDto;
import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.entity.Role;

public interface UserService {

    /**
     * Vérifie si un utilisateur exist
     * @param email email utilisateur
     * @return true or false.
     */
    boolean verifyIfUserExist(String email);

    /**
     * Récupère User grâce à son email.
     * @param email email utilisateur
     * @return user
     */
    UserDto getUserByEmail(String email);

    /**
     * Vérifie si le rôle utilisateur est correcte
     * @param role rôle de l'utilisateur.
     * @return true or false.
     */
    boolean verifyIfRoleIsValid( String role);

    /**
     * Créer un utilisateur
     * @param userFormDto infos utilisateur.
     */
    void createUser(UserFormDto userFormDto);

    /**
     * Met à jour le prénom utilisateur.
     * @param id id user
     * @param firstName prénom
     */
    void updateFirstName(Long id, String firstName);

    /**
     * Met à jour le nom utilisateur.
     * @param id id user
     * @param lastName nom.
     */
    void updateLastName(Long id, String lastName);
}
