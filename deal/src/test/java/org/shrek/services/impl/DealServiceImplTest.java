package org.shrek.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shrek.model.*;
import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.shrek.model.ApplicationStatus.APPROVED;
import static com.shrek.model.ApplicationStatus.CC_APPROVED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.shrek.models.CreditStatus.CALCULATED;
import static org.shrek.services.impl.loanapplreqdtoconfig.ClientInitializer.initClient;
import static org.shrek.services.impl.loanapplreqdtoconfig.CreditDtoInitializer.initCreditDto;
import static org.shrek.services.impl.loanapplreqdtoconfig.CreditInstanceInitializer.initCredit;
import static org.shrek.services.impl.loanapplreqdtoconfig.FinishRegistrationRequestDTOInitializer.initFinishRegistrationRequestDTO;
import static org.shrek.services.impl.loanapplreqdtoconfig.FinishRegistrationRequestDTOInitializer.initialEmploymentDTO;
import static org.shrek.services.impl.loanapplreqdtoconfig.LoanApplicationRequestDTOLoanOfferDTOInitializer.initLoanOffer;
import static org.shrek.services.impl.loanapplreqdtoconfig.LoanApplicationRequestDTOLoanOfferDTOInitializer.loanAppReqInit;
import static org.shrek.services.impl.loanapplreqdtoconfig.ScoringDataDtoInitializer.initialScoringDataDTO;

