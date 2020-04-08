# 1. Spring RESTful Api
> `Spring`을 활용한 RESTful(`Self-descriptive`, `HATEOAS`)한 API 생성


<!-- TOC -->

- [1. Spring RESTful Api](#1-spring-restful-api)
    - [1.1. 기술 스택](#11-기술-스택)
- [2. 개발 목표](#2-개발-목표)
- [3. 개발 과정](#3-개발-과정)
    - [3.1. 개발환경 세팅](#31-개발환경-세팅)
    - [3.2. `Event` Domain 구현](#32-event-domain-구현)
    - [3.3. 이벤트 생성 API](#33-이벤트-생성-api)
        - [3.3.1. EventController Test `createEvent()` 생성](#331-eventcontroller-test-createevent-생성)
        - [3.3.2. `EventController createEvent()` 생성](#332-eventcontroller-createevent-생성)
        - [3.3.3. 입력값 제한하기](#333-입력값-제한하기)
        - [3.3.4. 입력값 이외에 에러발생](#334-입력값-이외에-에러발생)
        - [3.3.5. Bad Request 처리](#335-bad-request-처리)
            - [3.3.5.1. 입력값 Empty](#3351-입력값-empty)
            - [3.3.5.2. 입력값 Wrong value](#3352-입력값-wrong-value)
        - [3.3.6. TEST CODE Description 추가](#336-test-code-description-추가)
        - [3.3.7. Bad Request 응답](#337-bad-request-응답)
        - [3.3.8. 비즈니스 로직 추가](#338-비즈니스-로직-추가)
        - [3.3.9. 비즈니스 로직 테스트 리펙토링](#339-비즈니스-로직-테스트-리펙토링)
    - [3.4. `HATEOAS`와 `Self-Describtive Message` 적용](#34-hateoas와-self-describtive-message-적용)
        - [3.4.1. `HATEOAS` 적용](#341-hateoas-적용)
        - [3.4.2. `REST Docs` 적용](#342-rest-docs-적용)
        - [3.4.3. 스프링 REST Docs 각종 문서 조각 생성하기](#343-스프링-rest-docs-각종-문서-조각-생성하기)
        - [3.4.4. 스프링 REST Docs 문서 빌드](#344-스프링-rest-docs-문서-빌드)
        - [3.4.5. 테스트용 DB와 설정 분리하기](#345-테스트용-db와-설정-분리하기)
    - [3.5. 이벤트 조회 및 수정 REST API 개발](#35-이벤트-조회-및-수정-rest-api-개발)
        - [3.5.1. 이벤트 목록 조회 API 구현](#351-이벤트-목록-조회-api-구현)
        - [3.5.2. 이벤트 조회 API](#352-이벤트-조회-api)
        - [3.5.3. 이벤트 수정 API](#353-이벤트-수정-api)
        - [3.5.4. 테스트 코드 리팩토링](#354-테스트-코드-리팩토링)
    - [REST API 보안 적용](#rest-api-보안-적용)
        - [Account 도메인 추가](#account-도메인-추가)
        - [스프링 시큐리티 적용](#스프링-시큐리티-적용)
        - [예외 테스트](#예외-테스트)

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

![img](https://lh6.googleusercontent.com/NmIC9arnfnb6khNTpj16g7TE7i5vXl9uN_fZMrwEpJXC8_k1mQNLmrq_GTlbsOzZtNCbgzBzYnhHRVriqTljhip7IXzOPMGNTIfCdOCF8E9I2vZIIk7NLKuaNhG_SlqYDqashA8)

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


### 3.4.3. 스프링 REST Docs 각종 문서 조각 생성하기

1. 요청 헤더 문서화
2. 요청 필드 문서화
3. 응답 헤더 문서화
4. 응답 필드 문서화
5. 링크 문서화
   1. self
   2. query-events
   3. update-event
   

### 3.4.4. 스프링 REST Docs 문서 빌드

1. main/asciidoc/index.html 생성
2. maven plugin 추가
3. mvn package
4. profile 
   1. test에 profile 추가
   2. controller에 link 추가
      - `eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));` 

### 3.4.5. 테스트용 DB와 설정 분리하기

- `docker`
  1. 기본 세팅
     - 도커 설치
       - [ubuntu docker 설치](https://hiseon.me/linux/ubuntu/install-docker/)
     - 사용자 group 추가하기
       - `sudo usermod -aG docker $USER`
       - `echo $USER` 하면 현재 로그인된 아이디가 echo 된다.
     - `sudo chmod 666 /var/run/docker.sock`
       - 권한 부여
  2. 도커로 `PostgreSQL` 컨테이너 실행
     - `docker run --name rest -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres`
```console
minkj1992@minkj1992-900X5L:~$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
06bda7e4d874        postgres            "docker-entrypoint.s…"   3 minutes ago       Up 3 minutes        0.0.0.0:5432->5432/tcp   rest
```
  3. 컨테이너 실행
```console
minkj1992@minkj1992-900X5L:~$ docker exec -i -t rest bash
root@06bda7e4d874:/# 
```
  4. Connect to a database
     - 권한 부여
     - 기본유저와 데이터베이스 이름
```docker
root@06bda7e4d874:/# su - postgres
postgres@06bda7e4d874:~$ 
postgres@06bda7e4d874:~$ psql -d postgres -U postgres
psql (12.2 (Debian 12.2-2.pgdg100+1))
Type "help" for help.

postgres=# 
postgres=# \l
                                 List of databases
   Name    |  Owner   | Encoding |  Collate   |   Ctype    |   Access privileges
   
-----------+----------+----------+------------+------------+--------------------
---
 postgres  | postgres | UTF8     | en_US.utf8 | en_US.utf8 | 
 template0 | postgres | UTF8     | en_US.utf8 | en_US.utf8 | =c/postgres        
  +
           |          |          |            |            | postgres=CTc/postgr
es
 template1 | postgres | UTF8     | en_US.utf8 | en_US.utf8 | =c/postgres        
  +
           |          |          |            |            | postgres=CTc/postgr
es
(3 rows)

postgres=# \dt
Did not find any relations.

```
- spring run
```console

Consider the following:
	If you want an embedded database (H2, HSQL or Derby), please put it on the classpath.
	If you have database settings to be loaded from a particular profile you may need to activate it (no profiles are currently active).

```
test용 config와 운영 상태의 db가 (`H2`,`postgresQL`) 혼용되어 에러 발생
- `./resources/application.properties`
```properties
#postgres 설정
spring.datasource.username=postgres
spring.datasource.password=pass
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# hibernate 설정
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```
하지만 테스트에 `@SpringBootTest`를 해주어서 .properties를 혼용(h2 대신에, postgres 사용)하기 때문에, test와 application용을 분리 시켜준다.
- 테스트 분리
  - intellij 테스트 리소스 설정
  - `./test/resources/application.properties`생성

- `./test/resources/application.properties` -> `./test/resources/application-test.properties` 이름 변경을 해주어 `./resources/application.properties`에서 불필요한 설정만 오버라이딩 해주어 바꿔준다.
```properties
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver

spring.datasource.hikari.jdbc-url=jdbc:h2:mem:testdb

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```
- 설정이 필요한 test 파일에 `@ActiveProfiles("test")` 넣어주기


## 3.5. 이벤트 조회 및 수정 REST API 개발

### 3.5.1. 이벤트 목록 조회 API 구현

- Event 목록 Page 정보와 함께 받기
    - content[0].id 확인
    - pageable 경로 확인
- Sort과 Paging 확인
    - 30개를 만들고, 10개 사이즈로 두번째 페이지 조회하면 이전, 다음 페이지로 가는 링크가 있어야 한다.
    - 이벤트 이름순으로 정렬하기
    - page 관련 링크


- TODO: java.lang.AssertionError: No value at JSON path "_embedded.eventList[0]._links.self"

```java
    private void generateEvent(int i) {
        Event event = Event.builder()
                .name("event" + i)
                .description(i + "th test event")
                .build();
        this.eventRepository.save(event);
    }
```
- `.save`를 해주지 않아서 update가 되지 않았다.

### 3.5.2. 이벤트 조회 API
- 이벤트 1개 생성 후 생성 검사 test
- 이벤트가 없을 경우 404 error 테스트 추가
- 이벤트가 없다면 404 존재한다면 200으로 return 해주는 controller method() 생성


### 3.5.3. 이벤트 수정 API

### 3.5.4. 테스트 코드 리팩토링
- `BaseControllerTest` 생성

## REST API 보안 적용
 
### Account 도메인 추가
- `./accounts/Account`

### 스프링 시큐리티 적용

![img](https://lh6.googleusercontent.com/N8ucQrC7V1oHQRXLZaJW5TTizWtOq4prAeLBQO5i1X7TYFog4pb3dfTE_QO8FU-UNcq3uK5m_Zs4uPWLBFhTkqI50JH4C-gca1EUJ9K564ewKBnqn3h0KNkNj6P4L0I8kdSJ8Po)

### 예외 테스트

