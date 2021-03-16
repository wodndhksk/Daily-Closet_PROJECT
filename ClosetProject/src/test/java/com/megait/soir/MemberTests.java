package com.megait.soir;

import com.megait.soir.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberTests {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("회원 가입: 정상적으로 입력 시")
    void signUpSubmit_with_correct_input() throws Exception{
        mockMvc.perform(post("/signup")
                .param("email", "admin@test.com")
                .param("password", "1111")
                .param("street", "")
                .param("city", "")
                .param("zipcode", "")
                .param("agreePolicy", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        // db에 들어갔는지 확인
        assertTrue(memberRepository.existsByEmail("admin@test.com"));

    }

    @Test
    @DisplayName("회원 가입: 비정상적 입력 시")
    void signUpSubmit_with_wrong_input() throws Exception{
        mockMvc.perform(post("/signup")
                .param("email", "admin@test.com")
                .param("password", "1111")
                .param("street", "")
                .param("city", "")
                .param("zipcode", "")
//				.param("agreePolicy", "true") -> 약관 미동의
                .with(csrf()))
                .andExpect(view().name("/view/signup"));
        // 잘못된 입력 시 redirect되지 않고 다시 signup page로 돌아가므로 경로 변경

        // DB에 들어가지 않음을 확인
        assertFalse(memberRepository.existsByEmail("admin@test.com"));

    }

    @Test
    @DisplayName("로그인 성공 Test")
    void loginSubmit_success() throws Exception{

        // given : id, pw
        String email= "test@test.com";
        String password = "1111";

        // when : try to login
        mockMvc.perform(post("/login")
                .param("username", email)
                .param("password", password)
                .with(csrf()))

                // then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated());

    }

    @Test
    @DisplayName("로그인 실패 Test : pw 틀림")
    void loginSubmit_failed_with_password() throws Exception{
        String email= "test@test.com";
        String password = "1111";

        mockMvc.perform(post("/login")
                .param("email", "test@test.com")
                .param("password", "wrong_password")
                .with(csrf()))
                .andExpect(view().name("/login/error")); // login success : redirect to '/login/error'
    }

}
