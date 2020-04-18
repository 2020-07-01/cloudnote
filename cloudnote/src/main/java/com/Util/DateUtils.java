package com.Util;

import org.apache.commons.codec.language.bm.Languages;
import org.checkerframework.checker.units.qual.C;
import org.springframework.dao.DataAccessException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * @program: DateUtils
 * @description:
 * @create: 2020-02-01 16:06
 **/
public class DateUtils {

    public static final String DATE_FORAT = "yyyy-MM-dd";
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 判断日期firstDate是否在日期secondDate之前
     *
     * @param firstDate
     * @param secondDate
     * @return
     */
    public static boolean isBefore(String firstDate, String secondDate) throws ParseException {
        Long first = parse(firstDate).getTime();
        Long second = parse(secondDate).getTime();

        Comparator<Long> comparator = new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {

                return (o1 < o2) ? 0 : 1;
            }
        };
        return comparator.compare(first, second) == 0 ? true : false;
    }

    /**
     * 字符串转换为日期
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date parse(String date) throws ParseException {
        return format.parse(date);
    }


    public static String parse(Long hour, Long minute, String executeTime) {
        try {
            Date date = format.parse(executeTime);
            Long milliSecond = date.getTime() - hour * 60 * 60 * 1000 - minute * 60 * 1000;
            Date date2 = new Date(milliSecond);
            return format.format(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Date转换为字符串
     *
     * @return
     */
    public static String parse(Date date) {
        return format.format(date);
    }


    /**
     * 获得N天前日期
     *
     * @param days
     * @return
     */
    public static String getBeforeDay(int days) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORAT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return format.format(calendar.getTime());
    }

    /**
     * 获取当天日期 yyyy-MM-dd
     * @return
     */
    public static String CurrentDay() {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORAT);
        return format.format(new Date());
    }


}
