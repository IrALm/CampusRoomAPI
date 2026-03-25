package com.campusRoom.api.service.research;

import com.campusRoom.api.dto.researchDto.RoomPageDto;
import com.campusRoom.api.dto.researchDto.RoomSearchDto;

public interface RoomSearchService {

    /**
     * Recherche des rooms avec des filtres
     * @param searchDto contenant filtre de recherche
     * @return résultat paginé.
     */
    RoomPageDto search(RoomSearchDto searchDto);
}
