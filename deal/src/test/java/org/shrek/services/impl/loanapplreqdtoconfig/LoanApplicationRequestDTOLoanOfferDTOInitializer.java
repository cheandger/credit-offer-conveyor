package org.shrek.services.impl.loanapplreqdtoconfig;

import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanApplicationRequestDTOLoanOfferDTOInitializer {


    public static LoanApplicationRequestDTO loanAppReqInit(
            BigDecimal amount, Integer term,

            LocalDate birthdate
    ) {
        return new LoanApplicationRequestDTO()
                .amount(amount)
                .term(term)
                .firstName("Вася")
                .lastName("Пупкин")
                .middleName("Поликарпович")
                .email("no-reply@mcplat.ru")
                .birthDate(birthdate)
                .passportSeries("789456")
                .passportNumber("1239");

    }

    public static LoanOfferDTO initLoanOffer(Long appId, BigDecimal reqAmount, BigDecimal totalAmmount
            , Integer term, BigDecimal monthlyPay, BigDecimal rate, Boolean isSalClient, Boolean isInsurance) {
        return new LoanOfferDTO()
                .applicationId(appId)
                .requestedAmount(reqAmount)
                .term(term)
                .totalAmount(totalAmmount)
                .monthlyPayment(monthlyPay)
                .rate(rate)
                .isSalaryClient(isSalClient)
                .isInsuranceEnabled(isInsurance);
    }


}
