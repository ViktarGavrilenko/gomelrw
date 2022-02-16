package utils;

import aquality.selenium.core.logging.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class StringUtils {
    private static final String INVALID_DATE = "Invalid date %s. %s";
    private static final String DATE_PATTERN = "d MMMM yyyy";
    private static final String DATE_PATTERN_WITH_OUT_YEAR = "d MMMM";
    private static final String DATE_PATTERN_FOR_SQL = "yyyy-MM-dd";
    private static final String DATE_PATTERN_SCAN = "yyyyMMdd";
    private static final String YEAR_OF_BIRTH_OF_WOMAN = "0004";

    public static Date convertStrToDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            try {
                return formatter.parse(date + " " + YEAR_OF_BIRTH_OF_WOMAN);
            } catch (ParseException parseException) {
                String messageException = String.format(INVALID_DATE, date, e);
                Logger.getInstance().error(messageException);
                throw new IllegalArgumentException(messageException);
            }
        }
    }

    public static String convertDateToStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN_FOR_SQL);
        return formatter.format(date);
    }

    public static LocalDate convertStrToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN_FOR_SQL);
        return LocalDate.parse(date, formatter);
    }

    public static String convertLocalDateToStr(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN_SCAN));
    }

    public static String fistUpCase(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    public static String convertStrToStr(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN_FOR_SQL);
        LocalDate localDate = LocalDate.parse(date, formatter);

        if (localDate.getYear() == Integer.parseInt(YEAR_OF_BIRTH_OF_WOMAN)) {
            return localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN_WITH_OUT_YEAR));
        } else {
            return localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        }
    }
}