package org.shrek.models;


import com.shrek.model.ApplicationStatus;
import com.shrek.model.ApplicationStatusHistoryDTO;
import com.shrek.model.LoanOfferDTO;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "application")
@Data
@RequiredArgsConstructor
//@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)???
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @OneToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Type(type = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)???
    private LoanOfferDTO appliedOffer;
    @Column(name = "sign_date")
    private LocalDate signDate;
    @Column(name = "ses_code")
    private String sesCode;
    @Column(name = "status_history")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<ApplicationStatusHistoryDTO> statusHistory;
}
