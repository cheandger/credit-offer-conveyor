package org.shrek.mappers;

import com.shrek.model.CreditDTO;
import org.mapstruct.Mapper;
import org.shrek.models.Credit;

@Mapper
public interface CreditMapper {

    Credit creditDtoToCredit(CreditDTO dto);
}