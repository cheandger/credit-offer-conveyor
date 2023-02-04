package org.shrek.controllers;


import com.shrek.controller.DealServiceApi;
import com.shrek.model.FinishRegistrationRequestDTO;
import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import org.shrek.services.DealService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DealServiceController implements DealServiceApi {

    DealService dealService;

    public DealServiceController(DealService dealService) {
        this.dealService = dealService;
    }


    @Override
    public ResponseEntity<List<LoanOfferDTO>> createListOffersByFeignClient(@RequestParam LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(dealService.createListOffersByFeignClient(loanApplicationRequestDTO));
    }

    @Override
    public ResponseEntity<Void> formScoringData(@RequestParam Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        dealService.formScoringData(applicationId, finishRegistrationRequestDTO);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> getAppChangeStatus(@RequestParam LoanOfferDTO loanOfferDTO) {
        dealService.getAppChangeStatus(loanOfferDTO);
        return ResponseEntity.ok().build();
    }
}
