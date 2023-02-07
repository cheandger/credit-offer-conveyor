package org.shrek.mappers;

import com.shrek.model.LoanOfferDTO;
import org.mapstruct.Mapper;
import org.shrek.models.AppliedOffer;

@Mapper
public interface LoanOfferDtoMapper {

    AppliedOffer loanOfferDtoToAppliedOffer(LoanOfferDTO dto);
}