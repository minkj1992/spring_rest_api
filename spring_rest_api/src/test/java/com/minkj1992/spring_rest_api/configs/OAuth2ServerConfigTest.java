package com.minkj1992.spring_rest_api.configs;

import com.minkj1992.spring_rest_api.accounts.Account;
import com.minkj1992.spring_rest_api.accounts.AccountRole;
import com.minkj1992.spring_rest_api.common.BaseTest;
import com.minkj1992.spring_rest_api.common.TestDescription;
import org.junit.Test;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OAuth2ServerConfigTest extends BaseTest {

    @Test
    @TestDescription("인증 토큰을 발급받는 테스트")
    public void getAuthToken() throws Exception {
        String userName = "authAdmin@email.com";
        String userPassword = "password";
        // Given
		Account account = Account.builder()
				.email(userName)
				.password(userPassword)
				.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
				.build();
		this.accountService.saveAccount(account);
        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", userName)
                .param("password", userPassword)
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }
}