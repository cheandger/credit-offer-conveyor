package org.shrek.services.impl.loanapplreqdtoconfig;

import com.shrek.model.PaymentScheduleElement;
import org.shrek.models.Credit;

import java.math.BigDecimal;
import java.util.List;

public class CreditInstanceInitializer {


    public static Credit initCredit
            (BigDecimal amount, Integer term,
             BigDecimal monthlyPayment, BigDecimal rate, BigDecimal psk,
             Boolean isInsurance, Boolean isSalary, List<PaymentScheduleElement> list) {
        Credit credit = new Credit();
        credit.setAmount(amount);
        credit.setTerm(term);
        credit.setMonthlyPayment(monthlyPayment);
        credit.setRate(rate);
        credit.setPsk(psk);
        credit.setIsInsuranceEnabled(isInsurance);
        credit.setIsSalaryClient(isSalary);
        credit.setPaymentSchedule(list);
        return credit;
    }
}