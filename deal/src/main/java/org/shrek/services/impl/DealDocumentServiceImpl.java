package org.shrek.services.impl;


import com.shrek.model.ApplicationStatus;
import com.shrek.model.ApplicationStatusHistoryDTO;
import com.shrek.model.EmailMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shrek.exceptions.BusinessException;
import org.shrek.models.Application;
import org.shrek.models.Credit;
import org.shrek.models.CreditStatus;
import org.shrek.repository.ApplicationRepository;
import org.shrek.repository.CreditRepository;
import org.shrek.services.DealDocumentService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.shrek.utils.DealServiceUtils.prepareMessage;


@Service
@Slf4j
@RequiredArgsConstructor
public class DealDocumentServiceImpl implements DealDocumentService {


    private final DossierServiceImpl dossierService;

    private final ApplicationRepository applicationRepository;

    private final CreditRepository creditRepository;



    @Override
    public void sendDocuments(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found. id: " + applicationId));

        if (application.getStatus() != ApplicationStatus.CC_APPROVED) {
            throw new BusinessException(422, "Application, isn't approve by CC_APPROVED status id:  " + application.getId());
        }
        List<ApplicationStatusHistoryDTO> history = application.getStatusHistory();
        history.add(new ApplicationStatusHistoryDTO()
                .status(ApplicationStatus.PREPARE_DOCUMENTS)// to exclude static reff's
                .timeStamp(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL));
        application.setStatus(ApplicationStatus.PREPARE_DOCUMENTS);
        application.setStatusHistory(history);

        applicationRepository.save(application);

        log.info("Sending document request for application {}, to email {}",
                application, application.getClient().getEmail());

        EmailMessageDTO emailMessageCreateDoc = prepareMessage(application.getClient().getEmail(), application.getId(), EmailMessageDTO.ThemeEnum.SEND_DOCUMENT);

        dossierService.sendMessage(emailMessageCreateDoc);
    }

    @Override
    public void signDocuments(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found. id: " + applicationId));

        if (application.getStatus() != ApplicationStatus.PREPARE_DOCUMENTS) {
            throw new BusinessException(422, "Application, isn't approve by DOCUMENT_CREATED status id:  " + application.getId());
        }
        List<ApplicationStatusHistoryDTO> history = application.getStatusHistory();
        history.add(new ApplicationStatusHistoryDTO()
                .status(ApplicationStatus.DOCUMENT_CREATED)// to exclude static reff's
                .timeStamp(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL));
        application.setStatus(ApplicationStatus.DOCUMENT_CREATED);
        application.setStatusHistory(history);

        Long random_number = new SecureRandom().nextLong(1000, Long.MAX_VALUE);
        application.setSesCode(random_number);

        applicationRepository.save(application);

        log.info("Sending sign document request for application {}, to email {}",
                application, application.getClient().getEmail());

        EmailMessageDTO emailMessageCreateDoc = prepareMessage(application.getClient().getEmail(), application.getId(), EmailMessageDTO.ThemeEnum.SEND_SES);

        dossierService.sendMessage(emailMessageCreateDoc);//ссылка и код отправляются клиенту.

    }

    @Override
    public void sendCode(Long applicationId, Long ses) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found. id:" + applicationId));

        if (application.getStatus() != ApplicationStatus.DOCUMENT_CREATED) {
            throw new BusinessException(422, "Application, isn't approve by DOCUMENT_CREATED status id:  " + application.getId());
        }

        if (!ses.equals(application.getSesCode())) {
            throw new BusinessException(422, "Application, isn't approve by SesCode  id:  " + application.getId());
        }

        List<ApplicationStatusHistoryDTO> history = application.getStatusHistory();
        history.add(new ApplicationStatusHistoryDTO()
                .status(ApplicationStatus.DOCUMENT_SIGNED)// to exclude static reff's
                .timeStamp(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL));
        application.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
        application.setStatusHistory(history);


        applicationRepository.save(application.setSignDate(LocalDateTime.from(LocalDate.now())));

        resolveCredit(applicationId);

    }


    @Override
    public void resolveCredit(Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found. id:" + applicationId));

        Long creditId = application.getCredit().getId();
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found. id:" + creditId));

        List<ApplicationStatusHistoryDTO> history = application.getStatusHistory();
        history.add(new ApplicationStatusHistoryDTO()
                .status(ApplicationStatus.CREDIT_ISSUED)
                .timeStamp(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL));
        application.setStatus(ApplicationStatus.CREDIT_ISSUED);
        application.setStatusHistory(history);

        EmailMessageDTO mailMessageDTO = new EmailMessageDTO()
                .address(application.getClient().getEmail())
                .applicationId(applicationId)
                .theme(EmailMessageDTO.ThemeEnum.CREDIT_ISSUED);

        applicationRepository.save(application);

        creditRepository.save(credit.setCreditStatus(CreditStatus.ISSUED));

        dossierService.sendMessage(mailMessageDTO);

    }
}