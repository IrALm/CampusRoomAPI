package com.campusRoom.api.repository;

import com.campusRoom.api.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> , JpaSpecificationExecutor<Reservation> {

    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.id != :excludeId
          AND r.startTime < :end
          AND r.endTime   > :start
    """)
    boolean existsConflict(
            @Param("roomId")    Long roomId,
            @Param("start")     LocalDateTime start,
            @Param("end")       LocalDateTime end,
            @Param("excludeId") Long excludeId
    );

    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.user.role = 'TEACHER'
          AND r.startTime < :end
          AND r.endTime   > :start
    """)
    boolean existsTeacherReservation(
            @Param("roomId") Long roomId,
            @Param("start") LocalDateTime start,
            @Param("end")    LocalDateTime end
    );

    @Query("""
        SELECT COUNT(r) FROM Reservation r
        WHERE r.user.id = :userId
          AND MONTH(r.startTime) = :month
          AND YEAR(r.startTime)  = :year
    """)
    long countByUserAndMonth(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year")   int year
    );

    @Query("""
        SELECT r FROM Reservation r
        INNER JOIN FETCH r.user
        INNER JOIN FETCH r.room
        WHERE r.id = :reservationId
    """)
    Reservation findReservationWithAllProperties(@Param("reservationId") Long reservationId);
}
