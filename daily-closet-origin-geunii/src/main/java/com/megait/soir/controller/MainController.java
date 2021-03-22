package com.megait.soir.controller;

import com.google.gson.JsonObject;
import com.megait.soir.form.CodyForm;
import com.megait.soir.domain.*;
import com.megait.soir.repository.MemberRepository;
import com.megait.soir.service.*;
import com.megait.soir.user.CurrentUser;
import com.megait.soir.user.SignUpForm;
import com.megait.soir.user.SignUpValidator;
import com.megait.soir.user.UpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;


@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final SignUpValidator signUpValidator;
    private final MemberService memberService;
    private final ItemService itemService;
    private final MemberRepository memberRepository;
    private final OrderService orderService;
    private final SendEmailService sendEmailService;
    private final CodyService codyService;
    private final ReviewService reviewService;

//    @GetMapping("/") // root context가 들어오면 index page를 보여준다.
//    public String index(@CurrentUser Member member, Model model) {
//
//        if (member != null) {
//            model.addAttribute("member", member);
//        }
//        model.addAttribute("albumList", itemService.getItemList());
//        model.addAttribute("bookList", itemService.getItemList());
//        model.addAttribute("title", "Soir.");
//        return "/view/index";
//    }
    // 재우
    @GetMapping("/") // root context가 들어오면 index page를 보여준다.
    public String index(@CurrentUser Member member, Model model, String keyword, String searchType){

    if(member != null){
        model.addAttribute("member", member);
    }

    model.addAttribute("title", "Soir.");

    //common_fragments 에서 SearchType 받아서 처리 (검색)
    if(keyword != null){
        if(searchType.equals("item_all"))
            model.addAttribute("clothsList", itemService.findByAllKeyword(keyword));
        else if(searchType.equals("item_name"))
            model.addAttribute("clothsList", itemService.findByNameKeyword(keyword));
        else if(searchType.equals("brand_name"))
            model.addAttribute("clothsList", itemService.findByBrandKeyword(keyword));
    }
    else{
        model.addAttribute("clothsList", itemService.getItemList());

    }
    return "/view/index";
    }
