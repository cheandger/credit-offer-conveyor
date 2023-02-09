package org.shrek.services;

import com.shrek.model.FinishRegistrationRequestDTO;
import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;

import java.util.List;

public interface DealService {

    List<LoanOfferDTO> createListOffersByFeignClient(LoanApplicationRequestDTO loanApplicationRequestDTO);

    void getAppChangeStatusAfterApplying(LoanOfferDTO loanOfferDTO);

    void formScoringData(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO);
}
