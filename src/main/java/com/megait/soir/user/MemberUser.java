package com.megait.soir.user;

import com.megait.soir.domain.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/*
Spring security는 인증된(be logined) 사용자 정보는 User(class) 또는 UserDetails(interface) type으로 저장한다.
User, UserDetails는 spring framework가 공식적으로 제공하는 사용자 객체 타입이다.
 */
@Getter @Setter
public class MemberUser extends User {
    private Member member;

    // constructor
    public MemberUser(Member member){
        super(member.getEmail(), member.getPassword(), List.of(member.getType()));
        this.member = member;
    }
}
