package com.campusRoom.api.repository;

import com.campusRoom.api.entity.Campus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampusRepository extends JpaRepository<Campus , Long> {

    Optional<Campus> findByName(String name);

    boolean existsByName(String name);

    @Modifying
    @Query(" UPDATE Campus c SET c.name = :name , c.city = :city  where c.id = :id " )
    void updateNameAndCity(Long id , String name , String city);
}
