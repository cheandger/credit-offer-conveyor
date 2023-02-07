package org.shrek.models;

import com.shrek.model.PaymentScheduleElement;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity(name = "credit")
@Data

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credit_id", nullable = false)
    private Long id;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "term")
    private Integer term;
    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;
    @Column(name = "rate")
    private BigDecimal rate;
    @Column(name = "psk")
    private BigDecimal psk;
    @Column(name = "payment_schedule")
    @Type(type = "jsonb")
    private List<PaymentScheduleElement> paymentSchedule;
    @Column(name = "insurance_enabled")
    private Boolean isInsuranceEnabled;
    @Column(name = "salary_client")
    private Boolean isSalaryClient;
    @Column(name = "credit_status")
    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;
}
