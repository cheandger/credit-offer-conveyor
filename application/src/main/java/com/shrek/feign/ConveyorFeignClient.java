package com.shrek.feign;

import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "feign", url = "application.url")
public interface ConveyorFeignClient {

    @PostMapping("/application")
    ResponseEntity<List<LoanOfferDTO>> createOffers(@RequestBody LoanApplicationRequestDTO request);

    @PutMapping("/application/offer")
    void applyOffer(@RequestBody LoanOfferDTO request);

}

