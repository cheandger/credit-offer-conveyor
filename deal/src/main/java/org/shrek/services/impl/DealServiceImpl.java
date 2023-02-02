package org.shrek.services.impl;

import com.shrek.model.ClientDTO;
import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import com.shrek.model.PassportInfo;
import org.shrek.services.DealService;

import java.util.List;

public class DealServiceImpl implements DealService {

    //private final ApplicationRepository applicationRepository;
    // private final ConveyorFeignClient feinClient;
    /// private final ClientRepository clientRepository;

    public DealServiceImpl() {
    }

    public List<LoanOfferDTO> getListOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        ClientDTO client = new ClientDTO();

        PassportInfo passport = new PassportInfo();
        passport.passportNumber(loanApplicationRequestDTO.getPassportSeries());
        passport.passportNumber(loanApplicationRequestDTO.getPassportNumber());


        client.firstName(loanApplicationRequestDTO.getFirstName());
        client.lastName(loanApplicationRequestDTO.getLastName());
        client.middleName(loanApplicationRequestDTO.getMiddleName());
        client.email(loanApplicationRequestDTO.getEmail());
        client.birthdate(loanApplicationRequestDTO.getBirthdate());
        client.passportInfo(passport);

      /*  clientRepository.save(client);

        Application application = new Application();
        application.setClient(client);

        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTO.setStatus(ApplicationStatus.PREAPPROVAL);
        applicationStatusHistoryDTO.setTime(LocalDateTime.now());

        List<ApplicationStatusHistoryDTO> statusHistory = new ArrayList<>();
        statusHistory.add(applicationStatusHistoryDTO);

        application.setStatus(ApplicationStatus.PREAPPROVAL);
        application.setStatusHistory(statusHistory);

        applicationRepository.save(application);

        List<LoanOfferDTO> loanOffers = null;

        try {
            loanOffers = creditConveyorClient.getOffers(loanApplicationRequestDTO);
            log.info("Possible loan terms for applicationId: " + application.getId() + " are calculated: " + loanOffers);
        } catch (RuntimeException e) {
            EmailMessage emailMessage = new EmailMessage();
            emailMessage.setApplicationId(application.getId());
            emailMessage.setTheme(EmailMessage.ThemeEnum.APPLICATION_DENIED);
            emailMessage.setAddress(application.getClient().getEmail());

            kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);

            throw new RuntimeException(e);
        }

        loanOffers.stream()
                .peek(loanOfferDTO -> loanOfferDTO.setApplicationId(application.getId()))
                .collect(Collectors.toList());

        return loanOffers;
    }*/
        return null;
    }
}
