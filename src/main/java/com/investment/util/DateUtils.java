package com.investment.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Date class util. Defines operations with date classes
 */
public class DateUtils {
    // private constructor to avoid unnecessary instantiation of the class
    private DateUtils() {
    }

    /**
     * Converts a String of the specified format to LocalDate
     *
     * @param date string containing a date
     * @return LocalDate converted from the String
     */
    public static LocalDate dateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    /**
     * Converts epoch time in milliseconds in LocalDateTime
     *
     * @param milliseconds epoch time in milliseconds
     * @return LocalDate converted from the epoch time in milliseconds
     */
    public static LocalDateTime dateFromMilliseconds(long milliseconds) {
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
