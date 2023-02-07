package org.shrek.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity(name = "applied_offer")
@Data
public class AppliedOffer {

    @JsonProperty(value = "application_id")
    private long applicationId;

    @JsonProperty(value = "requested_amount")
    private BigDecimal requestedAmount;

    @JsonProperty(value = "total_amount")
    private BigDecimal totalAmount;

    private int term;

    @JsonProperty(value = "monthly_payment")
    private BigDecimal monthlyPayment;

    private BigDecimal rate;

    @JsonProperty(value = "isInsuranceEnabled")
    private boolean isInsuranceEnabled;

    @JsonProperty(value = "isInsuranceEnabled")
    private boolean isSalaryClient;
}