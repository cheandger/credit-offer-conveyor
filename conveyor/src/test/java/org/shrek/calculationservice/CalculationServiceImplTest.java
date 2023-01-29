package org.shrek.calculationservice;

import com.shrek.model.CreditDTO;
import com.shrek.model.EmploymentDTO;
import com.shrek.model.ScoringDataDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.shrek.calculationservice.scoringdatadtoconfig.ScoringDataDtoInitializer;
import org.shrek.exceptions.ParametersValidationException;
import org.shrek.servises.CalculationService;
import org.shrek.servises.impl.CalculationServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static com.shrek.model.EmploymentDTO.EmploymentStatusEnum.EMPLOYED;
import static com.shrek.model.EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED;
import static com.shrek.model.EmploymentDTO.PositionEnum.WORKER;
import static com.shrek.model.ScoringDataDTO.GenderEnum.MALE;
import static com.shrek.model.ScoringDataDTO.MaritalStatusEnum.MARRIED;
import static com.shrek.model.ScoringDataDTO.MaritalStatusEnum.SINGLE;


public class CalculationServiceImplTest {

    private final BigDecimal BASE_RATE = BigDecimal.valueOf(27.00).setScale(2, RoundingMode.HALF_UP);

    private final BigDecimal INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE = BigDecimal.valueOf(3.00).setScale(2, RoundingMode.HALF_UP);

    CalculationService calculationService = new CalculationServiceImpl(BASE_RATE, INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE);


    @Test
    @DisplayName("Test calculation by NonValid EmploymentStatus Unemployed")
    void nonValidUnemployedEmploymentStatusTest() {

        EmploymentDTO UNEMPLOYEDEmploymentStatusDTO = ScoringDataDtoInitializer.initialEmploymentDTO(UNEMPLOYED, BigDecimal.valueOf(60000), WORKER, 56, 12);

        ScoringDataDTO UNEMPLOYEDScoringDataDTO = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE, LocalDate.parse("1985-01-29"), MARRIED, 1,
                UNEMPLOYEDEmploymentStatusDTO, true, true);

        ParametersValidationException exception = Assertions.assertThrows(ParametersValidationException.class, () ->
                calculationService.calculate(UNEMPLOYEDScoringDataDTO), "We can't help you. Please find a jod and try again");

