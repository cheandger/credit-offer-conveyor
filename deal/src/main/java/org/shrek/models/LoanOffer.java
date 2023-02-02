package org.shrek.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor

public class LoanOffer {

    @JsonProperty("applicationId")
    private Long applicationId;

    @JsonProperty("requestedAmount")
    private BigDecimal requestedAmount;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("term")
    private Integer term;

    @JsonProperty("monthlyPayment")
    private BigDecimal monthlyPayment;

    @JsonProperty("rate")
    private BigDecimal rate;

    @JsonProperty("isInsuranceEnabled")
    private Boolean isInsuranceEnabled;

    @JsonProperty("isSalaryClient")
    private Boolean isSalaryClient;


}


