package com.megait.soir.service;

import com.megait.soir.domain.Address;
import com.megait.soir.domain.Item;
import com.megait.soir.domain.Member;
import com.megait.soir.domain.MemberType;
import com.megait.soir.repository.ItemRepository;
import com.megait.soir.repository.MemberRepository;
import com.megait.soir.user.MemberUser;
import com.megait.soir.user.SignUpForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// Controller의 부담을 줄이기 위해 생성.
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder; // password encoding
    private final ItemRepository itemRepository;


    @Transactional
     public EmailCheckStatus processSignUp(String email, String token) {
        Member member = memberRepository.findByEmail(email);

        // member에 값이 할당되지 않은 경우
        if(member == null){
            return EmailCheckStatus.WRONG_EMAIL;
        }

        if(member.getEmailCheckToken().equals(token)){

            if(member.isEmailVerified()){
                return EmailCheckStatus.MODIFIED;
            }

            member.setEmailVerified(true);
            member.setJoinedAt(LocalDateTime.now());

            return EmailCheckStatus.COMPLETE;
        }

        return EmailCheckStatus.WRONG_TOKEN;
    }


    public void sendSignUpEmail(Member member) {
        SimpleMailMessage mailMessage = new SimpleMailMessage(); // mail의 message만을 위한 객체
        mailMessage.setTo(member.getEmail());
        mailMessage.setSubject("Soir: Sign up certification Mail"); // mail title
        mailMessage.setText(
                // 인증 링크를 첨부한다.
                "/check-email-token?email=" + member.getEmail()
                        + "&token=" + member.getEmailCheckToken()
        );

        // 해당 email로 메일 보내기
        javaMailSender.send(mailMessage);
    }


    public Member createNewMember(SignUpForm signUpForm) {
        Member member = Member.builder()
                .email(signUpForm.getEmail())
                .password(signUpForm.getPassword())
                .address(Address.builder()
                        .zipcode(signUpForm.getZipcode())
                        .city(signUpForm.getCity())
                        .street(signUpForm.getStreet())
                        .build())
                .type(MemberType.USER)
                .build();

        // email 검증 token 생성 -> DB에 저장
        member.generateEmailCheckToken();
        member.encodePassword(passwordEncoder); // pw를 인코딩하여 저장

        memberRepository.save(member);
        return member;
    }

    public void autologin(Member member) {
        MemberUser memberUser = new MemberUser(member);
        // Certification Token create
        // SecurityContext는 인증 유저(현재 login status인 user들의 정보)를 관리한다.
        // user의 구별은 AuthenticationToken을 통해 이루어진다.
        // Token 구조 : username, password, role
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        memberUser, // username
                        memberUser.getMember().getPassword(), // password
                        memberUser.getAuthorities() // authorities(data type : Collection)
                );

        // SecurityContext에 token 저장
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(token); // 인증 완료
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username);

        if(member == null){
            throw new UsernameNotFoundException(username);
        }

        return new MemberUser(member);
    }

    public void sendResetPasswordEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if(member == null){
            throw new IllegalArgumentException("잘못된 이메일");
        }
        // TODO 이메일 재전송 코드 리팩토링
        // 이메일 검증 링크 생성
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(member.getEmail());  // 수신자
        mailMessage.setSubject("My Store - 비밀번호 재생성 메일");  // 제목
        mailMessage.setText(
                "/reset-password?email=" + member.getEmail()
                        + "&token=" + member.getEmailCheckToken()
        );

        // 이메일 보내기
        javaMailSender.send(mailMessage);
    }


    public boolean checkEmailToken(String email, String token) {
        if(email == null || token == null){
            return false;
        }

        Member member = memberRepository.findByEmail(email);

        if(member == null){
            return false;
        }

        return token.equals(member.getEmailCheckToken());
    }

    @Transactional
    public void resetPassword(String email, String password) {
        Member member = memberRepository.findByEmail(email);
        member.setPassword(password);
        member.encodePassword(passwordEncoder); //encoding하여 password set
    }

    @Transactional
    public boolean addLike(Member member, Long itemId) {
        if (member == null){
            throw new UsernameNotFoundException("wrong user");
        }

        Item item = itemRepository.findById(itemId).orElse(null);
        
        // itemId의 유효성 판별
        if(item == null){
            throw new IllegalArgumentException("wrong item info!");
        }

        // member는 detach 상태 -> Repo를 통해 Select문으로 한 번 조회해야 한다.
        member = memberRepository.getOne(member.getId()); // detach -> persist 상태로 변환된다.
        List<Item> likeList = member.getLikes();

        if(likeList.contains(item)){
            likeList.remove(item);
            return false;
        }

        likeList.add(item);
        return true;
    }

    public List<Item> getLikeList(Member member) {
        return memberRepository.findByEmail(member.getEmail()).getLikes();
    }
}
