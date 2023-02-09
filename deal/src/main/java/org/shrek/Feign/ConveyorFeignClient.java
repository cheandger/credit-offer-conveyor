package org.shrek.feign;

import com.shrek.model.CreditDTO;
import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import com.shrek.model.ScoringDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "feign", url = "${conveyor.url}")
public interface ConveyorFeignClient {

    @PostMapping("conveyor/offers")
    ResponseEntity<List<LoanOfferDTO>> createOffers(@RequestBody LoanApplicationRequestDTO request);

    @PostMapping("conveyor/calculation")
    ResponseEntity<CreditDTO> calculate(@RequestBody ScoringDataDTO scoringData);


}
