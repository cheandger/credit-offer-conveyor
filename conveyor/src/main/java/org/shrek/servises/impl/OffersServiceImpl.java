package org.shrek.servises.impl;

import com.shrek.model.LoanApplicationRequestDTO;
import com.shrek.model.LoanOfferDTO;
import org.shrek.exceptions.ParametersValidationException;
import org.shrek.servises.OffersService;
import org.shrek.validators.LoanApplicationRequestDTOValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.shrek.util.CalculationUtil.calculateIsInsuranceCaseTotalAmount;
import static org.shrek.util.CalculationUtil.calculateMonthlyPayment;

@Service
@PropertySource("config_properties.yaml")
public class OffersServiceImpl implements OffersService {

    private static final Logger log = LoggerFactory.getLogger(OffersServiceImpl.class);

    private final BigDecimal BASE_RATE;
    private final BigDecimal INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE;

    public OffersServiceImpl(@Value("${BASE_RATE}") BigDecimal BASE_RATE,
                             @Value("${INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE}") BigDecimal INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE) {

        this.BASE_RATE = BASE_RATE;
        this.INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE = INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE;
    }

    @Override
    public List<LoanOfferDTO> createOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) throws ParametersValidationException {

        DataBinder dataBinder = new DataBinder(loanApplicationRequestDTO);
        dataBinder.addValidators(new LoanApplicationRequestDTOValidator());
        dataBinder.validate();
        if (dataBinder.getBindingResult().hasErrors()) {
            ObjectError objectError = dataBinder.getBindingResult().getAllErrors().get(0);
            log.info("Проверка валидности входных данных");
            throw new ParametersValidationException(objectError.getDefaultMessage());
        }

        List<LoanOfferDTO> loanOfferDTOList = new ArrayList<>();
        log.info("Создание вариантов предварительных кредитных предложений");
        loanOfferDTOList.add(createLoanOffer(loanApplicationRequestDTO, false, false));
        loanOfferDTOList.add(createLoanOffer(loanApplicationRequestDTO, false, true));
        loanOfferDTOList.add(createLoanOffer(loanApplicationRequestDTO, true, false));
        loanOfferDTOList.add(createLoanOffer(loanApplicationRequestDTO, true, true));

        log.info("Формирование списка возможных вариантов кредитных предложений завершено");

        Comparator<LoanOfferDTO> rateComparator = Comparator.comparing(LoanOfferDTO::getRate);
        loanOfferDTOList.sort(Collections.reverseOrder(rateComparator));

        log.info("Список возможных вариантов кредитных предложений " + loanOfferDTOList);
        return loanOfferDTOList;
    }

    @Override
    public LoanOfferDTO createLoanOffer(@NotNull LoanApplicationRequestDTO loanApplicationRequestDTO,
                                        @NotNull Boolean isInsuranceEnabled, @NotNull Boolean isSalaryClient) {

        BigDecimal totalAmount = calculateIsInsuranceCaseTotalAmount(loanApplicationRequestDTO.getAmount(),
                isInsuranceEnabled, loanApplicationRequestDTO.getTerm(), INSURANCE_RATE_IF_IS_INSURANCE_ENABLED_TRUE);

        log.info("Расчет актуального размера тела кредита");

        BigDecimal finalRate = BASE_RATE;

        if (isSalaryClient) {
            finalRate = finalRate.subtract(BigDecimal.valueOf(1));
        }
        if (isInsuranceEnabled) {
            finalRate = finalRate.subtract(BigDecimal.valueOf(3));
        }

        log.info("Завершен расчет актуального размера кредитной ставки: " + finalRate + " % ");

        BigDecimal monthlyPayment = calculateMonthlyPayment(loanApplicationRequestDTO.getAmount(),
                loanApplicationRequestDTO.getTerm(), finalRate);


        return new LoanOfferDTO()
                .requestedAmount(loanApplicationRequestDTO.getAmount())
                .totalAmount(totalAmount)
                .term(loanApplicationRequestDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(finalRate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient);
    }


}






