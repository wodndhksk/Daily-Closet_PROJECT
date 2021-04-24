package com.megait.soir.domain;

import lombok.Getter;

@Getter
public class WeatherListResponse {
    private Long id;
    private String baseDate;
    private String meridiem;
    private String city;
    private Long temperature;
    private String localWeather;

    public WeatherListResponse(Weather entity) {
        this.id = entity.getId();
        this.baseDate = entity.getBaseDate();
        this.meridiem = entity.getMeridiem();
        this.city = entity.getCity();
        this.temperature = entity.getTemperature();
        this.localWeather = entity.getLocalWeather();
    }


}
