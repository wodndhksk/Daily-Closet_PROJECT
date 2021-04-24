package com.megait.soir.user;

import com.megait.soir.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UpdateValidator implements Validator {
    @Autowired
    private MemberRepository memberRepository;
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(UpdateForm.class);
        // paramerter가 SignUpForm의 class type을 소화할 수 있는지 검증
    }
    @Override
    public void validate(Object target, Errors errors) {
        UpdateForm updateForm = (UpdateForm) target;

    }
}
