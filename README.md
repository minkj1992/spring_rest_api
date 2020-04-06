# 1. Spring RESTful Api
> `Spring`을 활용한 RESTful(`Self-descriptive`, `HATEOAS`)한 API 생성


<!-- TOC -->

- [1. Spring RESTful Api](#1-spring-restful-api)
  - [1.1. 기술 스택](#11-%ea%b8%b0%ec%88%a0-%ec%8a%a4%ed%83%9d)
- [2. 개발 목표](#2-%ea%b0%9c%eb%b0%9c-%eb%aa%a9%ed%91%9c)
- [3. 개발 과정](#3-%ea%b0%9c%eb%b0%9c-%ea%b3%bc%ec%a0%95)
  - [3.1. 개발환경 세팅](#31-%ea%b0%9c%eb%b0%9c%ed%99%98%ea%b2%bd-%ec%84%b8%ed%8c%85)
  - [3.2. `Event` Domain 구현](#32-event-domain-%ea%b5%ac%ed%98%84)
  - [3.3. 이벤트 생성 API](#33-%ec%9d%b4%eb%b2%a4%ed%8a%b8-%ec%83%9d%ec%84%b1-api)
    - [3.3.1. EventController Test `createEvent()` 생성](#331-eventcontroller-test-createevent-%ec%83%9d%ec%84%b1)
    - [3.3.2. `EventController createEvent()` 생성](#332-eventcontroller-createevent-%ec%83%9d%ec%84%b1)
    - [3.3.3. 입력값 제한하기](#333-%ec%9e%85%eb%a0%a5%ea%b0%92-%ec%a0%9c%ed%95%9c%ed%95%98%ea%b8%b0)
    - [3.3.4. 입력값 이외에 에러발생](#334-%ec%9e%85%eb%a0%a5%ea%b0%92-%ec%9d%b4%ec%99%b8%ec%97%90-%ec%97%90%eb%9f%ac%eb%b0%9c%ec%83%9d)
    - [3.3.5. Bad Request 처리](#335-bad-request-%ec%b2%98%eb%a6%ac)
      - [3.3.5.1. 입력값 Empty](#3351-%ec%9e%85%eb%a0%a5%ea%b0%92-empty)
      - [3.3.5.2. 입력값 Wrong value](#3352-%ec%9e%85%eb%a0%a5%ea%b0%92-wrong-value)
    - [3.3.6. TEST CODE Description 추가](#336-test-code-description-%ec%b6%94%ea%b0%80)
    - [3.3.7. Bad Request 응답](#337-bad-request-%ec%9d%91%eb%8b%b5)
    - [3.3.8. 비즈니스 로직 추가](#338-%eb%b9%84%ec%a6%88%eb%8b%88%ec%8a%a4-%eb%a1%9c%ec%a7%81-%ec%b6%94%ea%b0%80)
    - [3.3.9. 비즈니스 로직 테스트 리펙토링](#339-%eb%b9%84%ec%a6%88%eb%8b%88%ec%8a%a4-%eb%a1%9c%ec%a7%81-%ed%85%8c%ec%8a%a4%ed%8a%b8-%eb%a6%ac%ed%8e%99%ed%86%a0%eb%a7%81)
  - [3.4. `HATEOAS`와 `Self-Describtive Message` 적용](#34-hateoas%ec%99%80-self-describtive-message-%ec%a0%81%ec%9a%a9)
    - [3.4.1. `HATEOAS` 적용](#341-hateoas-%ec%a0%81%ec%9a%a9)
    - [3.4.2. `REST Docs` 적용](#342-rest-docs-%ec%a0%81%ec%9a%a9)

<!-- /TOC -->







## 1.1. 기술 스택
- `Spring boot`
- `Spring framework`
- `Spring data JPA`
- `Spring HATEOAS`
- `Spring REST Docs`
- `Spring Security OAuth2` 
- `TDD`

# 2. 개발 목표

# 3. 개발 과정

## 3.1. 개발환경 세팅
- `mvn package`
## 3.2. `Event` Domain 구현
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
## 3.3. 이벤트 생성 API
### 3.3.1. EventController Test `createEvent()` 생성
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

### 3.3.2. `EventController createEvent()` 생성
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

### 3.3.3. 입력값 제한하기
> 입력값 중 id, price와 같은 필드들은 입력을 받아 update 되면 안된다.

- DTO 생성
- `ModelMapper`를 통한 builder 패턴 간소화
    - mvn 의존성 가져오기
- `Matchers.not()`을 통한 setId, setStatus, .... 이 테스트에서 통과되지 않는지 검사
    - `Matchers`가 deprecated되었다.
    - @TODO: Matchers를 대체하여 테스트하는 방법 찾기

### 3.3.4. 입력값 이외에 에러발생
> 입력값 중 id, price와 같은 필드들은 입력을 받아 update 되면 에러를 발생시킨다.
```java
public void createEvent_Bad_Request() throws Exception {
        ...
}
```
- `application.properties`
    - `spring.jackson.deserialization.fail-on-unknown-properties=true`

### 3.3.5. Bad Request 처리
> 입력값이 이상한 경우에 Bad Request를 보내는 방법

#### 3.3.5.1. 입력값 Empty
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
#### 3.3.5.2. 입력값 Wrong value
- `EventValidator`클래스 생성
- Controller 검증 코드 추가
- 테스트 추가

### 3.3.6. TEST CODE Description 추가
> 테스트 코드가 길어지니 테스트 description을 추가한다.
- junit5는 이렇게 하지 않아도 되지만, 4에서는 다르게 한다.
```java
@Target(ElementType.METHOD) // method에 대해 검증
@Retention(RetentionPolicy.SOURCE)    //보유,유지: life cycle how long
public @interface TestDescription {
    String value();
}
```

### 3.3.7. Bad Request 응답
> Bad Request 응답에 body가 있도록 한다.
- controller에 bad request시 `.build()` 대신 `.body()`사용

- **java Bean은 `BeanSerializer`에 의해서 json으로 변환이 가능하지만, SpringFramework의 Errors는 bean규약을 따르지 않아 따로 serializer를 생성해주어야 한다.**
- `/common/ErrorSerializer`
    - Error를 json으로 변환시켜 전달해준다.
```java
@JsonComponent  //Spring의 ObjectMapper에 등록
public class ErrorSerializer extends JsonSerializer<Errors> {

    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ...
    }
}
```
- **ObjectMapper는 Spring의 Errors 타입을 Serialize할 때 `ErrorSerializer`를 사용한다.**

- 응답값
```json

String parseJS eval
[
{
"field":"endEventDateTime",
"objectName":"eventDto",
"code":"WrongValue",
"defaultMessage":"endEventDateTime is Wrong",
"rejected":"2000-12-14T11:11"
},
{
"objectName":"eventDto",
"code":"WrongPrices",
"defaultMessage":"Values of Prices are Wrong"
}
]
```

### 3.3.8. 비즈니스 로직 추가

- Event Domain Test 추가
    - `testFree()`
    - `testOffline()`
- Domain `update()` 추가
```java
    public void update() {
        free = basePrice == 0 && maxPrice == 0;
        offline = !(location == null || location.isBlank());    //null 검사 먼저 주의
    }
```
### 3.3.9. 비즈니스 로직 테스트 리펙토링
> 테스트 코드의 중복 해결 방법 -> `JUnitParams`

```java
    @Test
    public void testFree(int basePrice, int maxPrice, boolean isFree) throws Exception {}
    private Object[] parametersForTestFree() {}
```
위와 같은 parameter 함수가 있다면

```java
    @Parameters
    @Parameters(method = "parametersForTestFree")
    @Test
    public void testFree(int basePrice, int maxPrice, boolean isFree) throws Exception {}
```
- 위의 @Parameters 중 아무거 써도 결과가 같다. 
- parametersFor가 convention이여서 찾아준다.


## 3.4. `HATEOAS`와 `Self-Describtive Message` 적용


### 3.4.1. `HATEOAS` 적용
- `ResourceSupport` from Spring-HATEOAS
  - HATEOAS의 resource를 제공해주는 class(impl)
  - ver 1.0.2 이후 부터는 `import org.springframework.hateoas.EntityModel;`를 사용한다.

```java
public class EventResource extends EntityModel<Event> {
	
	/*
	 * self link는 resource마다 설정해줘야 하므로 여기에 공통으로 추가한다.
	 */
	public EventResource(Event event, Link... links) {
		super(event,  links);
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
	}
}
```

- `ResourceSupport`는 `@JsonUnwrapped`를 사용하여 BeanSerializer의 json 전략을 회피해주어야 하지만, `EntityModel`는 기본적으로 `@JsonUnwrapped`이 사용된다.

- 최종적으로 아래 3가지를 생성하였다.
  - `self`(view)
  - `update`(수정 링크)
  - `events`(목록으로가는 링크)

### 3.4.2. `REST Docs` 적용


1. `@AutoConfigureRestDocs`를 만들어 놓은 `test class`위에 작성한다.
2. `.andDo(document())`를 활용하여 `snippet`들을 생성해준다.
3. `RestDocsMockMvcConfigurationCustomizer`를 활용해 보기  어려운 snippet들을 커스터마이징 시켜준다.
   1. test 패키지에 클래스를 생성해준다.
   2. `RestDocsMockMvcConfigurationCustomizer`를 return 해주는 함수를 만들어준다.
4. `@Import(RestDocsConfiguration.class)`를 테스트에 넣어주어 클래스를 설정을 불러온다.



