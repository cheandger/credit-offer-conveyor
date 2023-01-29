package org.shrek.servises;


import com.shrek.model.CreditDTO;
import com.shrek.model.ScoringDataDTO;

public interface CalculationService {

    CreditDTO calculate(ScoringDataDTO scoringDataDTO);


}
