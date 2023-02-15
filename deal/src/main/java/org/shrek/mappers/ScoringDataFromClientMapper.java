package org.shrek.mappers;

import com.shrek.model.ScoringDataDTO;
import org.mapstruct.Mapper;
import org.shrek.models.Client;


@Mapper
public interface ScoringDataFromClientMapper {

    ScoringDataDTO scoringDataDtoFromClient(Client client);
}