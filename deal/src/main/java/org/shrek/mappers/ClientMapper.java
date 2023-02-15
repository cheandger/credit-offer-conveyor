package org.shrek.mappers;

import com.shrek.model.LoanApplicationRequestDTO;
import org.mapstruct.Mapper;
import org.shrek.models.Client;

@Mapper//(componentModel = "spring", uses = ClientMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClientMapper {

    Client loanApplicationRequestDtoToClient(LoanApplicationRequestDTO dto);
}