package org.shrek.services.impl.loanapplreqdtoconfig;

import com.shrek.model.CreditDTO;
import com.shrek.model.PaymentScheduleElement;

import java.math.BigDecimal;
import java.util.List;

public class CreditDtoInitializer {

    public static CreditDTO initCreditDto
            (BigDecimal amount, Integer term,
             BigDecimal monthlyPayment, BigDecimal rate, BigDecimal psk,
             Boolean isInsurance, Boolean isSalary, List<PaymentScheduleElement> list) {
        return new CreditDTO()
                .amount(amount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .psk(psk)
                .isInsuranceEnabled(isInsurance)
                .isSalaryClient(isSalary)
                .paymentSchedule(list);
    }
}
