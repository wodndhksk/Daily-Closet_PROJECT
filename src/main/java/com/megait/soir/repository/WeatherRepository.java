package com.megait.soir.repository;

import com.megait.soir.domain.Item;
import com.megait.soir.domain.Weather;
import com.megait.soir.domain.WeatherListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
    @Query("SELECT w FROM Weather w ORDER BY w.id ASC")
    List<Weather> findAllDesc();

    //    @Query(value = "SELECT * FROM WEATHER WHERE (MERIDIEM LIKE '%mer%') AND (CITY LIKE '%서%') AND (BASE_DATE BETWEEN '20210325' AND '20210402')", nativeQuery = true)
//    List <Weather> findByNameLike(String city);
//
    ///////////////////
    @Query(value = "select * from  WEATHER w where w.meridiem = :meridiem and w.city like %:weatherCity% and w.BASE_DATE BETWEEN :startDate and :endDate", nativeQuery = true)
    List<Weather> findByWeatherCityAndBaseDateAndMeridiemOrderByBaseDateDesc(
            @Param("weatherCity") String weatherCity,
            @Param("meridiem") String meridiem,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /////////////////////
    //원하는 지역만 찾을때 사용
    @Query(value = "select * from  WEATHER e where e.city like %:city% ", nativeQuery = true)
    List<Weather> findByCurrentLocalWeather(@Param("city") String city);

    // 날짜, 지역, 오전/오후인지와 일치하는 값 뽑기
    @Query(value = "select * from  WEATHER e where e.base_date like %:base_date%  and e.city like %:city% and e.meridiem like %:meridiem%", nativeQuery = true)
    List<Weather> findCurrentDateTemperature(@Param("base_date") String currentDate,
                                             @Param("city") String city,
                                             @Param("meridiem") String meridiem
    );

    // 부모
    // id=3 상의 {자식 id : id=2 후드티셔츠, id=4 맨투맨 6, 5, 7}
    // id=12 아우터 {자식 id : 29,28,27,26,24,23,22,21,20,19,18,17,15,14,13,11}
    // id=31 바지 {자식 id : 36,35,34,33,32,31,30,}
//    @Query(value = "select * from  ITEM e where e.PARENT_CATEGORY_ID  like %:parent_category_id%  and " +
//            "e.CHILD_CATEGORY_ID like %:child_category_id%", nativeQuery = true)
//    List <Item> findRecommendCategory(@Param("parent_category_id") Long parent,
//                                              @Param("child_category_id") Long child
//    );
    //
   // List<Weather.WeatherMapping> findAllBy(String city);


}
