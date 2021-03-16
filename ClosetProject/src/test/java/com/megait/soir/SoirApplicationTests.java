package com.megait.soir;

import com.megait.soir.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SoirApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	@DisplayName("회원 가입 화면 테스트")
	void signupForm() throws Exception{
		mockMvc.perform(get("/signup"))
				.andExpect(status().isOk())
				.andExpect(view().name("/view/signup"));
//				.andExpect(model().attributeExists("signupForm"));
	}


}
