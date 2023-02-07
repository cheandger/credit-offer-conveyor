package org.shrek.services.utils;

import com.shrek.model.*;
import lombok.RequiredArgsConstructor;
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

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.shrek.model.ApplicationStatus.*;

@RequiredArgsConstructor

public class DealServiceUtils<PassportMapper> {

    private static final Logger log = LoggerFactory.getLogger(DealServiceImpl.class);


    public static Application createAndFillTheApplication(LoanApplicationRequestDTO loanApplicationRequestDTO, ClientMapper clientMapper,
                                                          ApplicationRepository appRepo, ClientRepository clientRepository) {
        Client client = new Client();
        client = clientMapper.loanApplicationRequestDtoToClient(loanApplicationRequestDTO);
        log.info("create Client:  " + client);
        client.setPassport(new PassportInfo()
                .passportSeries(loanApplicationRequestDTO.getPassportSeries())
                .passportNumber(loanApplicationRequestDTO.getPassportNumber()));
        log.info("A part of passport data was added to client ");
        clientRepository.save(client);
        log.info("save Client:  " + client);

        Application application = new Application();
        application.setClient(client);
        log.info("create Application:  " + application);
        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTO.status(PREAPPROVAL);
        applicationStatusHistoryDTO.timeStamp(LocalDateTime.now());
        application.setCreationDate(LocalDateTime.now());
        applicationStatusHistoryDTO.changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL);
        List<ApplicationStatusHistoryDTO> statusHistory = new ArrayList<>();
        statusHistory.add(applicationStatusHistoryDTO);
        application.setStatus(PREAPPROVAL);
        application.setStatusHistory(statusHistory);
        appRepo.save(application);
        log.info("create and Application:  " + application);
        return application;
    }

    public static List<LoanOfferDTO> loanOffersResponseGetBody(ResponseEntity<List<LoanOfferDTO>> response) {
        List<LoanOfferDTO> loanOfferDTOS = response.getBody();

        log.info("Received offers: {}", loanOfferDTOS);
        return loanOfferDTOS;
    }

    public static void changeApplicationDeniedStatus(Application application, ApplicationRepository applicationRepository) {
        ApplicationStatusHistoryDTO applicationDeniedStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationDeniedStatusHistoryDTO.status(REQUEST_DENIED);
        applicationDeniedStatusHistoryDTO.timeStamp(LocalDateTime.now());
        applicationDeniedStatusHistoryDTO.changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC);
        application.setStatus(REQUEST_DENIED);
        List<ApplicationStatusHistoryDTO> deniedStatus = application.getStatusHistory();
        deniedStatus.add(applicationDeniedStatusHistoryDTO);
        application.setStatusHistory(deniedStatus);
        applicationRepository.save(application);

    }

    public static void changeAppStatusToPreapproval(Application application, LoanOfferDTO loanOfferDTO,
                                                    ApplicationRepository applicationRepository) {
        application.setStatus(ApplicationStatus.PREAPPROVAL);
        log.info("The loan status was changed on Preapproval");

        List<ApplicationStatusHistoryDTO> history = application.getStatusHistory();
        history.add(new ApplicationStatusHistoryDTO()
                .status(PREAPPROVAL)
                .timeStamp(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL));

        application.setStatusHistory(history);

        log.info("The status by AppStatus history was change on Preapproval");


        application.setAppliedOffer(loanOfferDTO);

        log.info("The Application was update by new AppliedOffer");

        applicationRepository.save(application);
    }

    public static Client settingClient(Application application, FinishRegistrationRequestDTO finishRegistrationRequestDTO,
                                       ClientFromFinishRegMapper clientFMapper, ClientRepository clientRepository) {
        Client client = application.getClient();
        client.getPassport().setPassportIssueBranch(finishRegistrationRequestDTO.getPassportIssueBranch());
        client.getPassport().setPassportIssueDate(finishRegistrationRequestDTO.getPassportIssueDate());
        client = clientFMapper.clientFromFinishRegistration(finishRegistrationRequestDTO);
        client.setEmployment(finishRegistrationRequestDTO.getEmployment());
        client.setAccount(finishRegistrationRequestDTO.getAccount());
        log.info("FullsUp the clients data");

        clientRepository.save(client);
        return client;
    }

    public static ScoringDataDTO createScoringDataDTO(ScoringDataFromClientMapper scoringMapper, Client client
            , Application application) {

        ScoringDataDTO scoringDataDTO = scoringMapper.scoringDataDtoFromClient(client);
        scoringDataDTO.amount(application.getAppliedOffer().getRequestedAmount())
                .term(application.getAppliedOffer().getTerm())
                .firstName(client.getFirstName())
                .isInsuranceEnabled(application.getAppliedOffer().getIsInsuranceEnabled())
                .isSalaryClient(application.getAppliedOffer().getIsSalaryClient());

        return scoringDataDTO;
    }

    public static Credit createCredit(CreditDTO creditDTO, CreditMapper creditMapper) {
        Credit credit = creditMapper.creditDtoToCredit(creditDTO);
        credit.setCreditStatus(CreditStatus.CALCULATED);

        log.info("calculateCredit(), saved credit={}", creditDTO);
        return credit;
    }

    public static void changeAppStatusToCC_APPROVED(Application application, ApplicationRepository applicationRepository) {
        List<ApplicationStatusHistoryDTO> history = application.getStatusHistory();
        history.add(new ApplicationStatusHistoryDTO()
                .status(CC_APPROVED)
                .timeStamp(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL));
        application.setStatusHistory(history);

        log.info("The history of loan request is updated");
        Long random_number = new SecureRandom().nextLong(100, Long.MAX_VALUE);
        application.setSesCode(random_number);
        applicationRepository.save(application);
        log.info("The Application was update and saved");
    }

    public static void changeApplicationCC_DeniedStatus(Application application, ApplicationRepository applicationRepository) {
        ApplicationStatusHistoryDTO applicationDeniedStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationDeniedStatusHistoryDTO.status(CC_DENIED);
        applicationDeniedStatusHistoryDTO.timeStamp(LocalDateTime.now());
        applicationDeniedStatusHistoryDTO.changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC);
        application.setStatus(CC_DENIED);
        List<ApplicationStatusHistoryDTO> deniedStatus = application.getStatusHistory();
        deniedStatus.add(applicationDeniedStatusHistoryDTO);
        application.setStatusHistory(deniedStatus);
        applicationRepository.save(application);
        log.warn("The response was bad, it hasn't body");
    }
}
