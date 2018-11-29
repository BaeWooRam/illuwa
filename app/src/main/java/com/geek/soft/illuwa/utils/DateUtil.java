package com.geek.soft.illuwa.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jayjay on 15. 4. 8..
 */
public class DateUtil {

    public static String getSupportChangeLeftTime(int leftTime)
    {
        int day = 0;
        int time = 0;
        int minute = 0;

        String returnStr = "";

        while(60 < leftTime)
        {
            if(60 * 60 * 24 < leftTime)
            {
                day += 1;
                leftTime -= (60 * 60 * 24);
            }
            else if(60 * 60 < leftTime)
            {
                time += 1;
                leftTime -= (60 * 60);
            }
            else if(60 < leftTime)
            {
                minute += 1;
                leftTime -= 60;
            }
        }

        if(0 < day) {
            returnStr += day + "일 ";
        }
        if(0 < time) {
            returnStr += time + "시간 ";
        }
        if(0 < minute) {
            returnStr += minute + "분 ";
        }
        if(CommonUtils.isEmpty(returnStr)) {
            returnStr += leftTime + "초 ";
        }

        return returnStr;
    }

    public static String getNowDateYYYYMMdd()
    {
        String returnDate = "";

        try{
            Date date = new Date ();
            DateFormat fomat = new SimpleDateFormat("yyyyMMdd");
            returnDate = fomat.format(date);
        } catch (Exception e){
            returnDate = "";
        }

        return returnDate;
    }

    public static String formatYYYYMMdd(String param)
    {
        String returnDate = "";

        try{
            Date date = new Date (StringUtil.stringToLong(param));
            SimpleDateFormat fomat = new SimpleDateFormat("yyyy.MM.dd");
            returnDate = fomat.format(date);

        } catch (Exception e){
            returnDate = "";
        }

        return returnDate;
    }

    public static String getYYYYMM(String formatDate, int month)
    {
        String returnDate = "";

        try{
            Date date = new Date ();
            SimpleDateFormat fomat = new SimpleDateFormat(formatDate);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, month);

            date = cal.getTime();

            returnDate = fomat.format(date);

        } catch (Exception e){
            returnDate = "";
        }

        return returnDate;
    }

    public static String getMonthDesc(int month)
    {

        String returnDate = "";

        try{
            Date date = new Date ();
            SimpleDateFormat fomat2 = new SimpleDateFormat("yyyy.MM");

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, month);

            date = cal.getTime();

            returnDate += " (" + fomat2.format(date) + ".01"  +
                    "~" + fomat2.format(date) + "." + StringUtil.intToString(cal.getActualMaximum(Calendar.DAY_OF_MONTH)) + ")";

        } catch (Exception e){
            returnDate = "";
        }

        return returnDate;
    }


}
