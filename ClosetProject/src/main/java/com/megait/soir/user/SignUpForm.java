package com.megait.soir.user;

/*
signup.html의 form 내용을 SignUpForm DTO로 포장 --> Validator 객체에 전달
즉, 유효성 검증을 위한 DTO
따라서 physical DB에 들어가지 않음

*Lombok @Data annotation => data 역할을 할 class에게 주어짐
자동 getter, setter 생성
 */

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SignUpForm {

    // fields

    @NotBlank
    @Length(min = 5, max = 40)
    @Email
    private String email;

    @NotBlank
    @Length(min = 4, max = 18)
    private String password;

    @NotBlank
    private String agreePolicy;

    private String zipcode;
    private String city;
    private String street;
}
