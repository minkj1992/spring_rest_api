package com.minkj1992.spring_rest_api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minkj1992.spring_rest_api.accounts.AccountRepository;
import com.minkj1992.spring_rest_api.accounts.AccountService;
import com.minkj1992.spring_rest_api.events.EventRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Ignore
public class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected AccountService accountService;

    @Autowired
    protected AppProperties appProperties;

    @Autowired
    protected EventRepository eventRepository;

    @Autowired
    protected AccountRepository accountRepository;

    @Before
    //https://pupupee9.tistory.com/89
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

}
