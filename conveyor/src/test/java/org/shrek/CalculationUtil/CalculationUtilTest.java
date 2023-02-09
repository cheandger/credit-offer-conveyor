package org.shrek.CalculationUtil;

import com.shrek.model.EmploymentDTO;
import com.shrek.model.PaymentScheduleElement;
import com.shrek.model.ScoringDataDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.shrek.calculationservice.scoringdatadtoconfig.ScoringDataDtoInitializer;
import org.shrek.services.CalculationService;
import org.shrek.services.impl.CalculationServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static com.shrek.model.EmploymentDTO.EmploymentStatusEnum.*;
import static com.shrek.model.EmploymentDTO.PositionEnum.*;
import static com.shrek.model.ScoringDataDTO.GenderEnum.*;
import static com.shrek.model.ScoringDataDTO.MaritalStatusEnum.*;
import static org.shrek.util.CalculationUtil.*;

public class CalculationUtilTest {

    private final BigDecimal BASE_RATE = BigDecimal.valueOf(27.00).setScale(2, RoundingMode.HALF_UP);

    private final BigDecimal INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE = BigDecimal.valueOf(3.00).setScale(2, RoundingMode.HALF_UP);

    @Test
    @DisplayName("EvaluateRateByScoring and calculateIsInsuranceCaseTotalAmount methods testing by isInsuranceEnabled true case. It'll decrease BaseRate by 3 but increase the fullLoanAmount by (amount*IS_INSURANCE_RATE percents/100) value")
    void decreaseRateByThreeTotalAmountEvaluateByIsInsuranceEnabledTrueScoringDTOTest() {

        EmploymentDTO employmentDTOis_INSURANCE_RATETrue = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTOis_INSURANCE_RATETrue = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTOis_INSURANCE_RATETrue, true, false);

        BigDecimal fullLoanAmount = calculateIsInsuranceCaseTotalAmount(scoringDataDTOis_INSURANCE_RATETrue.getAmount(),
                scoringDataDTOis_INSURANCE_RATETrue.getIsInsuranceEnabled(), scoringDataDTOis_INSURANCE_RATETrue.getTerm(),
                INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE);

