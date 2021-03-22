package com.megait.soir.domain;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Member {

    @Id @GeneratedValue
    private long id;

    private String email;

    private String password;

    @Embedded //다른 Entity(external entity)를 내장함을 의미한다.
    private Address address;

    // 회원가입 시간(이메일 인증을 완료한 시간)
    private LocalDateTime joinedAt;

    private boolean emailVerified;

    private String emailCheckToken; // email 인증 시 필요한 email Token value

    @Enumerated(EnumType.STRING)
    private MemberType type;

    @OneToMany // Member : Item => 1 : N Relationship(단방향)
    private List<Item> likes = new ArrayList<>();
    // convention : NullPointerException 방지

    @OneToMany(mappedBy = "member") // Orders와 1:N 양방향
    private List<Orders> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Cody> codies = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Review> reviews = new ArrayList<>();

    @Transactional
    public void generateEmailCheckToken() {
        /*
        @Transactional
        persistence Context 사용 시 transcation 위에서 수행해야 한다.
        따라서 JpaRepository를 상속받은 interface를 통해 수정하지 않고 값을 변경할 경우 해당 annotation을 사용한다.
        이 경우 method 시작 시점에 transaction이 begin되고, exception이 나지 않을 경우 commit된다(exception 발생 시 rollback)
         */

        //email token value 생성
        emailCheckToken = UUID.randomUUID().toString();
    }

    @Transactional
    public void encodePassword(PasswordEncoder passwordEncoder){
        password = passwordEncoder.encode(password);
    }
}
