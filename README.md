
# Daily Closet

**기온에 따른 옷을 추천하고, 코디 조합을 미리 볼 수 있는 쇼핑 지원 웹 어플리케이션**

![image](https://user-images.githubusercontent.com/62981623/122635177-609b9d80-d11d-11eb-9d67-fa1a5154e61a.png)


## 개발환경

### - Back-end

Java : JDK 11

Framework : Spring Boot 3.4.1

IDE : IntelliJ

DBMS : h2.119

WAS : Tomcat

Python 3.9

OpenCV – 4.5.1

Beautiful Soup 4.9.3



### - Front-end

Bootstrap  5.0.0

Template Engine : Thymeleaf

HTML 5 CSS

JavaScript

### - 그 외

Git

SourceTree

iterm


## 주요 기능

- 기온별 상품 추천 
- 상품 코디




## 시연 영상

[![image](https://user-images.githubusercontent.com/62981623/122639563-816fed00-d135-11eb-8bed-42808cfbc60a.png)
](https://www.youtube.com/watch?v=WPs8O315-P4")


## ERD Diagram
## Entity Relation Diagram

![image](https://user-images.githubusercontent.com/73927761/124227333-76f62000-db45-11eb-9c6d-f40a225fe9da.png)




## 프로젝트의 특징점

### 1. 상품 코디 기능

사용자가 찜한 상품들의 코디를 조합해 볼 수 있는 기능을 제공한다.

- 코디 페이지
![image](https://user-images.githubusercontent.com/62981623/122635312-12d36500-d11e-11eb-9bad-b27636ffeabc.png)


- 코디 리스트 페이지
![image](https://user-images.githubusercontent.com/62981623/122635415-c76d8680-d11e-11eb-9f2f-bc8df9b07ff9.png)



##### MainController.java

```java
   /**
     * 상품 코디
     * @param member : 로그인한 사용자
     * @return : 상품 코디 페이지
     */
    @GetMapping("/cody")
    public String cody(@CurrentUser Member member, Model model) {

        model.addAttribute(new CodyForm());

        List<Item> likeList = memberService.getLikeList(member);
        List<Item> top = new ArrayList<>();
        List<Item> outer = new ArrayList<>();
        List<Item> bottom = new ArrayList<>();
        List<Item> acc = new ArrayList<>();
        List<Item> shoes = new ArrayList<>();

        for(int i = 0; i<likeList.size(); i++){ // 찜한 상품들 조회
            if(likeList.get(i).getParentCategory().getName().equals("상의")){
                top.add(likeList.get(i));
            }
            if(likeList.get(i).getParentCategory().getName().equals("아우터")){
                outer.add(likeList.get(i));
            }
            if(likeList.get(i).getParentCategory().getName().equals("바지")){
                bottom.add(likeList.get(i));
            }
            if(likeList.get(i).getParentCategory().getName().equals("신발")||likeList.get(i).getParentCategory().getName().equals("스니커즈") ){
                shoes.add(likeList.get(i));
            }
            if(likeList.get(i).getParentCategory().getName().equals("가방")){
                acc.add(likeList.get(i));
            }
        }
        model.addAttribute("topList", top);
        model.addAttribute("outerList", outer);
        model.addAttribute("bottomList", bottom);
        model.addAttribute("accList", acc);
        model.addAttribute("shoesList", shoes);
        model.addAttribute("member", member);

        return "/view/cody";
    }
```





### 2. 날씨별 상품 추천 기능

Google Map API와 기상청 API를 통해 위치와 10일치 기온 데이터를 받아와서 적절한 상품을 추천해 주는 기능을 제공한다.


![weather_service](https://user-images.githubusercontent.com/62981623/122636916-bfb1e000-d126-11eb-9de6-45d329e763c9.jpg)




##### MainController.java

```java
    /**
     * 날씨별 옷 추천
     * @return : 상품 추천 fragment
     */
    @GetMapping("/daily-recommend")
    public String daily_recommend(Model model, String city){

        Calendar calendar = Calendar.getInstance(); // 현재 '시간' 만 얻기
        SimpleDateFormat sdf = new SimpleDateFormat("HH"); // 24시간제로 표시
        String hour = sdf.format(calendar.getTime());
        int currentHour = Integer.parseInt(hour);

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

        String season = "winter";
        if(currentHour > 11 && currentHour < 24 ){ //오후
            Long pm_temperature = weatherService.pm_temperature();
            // 봄, 가을 날씨 온도
            if (pm_temperature > Long.valueOf("12") && pm_temperature < Long.valueOf("28")){
                season = "spring_fall";
            }
            else if (pm_temperature <= Long.valueOf("12")){ // 겨울
                season = "winter";
            }
            else if(pm_temperature >= Long.valueOf("28")){// 여름
                season = "summer";
            }
        }
        else{ //오전
            Long am_temperature = weatherService.am_temperature();
            // 봄, 가을 날씨 온도
            if (am_temperature > Long.valueOf("12") && am_temperature < Long.valueOf("28")){
                season = "spring_fall";
            }
            else if (am_temperature <= Long.valueOf("12")){ // 겨울
                season = "winter";
            }
            else if(am_temperature >= Long.valueOf("28")){// 여름
                season = "summer";
            }
        }

        Long parent1 = Long.valueOf("12");
        Long child1 = Long.valueOf(itemService.random_outer_list(season));

        Long parent2 = Long.valueOf("3");
        Long child2 = Long.valueOf(itemService.random_top_list(season));

        Long parent3 = Long.valueOf("31");
        Long child3 = Long.valueOf(itemService.random_bottom_list(season));


        model.addAttribute("outer", itemService.findRecommendCategory(parent1, child1));

        model.addAttribute("top", itemService.findRecommendCategory(parent2, child2));

        model.addAttribute("bottom", itemService.findRecommendCategory(parent3, child3));

        return "/view/daily-recommend";
    }
```

##### ItemService.java

```java
 /**
     * 계절별 랜덤 아우터(Outer) 아이템 카테고리 나누기
     * @param season
     * @return
     */
    // 계절에 따른 아우터 랜덤 추천 리스트
    public String random_outer_list(String season){
        Random random = new Random();

        switch (season){
            // 여름 아우터
            case "summer" :
                List<String> summer_outer = new ArrayList<>();
                summer_outer.add("27");
                summer_outer.add("17");
                summer_outer.add("15");
                String random_summer_outer = summer_outer.get(random.nextInt(summer_outer.size() - 1));
                return random_summer_outer;

            // 봄, 가을 아우터
            case "spring_fall" :
                List<String> spring_fall_outer = new ArrayList<>();
                spring_fall_outer.add("29");
                spring_fall_outer.add("27");
                spring_fall_outer.add("24");
                spring_fall_outer.add("23");
                spring_fall_outer.add("21");
                spring_fall_outer.add("20");
                spring_fall_outer.add("19");
                spring_fall_outer.add("18");
                spring_fall_outer.add("17");
                spring_fall_outer.add("15");
                spring_fall_outer.add("14");
                spring_fall_outer.add("13");
                spring_fall_outer.add("11");

                String random_spring_fall = spring_fall_outer.get(random.nextInt(spring_fall_outer.size() - 1));
                return random_spring_fall;

            // 겨울 아우터
            case "winter" :
                List<String> winter_outer = new ArrayList<>();
                winter_outer.add("28");
                winter_outer.add("26");
                winter_outer.add("24");
                winter_outer.add("22");
                String random_winter_outer = winter_outer.get(random.nextInt(winter_outer.size() - 1));
                return random_winter_outer;
        }

        return "29";
//        return null;
    }
    /**
     * 계절별 랜덤 상의(Top) 아이템 카테고리 나누기
     * @param season
     * @return
     */
    // 계절에 따른 상의 랜덤 추천 리스트
    public String random_top_list(String season){
        Random random = new Random();

        switch (season){

            // 여름 상의
            case "summer" :
                List<String> summer_top = new ArrayList<>();
                summer_top.add("6");
//                String random_summer_top = summer_top.get(random.nextInt(summer_top.size() - 1));
//                return random_summer_top;
                return "6";
            case  "spring_fall" :
                List<String> spring_fall_top = new ArrayList<>();
                spring_fall_top.add("2");
                spring_fall_top.add("4");
                spring_fall_top.add("5");
                spring_fall_top.add("7");
                String random_spring_fall_top = spring_fall_top.get(random.nextInt(spring_fall_top.size() - 1));
                return random_spring_fall_top;

            case "winter" :
                List<String> winter_top = new ArrayList<>();
                winter_top.add("2");
                winter_top.add("3");
                winter_top.add("5");
                String random_winter_top = winter_top.get(random.nextInt(winter_top.size() - 1));
                return random_winter_top;
        }
        return "2";
        //       return null;
    }

    /**
     * 계절별 랜덤 하의(bottom) 아이템 카테고리 나누기
     * @param season
     * @return
     */
    // 계절에 따른 하의 랜덤 추천 리스트
    public String random_bottom_list(String season){
        Random random = new Random();
        switch (season){

            // 여름 하의
            case "summer" :
                List<String> summer_bottom = new ArrayList<>();
                summer_bottom.add("36");
                summer_bottom.add("35");
                summer_bottom.add("34");
                summer_bottom.add("30");

                String random_summer_bottom = summer_bottom.get(random.nextInt(summer_bottom.size() - 1));
                return random_summer_bottom;

            // 봄,가을 하의
            case  "spring_fall" :
                List<String> spring_fall_bottom = new ArrayList<>();
                spring_fall_bottom.add("36");
                spring_fall_bottom.add("35");
                spring_fall_bottom.add("33");
                spring_fall_bottom.add("32");
                spring_fall_bottom.add("30");

                String random_spring_fall_bottom = spring_fall_bottom.get(random.nextInt(spring_fall_bottom.size() - 1));
                return random_spring_fall_bottom;

            // 겨울 하의
            case "winter" :
                List<String> winter_bottom = new ArrayList<>();
                winter_bottom.add("33");
                winter_bottom.add("32");

                String random_winter_bottom = winter_bottom.get(random.nextInt(winter_bottom.size() - 1));
                return random_winter_bottom;
        }
        return "36";
//        return null;
    }
```

##### 날씨 Api(OpenWeatherMap)를 사용하여 현재 날씨 상태 이미지로 띠우기
```javascript
<script>
                //openWeatherMap 으로 오늘 실시간 날씨 + 아이콘 받기
                const OpenWeatherKey = "XXXXXXXXXXXXXXXXXXXXXX";
                var currentWeather = document.getElementById("loadWeather");
                var loadWeatherIcon = document.getElementById("loadWeatherIcon");

                navigator.geolocation.getCurrentPosition(function (pos) {
                    var latitude = pos.coords.latitude;
                    var longitude = pos.coords.longitude;
                    let openWeatherUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + OpenWeatherKey;

                    console.log("체크용 222 lat: " + latitude + ", lng:" + longitude);
                    fetch(openWeatherUrl)
                        .then(response => response.json())
                        .then(data => {
                            // console.log(data);
                            console.log("현재온도 : " + (data.main.temp - 273.15));
                            console.log("현재습도 : " + data.main.humidity);
                            console.log("날씨 : " + data.weather[0].main);
                            console.log("상세날씨설명 : " + data.weather[0].description);
                            console.log("날씨 이미지 : " + data.weather[0].icon);
                            console.log("바람   : " + data.wind.speed);
                            console.log("나라   : " + data.sys.country);
                            console.log("도시이름  : " + data.name);
                            console.log("구름  : " + (data.clouds.all) + "%");

                            let temperature = Math.floor(data.main.temp - 273.15);
                            let weather = data.weather[0].main;
                            let weatherDetail = data.weather[0].description;
                            //let image = data.weather[0].icon;
                            let icon = "http://openweathermap.org/img/w/" + data.weather[0].icon + ".png";
                            currentWeather.innerText = temperature + "°C";
                            switch (data.weather[0].description) {
                                case "clear sky": {
                                    loadWeatherIcon.src = "[[@{/images/icon/sunny.png}]]";
                                    break;
                                }
                                case "few clouds": {
                                    loadWeatherIcon.src = "[[@{/images/icon/cloudy.png}]]";
                                    break;
                                }
                                case "scattered clouds":
                                case "broken clouds": {
                                    loadWeatherIcon.src = "[[@{/images/icon/cloudy.png}]]";
                                    break;
                                }
                                case "shower rain": {
                                    loadWeatherIcon.src = "[[@{/images/icon/snower rain.png}]]";
                                    break;
                                }
                                case "rain": {
                                    loadWeatherIcon.src = "[[@{/images/icon/rainy.png}]]";
                                    break;
                                }
                                case "thunderstorm": {
                                    loadWeatherIcon.src = "[[@{/images/icon/thunder.png}]]";
                                    break;
                                }
                                case "snow": {
                                    loadWeatherIcon.innerHTML = "<i class='fas fa-snowflake fa-5x'></i>"
                                    break;
                                }
                                case "mist": {
                                    loadWeatherIcon.innerHTML = "<i class='fas fa-smog fa-5x'></i>"
                                    break;
                                }
                            }
                        });
                });
            </script>
```

##### 현재 위치값(현 좌표```(Geolocation)```를 통한 주소 이름```(googleAPI)``` 얻기) 스크립트 
```javascript
<!--현재 위치값(좌표,주소값) 스크립트 -->
            <script>
                const KEY = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
                let url;
                let city;
                let city_address;
                let city_adress2;
                var full_address = document.getElementById("full_address")

                navigator.geolocation.getCurrentPosition(function (pos) {
                    var latitude = pos.coords.latitude;
                    var longitude = pos.coords.longitude;
                    url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + KEY;

                    console.log("lat: " + latitude + ", lng:" + longitude);

                    fetch(url)
                        .then(response => response.json())
                        .then(data => {
                            //console.log(data);
                            let parts = data.results[0].formatted_address
                            city = parts.split(" ")[1];
                            city_address = parts.split(" ")[2]; // ex) 마포구
                            city_adress2 = parts.split(" ")[3]; // ~~1가
                            full_address.innerText = city + " " + city_address + " " + city_adress2;
                            console.log(city + " fetch");

                            $("#locationBtn").click(function () {
                                window.location.href = '/daily-recommend?city=' + city;//
                            });
                        });
                });
               
            </script>
```



### 3. 페이징 처리

상품들을 페이지로 나누어 보여주는 기능을 제공한다. 

![image](https://user-images.githubusercontent.com/62981623/122637391-4f588e00-d129-11eb-9ac4-01815853dfef.png)




##### MainController.java

```java
      /**
     * 카테고리 별 아이템 조회
     * @param itemRequest : 카테고리
     * @return : 아이템 페이지
     */
    @GetMapping("/itemList")
    public String itemList(ItemRequest itemRequest, Model model) {
        String categoryName = itemRequest.getCategoryName();
        Pageable pageable = itemService.getPageable(itemRequest);

        Paginator paginator = new Paginator(5, itemRequest.getLimit(), itemService.getCountItemListByCategory(categoryName));
        Map<String, Object> pageInfo = paginator.getFixedBlock(itemRequest.getPage());

        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("itemList", itemService.getItemListByCategory(categoryName, pageable));
        model.addAttribute("categoryName", categoryName);

        return "/view/category";
    }

```





## 문제점 및 해결방안

### 1. 메일 서비스의 오류

Thymeleaf 엔진의 특성상 csrf 토큰 유효성을 판단하는데, Ajax POST 통신 시 csrf 토큰이 누락되었기 때문에 403 응답이 왔다.

--> Ajax 요청에 csrf 토큰을 추가하여 해결하였다.

![image](https://user-images.githubusercontent.com/73927761/124228072-98a3d700-db46-11eb-92a4-5f3f1db322b7.png)


![image](https://user-images.githubusercontent.com/73927761/124227844-3f3ba800-db46-11eb-8c1c-5df620394512.png)






### 2. 상품별 비율 조정의 어려움

상품 코디 기능 구현 중, 상품마다 이미지 사이즈가 달라 상/하의/악세사리 비율을 맞추는 것이 힘들었다.

--> 상품마다 Slider을 달아 크기를 조정하고, 각각 DB 에 저장하였다.

![bandicam 2021-06-19 17-31-37-178](https://user-images.githubusercontent.com/62981623/122636890-998c4000-d126-11eb-9bcd-d6f18d2c50e7.gif)





### 3. Layout 구성의 어려움

하나의 layout에 여러가지 기능을 넣어 페이지를 구성하려니 코드가 길어지고 복잡해 지는 어려움이 생겼다. 

--> 기능마다 Fragment로 나누어 코드 중복을 줄이면서 Layout을 구성하여 해결하였다.







## 개선방안

- 검색 시 Physical DB Access가 아닌 검색 엔진 오픈소스를 사용

- AWS 운영 서버 추가





## 팀 역할

#### <박근희>

- 웹 크롤링과 컴퓨터 비전을 활용한 데이터 수집 및 저장
- 상품 코디 기능 및 코디 리스트 조회 기능
- 상품 찜 기능 및 찜 리스트 조회 기능
- 구매 후기 게시글 기능 
- 찜 갯수 조회, 랭킹 조회 기능
- 코디 찜 갯수, 랭킹 조회 기능



#### <김재우>

- ERD 설계
- 날씨별 상품 추천 기능
- 상품 검색 기능
- google mail을 통한 임시비밀번호 발급(SMTP)
- OpenWeater API를 통한 날씨 데이터 조회
- Google API를 통한 위치 정보 조회

  

####  <이희진>

- 전체 페이지 View 및 레이아웃 디자인
- 찜 리스트 페이지 디자인
- 장바구니 기능
- 상품 상세 페이지 기능

 
