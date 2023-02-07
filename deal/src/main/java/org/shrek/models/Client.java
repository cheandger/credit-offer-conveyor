package org.shrek.models;

import com.shrek.model.EmploymentDTO;
import com.shrek.model.PassportInfo;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;


@Entity(name = "client")
@Data

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id", nullable = false)
    private Long id;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "email")
    private String email;
    @Column(name = "gender")
    private String gender;
    @Column(name = "marital_status")
    private String maritalStatus;
    @Column(name = "dependent_amount")
    private Integer dependentAmount;
    @Column(name = "passport_info")
    @Type(type = "jsonb")
    private PassportInfo passport;
    @Column(name = "employment_info")
    @Type(type = "jsonb")
    private EmploymentDTO employment;
    @Column(name = "account")
    private String account;
}


