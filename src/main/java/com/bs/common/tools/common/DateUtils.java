package com.bs.common.tools.common;

import java.util.Calendar;
import java.util.Date;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName DateUtils
 * @Description
 * @date 2021/7/26 15:52
 */
public class DateUtils {

    /**
     * 获取上一个月第一天0点
     *
     */
    public static Date getLastMonthFirstDay(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DATE,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setMinTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取上一个月最后一天23点
     *
     */
    public static Date getLastMonthEndDay(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取上个季度第一天0点
     *
     * @return
     */
    public static Date getLastQuarterFirstDay(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DATE,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setMinTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取上个季度最后一天23点
     *
     * @return
     */
    public static Date getLastQuarterEndDay(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMinTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取指定月的最后一天最后一秒
     *
     */
    public static Date getMonthEndDay(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取指定月的第一天第一秒
     *
     */
    public static Date getMonthFirstDay(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setMinTime(calendar);
        return calendar.getTime();
    }

    /**
     * 在指定时间增加月份
     *
     * @param monthNum 几个月
     * @return
     */
    public static Date addMonth(Date date,int monthNum){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,monthNum);
        return calendar.getTime();
    }


    private static void setMinTime(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setMaxTime(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
    }
}
