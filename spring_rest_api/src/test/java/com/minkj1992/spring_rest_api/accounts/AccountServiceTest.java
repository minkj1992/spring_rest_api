package com.minkj1992.spring_rest_api.accounts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void findByUserName() throws Exception {
        //given
        String password = "minkj1992";
        String userName = "minkj1992@gmail.com";
        Account account = Account.builder()
                .email(userName)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountRepository.save(account);
        //when
        UserDetailsService userDetailsService = this.accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        //then
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

}