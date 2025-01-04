package eckofox.efhomefinancialdb.date;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtility {

    /**
     * Calendar is quite practical when it comes to adding a time-period.
     * The following function splits a string (pre-approved by checkIsDate) into year, month, day
     * and converts it a Calendar object
     * @param sDate (string to be split)
     * @return Calendar object.
     */
    public static Calendar stringToCalendar(String sDate) {
        String[] dateArray = sDate.split("-");
        int year = Integer.parseInt(dateArray[0]);
        int month = Integer.parseInt(dateArray[1]);
        month--; //in Calendar, january starts at [0], every month has to be subtracted by 1
        int day = Integer.parseInt(dateArray[2]);
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.set(year, month, day); //would rather have a text month

        return calendarDate;
    }

    /*** NOT USED IN THIS PROJECT (YET)
     * leftover from previous versions, could be used if user was allowed to manually input
     * date in date picker (maybe in an if-statement to check the user has written a date in the proper format)
     * saved for future uses.
     */
    public static boolean checkIsDate(String sDate) {
        try {
            convertDateString(sDate);
        } catch (IllegalArgumentException | ParseException e) {
            System.out.println("Date format invalid, format example: '2024-05-25'.");
            return false;
        }
        return true;
    }

    /***
     *the three following functions take a date in and add required amount o time
     */
    public static Calendar addACalendarWeek(Calendar date) {
        date.add(Calendar.DATE, 7);
        return date;
    }

    public static Calendar addACalendarMonth(Calendar date) {
        date.add(Calendar.MONTH, 1);
        return date;
    }

    public static Calendar addACalendarYear(Calendar date) {
        date.add(Calendar.YEAR, 1);
        return date;
    }

    /**
     * simple date converter, could be useful for future use, now just used to check correct format from userId
     * @param sDate date written by the userId
     * @return Date but never actually used.
     * @throws ParseException actually used as a mean to determined if userId input is in the correct format.
     */
    public static Date convertDateString(String sDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        return formatter.parse(sDate);
    }

    /**
     * converts the transaction date into a StringProperty displayable on the tableview (JavaFX)
     * @param date java.util.Date to be formated
     * @return a date string formated according to set standard below.
     */
    public static StringProperty datePropertyFormat(Date date) {
        LocalDate localDate = ((java.sql.Date) date).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new SimpleStringProperty(localDate.format(formatter));
    }

    /** converts the transaction.getDate to LocalDate
     * the problem is when transaction date is retrieved through the SQL query in TransactionFilter it returns a
     * java.sql.Date which can mix with the original java.util.Date set in the Transaction class.
     * Instead of converting directly in TransactionFilter I took the opportunity to work with generics to keep
     * my knowledge about generics fresh.
     * @param date could be either java.util.Date or java.sql.Date
     * @return LocalDate
     * @param <T> either java.util.Date or java.sql.Date
     */
    public static <T> LocalDate dateToLocalDateConverter (T date) {
        java.util.Date utilDate = null;
        Class<?> clazz = date.getClass();
        if (clazz.equals(java.sql.Date.class)) {
            utilDate = new java.util.Date(((java.sql.Date) date).getTime());
        }
        if (clazz.equals(java.util.Date.class)) {
            utilDate = (Date) date;
        }
        return utilDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
