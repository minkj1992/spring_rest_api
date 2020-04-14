package com.minkj1992.spring_rest_api.events;

import com.minkj1992.spring_rest_api.accounts.Account;
import com.minkj1992.spring_rest_api.accounts.CurrentUser;
import com.minkj1992.spring_rest_api.common.ErrorResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {
        // empty value Request
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // wrong value Request
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update(); // TODO service
        event.setManager(currentUser);
        Event newEvent = this.eventRepository.save(event);  // TODO service

        // hateoas 링크 추가
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity<?> queryEvents(Pageable pageable,
                                         PagedResourcesAssembler<Event> assembler,
                                         @CurrentUser Account account) {
        Page<Event> page = this.eventRepository.findAll(pageable);

        //EventResource
        var pageResources = assembler.toModel(page, e -> new EventResource(e));
        pageResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        if (account != null) {
            pageResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(pageResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                   @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if (event.getManager().equals(currentUser)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {

        //없는 event 수정 요청
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 데이터 형식 잘못 맞춰졌을 경우
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // 비즈니스 로직 상에서 에러 경우
        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // 온전한 update 이벤트는 save 요청
        Event existingEvent = optionalEvent.get();
        if (!(existingEvent.getManager().equals(currentUser))) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        this.modelMapper.map(eventDto, existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);

        // 리소스화 시켜서 HATEOAS
        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity<ErrorResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorResource(errors));
    }
}
