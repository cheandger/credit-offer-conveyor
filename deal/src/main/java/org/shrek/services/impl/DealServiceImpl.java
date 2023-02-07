package org.shrek.services.impl;

import com.shrek.model.*;
import lombok.RequiredArgsConstructor;
import org.shrek.Feign.ConveyorFeignClient;
import org.shrek.exceptions.BusinessException;
import org.shrek.mappers.ClientFromFinishRegMapper;
import org.shrek.mappers.ClientMapper;
import org.shrek.mappers.CreditMapper;
import org.shrek.mappers.ScoringDataFromClientMapper;
import org.shrek.models.Application;
import org.shrek.models.Client;
import org.shrek.models.Credit;
import org.shrek.repository.ApplicationRepository;
import org.shrek.repository.ClientRepository;
import org.shrek.repository.CreditRepository;
import org.shrek.services.DealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.shrek.services.utils.DealServiceUtils.*;


@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private static final Logger log = LoggerFactory.getLogger(DealServiceImpl.class);
    private final CreditMapper creditMapper;

    private final ClientMapper clientMapper;
    private final ClientFromFinishRegMapper clientFromFinishRegMapperMapper;
    private final ScoringDataFromClientMapper scoringMapper;

    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final ConveyorFeignClient creditConveyorClient;

    @Override
    public List<LoanOfferDTO> createListOffersByFeignClient(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        Application application = createAndFillTheApplication(loanApplicationRequestDTO, clientMapper, applicationRepository, clientRepository);
        Long applicationId = application.getId();

        ResponseEntity<List<LoanOfferDTO>> loanOffersResponse = creditConveyorClient.createOffers(loanApplicationRequestDTO);

        if (loanOffersResponse.getStatusCode().is2xxSuccessful()) {
            List<LoanOfferDTO> loanOfferDTOS = loanOffersResponseGetBody(loanOffersResponse);
            log.info("Possible loan terms for applicationId: " + application.getId() + " are calculated: " + loanOfferDTOS);
            assert loanOfferDTOS != null;
            loanOfferDTOS.forEach((loanOfferDTO) -> loanOfferDTO.setApplicationId(applicationId));
            return loanOfferDTOS;
        } else {
            changeApplicationDeniedStatus(application, applicationRepository);

            log.warn("The response was bad, it hasn't body");
            throw new BusinessException(loanOffersResponse.getStatusCodeValue(), "Your loan request was denied.");
        }
    }

    @Override
    public void getAppChangeStatus(LoanOfferDTO loanOfferDTO) {

        Application application = applicationRepository.findById(loanOfferDTO.getApplicationId())
                .orElseThrow(() -> new EntityNotFoundException("Application with id = " + loanOfferDTO.getApplicationId() + " not found."));

        log.info("By id " + loanOfferDTO.getApplicationId() + " the request have found - " + application);

        changeAppStatusToPreapproval(application, loanOfferDTO, applicationRepository);

    }

    @Override
    public void formScoringData(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id = " + applicationId + " not found."));


        Client client = settingClient(application, finishRegistrationRequestDTO,
                clientFromFinishRegMapperMapper, clientRepository);

        ScoringDataDTO scoringDataDTO = createScoringDataDTO(scoringMapper, client, application);

        log.info("Doing request to Credit Conveyor");

        ResponseEntity<CreditDTO> responseCreditDTO = creditConveyorClient.calculate(scoringDataDTO);

        if (responseCreditDTO.getStatusCode().is2xxSuccessful()) {
            CreditDTO creditDTO = responseCreditDTO.getBody();
            log.info("The loan for applicationId = " + application.getId() + " is calculated: " + creditDTO);

            Credit credit = createCredit(creditDTO, creditMapper);
            creditRepository.save(credit);

            application.setCredit(credit);//?????????? Whaaa??????todo

            changeAppStatusToCC_APPROVED(application, applicationRepository);

        } else {
            changeApplicationCC_DeniedStatus(application, applicationRepository);
            log.warn("The response was bad, it hasn't body");
            throw new BusinessException(responseCreditDTO.getStatusCodeValue(), "Your loan request was denied by the Credit Conveyor.");


        }
    }
}































 /* EmailMessage emailMessage = new EmailMessage();
            emailMessage.setApplicationId(application.getId());
            emailMessage.setTheme(EmailMessage.ThemeEnum.APPLICATION_DENIED);
            emailMessage.setAddress(application.getClient().getEmail());

            kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);*/

  /* EmailMessage emailMessage = new EmailMessage();
        emailMessage.setApplicationId(loanOfferDTO.getApplicationId());
        emailMessage.setTheme(EmailMessage.ThemeEnum.FINISH_REGISTRATION);
        emailMessage.setAddress(application.getClient().getEmail());

      */
  /* EmailMessage emailMessage = new EmailMessage();
        emailMessage.setApplicationId(applicationId);
        emailMessage.setTheme(EmailMessage.ThemeEnum.CREATE_DOCUMENTS);
        emailMessage.setAddress(application.getClient().getEmail());

        kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);*/


    /* Client client = new Client();
        client.setFirstName(loanApplicationRequestDTO.getFirstName());
        client.setLastName(loanApplicationRequestDTO.getLastName());
        client.setMiddleName(loanApplicationRequestDTO.getMiddleName());
        client.setEmail(loanApplicationRequestDTO.getEmail());
        client.setBirthDate(loanApplicationRequestDTO.getBirthDate());
        log.info("create Client:  " + client);77
        clientRepository.save(client);
        log.info("save Client:  " + client);
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
        Application savedApp = applicationRepository.save(application);*/