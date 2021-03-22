package com.megait.soir.configuration;

import com.megait.soir.domain.Member;
import com.megait.soir.domain.MemberType;
import com.megait.soir.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;

@Profile("dev")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DevConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void createTestUser(){
        Member member = Member.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("1111"))
                .type(MemberType.USER)
                .build();

//        member.generateEmailCheckToken();
        memberRepository.save(member);
        log.info("TestUser(" + member.getEmail() + ") has been created.");
    }
}
