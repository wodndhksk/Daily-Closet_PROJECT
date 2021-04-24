package com.megait.soir.controller;

import com.google.gson.JsonObject;
import com.megait.soir.form.CodyForm;
import com.megait.soir.domain.*;
import com.megait.soir.form.ReviewForm;
import com.megait.soir.form.SearchForm;
import com.megait.soir.repository.MemberRepository;
import com.megait.soir.service.*;
import com.megait.soir.user.CurrentUser;
import com.megait.soir.user.SignUpForm;
import com.megait.soir.user.SignUpValidator;
import com.megait.soir.user.UpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
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
    private final WeatherService weatherService;
    private final CityNameService cityNameService;
    private final DateService dateService;

    // 재우
    @GetMapping("/") // root context가 들어오면 index page를 보여준다.
    public String index(@CurrentUser Member member, Model model, String keyword, String searchType, String city){

        model.addAttribute(new SearchForm());

        //////////////////////////////////코디/////////////////////////////////////
        model.addAttribute("codyList",codyService.getAllList());

        model.addAttribute("itemList",itemService.getItemList());


        /////////////////////////////////오늘의 날씨/////////////////////////////////
        String baseDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String meridien = dateService.currentHour();
        if(city != null) {
            String cityName = cityNameService.renameCity(city);
            model.addAttribute("currentTemperature", weatherService.findCurrentDateTemperature(baseDate, cityName, meridien));
            model.addAttribute("weatherList", weatherService.findCurrentLocalWeather(cityName));
            System.out.println("주소가 null 아님");

        }
        else{
            model.addAttribute("currentTemperature", weatherService.findCurrentDateTemperature(baseDate, "서울_인천_경기도", meridien));
            System.out.println("주소가 null일 경우");
        }


        //////////////////////////////////옷 추천/////////////////////////////////////
        //아우터
        Long parent1 = Long.valueOf("12");
        Long child1 = Long.valueOf("29");
        //상의
        Long parent2 = Long.valueOf("3");
        Long child2 = Long.valueOf("2");
        //하의
        Long parent3 = Long.valueOf("31");
        Long child3 = Long.valueOf("36");

        //아우터 가져오기
        model.addAttribute("outer", itemService.findRecommendCategory(parent1, child1));
        // 상의 가져오기
        model.addAttribute("top", itemService.findRecommendCategory(parent2, child2));
        //하의 가져오기
        model.addAttribute("bottom", itemService.findRecommendCategory(parent3, child3));

    return "/view/index";
    }

    /**
     * 도시 날씨 조회
     * @param city
     * @param model
     * @return
     */
    @GetMapping("/weather")
    public String weatherList(Model model, String city) {
        String cityName ="";
        if (city != null) { //NullPointException
            cityName = cityNameService.renameCity(city); //city 값과 cityName 값이 다를 경우에 사용할 것
            model.addAttribute("weatherList", weatherService.findCurrentLocalWeather(cityName));
            //System.out.println(cityName);
            //System.out.println(weatherService.findCurrentLocalWeather(cityName));
        }
        else {


            model.addAttribute("weatherList", weatherService.findAllDesc());
        }

        return "/view/weather";
    }
    /**
     * 도시 날씨 조회
     * @param city
     * @param model
     * @return
     */
    @GetMapping("/weather/weatherList")
    public String weatherCityList(String city, Model model) {
        model.addAttribute("weatherList", weatherService.findByWeatherCity(city));
        return "/view/weather :: #weatherList";
    }

    /**
     * 옷 추천
     * @return
     */
    @GetMapping("/daily-recommend")
    public String daily_recommend(Model model, String city){
        //String weatherCity = "서울_인천_경기도";;

        String baseDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String meridien = dateService.currentHour();
        if(city != null) {
            String cityName = cityNameService.renameCity(city);
            model.addAttribute("currentTemperature", weatherService.findCurrentDateTemperature(baseDate, cityName, meridien));
            model.addAttribute("weatherList", weatherService.findCurrentLocalWeather(cityName));
            System.out.println("주소가 null 아님");

        }
        else{
            model.addAttribute("currentTemperature", weatherService.findCurrentDateTemperature(baseDate, "서울_인천_경기도", meridien));
            System.out.println("주소가 null일 경우");
        }
        // id=3 상의 {자식 id : id=2 후드티셔츠, id=4 맨투맨 id=6 반팔, id=5 기타상의(춘추체육복느낌) , id=7 긴팔티셔츠}

        // id=12 아우터 {자식 id : 29=트레이닝 자켓, 28= 겨울 기타 코트,27 = 트러커 재킷(봄,가을), 26 =롱 패딩/롱 헤비 아우터 ,24 = 플리스/뽀글이 ,
        // 23=슈트/블레이저 재킷(봄,가을), 22 =숏 패딩/숏 헤비 아우터, 21=아노락 재킷(봄/가을) ,20 = 레더/라이더스 재킷(봄/가을) ,
        // 19= 기타 아우터 (봄/가을) ,18 =블루종/MA-1(봄/가을),17 = 베스트(봄/가을, 얇음),15= 나일론/코치 재킷(봄/가을, 얇),
        // 14= 후드 집업 (가을/봄),13=카디건(봄/가을), 11=사파리/헌팅 재킷(봄/가을)}

        // id=31 바지 {자식 id : 36,35,34,33,32,31,30,}


        //아우터 : 봄, 가을
       //상의   : 봄 ,가을= 2,4,5,7 / 여름 = 6 / 겨울 = 2,3,5
       // int temperature = weatherService.WeeklyTemperatureAverage(city);
        //아우터
        Long parent1 = Long.valueOf("12");
        Long child1 = Long.valueOf("29");
        //상의
        Long parent2 = Long.valueOf("3");
        Long child2 = Long.valueOf("2");

        //하의
        Long parent3 = Long.valueOf("31");
        Long child3 = Long.valueOf("36");



//        ArrayList<Long> arr = new ArrayList<>();
//
//        Long fall_spring[] = {Long.valueOf("29"), Long.valueOf("27")
//        ,Long.valueOf("23"),Long.valueOf("21"),Long.valueOf("20")
//                ,Long.valueOf("19"),Long.valueOf("18"),Long.valueOf("17"),Long.valueOf("15"),Long.valueOf("14")
//                ,Long.valueOf("13"),Long.valueOf("11")
//        };
//
//        Long winter[] = {Long.valueOf("27"),Long.valueOf("26"),Long.valueOf("24"),Long.valueOf("22")};
//
//        //겨울온도
//        if(temperature>5){
//            for(int i=0; i< winter.length; i++){
//                arr.add(winter[i]);
//            }
//            //아우터
//            Long parent1 = Long.valueOf("12");
//            Long child1 = Long.valueOf("27");
//            //상의
//            Long parent2 = Long.valueOf("3");
//            Long child2 = Long.valueOf("2");
//            //하의
//            Long parent3 = Long.valueOf("31");
//            Long child3 = Long.valueOf("36");
//            model.addAttribute("outer", itemService.findRecommendCategory(parent1, child1));
//            model.addAttribute("top", itemService.findRecommendCategory(parent2, child2));
//            model.addAttribute("bottom", itemService.findRecommendCategory(parent3, child3));
//        }
//        // 봄,가을 온도
//        else if(temperature>=5 || temperature <20){
//            for(int i=0; i< winter.length; i++){
//                arr.add(winter[i]);
//            }
//            //아우터
//            Long parent1 = Long.valueOf("12");
//            Long child1 = Long.valueOf("27");
//            //상의
//            Long parent2 = Long.valueOf("3");
//            Long child2 = Long.valueOf("2");
//            //하의
//            Long parent3 = Long.valueOf("31");
//            Long child3 = Long.valueOf("36");
//            model.addAttribute("outer", itemService.findRecommendCategory(parent1, child1));
//            model.addAttribute("top", itemService.findRecommendCategory(parent2, child2));
//            model.addAttribute("bottom", itemService.findRecommendCategory(parent3, child3));
//
//        }
//        else if(temperature>=20){
//            for(int i=0; i< winter.length; i++){
//                arr.add(winter[i]);
//            }
//            //아우터
//            Long parent1 = Long.valueOf("12");
//            Long child1 = Long.valueOf("27");
//            //상의
//            Long parent2 = Long.valueOf("3");
//            Long child2 = Long.valueOf("2");
//            //하의
//            Long parent3 = Long.valueOf("31");
//            Long child3 = Long.valueOf("36");
//            model.addAttribute("outer", itemService.findRecommendCategory(parent1, child1));
//            model.addAttribute("top", itemService.findRecommendCategory(parent2, child2));
//            model.addAttribute("bottom", itemService.findRecommendCategory(parent3, child3));
//
//        }

        //아우터 가져오기
        model.addAttribute("outer", itemService.findRecommendCategory(parent1, child1));
       // 상의 가져오기
        model.addAttribute("top", itemService.findRecommendCategory(parent2, child2));
        //하의 가져오기
        model.addAttribute("bottom", itemService.findRecommendCategory(parent3, child3));

      //  System.out.println("현재 온도 : " + temperature);
        return "/view/daily-recommend";
    }
