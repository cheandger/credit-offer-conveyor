package org.shrek.services.impl;

import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import com.shrek.model.PassportInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shrek.Feign.ConveyorFeignClient;
import org.shrek.models.Application;
import org.shrek.models.Client;
import org.shrek.repository.ApplicationRepository;
import org.shrek.repository.ClientRepository;
import org.shrek.repository.CreditRepository;
import org.shrek.services.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.shrek.services.impl.loanapplreqdtoconfig.LoanApplicationRequestDTOLoanOfferDTOInitializer.initLoanOffer;
import static org.shrek.services.impl.loanapplreqdtoconfig.LoanApplicationRequestDTOLoanOfferDTOInitializer.loanAppReqInit;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {
    @Mock
    ApplicationRepository applicationRepository;

    @Mock
    ClientRepository clientRepository;

    @Mock
    CreditRepository creditRepository;

    @Mock
    ConveyorFeignClient offersClient;

    @Autowired
    DealService dealService;

    @Test
    @DisplayName("")
    void createListOffersByFeignClientTest() {

        LoanApplicationRequestDTO input = loanAppReqInit(BigDecimal.valueOf(100000), 12, LocalDate.parse("1985-01-29"));


        List<LoanOfferDTO> output = new ArrayList<>();
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(100000.00), 12, BigDecimal.valueOf(9601.70), BigDecimal.valueOf(27.00), false, false));
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(100000.00), 12, BigDecimal.valueOf(9553.00), BigDecimal.valueOf(26.00), false, true));
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(103000.00), 12, BigDecimal.valueOf(9456.00), BigDecimal.valueOf(24.00), true, false));
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(103000.00), 12, BigDecimal.valueOf(9407.70), BigDecimal.valueOf(24.00), true, true));

        when(offersClient.createOffers(isA(LoanApplicationRequestDTO.class))).thenReturn(ResponseEntity.ok(output));
        assertEquals(output, dealService.createListOffersByFeignClient(input));

    }

    /* @Test
     @DisplayName("")
     void saveApplicationTest() {
         LoanApplicationRequestDTO input = loanAppReqInit(BigDecimal.valueOf(100000), 12, LocalDate.parse("1985-01-29"));


         Client client = new Client();
         client.setId(123456L);
         Optional<Client> optionalClient = Optional.of(client);
         clientRepository.save(client);
         Optional<Client> findClient = clientRepository.findById(optionalClient.get().getId());

         assertEquals(optionalClient, findClient);
     }
 */
    @Test
    void getAppChangeStatusTest() {
        //DealService service = new DealServiceImpl(applicationRepository, clientRepository, creditRepository, offersClient);
        LoanOfferDTO testLoanOfferDTO = initLoanOffer(123L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(103000.00), 12, BigDecimal.valueOf(9407.70), BigDecimal.valueOf(24.00), true, true);

        PassportInfo clientsPassport = new PassportInfo();

        clientsPassport.setPassportSeries("1234");
        clientsPassport.setPassportNumber("123456");

        Client testClient = new Client();
        testClient.setFirstName("Vasiliy");
        testClient.setLastName("Pupkin");
        testClient.setMiddleName("Aristarkhovich");
        testClient.setBirthDate(LocalDate.parse("1985-01-29"));
        testClient.setEmail("non_of_your_business@mail.ru");
        testClient.setPassport(clientsPassport);

        Application clientApplication = new Application();
        clientApplication.setClient(testClient);
        clientApplication.setCreationDate(LocalDateTime.now());
        // clientApplication.setAppliedOffer(testLoanOfferDTO);

        // service.getAppChangeStatus(testLoanOfferDTO);
    }

    @Test
    void formScoringData() {
    }
}