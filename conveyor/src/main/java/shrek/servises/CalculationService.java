package org.shrek.servises;


import com.shrek.model.CreditDTO;
import com.shrek.model.PaymentScheduleElement;
import com.shrek.model.ScoringDataDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

public interface CalculationService {

    CreditDTO calculation(ScoringDataDTO scoringDataDTO);

    BigDecimal isInsuranceTotalAmountCalculation(ScoringDataDTO scoringDataDTO);

    BigDecimal pskCalculation(BigDecimal amount, List<PaymentScheduleElement> paymentScheduleElementList);
}