/////////

    // 회원정보 조회
    @GetMapping("/memberInfo")
    public String findMember(@CurrentUser Member member, Model model) {
        model.addAttribute(member);
        return "/view/memberInfo";
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 회원정보 수정 Get
    @GetMapping("/update-memberInfo")
    public String update(@CurrentUser Member member, Model model) {
        model.addAttribute(member);
        model.addAttribute(new UpdateForm());
        return "/view/update-memberInfo";
    }
    // 회원정보 수정 Post
    @PostMapping("/update-memberInfo") // post 요청 시 실행되는 메소드 -> 즉 회원가입 form 작성 시 수행된다.
    public String updateSubmit(@CurrentUser Member member, @Valid UpdateForm updateForm) {

        memberService.updateMember(member, updateForm);
        return "redirect:/"; // root로 redirect
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ajax

    /**
     * 회원탈퇴
     * @param id
     * @return
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public Map<String, Object> delete(@PathVariable Long id) {
        memberService.delete(id);
        System.out.println("======================================================컨트롤러==============================");

        Map<String, Object> json = new HashMap<>();
        json.put("status", 200);
        json.put("id", id);
        return json;
    }

    /**
     * 카테고리 아이템 조회
     * @param itemRequest
     * @param model
     * @return
     */
    @GetMapping("/itemList")
    public String itemList(ItemRequest itemRequest ,Model model) {
        String categoryName = itemRequest.getCategoryName();
        Pageable pageable = itemService.getPageable(itemRequest);
        if (itemRequest.getCategoryName().equals("best")) {
            model.addAttribute("itemList", itemService.getBestItemList());
        } else {
            model.addAttribute("itemList", itemService.getParentCategoryItemList(categoryName, pageable));
        }
        // <h2 th:text="This is ${title} Page."><h2>
        model.addAttribute("title", categoryName);
        return "/view/category/category";
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute(new SignUpForm()); // 빈 DTO를 view에게 전달?
        return "/view/signup";
    }

    @PostMapping("/signup") // post 요청 시 실행되는 메소드 -> 즉 회원가입 form 작성 시 수행된다.
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {

        if (errors.hasErrors()) { // annotation error가 발생 시 error가 담김 -> errors의 error 포함 여부로 판단.
            log.info("validation error occur!");
            return "/view/signup";
        }

        signUpValidator.validate(signUpForm, errors); // 여기서 이메일 유효성 검증
        log.info("check validation complete!");

        Member member = memberService.createNewMember(signUpForm);
        memberService.sendSignUpEmail(member);
        memberService.autologin(member); // 해당 member를 자동 로그인 하기

        return "redirect:/"; // root로 redirect
    }


    @GetMapping("/check-email-token")
    public String checkEmailToken(String email, String token, Model model) {
        EmailCheckStatus status = memberService.processSignUp(email, token);

        switch (status) {
            case WRONG_EMAIL:
                model.addAttribute("error", "wrong email.");
                break;

            case WRONG_TOKEN:
                model.addAttribute("error", "wrong token.");
                break;

            case COMPLETE:
                break;

            case MODIFIED:
                model.addAttribute("error", "already verified account.");
                break;
        }

        model.addAttribute("email", email);
        return "/view/check-email-result";
    }


    @GetMapping("/login")
    public String login() {

        return "/view/login";
    }


    @GetMapping("/send-reset-password-link")
    public String sendResetPasswordView() {

        return "/view/send-reset-password-link";
    }


    @PostMapping("/send-reset-password-link")
    public String sendResetPassword(String email, Model model) {
        // 해당 email로 '/reset-password?email=이메일&token=토큰' 형태의 문장을 email로 전송

        try {
            memberService.sendResetPasswordEmail(email);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error_code", "password.reset.failed");
            return "/view/notify";
        }

        model.addAttribute("email", email);
        model.addAttribute("result_code", "password.reset.send");

        return "/view/notify";
    }


    @GetMapping("/reset-password")
    public String resetPasswordView(String email, String token, Model model) {
        // email과 token 무결성 체크
        boolean result = memberService.checkEmailToken(email, token);

        model.addAttribute("result", result);
        model.addAttribute("email", email);

        return "/view/reset-password"; // 비밀번호 수정창으로 이동

    }


    @PostMapping("/reset-password")
    public String resetPassword(String email, String password, Model model) {
        // 패스워드 변경 실행
        memberService.resetPassword(email, password);
        model.addAttribute("result_code", "password.reset.complete");

        // 자동 로그인 실행

        return "/view/notify";
    }


    @GetMapping("/store/detail/{id}")
    public String detail(@CurrentUser Member member, @PathVariable Long id, Model model) {
        log.info("id : " + id);

        Item item = itemService.findItem(id);

        model.addAttribute("like_status", false);
        if (member != null) {
            member = memberRepository.findByEmail(member.getEmail());
            model.addAttribute("like_status", member.getLikes().contains(item));
        }
        model.addAttribute("item", item);
        model.addAttribute("currentUser",member);

        System.out.println();

        return "/view/detail";
    }

    @GetMapping("/store/like")
    @ResponseBody
    public String addLike(@CurrentUser Member member, @RequestParam("id") Long itemId) {

        boolean result = false;

        // JSON 객체를 만든다.
        JsonObject jsonObject = new JsonObject();

        //사용자의 likes (Member 엔티티의 likes)에 해당 상품을 추가

        try {
            result = memberService.addLike(member, itemId);
            // 찜 목록 추가
            if (result) {
                jsonObject.addProperty("message", "찜 목록에 추가하였습니다");
            }
            // 찜 목록 삭제
            else {
                jsonObject.addProperty("message", "찜 목록에서 삭제되었습니다");
            }
            jsonObject.addProperty("status", result);
        } catch (IllegalArgumentException e) {
            jsonObject.addProperty("message", "잘못된 정보입니다");
        } catch (UsernameNotFoundException e) {

        }
        return jsonObject.toString();
    }


    @GetMapping("/store/like-list")
    public String likeList(@CurrentUser Member member, Model model) {
        List<Item> likeList = memberService.getLikeList(member);
        model.addAttribute("likeList", likeList);
        return "/view/like-list";
    }


    @PostMapping("/cart/list")
    public String addCart(@RequestParam("item_id") String[] itemId, @CurrentUser Member member, Model model) {

        // itemId parameter를 Long type으로 변환한다.
        // ["20", "30"] -> [20L, 30L]
        Long[] idArr = Arrays.stream(itemId).map(Long::parseLong).toArray(Long[]::new);
        List<Long> itemIdList = List.of(idArr);

        orderService.addCart(member, itemIdList);

        return cartList(member, model);
    }


    @GetMapping("/cart/list")
    public String cartList(@CurrentUser Member member, Model model) {
        // view page 구현
        try {
            List<OrderItem> cartList = orderService.getCart(member);
            model.addAttribute("cartList", cartList);
            model.addAttribute("totalPrice", orderService.getTotalPrice(cartList));

        } catch (IllegalArgumentException e) {
            model.addAttribute("error_message", e.getMessage());
        }

        return "/view/cart";
    }

    @PostMapping("/find-pw")
    @ResponseBody
    // @EventListener(ApplicationReadyEvent.class)
    public String find_pw_form(String email) throws Exception {
        log.info("aaa");

        String uuid = null;
        for (int i = 0; i < 5; i++) {
            uuid = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
            uuid = uuid.substring(0, 10); //uuid를 앞에서부터 10자리 잘라줌.
            System.out.println(i + ") " + uuid);
        }


        sendEmailService.sendEmail(email, "회원님의 임시비밀번호는 : " + uuid + " 입니다.", "Daily Closet 에서 임시 비밀번호 발급 ");
        memberService.resetPassword(email, uuid);

        JsonObject json = new JsonObject();
        json.addProperty("status", 200);
        return json.toString();

    }

    @GetMapping("/cody")
    public String cody(@CurrentUser Member member, Model model) {

        model.addAttribute(new CodyForm());



        List<Item> likeList = memberService.getLikeList(member);
        List<Item> top = new ArrayList<>();
        List<Item> outer = new ArrayList<>();
        List<Item> bottom = new ArrayList<>();
        List<Item> acc = new ArrayList<>();
        List<Item> shoes = new ArrayList<>();

        System.out.println("오잉!!!!!!!!"+likeList.get(0).getBrand());

        for(int i = 0; i<likeList.size(); i++){
            if(likeList.get(i).getParentCategory().getName().equals("상의")){
                top.add(likeList.get(i));
                System.out.println(likeList.get(i).getParentCategory().getName());
            }
            if(likeList.get(i).getParentCategory().getName().equals("아우터")){
                outer.add(likeList.get(i));
                System.out.println(likeList.get(i).getParentCategory().getName());
            }
            if(likeList.get(i).getParentCategory().getName().equals("바지")){
                bottom.add(likeList.get(i));
                System.out.println(likeList.get(i).getParentCategory().getName());
            }
            if(likeList.get(i).getParentCategory().getName().equals("신발")||likeList.get(i).getParentCategory().getName().equals("스니커즈") ){
                shoes.add(likeList.get(i));
                System.out.println(likeList.get(i).getParentCategory().getName());
            }
            if(likeList.get(i).getParentCategory().getName().equals("가방")){
                acc.add(likeList.get(i));
                System.out.println(likeList.get(i).getParentCategory().getName());
            }
        }
        model.addAttribute("topList", top);
        model.addAttribute("outerList", outer);
        model.addAttribute("bottomList", bottom);
        model.addAttribute("accList", acc);
        model.addAttribute("shoesList", shoes);

        System.out.println("찜리스트--------->" + likeList.toString());
        return "/view/cody";
    }

    @PostMapping("/cody")
    public String codySubmit(@CurrentUser Member member, @Valid CodyForm codyForm, @Valid long topId) {

        Cody cody = codyService.createNewCody(member,codyForm);

        return "redirect:/cody"; // root로 redirect

    }

//    @PostMapping("/review")
//    public String review(@CurrentUser Member member,  @RequestParam("id") Long itemId, @RequestParam("parentId")long parentId, @RequestParam("title")String title, @RequestParam("content") String content){
//        Item item = itemService.findItem(itemId);
//        reviewService.createNewReview(member,item,parentId,title,content);
//
//        return "redirect:/store/detail?{itemId}";
//    }

    @PostMapping("/review")
    public String review(Long itemId, String content) throws Exception{
        Item item = itemService.findItem(itemId);
        reviewService.createNewReview(item,content);

        return "redirect:/store/detail?{itemId}";
    }

}
