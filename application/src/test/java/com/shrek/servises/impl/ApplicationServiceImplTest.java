package com.shrek.servises.impl;

import com.shrek.exceptions.ParametersValidationException;
import com.shrek.feign.ConveyorFeignClient;
import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import com.shrek.servises.ApplicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.shrek.servises.impl.config.LoanApplicationRequestDTOLoanOfferDTOInitializer.initLoanOffer;
import static com.shrek.servises.impl.config.LoanApplicationRequestDTOLoanOfferDTOInitializer.loanAppReqInit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {
    @MockBean
    ConveyorFeignClient feignClient;

    @Autowired
    ApplicationService applicationService;

    @Test
    @DisplayName("Check invalid age")
    void getListOfPossibleLoanOffersInvalidAgeTest() {
        LoanApplicationRequestDTO testLoanApplicationRequestDTO = loanAppReqInit(BigDecimal.valueOf(100000),
                12, LocalDate.parse("2008-01-29"));
        ParametersValidationException exception = Assertions.assertThrows(ParametersValidationException.class,
                () -> applicationService.getListOfPossibleLoanOffers(testLoanApplicationRequestDTO), "The date of birth must be at least 18 years old before today");
        assertEquals("The params can't pass the validation: The date of birth must be at least 18 years old before today", exception.getMessage());

    }

    @Test
    @DisplayName("Check valid age")
    void getListOfPossibleLoanOffersValidAgeTest() {
        LoanApplicationRequestDTO testLoanApplicationRequestDTO = loanAppReqInit(BigDecimal.valueOf(100000),
                12, LocalDate.parse("1985-01-29"));
        List<LoanOfferDTO> output = new ArrayList<>();
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(100000.00), 12, BigDecimal.valueOf(9601.70), BigDecimal.valueOf(27.00), false, false));
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(100000.00), 12, BigDecimal.valueOf(9553.00), BigDecimal.valueOf(26.00), false, true));
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(103000.00), 12, BigDecimal.valueOf(9456.00), BigDecimal.valueOf(24.00), true, false));
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(103000.00), 12, BigDecimal.valueOf(9407.70), BigDecimal.valueOf(24.00), true, true));

        when(feignClient.createOffers(testLoanApplicationRequestDTO)).thenReturn(ResponseEntity.ok(output));
        assertEquals(output, applicationService.getListOfPossibleLoanOffers(testLoanApplicationRequestDTO));
    }

    @Test
    void choiceLoanOffer() {
        LoanOfferDTO testInput = initLoanOffer(123L, BigDecimal.valueOf(103000), BigDecimal.valueOf(100000), 12,
                BigDecimal.valueOf(9450), BigDecimal.valueOf(26), false, true);
        applicationService.choiceLoanOffer(testInput);
        verify(feignClient, times(1)).applyOffer(testInput);
    }
}