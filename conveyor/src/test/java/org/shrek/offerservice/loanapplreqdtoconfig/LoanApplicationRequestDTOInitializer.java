package org.shrek.offerservice.loanapplreqdtoconfig;

import com.shrek.model.LoanApplicationRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanApplicationRequestDTOInitializer {


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


}
