package com.megait.soir.user;

import com.megait.soir.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SignUpValidator implements Validator {

    @Autowired
    private MemberRepository memberRepository;

    // * Validator -> original Spring Framework interface
    // supports, validate를 override해줘야 함.

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
        // paramerter가 SignUpForm의 class type을 소화할 수 있는지 검증
    }

    /*
    SignUpForm의 유효성 검사
    default : SignUpForm에 선언한 Annotation 내용
              @NotBlank, @Length, @Pattern 등의 annotation을 검사
    +) validate() method run(annotation으로 진행할 수 없는 추가적인 검사)
       DB를 거쳐야 하는 검사인 경우 validate()를 활용한다.
     */
    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) target;

        if(memberRepository.existsByEmail(signUpForm.getEmail())){
            errors.rejectValue("email", // filed : 문제가 있는 field의 이름
                    "duplicated.email", // errorCode : customizing(code를 보고 view에서 판단)
                    new Object[]{signUpForm.getEmail()}, // MessageFormat에 binding할 error messages
                    "using email address. please enter another"); // defaultMessage
        }
    }
}
