package com.minkj1992.spring_rest_api.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventsTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() throws Exception {
        //given
        String name = "Event";
        String description = "Spring";
        //when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        //then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    public void testFree() throws Exception {

        // 1
        //given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();
        //when
        event.update();

        //then
        assertThat(event.isFree()).isTrue();

        // 2
        //given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();
        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();

        // 3
        //given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();
        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();
    }
    
    @Test
    public void testOffline() throws Exception{
        //given
        Event event = Event.builder()
                .location("코로나 free zone")
                .build();
        //when
        event.update();

        //then
        assertThat(event.isOffline()).isTrue();

        //given
        event = Event.builder()
                .build();
        //when
        event.update();

        //then
        assertThat(event.isOffline()).isFalse();
    }
}