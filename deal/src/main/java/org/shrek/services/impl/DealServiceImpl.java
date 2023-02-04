package org.shrek.services.impl;

import com.shrek.model.*;
import lombok.RequiredArgsConstructor;
import org.shrek.Feign.ConveyorFeignClient;
import org.shrek.models.Application;
import org.shrek.models.Client;
import org.shrek.models.Credit;
import org.shrek.models.CreditStatus;
import org.shrek.repository.ApplicationRepository;
import org.shrek.repository.ClientRepository;
import org.shrek.repository.CreditRepository;
import org.shrek.services.DealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.shrek.model.ApplicationStatus.CC_APPROVED;
import static com.shrek.model.ApplicationStatus.PREAPPROVAL;


@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private static final Logger log = LoggerFactory.getLogger(DealServiceImpl.class);

    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final ConveyorFeignClient creditConveyorClient;

    @Override
    public List<LoanOfferDTO> createListOffersByFeignClient(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        Client client = new Client();

        client.setFirstName(loanApplicationRequestDTO.getFirstName());
        client.setLastName(loanApplicationRequestDTO.getLastName());
        client.setMiddleName(loanApplicationRequestDTO.getMiddleName());
        client.setEmail(loanApplicationRequestDTO.getEmail());
        client.setBirthDate(loanApplicationRequestDTO.getBirthdate());


        clientRepository.save(client);

        Application application = new Application();
        application.setClient(client);

        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTO.status(PREAPPROVAL);
        applicationStatusHistoryDTO.timeStamp(LocalDateTime.now());
        applicationStatusHistoryDTO.changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL);

        List<ApplicationStatusHistoryDTO> statusHistory = new ArrayList<>();
        statusHistory.add(applicationStatusHistoryDTO);

        application.setStatus(PREAPPROVAL);
        application.setStatusHistory(statusHistory);

        applicationRepository.save(application);


        List<LoanOfferDTO> loanOffers = creditConveyorClient.getOffers(loanApplicationRequestDTO).getBody();
        log.info("Possible loan terms for applicationId: " + application.getId() + " are calculated: " + loanOffers);
        assert loanOffers != null;
        loanOffers.forEach((loanOfferDTO) -> loanOfferDTO.setApplicationId(application.getId()));

          /*  EmailMessage emailMessage = new EmailMessage();
            emailMessage.setApplicationId(application.getId());
            emailMessage.setTheme(EmailMessage.ThemeEnum.APPLICATION_DENIED);
            emailMessage.setAddress(application.getClient().getEmail());

            kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);*/


        return loanOffers;
    }

    @Override
    public void getAppChangeStatus(LoanOfferDTO loanOfferDTO) {
        Application application;
        application = applicationRepository.findById(loanOfferDTO.getApplicationId()).orElseThrow(() -> new IllegalArgumentException(loanOfferDTO.getApplicationId().toString()));
        log.info("API /deal/offer: По id " + loanOfferDTO.getApplicationId() + " найденна заявка - " + application);
        application.setStatus(ApplicationStatus.PREAPPROVAL);
        log.info("API /deal/offer: Статус заявки обновлне на Preapproval");
        List<ApplicationStatusHistoryDTO> history = application.getStatusHistory();
        history.add(new ApplicationStatusHistoryDTO()
                .status(PREAPPROVAL)
                .timeStamp(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL));

        application.setStatusHistory(history);
        log.info("API /deal/offer: Обновлено истроия статусов заявки");

        applicationRepository.save(application);

       /* EmailMessage emailMessage = new EmailMessage();
        emailMessage.setApplicationId(loanOfferDTO.getApplicationId());
        emailMessage.setTheme(EmailMessage.ThemeEnum.FINISH_REGISTRATION);
        emailMessage.setAddress(application.getClient().getEmail());

        kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);
        log.info("Application with id = " + application.getId() + " saved in database.");
*/

    }

    @Override
    public void formScoringData(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id = " + applicationId + " not found."));

        Client client = application.getClient();
        PassportInfo fullPassportInfo = new PassportInfo()
                .passportSeries(client.getPassport().getPassportSeries())
                .passportNumber(client.getPassport().getPassportNumber())
                .passportIssueDate(finishRegistrationRequestDTO.getPassportIssueDate())
                .passportIssueBranch(finishRegistrationRequestDTO.getPassportIssueBranch());


        client.setPassport(fullPassportInfo);
        client.setGender(finishRegistrationRequestDTO.getGender().getValue());
        client.setMaritalStatus(finishRegistrationRequestDTO.getMaritalStatus().getValue());
        client.setDependentAmount(finishRegistrationRequestDTO.getDependentAmount());
        client.setEmployment(finishRegistrationRequestDTO.getEmployment());
        client.setAccount(finishRegistrationRequestDTO.getAccount());


        ScoringDataDTO scoringDataDTO = new ScoringDataDTO()

                .amount(application.getAppliedOffer().getRequestedAmount())
                .term(application.getAppliedOffer().getTerm())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .gender(ScoringDataDTO.GenderEnum.valueOf(client.getGender()))
                .birthdate(client.getBirthDate())
                .passportSeries(client.getPassport().getPassportSeries())
                .passportNumber(client.getPassport().getPassportNumber())
                .passportIssueDate(client.getPassport().getPassportIssueDate())
                .passportIssueBranch((client.getPassport().getPassportIssueBranch()))
                .maritalStatus(ScoringDataDTO.MaritalStatusEnum.valueOf(client.getMaritalStatus()))
                .dependentAmount(client.getDependentAmount())
                .employment(client.getEmployment())
                .account(client.getAccount())
                .isInsuranceEnabled(application.getAppliedOffer().getIsInsuranceEnabled())
                .isSalaryClient(application.getAppliedOffer().getIsSalaryClient());

        CreditDTO creditDTO = creditConveyorClient.calculateCredit(scoringDataDTO).getBody();
        log.info("The loan for applicationId = " + application.getId() + " is calculated: " + creditDTO);

        Credit credit = new Credit();
        assert creditDTO != null;
        credit.setAmount(creditDTO.getAmount());
        credit.setTerm(creditDTO.getTerm());
        credit.setMonthlyPayment(creditDTO.getMonthlyPayment());
        credit.setRate(creditDTO.getRate());
        credit.setPsk(creditDTO.getPsk());
        credit.setPaymentSchedule(creditDTO.getPaymentSchedule());
        credit.setIsInsuranceEnabled(creditDTO.getIsInsuranceEnabled());
        credit.setIsSalaryClient(creditDTO.getIsSalaryClient());
        credit.setCreditStatus(CreditStatus.CALCULATED);


        log.info("calculateCredit(), saved credit={}", credit);


        creditRepository.save(credit);
        application.setCredit(credit);

        List<ApplicationStatusHistoryDTO> history = application.getStatusHistory();
        history.add(new ApplicationStatusHistoryDTO()
                .status(CC_APPROVED)
                .timeStamp(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL));

        application.setStatusHistory(history);
        log.info("deal/offer: The history of loan request is updated");
        applicationRepository.save(application);


        Long random_number = new SecureRandom().nextLong(100, Long.MAX_VALUE);
        application.setSesCode(String.valueOf(random_number));
        applicationRepository.save(application);
/*
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setApplicationId(applicationId);
        emailMessage.setTheme(EmailMessage.ThemeEnum.CREATE_DOCUMENTS);
        emailMessage.setAddress(application.getClient().getEmail());

        kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);

    }
*/
    }
}
