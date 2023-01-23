package org.shrek.servises;


import com.shrek.model.CreditDTO;
import com.shrek.model.PaymentScheduleElement;
import com.shrek.model.ScoringDataDTO;

import java.math.BigDecimal;
import java.util.List;

public interface CalculationService {

    CreditDTO calculation(ScoringDataDTO scoringDataDTO);

    BigDecimal calculateOfIsInsuranceCaseTotalAmount(ScoringDataDTO scoringDataDTO);

    BigDecimal calculationOfPsk(Double amount, List<PaymentScheduleElement> paymentScheduleElementList);

    BigDecimal evaluateRateByScoring(ScoringDataDTO scoringDataDTO);
}
