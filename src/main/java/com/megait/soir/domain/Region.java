package com.megait.soir.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class Region {
    /*
    CREATE TABLE region (
	no INT(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	regionWeatherId VARCHAR(100) NOT NULL COMMENT '예보구역코드',
	regionTemperatureId VARCHAR(100) NOT NULL COMMENT '기온구역코드',
	city VARCHAR(100) NOT NULL COMMENT '구역명',
    );
     */
    @Id @GeneratedValue
    private Long id;
    private String regionWeatherId;
    private String regionTemperatureId;
    private String city;
}
