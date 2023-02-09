package org.shrek.services.impl;

import com.shrek.model.*;
import lombok.RequiredArgsConstructor;
import org.shrek.exceptions.BusinessException;
import org.shrek.feign.ConveyorFeignClient;
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

        Application application = createAndFillTheApplication(loanApplicationRequestDTO, clientMapper, clientRepository);
        Long applicationId = application.getId();
        applicationRepository.save(application);

        ResponseEntity<List<LoanOfferDTO>> loanOffersResponse = creditConveyorClient.createOffers(loanApplicationRequestDTO);//POST

        if (loanOffersResponse.getStatusCode().is2xxSuccessful()) {
            List<LoanOfferDTO> loanOfferDTOS = loanOffersResponseGetBody(loanOffersResponse);
            log.info("Possible loan terms for applicationId: " + application.getId() + " are calculated: " + loanOfferDTOS);
            assert loanOfferDTOS != null;
            loanOfferDTOS.forEach((loanOfferDTO) -> loanOfferDTO.setApplicationId(applicationId));
            return loanOfferDTOS;
        } else {
            changeApplicationDeniedStatus(application);

            log.warn("The response was bad, it hasn't body");
            throw new BusinessException(loanOffersResponse.getStatusCodeValue(), "Your loan request was denied.");
        }
    }

    @Override
    public void getAppChangeStatusAfterApplying(LoanOfferDTO loanOfferDTO) {

        Application application = applicationRepository.findById(loanOfferDTO.getApplicationId())
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        log.info("Application has found, application id = {}", application.getId());

        changeAppStatusToApproval(application, loanOfferDTO);

        applicationRepository.save(application);
    }

    @Override
    public void formScoringData(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        log.info("Application has found, application id = {}", application.getId());
        Client client = settingClient(application, finishRegistrationRequestDTO,
                clientFromFinishRegMapperMapper);
        clientRepository.save(client);
        ScoringDataDTO scoringDataDTO = createScoringDataDTO(scoringMapper, client, application);


        ResponseEntity<CreditDTO> responseCreditDTO = creditConveyorClient.calculate(scoringDataDTO);//POST

        if (responseCreditDTO.getStatusCode().is2xxSuccessful()) {
            CreditDTO creditDTO = responseCreditDTO.getBody();
            log.info("The loan for applicationId {} ", application.getId() + " is calculated: " + creditDTO);

            Credit credit = createCredit(creditDTO, creditMapper);

            application.setCredit(credit);

            creditRepository.save(credit);

            changeAppStatusToCCAPPROVED(application);

            applicationRepository.save(application);
            log.info("applicationRepository.save(), application={}", application.getId());
        } else {
            changeApplicationCCDeniedStatus(application, applicationRepository);
            applicationRepository.save(application);
            log.warn("The response was bad, it hasn't body");
            throw new BusinessException(responseCreditDTO.getStatusCodeValue(), "Your loan request was denied by the Credit Conveyor.");


        }
    }
}












