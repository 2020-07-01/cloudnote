package com.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * @program: DateUtils
 * @description: 日期转换工具
 * @create: 2020-02-01 16:06
 **/
public class DateUtils {

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

               return (o1 < o2) ? 0:1;
           }
       };
        return comparator.compare(first,second)== 0 ? true:false;
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


    public static Long parse(Date date){
        return date.getTime();
    }

    public static String getCurrentDate(){
        return  format.format(new Date());
    }

}
