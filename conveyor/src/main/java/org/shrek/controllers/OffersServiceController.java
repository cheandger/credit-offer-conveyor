package org.shrek.controllers;

import com.shrek.controller.OffersServiceApi;
import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import org.shrek.servises.OffersService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated

public class OffersServiceController implements OffersServiceApi {

    private final OffersService offersService;

    public OffersServiceController(OffersService offersService) {
        this.offersService = offersService;
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createOffers(@RequestParam LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(offersService.createOffers(loanApplicationRequestDTO));
    }
}

