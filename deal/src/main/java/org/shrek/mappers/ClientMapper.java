package org.shrek.mappers;

import com.shrek.model.LoanApplicationRequestDTO;
import org.mapstruct.Mapper;
import org.shrek.models.Client;

@Mapper
public interface ClientMapper {

    Client loanApplicationRequestDtoToClient(LoanApplicationRequestDTO dto);
}