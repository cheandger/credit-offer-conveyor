package org.shrek.services.impl.loanapplreqdtoconfig;

import com.shrek.model.EmploymentDTO;
import com.shrek.model.PassportInfo;
import org.shrek.models.Client;

import java.time.LocalDate;

public class ClientInitializer {

    public static Client initClient(String firstName, String lastName, LocalDate birthDate, String gender,
                                    String marStatus, Integer depAmount, PassportInfo passportInfo, EmploymentDTO employmentDTO, String account) {
        Client client = new Client();
        client.setId(132L);
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setMiddleName("Поликарпович");
        client.setBirthDate(birthDate);
        client.setEmail("non_of_your_business@mail.ru");
        client.setGender(gender);
        client.setMaritalStatus(marStatus);
        client.setDependentAmount(depAmount);
        client.setPassport(passportInfo);
        client.setEmployment(employmentDTO);
        client.setAccount(account);


        return client;
    }
}
