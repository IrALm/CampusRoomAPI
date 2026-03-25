package com.campusRoom.api.service.research;

import com.campusRoom.api.dto.researchDto.UserPageDto;
import com.campusRoom.api.dto.researchDto.UserSearchDto;

public interface UserResearchService {

    /**
     * Recherche avec filtre
     * @param searchDto les filtres
     * @return les utilisateurs paginés
     */
    UserPageDto search(UserSearchDto searchDto);
}
