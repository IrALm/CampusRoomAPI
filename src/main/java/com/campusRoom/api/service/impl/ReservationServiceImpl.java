package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.ReservationFormDto;
import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.Room;
import com.campusRoom.api.entity.User;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.ReservationMapper;
import com.campusRoom.api.repository.ReservationRepository;
import com.campusRoom.api.service.ReservationService;
import com.campusRoom.api.service.RoomService;
import com.campusRoom.api.service.UserService;
import com.campusRoom.api.service.patternFactory.ReservationBehavior;
import com.campusRoom.api.service.patternFactory.ReservationFactory;
import com.campusRoom.api.service.patternStrategy.ValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationFactory reservationFactory;
    private final List<ValidationStrategy> validationStrategies;// Spring injecte automatiquement toutes les ValidationStrategy détectées
    private final UserService userService;
    private final RoomService roomService;
    private final ReservationMapper reservationMapper;

    @Override
    public void create(ReservationFormDto dto) {

        // 1. Factory : obtenir le comportement selon le type
        ReservationBehavior behavior = reservationFactory.create(dto.type());

        // 2. Construire l'entité
        User currentUser = userService.getUserById(dto.userId());
        Room room = roomService.getRoomById(dto.roomId());
        Reservation reservation = new Reservation();
        reservation.setStartTime(dto.startTime());
        reservation.setEndTime(dto.endTime());
        reservation.setType(dto.type());
        reservation.setUser(currentUser);
        reservation.setRoom(room);
        reservation.setDescription(behavior.getDescription());

        // 3. Vérifier durée max selon type (règle de la Factory)
        long hours = ChronoUnit.HOURS.between(dto.startTime(), dto.endTime());
        if (hours > behavior.getMaxDurationHours()) {
            throw new CampusRoomBusinessException(
                    "Durée max pour ce type : " + behavior.getMaxDurationHours() + "h",
                    HttpStatus.CONFLICT
            );
        }
        reservation.setMaxDurationHours((int) hours);

        // 4. Strategies : exécuter toutes les validations
        for (ValidationStrategy strategy : validationStrategies) {
            strategy.validate(reservation, currentUser);
        }

        reservationRepository.save(reservation);
    }

    @Override
    public ReservationDto getReservationWithAllProperties(Long reservationId){

        Reservation reservation = reservationRepository.findReservationWithAllProperties(reservationId);
        if( reservation == null){
            throw  new CampusRoomBusinessException("Aucune reservation n'existe pour cet id" ,
                    HttpStatus.NOT_FOUND);
        }

        return reservationMapper.toDTO(reservation);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new CampusRoomBusinessException(
                    "Aucune réservation trouvée pour l'id : " + id,
                    HttpStatus.NOT_FOUND
            );
        }
        reservationRepository.deleteById(id);
    }
}
