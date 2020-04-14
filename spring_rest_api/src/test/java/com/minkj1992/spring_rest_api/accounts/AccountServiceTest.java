package com.minkj1992.spring_rest_api.accounts;

import com.minkj1992.spring_rest_api.common.AppProperties;
import com.minkj1992.spring_rest_api.common.BaseTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest extends BaseTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername() throws Exception {
        //given

        Account account = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.saveAccount(account);
        //when
        UserDetailsService userDetailsService = this.accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(appProperties.getUserUsername());
        //then
        assertThat(passwordEncoder.matches(appProperties.getUserPassword(),userDetails.getPassword())).isTrue();
    }

    @Test
    public void findByUsernameFail() throws Exception {
        // expected
        String username = "random@email.com";
        expectedException.expect(UsernameNotFoundException.class);  //뒤에 두면 동작 안함, 먼저 예측해야한다.
        expectedException.expectMessage(Matchers.containsString(username));

        // when
        accountService.loadUserByUsername(username);

    }

}