package org.shrek.servises;


import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import org.springframework.beans.factory.annotation.Autowired;


import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public interface OffersService {
    List<LoanOfferDTO> offers(LoanApplicationRequestDTO loanApplicationRequestDTO);

    LoanOfferDTO createLoanOffer(@NotNull LoanApplicationRequestDTO loanApplicationRequestDTO,
                                 @NotNull Boolean isInsuranceEnabled, @NotNull Boolean isSalaryClient);

    BigDecimal changeRateByIsInsuranceEnabledOrIsSalaryClient(@NotNull Boolean isInsuranceEnabled, @NotNull Boolean isSalaryClient);
}
