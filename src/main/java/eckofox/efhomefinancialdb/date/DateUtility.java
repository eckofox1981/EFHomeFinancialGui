package eckofox.efhomefinancialdb.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    /***
     * use the convertDateString to see if userId input is Ok to be used as transaction date.
     * @param sDate (userId input)
     * @return a boolean that is used to approve sDate or ask the userId for a new, corrected, input.
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

}
