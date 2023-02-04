package org.shrek.models;


import com.shrek.model.ApplicationStatus;
import com.shrek.model.ApplicationStatusHistoryDTO;
import com.shrek.model.LoanOfferDTO;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity(name = "application")
@Data
//@RequiredArgsConstructor
@NoArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
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
    private LoanOfferDTO appliedOffer;
    @Column(name = "sign_date")
    private LocalDate signDate;
    @Column(name = "ses_code")
    private String sesCode;
    @Column(name = "status_history")
    @Type(type = "jsonb")
    private List<ApplicationStatusHistoryDTO> statusHistory;
/*
    public void updateApplicationStatus(ApplicationStatus newStatus,
                                        ApplicationStatusHistoryDTO.ChangeTypeEnum changeType) {

        List<ApplicationStatusHistoryDTO> updatedStatusHistory = updateStatusHistory(
                this.getStatusHistory(),
                newStatus,
                changeType
        );

        this.setStatus(newStatus)
                .setStatusHistory(updatedStatusHistory);
    }

    private List<ApplicationStatusHistoryDTO> updateStatusHistory(List<ApplicationStatusHistoryDTO> history,
                                                                  ApplicationStatus newStatus,
                                                                  ApplicationStatusHistoryDTO.ChangeTypeEnum changeType) {
        if (history == null) {
            history = new ArrayList<>();
        }

        history.add(new ApplicationStatusHistoryDTO()
                .status(newStatus)
                .timeStamp(LocalDateTime.now())
                .changeType(changeType));
        return history;
    }
}
*/

}