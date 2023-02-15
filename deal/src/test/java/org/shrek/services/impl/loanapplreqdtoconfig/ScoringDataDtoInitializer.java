package org.shrek.services.impl.loanapplreqdtoconfig;

import com.shrek.model.EmploymentDTO;
import com.shrek.model.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ScoringDataDtoInitializer {


    public static ScoringDataDTO initialScoringDataDTO(BigDecimal amount, Integer term, String firstName, String lastName,
                                                       ScoringDataDTO.GenderEnum gender, LocalDate birthdate, ScoringDataDTO.MaritalStatusEnum maritalStatus,
                                                       Integer dependentAmount, EmploymentDTO employmentDTO,
                                                       Boolean isInsuranceEnabled, Boolean isSalaryClient) {


        return new ScoringDataDTO()
                .amount(amount)
                .term(term)
                .firstName(firstName)
                .lastName(lastName)
                .gender(gender)
                .birthDate(birthdate)
                .passportSeries("1234")
                .passportNumber("123456")
                .passportIssueDate(LocalDate.of(2005, 5, 5))
                .passportIssueBranch("Центральным РОВД г.Воронеж")
                .maritalStatus(maritalStatus)
                .dependentAmount(dependentAmount)
                .employment(employmentDTO)
                .account("shrek")
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient);
    }

    public static EmploymentDTO initialEmploymentDTO(EmploymentDTO.EmploymentStatusEnum employmentStatus, BigDecimal salary, EmploymentDTO.PositionEnum position,
                                                     Integer workExperienceTotal, Integer workExperienceCurrent) {
        return new EmploymentDTO()
                .employmentStatus(employmentStatus)
                .employerINN("1112222333")
                .salary(salary)
                .position(position)
                .workExperienceTotal(workExperienceTotal)
                .workExperienceCurrent(workExperienceCurrent);

    }


}
