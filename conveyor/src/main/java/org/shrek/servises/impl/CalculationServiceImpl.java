package org.shrek.servises.impl;

import com.shrek.model.CreditDTO;
import com.shrek.model.PaymentScheduleElement;
import com.shrek.model.ScoringDataDTO;
import org.shrek.exceptions.ParametersValidationException;
import org.shrek.servises.CalculationService;
import org.shrek.validators.ScoringDataDTOValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.shrek.util.CalculationUtil.*;

@Service
@PropertySource("config_properties.yaml")
public class CalculationServiceImpl implements CalculationService {

    private static final Logger log = LoggerFactory.getLogger(CalculationServiceImpl.class);

    private final BigDecimal BASE_RATE;
    private final BigDecimal INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE;

    public CalculationServiceImpl(@Value("${BASE_RATE}") BigDecimal BASE_RATE,
                                  @Value("${INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE}") BigDecimal INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE) {
        this.BASE_RATE = BASE_RATE;
        this.INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE = INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE;
    }

    @Override

    public CreditDTO calculate(ScoringDataDTO scoringDataDTO) throws ParametersValidationException {

        DataBinder dataBinder = new DataBinder(scoringDataDTO);
        dataBinder.addValidators(new ScoringDataDTOValidator());
        dataBinder.validate();
        if (dataBinder.getBindingResult().hasErrors()) {
            ObjectError objectError = dataBinder.getBindingResult().getAllErrors().get(0);
            throw new ParametersValidationException(objectError.getDefaultMessage());
        }
        ;
        log.info("Проверка валидности входных данных");


        BigDecimal totalAmount = calculateIsInsuranceCaseTotalAmount(scoringDataDTO.getAmount(),
                scoringDataDTO.getIsInsuranceEnabled(), scoringDataDTO.getTerm(), INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE);

        BigDecimal finalRate = evaluateRateByScoring(scoringDataDTO, BASE_RATE);

        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount,
                scoringDataDTO.getTerm(), finalRate);


        BigDecimal aHundredthPartOfMonthlyRate = finalRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        BigDecimal firstMonthInterestPayment = totalAmount.multiply(aHundredthPartOfMonthlyRate).setScale(2, RoundingMode.HALF_UP);


        BigDecimal firstMonthDeptPayment = monthlyPayment.subtract(firstMonthInterestPayment).setScale(2, RoundingMode.HALF_UP);
        log.info("Расчет платежа по телу кредита за первый месяц:  " + firstMonthDeptPayment);

        List<PaymentScheduleElement> scheduleList = new ArrayList<>();

        log.info("Составление первого элемента списка ежемесячных платежей");
        PaymentScheduleElement firstMonth = new PaymentScheduleElement()
                .number(1)
                .date(LocalDate.now().plusMonths(1))
                .totalPayment(monthlyPayment.setScale(2, RoundingMode.HALF_UP))
                .interestPayment(firstMonthInterestPayment)
                .debtPayment(firstMonthDeptPayment)
                .remainingDebt(totalAmount.subtract(firstMonthDeptPayment));

        scheduleList.add(firstMonth);

        log.info("Составление графика ежемесячных платежей");

        for (Integer i = 1; i < scoringDataDTO.getTerm(); i++) {

            scheduleList.add(new PaymentScheduleElement()
                    .number(i + 1)
                    .date(firstMonth.getDate().plusMonths(i))
                    .totalPayment(monthlyPayment)
                    .interestPayment((scheduleList.get(scheduleList.size() - 1).getRemainingDebt().multiply(aHundredthPartOfMonthlyRate)).setScale(2, RoundingMode.HALF_UP))
                    .debtPayment((monthlyPayment.subtract(scheduleList.get(scheduleList.size() - 1).getRemainingDebt().multiply(aHundredthPartOfMonthlyRate))).setScale(2, RoundingMode.HALF_UP))
                    .remainingDebt((scheduleList.get(scheduleList.size() - 1).getRemainingDebt().subtract((monthlyPayment.subtract(scheduleList.get(scheduleList.size() - 1).getRemainingDebt()
                            .multiply(aHundredthPartOfMonthlyRate)))).setScale(2, RoundingMode.HALF_UP)))
            );
        }

        log.info("Проверка и перераспределение вероятного остатка, вызванного округлением");

        if ((scheduleList.get(scheduleList.size() - 1).getRemainingDebt()).compareTo(BigDecimal.valueOf(0)) != 0) {

            scheduleList.get(scheduleList.size() - 1).debtPayment(// If in the end of payment story the RemainingDebt doesn't 0(zero)(the problems of math rounding)
                    scheduleList.get(scheduleList.size() - 1).getDebtPayment()//we should add it to a debt payment and in to a totalPayment for the last month
                            .add(scheduleList.get(scheduleList.size() - 1).getRemainingDebt()));
            scheduleList.get(scheduleList.size() - 1).totalPayment(
                    scheduleList.get(scheduleList.size() - 1).getTotalPayment()
                            .add(scheduleList.get(scheduleList.size() - 1).getRemainingDebt()));

            scheduleList.get(scheduleList.size() - 1).remainingDebt(BigDecimal.valueOf(0));
        }
        log.info("График ежемесячных платежей  " + scheduleList);

        log.info("Кредитный продукт создан \n");

        return new CreditDTO()
                .amount(scoringDataDTO.getAmount())
                .term(scoringDataDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(finalRate)
                .psk(calculatePsk(totalAmount.doubleValue(), scheduleList))
                .isInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDTO.getIsSalaryClient())
                .paymentSchedule(scheduleList);
    }


}


