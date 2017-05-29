package com.hp.epilepsy.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * -----------------------------------------------------------------------------
 * Class Name: DateTimeFormats
 * Created By:mahmoud
 * Modified By:Nikunj & Shruti
 * Purpose: Date time format functions
 * -----------------------------------------------------------------------------
 */
public class DateTimeFormats {

    public static String isoDateFormat="yyyy-MM-dd";
    public static String isoDateFormatReverse="dd-MM-yyyy";
    public static String isoDateFormat2="yyyy-MM-dd hh:mm a";
    public static String isoTimeFormat="hh:mm a";
    public static String deafultDateFormat="EEE MMM dd HH:mm:ss z yyyy";

    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(isoDateFormat);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static Date convertStringToDateTimeFormat(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(isoDateFormat2);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }


    public static Date convertReportsStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(isoDateFormatReverse);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }



    public static Date convertStringToDateTime(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(isoDateFormat2);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }
    public static Date convertStringToDateTimeWithDeafultFormat(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(deafultDateFormat);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }


    public static Date convertStringToDateTimeWithDeafultFormat1(String dateString) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy  HH:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }



    public static Date convertStringToDateWithdeafultFormat(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(isoDateFormat);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }





    public static String reverseDateFormat(String dateToParse) {
        try {
            DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
            fromFormat.setLenient(false);
            DateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
            toFormat.setLenient(false);
            Date date = fromFormat.parse(dateToParse);
            return toFormat.format(date);
        } catch (ParseException ex) {

        } catch (Exception ex) {
        }
        return null;
    }



    public static String GetDateFromDateTimeFormat(String dateToParse) {
        try {
            DateFormat fromFormat = new SimpleDateFormat(isoDateFormat2);
            fromFormat.setLenient(false);
            DateFormat toFormat = new SimpleDateFormat(isoDateFormat);
            toFormat.setLenient(false);
            Date date = fromFormat.parse(dateToParse);
            return toFormat.format(date);
        } catch (ParseException ex) {

        } catch (Exception ex) {

        }
        return null;
    }

    public static String reverseDateFormatBack(String dateToParse) {
        try {
            DateFormat fromFormat = new SimpleDateFormat("dd-MM-yyyy");
            fromFormat.setLenient(false);
            DateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
            toFormat.setLenient(false);
            Date date = fromFormat.parse(dateToParse);
            return toFormat.format(date);
        } catch (ParseException ex) {

        } catch (Exception ex) {

        }
        return null;
    }


    public static String fromIsoFormatToDeafultFormat(String dateToParse) {
        try {
            DateFormat fromFormat = new SimpleDateFormat(isoDateFormat2);
            fromFormat.setLenient(false);
            DateFormat toFormat = new SimpleDateFormat(deafultDateFormat);
            toFormat.setLenient(false);
            Date date = fromFormat.parse(dateToParse);
            return toFormat.format(date);
        } catch (ParseException ex) {

        } catch (Exception ex) {

        }
        return null;
    }

    public static Date convertToIsoDateformat(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeFormats.deafultDateFormat);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static String convertDateToStringWithIsoFormat(Date date)
    {
        String stringDateFormat=new SimpleDateFormat(isoDateFormat).format(date);
        return stringDateFormat;
    }


    public static String convertReportDateToStringWithIsoFormat(Date date)
    {
        String stringDateFormat=new SimpleDateFormat(isoDateFormatReverse).format(date);
        return stringDateFormat;
    }

}
