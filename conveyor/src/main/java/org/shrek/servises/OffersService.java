package org.shrek.servises;


import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface OffersService {
    List<LoanOfferDTO> createOffers(LoanApplicationRequestDTO loanApplicationRequestDTO);

    LoanOfferDTO createLoanOffer(@NotNull LoanApplicationRequestDTO loanApplicationRequestDTO,
                                 @NotNull Boolean isInsuranceEnabled, @NotNull Boolean isSalaryClient);

}
