package com.megait.soir.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Weather {
    /*
    CREATE TABLE weather (
	no INT(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	baseDate VARCHAR(50) NOT NULL COMMENT '날짜',
	meridiem VARCHAR(50) NOT NULL COMMENT '오전/오후',
	city VARCHAR(100) NOT NULL COMMENT '구역',
	temperature INT(20) COMMENT '온도',
	localWeather VARCHAR(100) NOT NULL COMMENT '날씨',
    );
     */
    @Id @GeneratedValue
    private Long id;
    private String baseDate;
    private String meridiem;
    private String city;
    private Long temperature;
    private String localWeather;

    @Builder
    public Weather(String baseDate, String meridiem, String city, Long temperature, String localWeather) {
        this.baseDate = baseDate;
        this.meridiem = meridiem;
        this.city = city;
        this.temperature = temperature;
        this.localWeather = localWeather;
    }
}