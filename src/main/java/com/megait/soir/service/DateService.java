package com.megait.soir.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class DateService {

    public String currentHour() {
        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh");
        String meridiem = current.format(formatter);

        System.out.println(meridiem);
        String time ="PM";
        //오전: 00 01 02 03 04 05 06 07 08 09 10 11
        //오후: 12 13 ,14 15 16 17 18 19 20 21 22 23
        switch (meridiem) {
            case "01":
            case "02":
            case "03":
            case "04":
            case "05":
            case "06":
            case "07":
            case "08":
            case "09":
            case "10":
            case "11":
            case "00":
                time = "AM";
                break;
            case "12":
            case "13":
            case "14":
            case "15":
            case "16":
            case "17":
            case "18":
            case "19":
            case "20":
            case "21":
            case "22":
            case "23":
                time = "PM";
                break;
        }
        System.out.println(time);
        return time;
    }
}
