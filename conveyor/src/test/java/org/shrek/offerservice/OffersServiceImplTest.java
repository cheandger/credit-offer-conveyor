package org.shrek.offerservice;


import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.shrek.exceptions.ParametersValidationException;
import org.shrek.offerservice.loanapplreqdtoconfig.LoanApplicationRequestDTOInitializer;
import org.shrek.servises.OffersService;
import org.shrek.servises.impl.OffersServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;


class OffersServiceImplTest {

    private final BigDecimal BASE_RATE = BigDecimal.valueOf(27.00).setScale(2, RoundingMode.HALF_UP);
    private final BigDecimal INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE = BigDecimal.valueOf(27.00).setScale(2, RoundingMode.HALF_UP);

    OffersService offersService = new OffersServiceImpl(BASE_RATE, INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE);


    @Test
    @DisplayName("Test case nonValid age less than 18 years Old")
    void validateAgeLessThanEighteenTest() {
        LoanApplicationRequestDTO validAgeLessThanEighteen = LoanApplicationRequestDTOInitializer.loanAppReqInit(BigDecimal.valueOf(150000), 24, LocalDate.parse("2007-01-29"));
        ParametersValidationException exception = Assertions.assertThrows(ParametersValidationException.class,
                () -> offersService.createOffers(validAgeLessThanEighteen), "The date of birth must be at least 18 years old before today");
        Assertions.assertEquals("The params can't pass the validation: The date of birth must be at least 18 years old before today", exception.getMessage());
    }

    @Test
    @DisplayName("Test creating of full LoanOffer List with different rate values, depend on IsInsuranceEnabled Or IsSalaryClient fields. The age is valid ")
    void createOfFullListOfRateChangesByIsInsuranceEnabledOrIsSalaryClientTest() {
        LoanApplicationRequestDTO validLoanApplicationRequestDTO = LoanApplicationRequestDTOInitializer.loanAppReqInit(BigDecimal.valueOf(150000), 24, LocalDate.parse("1985-01-29"));

        List<LoanOfferDTO> fullListOfRateChangesByIsInsuranceEnabledOrIsSalaryClient = offersService.createOffers(validLoanApplicationRequestDTO);
        Assertions.assertEquals(BASE_RATE.setScale(2, RoundingMode.HALF_UP), fullListOfRateChangesByIsInsuranceEnabledOrIsSalaryClient.get(0).getRate());
        Assertions.assertEquals(BASE_RATE.subtract(BigDecimal.valueOf(1)), fullListOfRateChangesByIsInsuranceEnabledOrIsSalaryClient.get(1).getRate());
        Assertions.assertEquals(BASE_RATE.subtract(BigDecimal.valueOf(3)), fullListOfRateChangesByIsInsuranceEnabledOrIsSalaryClient.get(2).getRate());
        Assertions.assertEquals(BASE_RATE.subtract(BigDecimal.valueOf(4)), fullListOfRateChangesByIsInsuranceEnabledOrIsSalaryClient.get(3).getRate());
    }

    @Test
    @DisplayName("Test creating of full LoanOffer List with different rate values and it's not NULL")
    void checkNotNullFullListOfRateChangesByIsInsuranceEnabledOrIsSalaryClientTest() {
        LoanApplicationRequestDTO validLoanApplicationRequestDTO = LoanApplicationRequestDTOInitializer.loanAppReqInit(BigDecimal.valueOf(150000), 24, LocalDate.parse("1985-01-29"));

        List<LoanOfferDTO> fullListOfRateChangesByIsInsuranceEnabledOrIsSalaryClient = offersService.createOffers(validLoanApplicationRequestDTO);
        Assertions.assertNotNull(fullListOfRateChangesByIsInsuranceEnabledOrIsSalaryClient, "The list is null. Check up your actions.");
    }

    @Test
    @DisplayName("Test rate check IsInsuranceEnabled true case")
    void checkRateIsInsuranceEnabledTrueLoanOffer() {
        LoanApplicationRequestDTO IsInsuranceEnabledTrue = LoanApplicationRequestDTOInitializer.loanAppReqInit(BigDecimal.valueOf(150000), 24, LocalDate.parse("1985-01-29"));
        LoanOfferDTO IsInsuranceEnabledTrueLoanOfferDTO = offersService.createLoanOffer(IsInsuranceEnabledTrue, true, false);
        Assertions.assertEquals(BASE_RATE.subtract(BigDecimal.valueOf(3)), IsInsuranceEnabledTrueLoanOfferDTO.getRate());
    }

    @Test
    @DisplayName("Test rate check IsInsuranceEnabled false case")
    void checkRateIsInsuranceEnabledFalseLoanOffer() {
        LoanApplicationRequestDTO IsInsuranceEnabledFalse = LoanApplicationRequestDTOInitializer.loanAppReqInit(BigDecimal.valueOf(150000), 24, LocalDate.parse("1985-01-29"));
        LoanOfferDTO IsInsuranceEnabledFalseLoanOfferDTO = offersService.createLoanOffer(IsInsuranceEnabledFalse, false, false);
        Assertions.assertEquals(BASE_RATE, IsInsuranceEnabledFalseLoanOfferDTO.getRate());
    }

    @Test
    @DisplayName("Test rate check IsSalary false case")
    void checkRateIsSalaryFalseLoanOffer() {
        LoanApplicationRequestDTO IsSalaryFalse = LoanApplicationRequestDTOInitializer.loanAppReqInit(BigDecimal.valueOf(150000), 24, LocalDate.parse("1985-01-29"));
        LoanOfferDTO IsSalaryFalseFalseLoanOfferDTO = offersService.createLoanOffer(IsSalaryFalse, false, false);
        Assertions.assertEquals(BASE_RATE, IsSalaryFalseFalseLoanOfferDTO.getRate());
    }

    @Test
    @DisplayName("Test rate check IsSalary True case")
    void checkRateIsSalaryTrueLoanOffer() {
        LoanApplicationRequestDTO IsSalaryTrue = LoanApplicationRequestDTOInitializer.loanAppReqInit(BigDecimal.valueOf(150000), 24, LocalDate.parse("1985-01-29"));
        LoanOfferDTO IsSalaryTrueFalseLoanOfferDTO = offersService.createLoanOffer(IsSalaryTrue, false, true);
        Assertions.assertEquals(BASE_RATE.subtract(BigDecimal.valueOf(1)), IsSalaryTrueFalseLoanOfferDTO.getRate());
    }

    @Test
    @DisplayName("Test monthlyPayment check Is not NULL  case")
    void checkRateMonthlyPaymentIsNotNullLoanOffer() {
        LoanApplicationRequestDTO monthlyPaymentNotnull = LoanApplicationRequestDTOInitializer.loanAppReqInit(BigDecimal.valueOf(150000), 24, LocalDate.parse("1985-01-29"));
        LoanOfferDTO monthlyPaymentNotnullLoanOfferDTO = offersService.createLoanOffer(monthlyPaymentNotnull, false, true);

        Assertions.assertNotNull(monthlyPaymentNotnullLoanOfferDTO.getMonthlyPayment(), "The required value is null. Some wrong thing happened! ");
    }

}