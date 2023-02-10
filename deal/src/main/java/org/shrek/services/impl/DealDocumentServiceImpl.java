package org.shrek.services.impl;


import com.shrek.model.ApplicationStatus;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.shrek.utils.DealServiceUtils.changeAppStatus;
import static org.shrek.utils.DealServiceUtils.prepareMessage;


@Service
@Slf4j
@RequiredArgsConstructor
public class DealDocumentServiceImpl implements DealDocumentService {


    private final DossierServiceImpl dossierService;

    private final ApplicationRepository applicationRepository;

    private final CreditRepository creditRepository;


    private void createDocumentsRequest(Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found. id: " + applicationId));

        if (application.getStatus() != ApplicationStatus.CC_APPROVED) {
            throw new BusinessException(422, "Application, isn't approve by CC_APPROVED status id:  " + application.getId());
        }

        log.info("Sending create document request for application {}, to email {}",
                application, application.getClient().getEmail());

        EmailMessageDTO emailMessageCreateDoc = prepareMessage(application.getClient().getEmail(), application.getId(), EmailMessageDTO.ThemeEnum.CREATE_DOCUMENT);

        dossierService.sendMessage(emailMessageCreateDoc);
    }

    @Override
    public void sendDocuments(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found. id: " + applicationId));

        if (application.getStatus() != ApplicationStatus.CC_APPROVED) {
            throw new BusinessException(422, "Application, isn't approve by CC_APPROVED status id:  " + application.getId());
        }

        changeAppStatus(application, ApplicationStatus.PREPARE_DOCUMENTS);

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

        if (application.getStatus() != ApplicationStatus.DOCUMENT_CREATED) {
            throw new BusinessException(422, "Application, isn't approve by DOCUMENT_CREATED status id:  " + application.getId());
        }

        log.info("Sending sign document request for application {}, to email {}",
                application, application.getClient().getEmail());

        EmailMessageDTO emailMessageCreateDoc = prepareMessage(application.getClient().getEmail(), application.getId(), EmailMessageDTO.ThemeEnum.SEND_SES);

        dossierService.sendMessage(emailMessageCreateDoc);

    }

    @Override
    public void sendCode(Long applicationId, Integer sesCode) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found. id:" + applicationId));

        if (application.getStatus() != ApplicationStatus.DOCUMENT_CREATED) {
            throw new BusinessException(422, "Application, isn't approve by DOCUMENT_CREATED status id:  " + application.getId());
        }
        Long ses = application.getSesCode();
        if (!ses.equals(application.getSesCode())) {
            throw new BusinessException(422, "Application, isn't approve by SesCode  id:  " + application.getId());
        }

        changeAppStatus(application, ApplicationStatus.DOCUMENT_SIGNED);

        applicationRepository.save(application.setSignDate(LocalDateTime.from(LocalDate.now())));

        resolveCredit(applicationId);
    }


    private void resolveCredit(Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found. id:" + applicationId));

        Long creditId = application.getCredit().getId();
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found. id:" + creditId));

        changeAppStatus(application, ApplicationStatus.CREDIT_ISSUED);

        applicationRepository.save(application);

        creditRepository.save(credit.setCreditStatus(CreditStatus.ISSUED));

        EmailMessageDTO emailMessageCreateDoc = prepareMessage(application.getClient().getEmail(), application.getId(), EmailMessageDTO.ThemeEnum.CREDIT_ISSUED);

        dossierService.sendMessage(emailMessageCreateDoc);

    }
}