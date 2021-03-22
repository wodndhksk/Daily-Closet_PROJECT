package com.megait.soir.user;

import lombok.Data;

@Data
public class UpdateForm {
//    @NotBlank
//    @Email
//    @Length(min =5, max=40)
//    private String email;
//
//    @NotBlank
//    private String password;

    private String zipcode;
    private String city;
    private String street;

//    @NotBlank
//    private String agreeTermsOfService;
}

