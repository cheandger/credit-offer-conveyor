package com.shrek.servises;

import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;

import java.util.List;

public interface ApplicationService {
    public List<LoanOfferDTO> getListOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO);

    public void choiceLoanOffer(LoanOfferDTO loanOfferDTO);
}
