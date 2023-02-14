package org.shrek.utils;

import com.shrek.model.*;
import lombok.experimental.UtilityClass;
import org.shrek.mappers.ClientFromFinishRegMapper;
import org.shrek.mappers.ClientMapper;
import org.shrek.mappers.CreditMapper;
import org.shrek.mappers.ScoringDataFromClientMapper;
import org.shrek.models.Application;
import org.shrek.models.Client;
import org.shrek.models.Credit;
import org.shrek.models.CreditStatus;
import org.shrek.repository.ApplicationRepository;
import org.shrek.repository.ClientRepository;
import org.shrek.services.impl.DealServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.shrek.model.ApplicationStatus.*;


@UtilityClass
public class DealUtils {

    private static final Logger log = LoggerFactory.getLogger(DealServiceImpl.class);


    public static Application createAndFillTheApplication(LoanApplicationRequestDTO loanApplicationRequestDTO, ClientMapper clientMapper,
                                                          ClientRepository clientRepository) {

        Client client = clientMapper.loanApplicationRequestDtoToClient(loanApplicationRequestDTO);
        log.info("creation Client: {} ", client);
        client.setPassport(new PassportInfo()
                .passportSeries(loanApplicationRequestDTO.getPassportSeries())
                .passportNumber(loanApplicationRequestDTO.getPassportNumber()));
        log.info("A part of passport data was added to client ");
        clientRepository.save(client);
        log.info("save Client: {} ", client);

        Application application = new Application();
        application.setClient(client);
        log.info("create Application: {} ", application);
        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTO.status(PREAPPROVAL);
        applicationStatusHistoryDTO.timeStamp(LocalDateTime.now());
        application.setCreationDate(LocalDateTime.now());
        applicationStatusHistoryDTO.changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL);
        List<ApplicationStatusHistoryDTO> statusHistory = new ArrayList<>();
        statusHistory.add(applicationStatusHistoryDTO);
        application.setStatus(PREAPPROVAL);
        application.setStatusHistory(statusHistory);

        return application;
    }

    public static List<LoanOfferDTO> loanOffersResponseGetBody(ResponseEntity<List<LoanOfferDTO>> response) {
        List<LoanOfferDTO> loanOfferDTOS = response.getBody();

        log.info("Received offers: {}", loanOfferDTOS);
        return loanOfferDTOS;
    }

    public static void changeApplicationDeniedStatus(Application application) {
        ApplicationStatusHistoryDTO applicationDeniedStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationDeniedStatusHistoryDTO.status(CLIENT_DENIED);
        applicationDeniedStatusHistoryDTO.timeStamp(LocalDateTime.now());
        applicationDeniedStatusHistoryDTO.changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC);
        application.setStatus(CLIENT_DENIED);
        List<ApplicationStatusHistoryDTO> deniedStatus = application.getStatusHistory();
        deniedStatus.add(applicationDeniedStatusHistoryDTO);
        application.setStatusHistory(deniedStatus);

    }

    public static void changeAppStatusToApproval(Application application, LoanOfferDTO loanOfferDTO) {
        application.setStatus(APPROVED);

        List<ApplicationStatusHistoryDTO> history = application.getStatusHistory();
        history.add(new ApplicationStatusHistoryDTO()
                .status(APPROVED)
                .timeStamp(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL));

        application.setStatusHistory(history);

        log.info("The status by AppStatus history was change on Preapproval");


        application.setAppliedOffer(loanOfferDTO);

        log.info("The Application was update by new AppliedOffer");
        log.info("changeAppStatusToApproval(), new status ={}", application.getStatus());

    }

    public static Client settingClient(Application application, FinishRegistrationRequestDTO finishRegistrationRequestDTO,
                                       ClientFromFinishRegMapper clientFromMapper) {
        Client client = application.getClient();
        client.getPassport().setPassportIssueBranch(finishRegistrationRequestDTO.getPassportIssueBranch());
        client.getPassport().setPassportIssueDate(finishRegistrationRequestDTO.getPassportIssueDate());
        client = clientFromMapper.clientFromFinishRegistration(finishRegistrationRequestDTO);
        client.setEmployment(finishRegistrationRequestDTO.getEmployment());
        client.setAccount(finishRegistrationRequestDTO.getAccount());
        log.info("settingClient(), fillsUp the clients data = {}", client);
        return client;
    }

    public static ScoringDataDTO createScoringDataDTO(ScoringDataFromClientMapper scoringMapper, Client client
            , Application application) {

        ScoringDataDTO scoringDataDTO = scoringMapper.scoringDataDtoFromClient(client);
        scoringDataDTO.amount(application.getAppliedOffer().getRequestedAmount())
                .term(application.getAppliedOffer().getTerm())
                .isInsuranceEnabled(application.getAppliedOffer().getIsInsuranceEnabled())
                .isSalaryClient(application.getAppliedOffer().getIsSalaryClient());
        log.info("createScoringDataDTO(), scoringDataDTO ={}", scoringDataDTO);
        return scoringDataDTO;
    }

    public static Credit createCredit(CreditDTO creditDTO, CreditMapper creditMapper) {
        Credit credit = creditMapper.creditDtoToCredit(creditDTO);
        credit.setCreditStatus(CreditStatus.CALCULATED);

        log.info("createCredit(), created creditDto={}", creditDTO);
        return credit;
    }

    public void changeAppStatus(Application application, ApplicationStatus status) {
        List<ApplicationStatusHistoryDTO> history = application.getStatusHistory();
        history.add(new ApplicationStatusHistoryDTO()
                .status(status)
                .timeStamp(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL));
        application.setStatus(status);
        application.setStatusHistory(history);


        log.info("changeAppStatusToCCAPPROVED(), application status={}", application.getStatus());

    }

    public static void changeApplicationCCDeniedStatus(Application application, ApplicationRepository applicationRepository) {
        ApplicationStatusHistoryDTO applicationDeniedStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationDeniedStatusHistoryDTO.status(CC_DENIED);
        applicationDeniedStatusHistoryDTO.timeStamp(LocalDateTime.now());
        applicationDeniedStatusHistoryDTO.changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC);
        application.setStatus(CC_DENIED);
        List<ApplicationStatusHistoryDTO> deniedStatus = application.getStatusHistory();
        deniedStatus.add(applicationDeniedStatusHistoryDTO);
        application.setStatusHistory(deniedStatus);
        applicationRepository.save(application);
        log.info("changeApplicationCCDeniedStatus(), application status = {}", application.getStatus());
        log.warn("The response was bad, it hasn't body");
    }

    public static EmailMessageDTO prepareMessage(String email,
                                                 Long applicationId, EmailMessageDTO.ThemeEnum theme) {
        return new EmailMessageDTO().address(email)
                .applicationId(applicationId)
                .theme(theme);
    }
}
