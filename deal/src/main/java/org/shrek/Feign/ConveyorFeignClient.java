package org.shrek.Feign;

import com.shrek.model.CreditDTO;
import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import com.shrek.model.ScoringDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "conveyorFeignClient")
public interface ConveyorFeignClient {

    @PostMapping("/offers")
    ResponseEntity<List<LoanOfferDTO>> createOffers(@RequestBody LoanApplicationRequestDTO request);

    @PostMapping("/calculation")
    ResponseEntity<CreditDTO> calculate(@RequestBody ScoringDataDTO scoringData);

}
