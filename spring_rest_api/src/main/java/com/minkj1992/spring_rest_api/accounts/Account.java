package com.minkj1992.spring_rest_api.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {
    @Id @GeneratedValue
    private Integer id;

    private String email;

    private String password;

    // 가져올 데이터가 적기도 하고, 거의 모두 필요하니 EAGER 세팅한다.
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
