package com.campusRoom.api.repository;

import com.campusRoom.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.firstName = :firstName WHERE u.id = :id")
    void updateFirstName(Long id, String firstName);

    @Modifying
    @Query("UPDATE User u SET u.lastName = :lastName WHERE u.id = :id")
    void updateLastName(Long id, String lastName);
}
