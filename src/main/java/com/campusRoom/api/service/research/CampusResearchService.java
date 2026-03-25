package com.campusRoom.api.service.research;

import com.campusRoom.api.dto.researchDto.CampusPageDto;
import com.campusRoom.api.dto.researchDto.CampusSearchDto;

public interface CampusResearchService {

    /**
     * Recherche des campus avec filtres de recherche
     * @param searchDto dto contenant les filtres
     * @return les campus paginés
     */
    CampusPageDto search(CampusSearchDto searchDto);
}
