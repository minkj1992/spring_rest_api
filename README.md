# Spring RESTful Api
> `Spring`을 활용한 RESTful(`Self-descriptive`, `HATEOAS`)한 API 생성

## 기술 스택
- `Spring boot`
- `Spring framework`
- `Spring data JPA`
- `Spring HATEOAS`
- `Spring REST Docs`
- `Spring Security OAuth2` 
- `TDD`

# 개발 목표

# 개발 과정

## 1. 개발환경 세팅
- `mvn package`
## 2. `Event` Domain 구현
- `Event` 클래스 생성
- Test
    - `@Builder`를 활용해서 테스트 케이스 생성
    ```java
        @Test
        public void builder() {
            Event event = Event.builder().build();
        }
    ```
    - `@EqualsAndHashCode(of="id")`
        - id 값만으로 EqualsAndHashCode를 비교하라 라는 의미
        - 필요하다면 다른 필드를 더 추가할 수도 있지만 다른 Entity와의 묶음을 만드는 것은 좋지 않음
        - Equals 와 HashCode를 구현할 때 모든 필드를 기본적으로 다 사용함
        - 나중에 Entity 간에 연관관계가 있을 때 **상호참조**하는 관계가 되면 `EqualsAndHashCode`를 구현한 코드 안에서 서로간의 메소드를 계속 호출하다가 스택오버플로우가 발생할 수도 있음

