package com.minkj1992.spring_rest_api.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {

    public EventResource(Event event, Link... links) {
        super(event, links);
        //add self link
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());

    }
}
