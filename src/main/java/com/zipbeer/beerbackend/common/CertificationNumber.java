package com.zipbeer.beerbackend.common;

public class CertificationNumber {
    public static String getCertificationNumber(){
        String certificationNumber = "";

        for(int count = 0; count < 6; count++) certificationNumber += (int)(Math.random()*10);

        return certificationNumber;
    }

}