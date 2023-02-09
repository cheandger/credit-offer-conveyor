package com.shrek.servises.impl;


import com.shrek.exceptions.BusinessException;
import com.shrek.exceptions.ParametersValidationException;
import com.shrek.feign.ConveyorFeignClient;
import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import com.shrek.servises.ApplicationService;
import com.shrek.validator.LoanApplicationRequestDTOValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {


    private final ConveyorFeignClient feignClient;

    private static final Logger log = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    @Override
    public List<LoanOfferDTO> getListOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {


        DataBinder dataBinder = new DataBinder(loanApplicationRequestDTO);
        dataBinder.addValidators(new LoanApplicationRequestDTOValidator());
        dataBinder.validate();
        if (dataBinder.getBindingResult().hasErrors()) {
            ObjectError objectError = dataBinder.getBindingResult().getAllErrors().get(0);
            log.info("Valid input data checking");
            throw new ParametersValidationException(objectError.getDefaultMessage());
        }

        ResponseEntity<List<LoanOfferDTO>> loanOffersResponse = feignClient.createOffers(loanApplicationRequestDTO);
        if (loanOffersResponse.getStatusCode().is2xxSuccessful()) {
            List<LoanOfferDTO> loanOffers = loanOffersResponse.getBody();

            log.info("Received offers: {}", loanOffers);
            return loanOffers;
        } else {
            log.warn("The response was bad, it hasn't body");
            throw new BusinessException(loanOffersResponse.getStatusCodeValue(), "Your loan request was denied.");
        }
    }

    @Override
    public void choiceLoanOffer(LoanOfferDTO loanOfferDTO) {
        log.info("applyOffer() loanOfferDTO = {}", loanOfferDTO);
        feignClient.applyOffer(loanOfferDTO);
    }

}