        Assertions.assertEquals("The params can't pass the validation: We can't help you. Please find a jod and try again",
                exception.getMessage());
    }

    @Test
    @DisplayName("Test calculation by NonValid Little Salary Large loan Amount. The loan amount is 20 times the salary")
    void nonValidLittleSalaryLargeAmountTest() {

        EmploymentDTO NonValid_LittleSalary_LargeAmount_EmploymentDTO = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(30000), WORKER, 56, 12);

        ScoringDataDTO NonValid_LittleSalary_LargeAmount_ScoringDataDTO = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(1000000), 12, MALE, LocalDate.parse("1985-01-29"), MARRIED, 1,
                NonValid_LittleSalary_LargeAmount_EmploymentDTO, true, true);

        ParametersValidationException exception = Assertions.assertThrows(ParametersValidationException.class, () ->
                        calculationService.calculate(NonValid_LittleSalary_LargeAmount_ScoringDataDTO),
                "The sum you are want to loan is too large");
        Assertions.assertEquals("The params can't pass the validation: The sum you are want to loan is too large", exception.getMessage());
    }

    @Test
    @DisplayName("Test calculation by NonValid birthDate too young. The Age is less than 20 yearsOld")
    void nonValidAgeTooYoungTest() {

        EmploymentDTO NonValid_Age_TooYoung_EmploymentDTO = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(80000), WORKER, 56, 12);

        ScoringDataDTO NonValid_Age_TooYoung_ScoringDataDTO = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE, LocalDate.parse("2005-01-29"), MARRIED, 1,
                NonValid_Age_TooYoung_EmploymentDTO, true, true);

        ParametersValidationException exception = Assertions.assertThrows(ParametersValidationException.class, () ->
                calculationService.calculate(NonValid_Age_TooYoung_ScoringDataDTO), "Non valid age. It should be more than 20 or less than 60 years old");
        Assertions.assertEquals("The params can't pass the validation: Non valid age. It should be more than 20 or less than 60 years old", exception.getMessage());
    }

    @Test
    @DisplayName("Test calculation by NonValid birthDate too Old. The Age is more than 60 yearsOld")
    void nonValidAgeTooOldTest() {

        EmploymentDTO NonValid_Age_TooOld_EmploymentDTO = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(80000), WORKER, 56, 12);

        ScoringDataDTO NonValid_Age_TooOld_ScoringDataDTO = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE, LocalDate.parse("1962-01-29"), MARRIED, 1,
                NonValid_Age_TooOld_EmploymentDTO, true, true);

        ParametersValidationException exception = Assertions.assertThrows(ParametersValidationException.class, () ->
                calculationService.calculate(NonValid_Age_TooOld_ScoringDataDTO), "Non valid age. It should be more than 20 or less than 60 years old");

        Assertions.assertEquals("The params can't pass the validation: Non valid age. It should be more than 20 or less than 60 years old", exception.getMessage());
    }

    @Test
    @DisplayName("Test calculation by NonValid WorkExperience Total. The workExperienceTotal is less than 12 months")
    void nonValidWorkExperienceTotalTest() {

        EmploymentDTO NonValid_workExperienceTotal_EmploymentDTO = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(80000), WORKER, 9, 12);

        ScoringDataDTO NonValid_workExperienceTotal_ScoringDataDTO = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE, LocalDate.parse("1985-01-29"), MARRIED, 1,
                NonValid_workExperienceTotal_EmploymentDTO, true, true);

        ParametersValidationException exception = Assertions.assertThrows(ParametersValidationException.class, () ->
                calculationService.calculate(NonValid_workExperienceTotal_ScoringDataDTO), "Too little workExperienceTotal. It should be more than 12 month");
        Assertions.assertEquals("The params can't pass the validation: Too little workExperienceTotal. It should be more than 12 month", exception.getMessage());
    }

    @Test
    @DisplayName("Test calculation by NonValid WorkExperience Current. The workExperienceCurrent is less than 3 months")
    void nonValidWorkExperienceCurrentTest() {

        EmploymentDTO NonValid_workExperienceCurrent_EmploymentDTO = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(80000), WORKER, 12, 2);

        ScoringDataDTO NonValid_workExperienceCurrent_ScoringDataDTO = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE, LocalDate.parse("1985-01-29"), MARRIED, 1,
                NonValid_workExperienceCurrent_EmploymentDTO, true, true);

        ParametersValidationException exception = Assertions.assertThrows(ParametersValidationException.class, () ->
                calculationService.calculate(NonValid_workExperienceCurrent_ScoringDataDTO), "Too little workExperienceCurrent. It should be more than 3 month");
        Assertions.assertEquals("The params can't pass the validation: Too little workExperienceCurrent. It should be more than 3 month", exception.getMessage());
    }

    @Test
    @DisplayName("Test calculation by isInsuranceEnabled true. It'll decrease BaseRate by 3 but increase the fullLoanAmount by (amount*IS_INSURANCE_RATE percents/100) value")
    void decreaseRateByThreeTotalAmountEvaluateByIsInsuranceEnabledTrueScoringDTOTest() {

        EmploymentDTO employmentDTOis_INSURANCE_RATETrue = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTOis_INSURANCE_RATETrue = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTOis_INSURANCE_RATETrue, true, false);

        CreditDTO fullLoanAmount = calculationService.calculate(scoringDataDTOis_INSURANCE_RATETrue);

        Assertions.assertAll("The values of FullLoanAmount or Rate didn't mach ",
                () -> {
                    Assertions.assertEquals(scoringDataDTOis_INSURANCE_RATETrue.getAmount().add(scoringDataDTOis_INSURANCE_RATETrue
                                    .getAmount().multiply(INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE)
                                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)),
                            (fullLoanAmount.getPaymentSchedule().get(0).getDebtPayment().add(fullLoanAmount.getPaymentSchedule().get(0).getRemainingDebt())),
                            "FullLoanAmount values didn't mach, check the calculation");
                }
                , () -> {
                    Assertions.assertEquals(calculationService.calculate(scoringDataDTOis_INSURANCE_RATETrue)
                                    .getRate().setScale(2, RoundingMode.HALF_UP), BASE_RATE.subtract(BigDecimal.valueOf(3.00)),
                            "Rate values didn't mach, check the calculation");
                });
    }

    @Test
    @DisplayName("Test calculation by isInsuranceEnabled false. The rate and the amount will remain unchanged")
    void nonEvaluateRateTotalAmountIsInsuranceEnabledFalseScoringDTOTest() {

        EmploymentDTO employmentDTOis_INSURANCE_RATEFalse = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTOis_INSURANCE_RATEFalse = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTOis_INSURANCE_RATEFalse, false, false);

        BigDecimal fullLoanAmount = calculationService
                .calculate(scoringDataDTOis_INSURANCE_RATEFalse).getPaymentSchedule().get(0).getRemainingDebt().
                add(calculationService
                        .calculate(scoringDataDTOis_INSURANCE_RATEFalse).getPaymentSchedule().get(0).getDebtPayment());

        Assertions.assertAll("Check the fullLoanAmount value calculation and the result RATE", () -> {
                    Assertions.assertEquals(fullLoanAmount, scoringDataDTOis_INSURANCE_RATEFalse.getAmount().setScale(2, RoundingMode.HALF_UP));
                }
                , () -> {
                    Assertions.assertEquals(calculationService.calculate(scoringDataDTOis_INSURANCE_RATEFalse).getRate(), BASE_RATE);

                });
    }


}