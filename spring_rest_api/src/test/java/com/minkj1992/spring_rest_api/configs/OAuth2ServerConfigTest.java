package com.minkj1992.spring_rest_api.configs;

import com.minkj1992.spring_rest_api.accounts.Account;
import com.minkj1992.spring_rest_api.accounts.AccountRepository;
import com.minkj1992.spring_rest_api.accounts.AccountRole;
import com.minkj1992.spring_rest_api.accounts.AccountService;
import com.minkj1992.spring_rest_api.common.BaseControllerTest;
import com.minkj1992.spring_rest_api.common.TestDescription;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OAuth2ServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @Before
    //https://pupupee9.tistory.com/89
    public void setUp () {
        this.accountRepository.deleteAll();
    }

    @Test
    @TestDescription("인증 토큰을 발급받는 테스트")
    public void getAuthToken() throws Exception {

        //given
        String username = "minkj1992@gmail.com";
        String password = "minkj1992";
        Account minkj1992 = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(minkj1992);

        String clientId = "myapp";
        String clientSecret = "pass";
        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
        //when

        //then
    }
}