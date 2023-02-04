package org.shrek.util;

import com.shrek.model.PaymentScheduleElement;
import com.shrek.model.ScoringDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static com.shrek.model.EmploymentDTO.EmploymentStatusEnum.BUSINESS_OWNER;
import static com.shrek.model.EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED;
import static com.shrek.model.EmploymentDTO.PositionEnum.MID_MANAGER;
import static com.shrek.model.EmploymentDTO.PositionEnum.TOP_MANAGER;
import static com.shrek.model.ScoringDataDTO.GenderEnum.*;
import static com.shrek.model.ScoringDataDTO.MaritalStatusEnum.DIVORCED;
import static com.shrek.model.ScoringDataDTO.MaritalStatusEnum.MARRIED;
import static java.time.Duration.between;

@Component

public class CalculationUtil {

    private static final Logger log = LoggerFactory.getLogger(CalculationUtil.class);


    public static BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal finalRate) {


        log.info("Перерасчет базовой ставки с учетом условий скорринга");


        log.info("Расчет сотой доли месячной ставки");

        BigDecimal aHundredthPartOfMonthlyRate = finalRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);
        log.info("Сотая доля месячной ставки составляет  " + aHundredthPartOfMonthlyRate.multiply(BigDecimal.valueOf(100))
                .setScale(4, RoundingMode.HALF_UP));

        log.info("Расчет ежемесячного платежа ");

        /*
        Расчет ежемесячного платежа производится по формуле:

                    x=S*(P+P/((1+P)^N)-1)
          где:
          S — сумма займа
          P — 1/100 доля процентной ставки (в месяц)
          N — срок кредитования (в месяцах)

           Доля процентов (I) в ежемесячном взносе вычисляется по формуле:

                    I=S*P
         где:
          S — остаточный объем средств
          P — упомянутая ранее процентная ставка

         */

        BigDecimal monthlyPayment = totalAmount.multiply((aHundredthPartOfMonthlyRate
                .add((aHundredthPartOfMonthlyRate
                        .divide(((BigDecimal.valueOf(1)
                                .add(aHundredthPartOfMonthlyRate))
                                .pow(term))
                                .subtract(BigDecimal.valueOf(1)), 6, RoundingMode.HALF_UP))))).setScale(2, RoundingMode.HALF_UP);

        log.info("Ежемесячный платеж с учетом ануитетного графика погашения составляет " + monthlyPayment);


        return monthlyPayment;
    }


    public static BigDecimal calculateIsInsuranceCaseTotalAmount(BigDecimal requestAmount, Boolean isInsuranceEnabled, Integer term, BigDecimal insuranceRate) {

        BigDecimal totalAmount = requestAmount.setScale(2, RoundingMode.HALF_UP);

        log.info("Перерасчет тела кредита с учетом условий скорринга");

        if (isInsuranceEnabled) {

            totalAmount = (totalAmount.add((insuranceRate.multiply(totalAmount)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(term))));
        }
        log.info("Тело кредита с учетом условий скорринга составляет  " + totalAmount + "руб ");

        return totalAmount;
    }

    public static BigDecimal calculatePsk(Double amount, List<PaymentScheduleElement> paymentScheduleElementList) {
        log.info("Расчет ПСК");

        /*
         ПСК = i * ЧБП * 100.
         ЧБП – число базовых периодов в календарном году. Длительность календарного года принимается равной 365 дней.
          При стандартном графике платежей с ежемесячными выплатами по системе «аннуитет» ЧБП = 12.
          i – процентная ставка базового периода в десятичной форме. Находится способом подбора как самое меньшее
          положительное значение следующего уравнения:

          0 = - loanAmount + monthlyTotalPayment / ((1 + e[k] * i) * Math.pow(1 + i, q[k]))

          Где:

          - loanAmount        - отрицательное значение суммы займа, в ряду учитывается, как первый член;
          monthlyTotalPayment - ежемесячный суммарный платеж по кредиту;
          e[k]                - период, выраженный в частях установленного базового периода, рассчитанный
                                со времени завершения qk-ого периода до даты k-ого денежного платежа;
          q[k]                – число базовых периодов с даты выдачи займа до k-ого денежного платежа;

          */

        Integer size = paymentScheduleElementList.size() + 1;// размер будующих массивов

        Double basePeriod = 30d;//базовый период (по истечение которого будет производиться платеж)

        Double amountOfBasePeriodsInYear = Math.floor(365 / basePeriod);//количество базовых периодов в году

        Long[] days = new Long[size];//массив дней от нулевого до последнего в базовом периоде
        Double[] e = new Double[size];
        Double[] q = new Double[size];
        Double[] sum = new Double[size];// массив ежемесячных totalPayment
        LocalDate[] dates = new LocalDate[size]; //массив дат, по которым будет производиться платеж

        dates[0] = paymentScheduleElementList.get(0).getDate().minusMonths(1);//дата первого платежа из
        // paymentScheduleElementList

        sum[0] = -amount;// первый член ряда, и массива ежемесячных платежей - отрицательная сумма займа

        for (Integer n = 1; n < size; n++) {

            dates[n] = paymentScheduleElementList.get(n - 1).getDate();//массив дат последующих платежей
            sum[n] = paymentScheduleElementList.get(n - 1).getTotalPayment().doubleValue();// заполняем массив значениями totalPayment
        }

        for (Integer l = 0; l < size; l++) {
            days[l] = between(dates[0].atStartOfDay(), dates[l].atStartOfDay()).toDays();//количество дней периода займа
            // от первого дня  до последнего

            e[l] = ((days[l] % basePeriod) / basePeriod);//период, выраженный в частях установленного базового периода,
            // рассчитанный со времени завершения qk-ого периода до
            // даты k-ого денежного платежа;
            q[l] = Math.floor(days[l] / basePeriod);//величина степени, являющаяся порядковым номером базового периода
        }

        log.info("Расчет i");

        // Необходимо найти наименьшую 'i'. Рассчитаем 'i' подстановкой, увеличивая ее на 0,0001

        Double i = (double) 0;
        Double x = 1.0;
        Double s = 0.0001;//размер шага, на который мы будем увеличивать 'i'

        while (x > 0) {
            x = (double) 0;
            for (Integer k = 0; k < size; k++) {
                x = x + sum[k] / ((1 + e[k] * i) * Math.pow(1 + i, q[k]));
            }
            i = i + s;
        }

        log.info("Расчет i окончен  i =  " + i);

        BigDecimal psk = BigDecimal.valueOf(i * amountOfBasePeriodsInYear * 100).setScale(2, RoundingMode.HALF_UP);
        log.info("Рассчитаная ПСК составляет: " + psk + " % " +
                "////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        return psk;
    }

    ;

    public static BigDecimal evaluateRateByScoring(ScoringDataDTO scoringDataDTO, BigDecimal baseRate) {

        log.info("Расчет годовой процентной ставки с учетом условий скорринга");

        BigDecimal preEvalRate = baseRate;

        if (scoringDataDTO.getEmployment().getEmploymentStatus().equals(SELF_EMPLOYED)) {
            preEvalRate = preEvalRate.add(BigDecimal.valueOf(1));

        } else if (scoringDataDTO.getEmployment().getEmploymentStatus().equals(BUSINESS_OWNER)) {
            preEvalRate = preEvalRate.add(BigDecimal.valueOf(3));
        }


        if (scoringDataDTO.getEmployment().getPosition().equals(MID_MANAGER)) {
            preEvalRate = preEvalRate.subtract(BigDecimal.valueOf(2));
        }

        if (scoringDataDTO.getEmployment().getPosition().equals(TOP_MANAGER)) {
            preEvalRate = preEvalRate.subtract(BigDecimal.valueOf(4));
        }

        if (scoringDataDTO.getIsInsuranceEnabled()) {
            preEvalRate = preEvalRate.subtract(BigDecimal.valueOf(3));
        }

        if (scoringDataDTO.getIsSalaryClient()) {
            preEvalRate = preEvalRate.subtract(BigDecimal.valueOf(1));
        }

        if (scoringDataDTO.getMaritalStatus().equals(MARRIED)) {
            preEvalRate = preEvalRate.subtract(BigDecimal.valueOf(3));
        }

        if (scoringDataDTO.getMaritalStatus().equals(DIVORCED)) {
            preEvalRate = preEvalRate.add(BigDecimal.valueOf(1));
        }

        if (scoringDataDTO.getDependentAmount() > 1) {
            preEvalRate = preEvalRate.add(BigDecimal.valueOf(1));
        }

        if ((scoringDataDTO.getGender().equals(MALE) && (LocalDate.now().getYear() - scoringDataDTO.getBirthdate()
                .getYear()) < 55 && (LocalDate.now().getYear() - scoringDataDTO.getBirthdate().getYear()) > 30) ||
                (scoringDataDTO.getGender().equals(FEMALE) && (LocalDate.now().getYear() - scoringDataDTO.
                        getBirthdate().getYear()) < 60 && ((LocalDate.now().getYear() - scoringDataDTO.getBirthdate().getYear()) > 35))) {

            preEvalRate = preEvalRate.subtract(BigDecimal.valueOf(3));

        } else if (scoringDataDTO.getGender().equals(NON_BINARY)) {

            preEvalRate = preEvalRate.add(BigDecimal.valueOf(3));
        }


        log.info("Годовая процентная ставка по кредиту составляет  " + preEvalRate);

        return preEvalRate.setScale(2, RoundingMode.HALF_UP);
    }

}