@SpringBootTest

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {
    @MockBean
    ApplicationRepository applicationRepository;
    @MockBean
    ClientRepository clientRepository;
    @MockBean
    CreditRepository creditRepository;
    @MockBean
    ConveyorFeignClient offersClient;

    @Mock
    ClientMapper clientMapper;
    @Mock
    CreditMapper creditMapper;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    ClientFromFinishRegMapper clientFromFinishRegMapper;
    @Mock
    ScoringDataFromClientMapper scoringDataFromClientMapper;

    @Autowired
    DealService dealService;

    @Test
    @DisplayName("Creation the list of loan offers test")
    void createListOffersByFeignClientTest() {

        LoanApplicationRequestDTO input = loanAppReqInit(BigDecimal.valueOf(100000), 12, LocalDate.parse("1985-01-29"));

        List<LoanOfferDTO> output = new ArrayList<>();
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(100000.00), 12, BigDecimal.valueOf(9601.70), BigDecimal.valueOf(27.00), false, false));
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(100000.00), 12, BigDecimal.valueOf(9553.00), BigDecimal.valueOf(26.00), false, true));
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(103000.00), 12, BigDecimal.valueOf(9456.00), BigDecimal.valueOf(24.00), true, false));
        output.add(initLoanOffer(1L, BigDecimal.valueOf(100000.00), BigDecimal.valueOf(103000.00), 12, BigDecimal.valueOf(9407.70), BigDecimal.valueOf(24.00), true, true));

        when(offersClient.createOffers(isA(LoanApplicationRequestDTO.class))).thenReturn(ResponseEntity.ok(output));
        assertEquals(output, dealService.createListOffersByFeignClient(input));
        verify(clientRepository, times(1)).save(any(Client.class));

    }

    @Test
    @DisplayName("Exception test by creating of loan offers list")
    void getExceptionListOffersByFeignClientTest() {

        LoanApplicationRequestDTO input = loanAppReqInit(BigDecimal.valueOf(100000), 12, LocalDate.parse("1985-01-29"));

        when(offersClient.createOffers(input)).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        assertThrows(BusinessException.class, () -> {
            dealService.createListOffersByFeignClient(input);
        }, "The response is not empty.");

    }

    @Test
    @DisplayName("Test the clientMapper From LoanApplicationRequestDto ")
    void checkClientMapperTest() throws JsonProcessingException, JSONException {

        LoanApplicationRequestDTO input = loanAppReqInit(BigDecimal.valueOf(100000), 12, LocalDate.parse("1985-01-29"));

        String client = objectMapper.writeValueAsString(clientMapper.loanApplicationRequestDtoToClient(input));
        Client clientForTest = initClient(input.getFirstName(), input.getLastName(), input.getBirthDate(), null, null,
                null, new PassportInfo().passportNumber(input.getPassportNumber()).passportSeries(input.getPassportSeries()), null, null);
        String clientForTestString = objectMapper.writeValueAsString(clientForTest);
        JSONAssert.assertEquals(clientForTestString, client, true);
    }

    @Test
    @DisplayName("Test the clientMapper From FinishReq Dto")
    void checkClientFromFinishReqMapperTest() throws JsonProcessingException, JSONException {
        EmploymentDTO employmentDTO = initialEmploymentDTO("EMPLOYED", "WORKER", BigDecimal.valueOf(60000));
        FinishRegistrationRequestDTO input = initFinishRegistrationRequestDTO("MARRIED", 1, employmentDTO);

        String client = objectMapper.writeValueAsString(clientFromFinishRegMapper.clientFromFinishRegistration(input));
        Client clientForTest = initClient(null, null, null, String.valueOf(input.getGender()), String.valueOf(input.getMaritalStatus()),
                input.getDependentAmount(), new PassportInfo().passportIssueDate(input.getPassportIssueDate()).passportIssueBranch(input.getPassportIssueBranch()),
                input.getEmployment(), input.getAccount());

        String clientForTestString = objectMapper.writeValueAsString(clientForTest);
        JSONAssert.assertEquals(clientForTestString, client, true);
    }

    @Test
    @DisplayName("Test the creditMapper From CreditDto")
    void checkCreditFromCreditDtoTest() throws JsonProcessingException, JSONException {

        ArrayList<PaymentScheduleElement> list = new ArrayList<PaymentScheduleElement>();

        list.add(new PaymentScheduleElement()
                .date(LocalDate.parse("2003-12-14"))
                .debtPayment(BigDecimal.valueOf(7256.23))
                .interestPayment(BigDecimal.valueOf(2500.00))
                .remainingDebt(BigDecimal.valueOf(91760.00)));

        CreditDTO creditDTO = initCreditDto(BigDecimal.valueOf(10000), 12, BigDecimal.valueOf(9417.32),
                BigDecimal.valueOf(26.00), BigDecimal.valueOf(25.98), true, false, list);

        String credit = objectMapper.writeValueAsString(creditMapper.creditDtoToCredit(creditDTO));
        Credit creditForTest = initCredit(creditDTO.getAmount(), creditDTO.getTerm(), creditDTO.getMonthlyPayment(),
                creditDTO.getRate(), creditDTO.getPsk(), creditDTO.getIsInsuranceEnabled(),
                creditDTO.getIsSalaryClient(), creditDTO.getPaymentSchedule());

        String clientForTestString = objectMapper.writeValueAsString(creditForTest);
        JSONAssert.assertEquals(clientForTestString, credit, true);
    }

    @Test
    @DisplayName("Test the ScoringDataMapper From Credit")
    void checkScoringDataFromClientMapperTest() throws JsonProcessingException, JSONException {

        Client client = initClient("Вася", "Пупкин", LocalDate.parse("1985-01-29"),
                "MALE", "MARRIED", 1, new PassportInfo().passportNumber("123456")
                        .passportSeries("1234").passportIssueDate(LocalDate.parse("2005-05-05")).passportIssueBranch("Центральным РОВД"),
                initialEmploymentDTO("EMPLOYED", "WORKER", BigDecimal.valueOf(60000)), "shrek");


        String scoringData = objectMapper.writeValueAsString(scoringDataFromClientMapper.scoringDataDtoFromClient(client));
        ScoringDataDTO scoringDataForTest = initialScoringDataDTO(null, null, client.getFirstName(),
                client.getLastName(), ScoringDataDTO.GenderEnum.fromValue(client.getGender()), client.getBirthDate(),
                ScoringDataDTO.MaritalStatusEnum.fromValue(client.getMaritalStatus()), client.getDependentAmount(),
                client.getEmployment(), null, null);

        String clientForTestString = objectMapper.writeValueAsString(scoringDataForTest);
        JSONAssert.assertEquals(clientForTestString, scoringData, true);
    }

    @Test
    @DisplayName("Change application status Test")
    void getAppChangeStatusApprovedTest() {

        LoanOfferDTO testLoanOfferDTO = initLoanOffer(123L, BigDecimal.valueOf(100000.00),
                BigDecimal.valueOf(103000.00), 12, BigDecimal.valueOf(9407.70),
                BigDecimal.valueOf(24.00), true, true);

        when(applicationRepository.findById(123L)).thenReturn(Optional.of(new Application().setStatusHistory(new ArrayList<>())));
        dealService.getAppChangeStatusAfterApplying(testLoanOfferDTO);

        verify(applicationRepository, times(1)).save(argThat((application) ->
                APPROVED.equals(application.getStatus())
        ));
    }

    @Test
    @DisplayName("Change application status Test")
    void getAppChangeStatusCCApprovedTest() {

        LoanOfferDTO testLoanOfferDTO = initLoanOffer(123L, BigDecimal.valueOf(100000.00),
                BigDecimal.valueOf(103000.00), 12, BigDecimal.valueOf(9407.70),
                BigDecimal.valueOf(24.00), true, true);

        when(applicationRepository.findById(123L)).thenReturn(Optional.of(new Application().setStatusHistory(new ArrayList<>())));
        dealService.getAppChangeStatusAfterApplying(testLoanOfferDTO);

        verify(applicationRepository, times(1)).save(argThat((application) ->
                APPROVED.equals(application.getStatus())
        ));
    }


    @Test
    @DisplayName("Creation the scoringDataDto and clientPost test. Checking the entities inner values ")
    void formScoringData() {

        EmploymentDTO testEmploymentDTO = initialEmploymentDTO("EMPLOYED", "WORKER", BigDecimal.valueOf(50000));
        FinishRegistrationRequestDTO testFinishDTO = initFinishRegistrationRequestDTO("MARRIED", 1, testEmploymentDTO);
        Client testClient = initClient("Вася", "Пупкин", LocalDate.parse("1985-01-29"),
                "MALE", "MARRIED", 1, new PassportInfo().passportSeries("1234")
                        .passportNumber("123456").passportIssueDate(LocalDate.parse("2005-05-05"))
                        .passportIssueBranch("Центральным РОВД"), testEmploymentDTO, "shrek");
        LoanOfferDTO testLoanOfferData = initLoanOffer(123L, BigDecimal.valueOf(100000), BigDecimal.valueOf(103000),
                12, BigDecimal.valueOf(9450), BigDecimal.valueOf(26), true, false);

        ScoringDataDTO testScoringData = initialScoringDataDTO(testLoanOfferData.getTotalAmount(),
                12, testClient.getFirstName(), testClient.getLastName(),
                ScoringDataDTO.GenderEnum.fromValue(String.valueOf(testFinishDTO.getGender())), testClient.getBirthDate(),
                ScoringDataDTO.MaritalStatusEnum.fromValue(testClient.getMaritalStatus()),
                testClient.getDependentAmount(), testClient.getEmployment(), testLoanOfferData.getIsInsuranceEnabled(),
                testLoanOfferData.getIsSalaryClient());
        testScoringData.term(testLoanOfferData.getTerm()).amount(testLoanOfferData.getRequestedAmount())
                .isSalaryClient(testScoringData.getIsSalaryClient()).isInsuranceEnabled(testLoanOfferData.getIsInsuranceEnabled());

        List<PaymentScheduleElement> list = new ArrayList<>();
        list.add(new PaymentScheduleElement()
                .number(1)
                .date(LocalDate.now())
                .totalPayment(BigDecimal.valueOf(9450))
                .interestPayment(BigDecimal.valueOf(2189))
                .debtPayment(BigDecimal.valueOf(7261))
                .remainingDebt(BigDecimal.valueOf(93550)));

        CreditDTO testCreditDto = initCreditDto(testLoanOfferData.getTotalAmount(), testScoringData.getTerm(),
                BigDecimal.valueOf(9400), testLoanOfferData.getRate(), BigDecimal.valueOf(25.88),
                testLoanOfferData.getIsSalaryClient(), testLoanOfferData.getIsInsuranceEnabled(), list);

        when(applicationRepository.findById(123L)).thenReturn(Optional.of(getMockApp()));
        when(offersClient.calculate(any(ScoringDataDTO.class))).thenReturn(ResponseEntity.ok(testCreditDto));

        dealService.formScoringData(123L, testFinishDTO);

        verify(clientRepository, times(1)).save(argThat((client) ->
                testEmploymentDTO.equals(client.getEmployment())));

        verify(applicationRepository, times(1)).save(argThat((application) ->
                CC_APPROVED.equals(application.getStatus())));
        verify(creditRepository, times(1)).save(argThat((credit) ->
                CALCULATED.equals(credit.getCreditStatus())));
    }

    @Test
    @DisplayName("Exception test by creating of loan offers list")
    void getExceptionFormScoringDataTest() {
        EmploymentDTO testEmploymentDTO = initialEmploymentDTO("EMPLOYED", "WORKER", BigDecimal.valueOf(50000));
        FinishRegistrationRequestDTO testFinishDTO = initFinishRegistrationRequestDTO("MARRIED", 1, testEmploymentDTO);
        ScoringDataDTO testScoringData = initialScoringDataDTO(BigDecimal.valueOf(103000),
                12, "Вася", "Пупкин",
                ScoringDataDTO.GenderEnum.fromValue("MALE"), LocalDate.parse("1985-01-29"),
                ScoringDataDTO.MaritalStatusEnum.fromValue("MARRIED"),
                1, testEmploymentDTO, true,
                false);

        when(offersClient.calculate(any(ScoringDataDTO.class))).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        when(applicationRepository.findById(123L)).thenReturn(Optional.of(getMockApp()));
        assertThrows(BusinessException.class, () -> {
            dealService.formScoringData(123L, testFinishDTO);
        }, "The response is not empty.");
    }

    private Application getMockApp() {
        EmploymentDTO testEmploymentDTO = initialEmploymentDTO("EMPLOYED", "WORKER", BigDecimal.valueOf(50000));
        Client testClient = initClient("Вася", "Пупкин", LocalDate.parse("1985-01-29"),
                "MALE", "MARRIED", 1, new PassportInfo().passportSeries("1234")
                        .passportNumber("123456").passportIssueDate(LocalDate.parse("2005-05-05"))
                        .passportIssueBranch("Центральным РОВД"), testEmploymentDTO, "shrek");
        LoanOfferDTO testLoanOfferData = initLoanOffer(123L, BigDecimal.valueOf(100000), BigDecimal.valueOf(103000),
                12, BigDecimal.valueOf(9450), BigDecimal.valueOf(26), true, false);

        return new Application().setId(123L).setClient(testClient).setCredit(null)
                .setStatus(CC_APPROVED).setCreationDate(LocalDateTime.now()).setAppliedOffer(testLoanOfferData)
                .setSignDate(null).setSesCode(123456L).setStatusHistory(new ArrayList<>());
    }

}