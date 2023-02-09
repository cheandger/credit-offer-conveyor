package org.shrek.services.impl.loanapplreqdtoconfig;

import com.shrek.model.EmploymentDTO;
import com.shrek.model.FinishRegistrationRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinishRegistrationRequestDTOInitializer {

    public static FinishRegistrationRequestDTO initFinishRegistrationRequestDTO(
            String marStatus, Integer dependAmount, EmploymentDTO employment) {
        return new FinishRegistrationRequestDTO()
                .gender(FinishRegistrationRequestDTO.GenderEnum.valueOf("MALE"))
                .maritalStatus(FinishRegistrationRequestDTO.MaritalStatusEnum.valueOf(marStatus))
                .dependentAmount(dependAmount)
                .passportIssueDate(LocalDate.ofEpochDay(2005 - 5 - 5))
                .passportIssueBranch("Центральным РОВД")
                .employment(employment)
                .account("shrek");


    }

    public static EmploymentDTO initialEmploymentDTO(String empStatus, String position, BigDecimal salary) {
        return new EmploymentDTO()
                .employmentStatus(EmploymentDTO.EmploymentStatusEnum.valueOf(empStatus))
                .employerINN("1112222333")
                .salary(salary)
                .position(EmploymentDTO.PositionEnum.valueOf(position))
                .workExperienceTotal(12)
                .workExperienceCurrent(24);

    }


}
