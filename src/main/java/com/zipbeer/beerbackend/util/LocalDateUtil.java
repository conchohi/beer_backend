package com.zipbeer.beerbackend.util;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class LocalDateUtil {
    public static String getDayOfWeek(LocalDate localDate){
        return localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
    }
}
