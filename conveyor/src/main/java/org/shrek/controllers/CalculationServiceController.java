package org.shrek.controllers;

import com.shrek.controller.CalculationServiceApi;
import com.shrek.model.CreditDTO;
import com.shrek.model.ScoringDataDTO;
import org.shrek.services.CalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated

public class CalculationServiceController implements CalculationServiceApi {

    CalculationService calculationService;

    public CalculationServiceController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @Override
    public ResponseEntity<CreditDTO> calculate(@RequestParam ScoringDataDTO scoringDataDTO) {
        return ResponseEntity.ok(calculationService.calculate(scoringDataDTO));
    }
}



