package com.campusRoom.api.repository;

import com.campusRoom.api.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room , Long> {

    boolean existsByName(String name);

    Optional<Room> findByName(String name);

    @Modifying
    @Query("UPDATE Room r set r.capacity = :capacity where r.id = :id")
    void updateRoomCapacity(Long id , int capacity);

    @Modifying
    @Query("UPDATE Room r set r.name = :name where r.id = :id")
    void updateRoomName(Long id , String name);
}