/////////


/////////

    // 회원정보 조회
    @GetMapping("/memberInfo")
    public String findMember(@CurrentUser Member member, Model model) {
        model.addAttribute(member);
        return "/view/memberInfo";
    }

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
    public String itemList(ItemRequest itemRequest, Model model) {
        String categoryName = itemRequest.getCategoryName();
        Pageable pageable = itemService.getPageable(itemRequest);

        Paginator paginator = new Paginator(5, itemRequest.getLimit(), itemService.getCountItemListByCategory(categoryName));
        Map<String, Object> pageInfo = paginator.getFixedBlock(itemRequest.getPage());
//        Map<String, Object> pageInfo = paginator.getElasticBlock(itemRequest.getPage());
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("itemList", itemService.getItemListByCategory(categoryName, pageable));
        model.addAttribute("categoryName", categoryName);

        return "/view/category";
    }


    @GetMapping("/searchList")
    public String searchList(@Valid SearchForm searchForm, Model model) {


        System.out.println("durlsfdsfse");

        if(searchForm.getOption().equals("item_all")){
            model.addAttribute("itemList", itemService.findByAllKeyword(searchForm.getKeyword()));
        }
        else if(searchForm.getOption().equals("item_name")){
            model.addAttribute("itemList", itemService.findByNameKeyword(searchForm.getKeyword()));
        }
        else if(searchForm.getOption().equals("brand_name")){
            model.addAttribute("itemList", itemService.findByBrandKeyword(searchForm.getKeyword()));
        }

        else{
            model.addAttribute("itemList", itemService.getItemList());
        }
        return "/view/category";
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
//        List<Review> reviewList = reviewService.findAll(item);

        model.addAttribute(new ReviewForm());
        model.addAttribute("like_status", false);
        if (member != null) {
            member = memberRepository.findByEmail(member.getEmail());
            model.addAttribute("like_status", member.getLikes().contains(item));
        }
        model.addAttribute("item", item);
        model.addAttribute("currentUser",member);
//        model.addAttribute("reviewList",reviewList);

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
                jsonObject.addProperty("message", "Add like list Complelte!");
            }
            // 찜 목록 삭제
            else {
                jsonObject.addProperty("message", "Delete from like list.");
            }
            jsonObject.addProperty("status", result);
        } catch (IllegalArgumentException e) {
            jsonObject.addProperty("message", "Wrong access.");
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

    @GetMapping("/cart/minus")
    public String cartMinus(@RequestParam("id") String itemId, @CurrentUser Member member, Model model){
        // cart 아이템 삭제

        Long deleteItemId = Long.parseLong(itemId);
        orderService.minusCart(member, deleteItemId);

        return cartList(member, model);
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
        model.addAttribute("member", member);

        System.out.println("상의--------->" + top);
        return "/view/cody";
    }

    @PostMapping("/cody")
    public String codySubmit(@CurrentUser Member member, @Valid CodyForm codyForm) {

        Cody cody = codyService.createNewCody(member,codyForm);

        return "redirect:/cody"; // root로 redirect
    }

    // 새 글 올리기
    @PostMapping("/review")
    public String create(@CurrentUser Member member, @Valid ReviewForm reviewForm) throws Exception {
        log.info("POST /review : " + reviewForm.toString());
        Item item = itemService.findItem(reviewForm.getItemId());
        // 리뷰 수정
        if(reviewForm.getReviewId()!=null){
            Optional<Review> optional = reviewService.findById(reviewForm.getReviewId());
            if(optional!=null){
                Review review = optional.get();
                reviewService.updateReview(review,reviewForm);
                return "redirect:/store/detail/"+reviewForm.getItemId();
            }
        }
        // 리뷰 생성
        reviewService.createReview(member,item,reviewForm);
        return "redirect:/store/detail/"+reviewForm.getItemId();
    }
//
//    // 리뷰 수정
//    @PutMapping("/review")
//    public void modify(Review review, @Valid ReviewForm reviewForm) throws Exception{
//        log.info("PUT data : " + reviewForm.toString());
//        reviewService.update(review, reviewForm);
//    }

    // 리뷰 삭제
    @PostMapping("/review/delete")
    public String deleteReview(@RequestParam("reviewId") Long reviewId, @RequestParam("itemId") Long itemId ){
        log.info("DELETE no : " + reviewId);
        reviewService.deleteReview(reviewId);
        return "redirect:/store/detail/"+itemId;

    }

    @GetMapping("/codyList")
    public String codyList(@CurrentUser Member member, Model model) {

        model.addAttribute("codyList",codyService.getCodyList(member));
        return "/view/codyList";
    }

    @GetMapping("/allCodyList")
    public String allCodyList(@CurrentUser Member member,Model model) {

        model.addAttribute("codyList",codyService.getAllList());

        model.addAttribute("cody_like_status", false);
//        if (member != null) {
//            member = memberRepository.findByEmail(member.getEmail());
//            model.addAttribute("cody_like_status", member.getCodyLikes().contains(cody));
//        }
        return "/view/allCodyList";
    }

    @GetMapping("/cody/like")
    @ResponseBody
    public String addCodyLike(@CurrentUser Member member, @RequestParam("id") Long codyId) {

        boolean result = false;

        JsonObject jsonObject = new JsonObject();

        try {
            result = memberService.addLike(member, codyId);
            // 찜 목록 추가
            if (result) {
                jsonObject.addProperty("message", "Add like list Complelte!");
            }
            // 찜 목록 삭제
            else {
                jsonObject.addProperty("message", "Delete from like list.");
            }
            jsonObject.addProperty("status", result);
        } catch (IllegalArgumentException e) {
            jsonObject.addProperty("message", "Wrong access.");
        } catch (UsernameNotFoundException e) {

        }
        return jsonObject.toString();
    }
}