- `EqualsAndHashCode()`
```java
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Event)) {
            return false;
        } else {
            Event other = (Event)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label139: {
                    Object this$name = this.getName();
                    Object other$name = other.getName();
                    if (this$name == null) {
                        if (other$name == null) {
                            break label139;
                        }
                    } else if (this$name.equals(other$name)) {
                        break label139;
                    }

                    return false;
                }

                Object this$description = this.getDescription();
                Object other$description = other.getDescription();
                if (this$description == null) {
                    if (other$description != null) {
                        return false;
                    }
                } else if (!this$description.equals(other$description)) {
                    return false;
                }
                
                
                (중략 ....)
                
                
                    }
                }
            }
        }
    }
```
- 이처럼 모든 field에 대하여 Equals를 구현해버린다.
- `@EqualsAndHashCode(of="id")`
```java

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Event)) {
            return false;
        } else {
            Event other = (Event)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id != null) {
                        return false;
                    }
                } else if (!this$id.equals(other$id)) {
                    return false;
                }

                return true;
            }
        }
    }

```
## 3. 이벤트 생성 API
### 1. EventController Test `createEvent()` 생성
- 
    - 
    ```java
        @Test
        public void 입력값_JSON_201응답() throws Exception {
            //given
            mockMvc.perform(post("/api/events/")    // "/"로 앞,뒤 막아준다.
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON))
                    .andExpect(status().isCreated());

    ```
    - `HAL_JSON`
        - `HyperText Application Language`
            - HAL은 API의 리소스들 사이에 쉽고 일관적인 하이퍼링크를 제공하는 방식이다. API 설계시 HAL을 도입하면 API간에 쉽게 검색이 가능하다. 
            - HAL을 API Response 메시지에 적용하면 그 메시지가 JSON 포맷이건 XML 포맷이건 API를 쉽게 찾을 수 있는 메타 정보들을 포함시킬 수 있다는 것이다. 
    - `MockHttpServletRequestBuilder accept`(java.lang.String… mediaTypes) : ‘Accept’ 헤더를 설정해줍니다.
    - `Accept 헤더`
        - 요청을 보낼 때 서버에 이런 타입(`MIME`)의 데이터를 보내줬으면 좋겠다고 명시할 때 사용
        - Accept: image/png, image/gif
        - Accept: text/*
        - **Accept로 원하는 형식을 보내면, 서버가 그에 맞춰 보내주면서 응답 헤더의 Content를 알맞게 설정한다.**

### 2. `EventController createEvent()` 생성
- 
    - `import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;`
        - Deprecated: use `WebMvcLinkBuilder` instead.
        - `import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;`
    - `ResponseEntity`
        - HTTP response 객체 생성
        - HTTP의 표준 규약을 지켜서 Response 하게 해준다
        - 이를 통해 client는 서버의 응답에 신뢰를 가진다.
        - `Entity.created(URI).build()` 하면 ResponseEntity가 생성된다.

```java
    @PostMapping("/api/events")
    public ResponseEntity createEvent() {
        URI createdUri = linkTo(methodOn(EventController.class).createEvent()).slash("{id}").toUri();
        return ResponseEntity.created(createdUri).build();
    }
```
- `HATEOAS`를 통해서 class의 method를 불러주고 뒤에 `/{id}`를 붙여준 URI를 만든다.
- 이후 ResponseEntity를 통해 URi를 create()시킨 builer를 .build()시켜준다. 

테스트를 돌려보면 응답값을 볼 수 있다.
```json

MockHttpServletRequest:
      HTTP Method = POST
      Request URI = /api/events/
       Parameters = {}
          Headers = [Content-Type:"application/json", Accept:"application/hal+json"]
             Body = <no character encoding set>
    Session Attrs = {}

Handler:
             Type = com.minkj1992.springrestapi.events.EventController
           Method = com.minkj1992.springrestapi.events.EventController#createEvent()

Async:
    Async started = false
     Async result = null

Resolved Exception:
             Type = null

ModelAndView:
        View name = null
             View = null
            Model = null

FlashMap:
       Attributes = null

MockHttpServletResponse:
           Status = 201
    Error message = null
          Headers = [Location:"http://localhost/api/events/%257Bid%257D"]
     Content type = null
             Body = 
    Forwarded URL = null
   Redirected URL = http://localhost/api/events/%257Bid%257D
          Cookies = []

```
- 참고로 `%257Bid%257D`값은 .slash("{id}")에서 String 객체의 byte코드를 기반으로 /뒤에 해당 값을 넣어준 것같다.

- `.andExpect(jsonPath("id").exists());`
    - Error:(57, 49) java: incompatible types: org.springframework.test.web.client.RequestMatcher cannot be converted to org.springframework.test.web.servlet.ResultMatcher
    - 찾아보니 `client`, `servlet`이렇게 같은 이름으로 여러개가 존재하였다.
- `MediaTypes`
    - **만약 `@RequestMapping(value="/api/events",produces = MediaTypes.HAL_FORMS_JSON_VALUE)`를 controller에 해주고, test에서 `HAL_JSON_VALUE` 해주면 406에러 뜬다.**
    - `Resolved Exception:
             Type = org.springframework.web.HttpMediaTypeNotAcceptableException` 에러가 뜬다.
    - @TODO: 차이가 뭘까?
    - `HAL_JSON_VALUE`
    - `HAL_JSON`
    - `HAL_FORMS_JSON_VALUE` 
    - `MediaType.APPLICATION_JSON`
        - "public constant media type for application/json"
    - `MediaType.APPLICATION_JSON_VALUE`
        -  "String equivalent of MediaType.APPLICATION_JSON"

### 3. 입력값 제한하기
> 입력값 중 id, price와 같은 필드들은 입력을 받아 update 되면 안된다.

- DTO 생성
- `ModelMapper`를 통한 builder 패턴 간소화
    - mvn 의존성 가져오기
- `Matchers.not()`을 통한 setId, setStatus, .... 이 테스트에서 통과되지 않는지 검사
    - `Matchers`가 deprecated되었다.
    - @TODO: Matchers를 대체하여 테스트하는 방법 찾기

### 4. 입력값 이외에 에러발생
> 입력값 중 id, price와 같은 필드들은 입력을 받아 update 되면 에러를 발생시킨다.
```java
public void createEvent_Bad_Request() throws Exception {
        ...
}
```
- `application.properties`
    - `spring.jackson.deserialization.fail-on-unknown-properties=true`

### 5. Bad Request 처리
> 입력값이 이상한 경우에 Bad Request를 보내는 방법

#### 5-1. 입력값 Empty
```java
    @Test
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        //given
        EventDto eventDto = EventDto.builder().build();
        //when then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }
```

- Controller `@Valid`추가
- EventDto `@Min(0)`, `@NotEmpty` .. 추가
- Controller `Errors errors` 파라미터 추가
- COntroller DTO Validation 추가
```java
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

```
#### 5-1. 입력값 Wrong value
- `EventValidator`클래스 생성
- Controller 검증 코드 추가
- 테스트 추가

### 6. TEST CODE Description 추가
> 테스트 코드가 길어지니 테스트 description을 추가한다.
- junit5는 이렇게 하지 않아도 되지만, 4에서는 다르게 한다.
```java
@Target(ElementType.METHOD) // method에 대해 검증
@Retention(RetentionPolicy.SOURCE)    //보유,유지: life cycle how long
public @interface TestDescription {
    String value();
}
```