        Assertions.assertAll("The values of FullLoanAmount or Rate didn't mach ",
                () -> {
                    Assertions.assertEquals(scoringDataDTOis_INSURANCE_RATETrue.getAmount().add(scoringDataDTOis_INSURANCE_RATETrue
                                    .getAmount().multiply(INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE)
                                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)),
                            (fullLoanAmount),
                            "FullLoanAmount values didn't mach, check the calculation");
                }
                , () -> {
                    Assertions.assertEquals(evaluateRateByScoring(scoringDataDTOis_INSURANCE_RATETrue, BASE_RATE), BASE_RATE.subtract(BigDecimal.valueOf(3.00)),
                            "Rate values didn't mach, check the calculation");
                });
    }

    @Test
    @DisplayName("EvaluateRateByScoring and calculateIsInsuranceCaseTotalAmount methods testing by isInsuranceEnabled false case. The rate and the amount will remain unchanged")
    void nonEvaluateRateTotalAmountIsInsuranceEnabledFalseScoringDTOTest() {

        EmploymentDTO employmentDTOis_INSURANCE_RATEFalse = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTOis_INSURANCE_RATEFalse = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTOis_INSURANCE_RATEFalse, false, false);

        BigDecimal fullLoanAmount = calculateIsInsuranceCaseTotalAmount(scoringDataDTOis_INSURANCE_RATEFalse.getAmount(),
                scoringDataDTOis_INSURANCE_RATEFalse.getIsInsuranceEnabled(), scoringDataDTOis_INSURANCE_RATEFalse.getTerm(),
                INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE);
        Assertions.assertAll("Check the fullLoanAmount value calculation and the result RATE", () -> {
                    Assertions.assertEquals(fullLoanAmount, scoringDataDTOis_INSURANCE_RATEFalse.getAmount().setScale(2, RoundingMode.HALF_UP));
                }
                , () -> {
                    Assertions.assertEquals(evaluateRateByScoring(scoringDataDTOis_INSURANCE_RATEFalse, BASE_RATE), BASE_RATE);

                });
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by isSalary true case. The rate will decrease by 1 and the amount will remain unchanged")
    void calculateIsSalaryTrueRateReducedByOneScoringDTOTest() {

        EmploymentDTO employmentDTOis_Salary_True = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTOis_Salary_True = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTOis_Salary_True, false, true);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTOis_Salary_True, BASE_RATE), BASE_RATE.subtract(BigDecimal.valueOf(1)));
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by isSalary false case. The rate will remain unchanged")
    void calculateIsSalaryFalseBaseRateScoringDTOTest() {

        EmploymentDTO employmentDTOis_Salary_False = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTOis_Salary_False = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTOis_Salary_False, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTOis_Salary_False, BASE_RATE), BASE_RATE);
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by employmentStatus SELF_EMPLOYED case. The rate will increase by 1")
    void calculateIncreaseRateByOneEmploymentStatusSelfEmployedScoringDTOTest() {

        EmploymentDTO employmentDTO_EmploymentStatus_SELF_EMPLOYED_Rate_increased_By_One = ScoringDataDtoInitializer.initialEmploymentDTO(SELF_EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTOis_EmploymentStatus_SELF_EMPLOYED_Rate_increased_By_One = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTO_EmploymentStatus_SELF_EMPLOYED_Rate_increased_By_One, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTOis_EmploymentStatus_SELF_EMPLOYED_Rate_increased_By_One, BASE_RATE), BASE_RATE.add(BigDecimal.valueOf(1)));
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by employmentStatus BUSINESS_OWNER case. The rate will increase by 3")
    void calculateIncreaseRateByThreeEmploymentStatusBusinessOwnerScoringDTOTest() {

        EmploymentDTO employmentDTO_EmploymentStatus_BUSSINESS_OWNER_Rate_increased_By_Three = ScoringDataDtoInitializer.initialEmploymentDTO(BUSINESS_OWNER, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTO_EmploymentStatus_BUSSINESS_OWNER_Rate_increased_By_Three = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTO_EmploymentStatus_BUSSINESS_OWNER_Rate_increased_By_Three, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_EmploymentStatus_BUSSINESS_OWNER_Rate_increased_By_Three, BASE_RATE), BASE_RATE.add(BigDecimal.valueOf(3)));

    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by employmentStatus MID_MANAGER case. The rate will decrease by 2")
    void calculateEmploymentGetPositionMidManagerRateDecreaseByTwoScoringDTOTest() {

        EmploymentDTO employmentDTO_GetPosition_MID_MANAGER_Rate_subtract_By_Two = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                MID_MANAGER, 56, 12);

        ScoringDataDTO scoringDataDTO_GetPosition_MID_MANAGER_Rate_subtract_By_Two = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTO_GetPosition_MID_MANAGER_Rate_subtract_By_Two, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_GetPosition_MID_MANAGER_Rate_subtract_By_Two, BASE_RATE), BASE_RATE.subtract(BigDecimal.valueOf(2)));
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by employmentStatus TOP_MANAGER case. The rate will decrease by 4")
    void calculateEmploymentTopManagerRateDecreaseByFourScoringDTOTest() {

        EmploymentDTO employmentDTO_GetPosition_TOP_MANAGER_Rate_subtract_By_Four = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                TOP_MANAGER, 56, 12);

        ScoringDataDTO scoringDataDTO_GetPosition_TOP_MANAGER_Rate_subtract_By_Four = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTO_GetPosition_TOP_MANAGER_Rate_subtract_By_Four, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_GetPosition_TOP_MANAGER_Rate_subtract_By_Four, BASE_RATE), BASE_RATE.subtract(BigDecimal.valueOf(4)));
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by maritalStatus MARRIED case. The rate will decrease by 3")
    void calculateMaritalStatusMarriedRateSubtractByThreeScoringDTOTest() {

        EmploymentDTO employmentDTO_MaritalStatus_MARRIED_Rate_subtract_By_Three = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTO_MaritalStatus_MARRIED_Rate_subtract_By_Three = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), MARRIED, 0,
                employmentDTO_MaritalStatus_MARRIED_Rate_subtract_By_Three, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_MaritalStatus_MARRIED_Rate_subtract_By_Three, BASE_RATE), BASE_RATE.subtract(BigDecimal.valueOf(3)));

    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by maritalStatus DIVORCED case. The rate will increase by 1")
    void calculateMaritalStatusDivorcedRateIncreasedByOneScoringDTOTest() {

        EmploymentDTO employmentDTO_MaritalStatus_DIVORCED_Rate_increased_By_One = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTO_MaritalStatus_DIVORCED_Rate_increased_By_One = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), DIVORCED, 0,
                employmentDTO_MaritalStatus_DIVORCED_Rate_increased_By_One, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_MaritalStatus_DIVORCED_Rate_increased_By_One, BASE_RATE), BASE_RATE.add(BigDecimal.valueOf(1)));
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by maritalStatus WIDOW_WIDOWER case. The rate will remain unchanged")
    void calculateMaritalStatusWidowWidowerSameRateScoringDTOTest() {

        EmploymentDTO employmentDTO_MaritalStatus_WIDOW_WIDOWER_Same_Rate = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTO_MaritalStatus_WIDOW_WIDOWER_Same_Rate = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), WIDOW_WIDOWER, 0,
                employmentDTO_MaritalStatus_WIDOW_WIDOWER_Same_Rate, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_MaritalStatus_WIDOW_WIDOWER_Same_Rate, BASE_RATE), BASE_RATE);
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by DependentAmount more than 1 case. The rate will increase by 1")
    void getDependentAmountMoreThanOneRateIncreaseByOneScoringDTOTest() {

        EmploymentDTO employmentDTO_getDependentAmount_More_Than_One_Rate_increased_By_One = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTO_getDependentAmount_More_Than_One_Rate_increased_By_One = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 2,
                employmentDTO_getDependentAmount_More_Than_One_Rate_increased_By_One, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_getDependentAmount_More_Than_One_Rate_increased_By_One, BASE_RATE), BASE_RATE.add(BigDecimal.valueOf(1)));
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by FEMALE age less than Sixty & more than ThirtyFive case. The rate will decrease by 3")
    void getGenderFemaleAgeLessSixtyMoreThirtyFiveSubtractRateByThreeScoringDTOTest() {

        EmploymentDTO employmentDTO_gender_FEMALE_Age_Less_Sixty_More_ThirtyFive_subtract_Rate_by_Three = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTO_gender_FEMALE_Age_Less_Sixty_More_ThirtyFive_subtract_Rate_by_Three = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, FEMALE,
                LocalDate.parse("1978-01-29"), SINGLE, 0,
                employmentDTO_gender_FEMALE_Age_Less_Sixty_More_ThirtyFive_subtract_Rate_by_Three, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_gender_FEMALE_Age_Less_Sixty_More_ThirtyFive_subtract_Rate_by_Three, BASE_RATE), BASE_RATE.subtract(BigDecimal.valueOf(3)));
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by FEMALE age less than ThirtyFive case. The rate will remain unchanged")
    void getGender_FEMALE_Age_Less_ThirtyFive_BASE_Rate_ScoringDTOTest() {

        EmploymentDTO employmentDTO_gender_FEMALE_Age_Less_ThirtyFive_BASE_Rate = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTO_gender_FEMALE_Age_Less_ThirtyFive_BASE_Rate = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, FEMALE,
                LocalDate.parse("1995-01-29"), SINGLE, 0,
                employmentDTO_gender_FEMALE_Age_Less_ThirtyFive_BASE_Rate, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_gender_FEMALE_Age_Less_ThirtyFive_BASE_Rate, BASE_RATE), BASE_RATE);
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by MALE age less than Thirty case. The rate will remain unchanged")
    void getGenderMaleAgeLessThirtyBASERateScoringDTOTest() {

        EmploymentDTO employmentDTO_gender_MALE_Age_Less_Thirty_BASE_Rate = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTO_gender_MALE_Age_Less_Thirty_BASE_Rate = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("2000-01-29"), SINGLE, 0,
                employmentDTO_gender_MALE_Age_Less_Thirty_BASE_Rate, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_gender_MALE_Age_Less_Thirty_BASE_Rate, BASE_RATE), BASE_RATE);
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by MALE age less than FiftyFive more than Thirty case. The rate will decrease by 3")
    void getGenderMailAgeLessFiftyFiveMoreThirtySubtractRateByThreeScoringDTOTest() {

        EmploymentDTO employmentDTO_gender_MALE_Age_Less_FiftyFive_More_Thirty_subtract_Rate_by_Three = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTO_gender_MALE_Age_Less_FiftyFive_More_Thirty_subtract_Rate_by_Three = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1985-01-29"), SINGLE, 0,
                employmentDTO_gender_MALE_Age_Less_FiftyFive_More_Thirty_subtract_Rate_by_Three, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_gender_MALE_Age_Less_FiftyFive_More_Thirty_subtract_Rate_by_Three, BASE_RATE), BASE_RATE.subtract(BigDecimal.valueOf(3)));
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by MALE age more than FiftyFive case. The rate will remain unchanged")
    void getGenderMaleAgeMoreFiftyFiveBASERateScoringDTOTest() {

        EmploymentDTO employmentDTO_gender_MALE_Age_More_FiftyFive_BASE_Rate = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTO_gender_MALE_Age_More_FiftyFive_BASE_Rate = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1963-01-29"), SINGLE, 0,
                employmentDTO_gender_MALE_Age_More_FiftyFive_BASE_Rate, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_gender_MALE_Age_More_FiftyFive_BASE_Rate, BASE_RATE), BASE_RATE);
    }

    @Test
    @DisplayName("Test the evaluateRateByScoring method by gender NON BINARY case. The rate will increase by 3")
    void getGenderNonBinaryIncreaseRateByThreeScoringDTOTest() {

        EmploymentDTO employmentDTO_gender_NON_BINARY_Increase_Rate_By_Three = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTO_gender_NON_BINARY_Increase_Rate_By_Three = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, NON_BINARY,
                LocalDate.parse("1963-01-29"), SINGLE, 0,
                employmentDTO_gender_NON_BINARY_Increase_Rate_By_Three, false, false);

        Assertions.assertEquals(evaluateRateByScoring(scoringDataDTO_gender_NON_BINARY_Increase_Rate_By_Three, BASE_RATE), BASE_RATE.add(BigDecimal.valueOf(3)));
    }

    @Test
    @DisplayName("Test The calculateMonthlyPayment method")
    void checkTheCalculateMonthlyPayment() {

        EmploymentDTO employmentDTOis_INSURANCE_RATETrue = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTOis_INSURANCE_RATETrue = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTOis_INSURANCE_RATETrue, true, false);

        BigDecimal finalRate = evaluateRateByScoring(scoringDataDTOis_INSURANCE_RATETrue, BASE_RATE);

        BigDecimal monthlyPaymentByDTO = calculateMonthlyPayment(scoringDataDTOis_INSURANCE_RATETrue.getAmount(),
                scoringDataDTOis_INSURANCE_RATETrue.getTerm(),
                finalRate);

        BigDecimal monthlyPaymentByHand = calculateMonthlyPayment(BigDecimal.valueOf(100000),
                12, BASE_RATE.subtract(BigDecimal.valueOf(3.00)));
        Assertions.assertEquals(monthlyPaymentByDTO, monthlyPaymentByHand);
    }

    @Test
    @DisplayName("Test The calculateMonthlyPayment method")
    void checkTheCalculatePsk() {

        EmploymentDTO employmentDTOis_INSURANCE_RATETrue = ScoringDataDtoInitializer.initialEmploymentDTO(EMPLOYED, BigDecimal.valueOf(60000),
                WORKER, 56, 12);

        ScoringDataDTO scoringDataDTOis_INSURANCE_RATETrue = ScoringDataDtoInitializer.initialScoringDataDTO(BigDecimal.valueOf(100000), 12, MALE,
                LocalDate.parse("1999-01-29"), SINGLE, 0,
                employmentDTOis_INSURANCE_RATETrue, true, false);

        CalculationService calculationService = new CalculationServiceImpl(BASE_RATE, INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE);
        List<PaymentScheduleElement> list = calculationService.calculate(scoringDataDTOis_INSURANCE_RATETrue).getPaymentSchedule();

        BigDecimal pskByHand = calculatePsk(100000.00, list);
        BigDecimal pskByDTO = calculatePsk((scoringDataDTOis_INSURANCE_RATETrue.getAmount().doubleValue()), list);

        Assertions.assertAll("There is an exception. The results of calculation by psk" +
                        "are not the same, or the result of calculation is null",
                () -> {
                    Assertions.assertEquals(pskByHand, pskByDTO);
                },
                () -> {
                    Assertions.assertNotNull(pskByDTO);
                }
        );
    }
}
