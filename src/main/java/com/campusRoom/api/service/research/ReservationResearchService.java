package com.campusRoom.api.service.research;

import com.campusRoom.api.dto.researchDto.ReservationPageDto;
import com.campusRoom.api.dto.researchDto.ReservationSearchDto;

public interface ReservationResearchService {

    /**
     * Retourne la pagination contenant les réservations en fonction des critères de recherche
     * @param searchDto contenant les critères de recherche
     * @return le résultat paginé.
     */
    ReservationPageDto search(ReservationSearchDto searchDto);
}
