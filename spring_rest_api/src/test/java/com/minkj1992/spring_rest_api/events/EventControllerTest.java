package com.minkj1992.spring_rest_api.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;

import static org.mockito.AdditionalMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createEvent() throws Exception {
        //given
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(1992, 07, 24, 11, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(1992, 07, 24, 11, 11))
                .beginEventDateTime(LocalDateTime.of(2018, 12, 13, 11, 11))
                .endEventDateTime(LocalDateTime.of(2018, 12, 14, 11, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("Seoul Spring Shop")
                .build();

        //then
        mockMvc.perform(post("/api/events")    // 앞 뒤로 /막아주어야 한다.
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))   // 상수들 모음
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
//                        .andExpect(jsonPath("id").value(Matchers.isNull(AdditionalMatchers.and(100))))    // 100이 아닌지 검사
//                        .andExpect(jsonPath("free").value(Matchers.not(true)))    // free가 아닌지 검사
    }

    @Test
    public void createEvent_Bad_Request() throws Exception {
        //given
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(1992, 07, 24, 11, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(1992, 07, 24, 11, 11))
                .beginEventDateTime(LocalDateTime.of(2018, 12, 13, 11, 11))
                .endEventDateTime(LocalDateTime.of(2018, 12, 14, 11, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("Seoul Spring Shop")
                .eventStatus(EventStatus.PUBLISHED)
                .free(true)
                .build();

        //then
        mockMvc.perform(post("/api/events")    // 앞 뒤로 /막아주어야 한다.
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}