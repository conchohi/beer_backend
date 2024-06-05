package com.zipbeer.beerbackend.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmailCertificationDto {
    private String id;
    private String email;
    private String certificationNumber;
}
