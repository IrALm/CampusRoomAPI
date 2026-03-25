package com.campusRoom.api.service.impl;

import com.campusRoom.api.repository.ReservationRepository;
import com.campusRoom.api.service.ReservationChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationCheckerImpl implements ReservationChecker {

    private final ReservationRepository reservationRepository;

    @Override
    public boolean existsByRoomIdAndStartTimeAfter(Long roomId, LocalDateTime date){

        return reservationRepository
                .existsByRoomIdAndStartTimeAfter(roomId, date);
    }


    @Override
    public boolean existsByRoomCampusIdAndStartTimeAfter(Long campusId, LocalDateTime date){

        return reservationRepository
                .existsByRoomCampusIdAndStartTimeAfter(campusId, date);
    }
}
