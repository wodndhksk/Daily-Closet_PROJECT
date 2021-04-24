package com.megait.soir.service;

import com.megait.soir.domain.*;
import com.megait.soir.repository.CodyRepository;
import com.megait.soir.repository.ItemRepository;
import com.megait.soir.repository.MemberRepository;
import com.megait.soir.user.MemberUser;
import com.megait.soir.user.SignUpForm;
import com.megait.soir.user.UpdateForm;
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
import java.util.Optional;

// Controller의 부담을 줄이기 위해 생성.
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder; // password encoding
    private final ItemRepository itemRepository;
    private final CodyRepository codyRepository;

    // 회원정보 수정
    @Transactional
    public Member updateMember(Member member, UpdateForm updateForm) {
        Member member1 = memberRepository.getOne(member.getId());
        member1.getAddress().setZipcode(updateForm.getZipcode());
        member1.getAddress().setCity(updateForm.getCity());
        member1.getAddress().setStreet(updateForm.getStreet());
        return member1;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ajax

    // 회원탈퇴
    @Transactional
    public void delete(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("해당 아이디가 없습니다. id=" + id));
        // memberRepository.delete(posts)
        // JpaRepository에서 이미 delete 메소드를 지원하고 있으니 이를 활용함.
        // 엔티티를 파라미터로 삭제할 수도 있고, dleteById 메소드를 이용하면
        // id로 삭제할 수 있음.
        // 존재하는 Member 인지 확인을 위해 엔티티 조회 후 그대로 삭제함.
        System.out.println("=============================================================================서비스==============================================");
        memberRepository.delete(member);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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


    @Transactional
    public boolean addCodyLike(Member member, Long codyId) {
        if (member == null){
            throw new UsernameNotFoundException("wrong user");
        }
        Optional<Cody> optional = codyRepository.findById(codyId);
        Cody cody = optional.get();

        // codyId의 유효성 판별
        if(cody == null){
            throw new IllegalArgumentException("wrong cody info!");
        }

        // member는 detach 상태 -> Repo를 통해 Select문으로 한 번 조회해야 한다.
        member = memberRepository.getOne(member.getId()); // detach -> persist 상태로 변환된다.
        List<Cody> codyLikeList = member.getCodyLikes();

        if(codyLikeList.contains(cody)){
            codyLikeList.remove(cody);
            return false;
        }

        codyLikeList.add(cody);
        return true;
    }
}
