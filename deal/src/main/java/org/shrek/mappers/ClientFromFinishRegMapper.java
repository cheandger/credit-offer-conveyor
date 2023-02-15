package org.shrek.mappers;

import com.shrek.model.FinishRegistrationRequestDTO;
import org.mapstruct.Mapper;
import org.shrek.models.Client;

@Mapper
public interface ClientFromFinishRegMapper {

    Client clientFromFinishRegistration(FinishRegistrationRequestDTO dto);
}