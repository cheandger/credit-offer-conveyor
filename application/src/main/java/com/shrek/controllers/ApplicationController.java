package com.shrek.controllers;

import com.shrek.controller.ApplicationServiceApi;
import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import com.shrek.servises.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor

public class ApplicationController implements ApplicationServiceApi {

    private final ApplicationService applicationService;


    @Override
    @PostMapping("/application")
    public ResponseEntity<List<LoanOfferDTO>> getListOfPossibleLoanOffers(@RequestBody LoanApplicationRequestDTO request) {

        return ResponseEntity.ok(applicationService.getListOfPossibleLoanOffers(request));

    }

    @Override
    @PutMapping("/offer")
    public ResponseEntity<Void> choiceLoanOffer(@RequestBody LoanOfferDTO loanOfferDTO) {
        applicationService.choiceLoanOffer(loanOfferDTO);
        return ResponseEntity.ok().build();
    }

}
