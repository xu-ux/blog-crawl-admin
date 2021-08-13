package com.bs.common.tools.common;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName DateRangeUtils
 * @Description 日期范围工具
 * @date 2021/5/27 17:24
 */
public class DateRangeUtils {

    /**
     * 获取date的月份的时间范围
     * @param date
     * @return
     */
    public static Range getMonthRange(Date date) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(date);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        setMinTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(date);
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(endCalendar);

        return new Range().setStart(startCalendar.getTime()).setEnd(endCalendar.getTime());
    }
    /**
     * 获取当前季度的时间范围
     * @return current quarter
     */
    public static Range getThisQuarter() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.MONTH, ((int) startCalendar.get(Calendar.MONTH) / 3) * 3);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        setMinTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.MONTH, ((int) startCalendar.get(Calendar.MONTH) / 3) * 3 + 2);
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(endCalendar);

        return new Range().setStart(startCalendar.getTime()).setEnd(endCalendar.getTime());
    }

    /**
     * 获取昨天的时间范围
     * @return
     */
    public static Range getYesterdayRange() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DAY_OF_MONTH, -1);
        setMinTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DAY_OF_MONTH, -1);
        setMaxTime(endCalendar);

        return new Range().setStart(startCalendar.getTime()).setEnd(endCalendar.getTime());
    }

    /**
     * 获取当前月份的时间范围
     * @return
     */
    public static Range getThisMonth(){
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        setMinTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(endCalendar);

        return new Range().setStart(startCalendar.getTime()).setEnd(endCalendar.getTime());
    }

    /**
     * 获取上个月的时间范围
     * @return
     */
    public static Range getLastMonth(){
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MONTH, -1);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        setMinTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.MONTH, -1);
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(endCalendar);

        return new Range().setStart(startCalendar.getTime()).setEnd(endCalendar.getTime());
    }

    /**
     * 获取上个季度的时间范围
     * @return
     */
    public static Range getLastQuarter() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.MONTH, ((int) startCalendar.get(Calendar.MONTH) / 3 - 1) * 3);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        setMinTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.MONTH, ((int) endCalendar.get(Calendar.MONTH) / 3 - 1) * 3 + 2);
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(endCalendar);

        return new Range().setStart(startCalendar.getTime()).setEnd(endCalendar.getTime());
    }


    /**
     * 从上个月开始计算间隔month个月的开始时间 结束时间
     *
     * @param month 间隔月的数量
     * @return
     */
    public static Range getIntervalByLastMonth(int month){
        Date endDay = DateUtils.getLastMonthEndDay();

        Calendar calendar=Calendar.getInstance();
        calendar.setTime(endDay);
        calendar.add(Calendar.MONTH, -month);
        calendar.set(Calendar.DATE,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Date startDay = calendar.getTime();

        Range range = new Range().setStart(startDay).setEnd(endDay);
        return range;
    }

    /**
     * 获取上个月之前6个月的开始和结束时间
     * @return
     */
    public static List<Range> getSixMonthByLastMonth(){
        List<Range> list = new ArrayList<>();
        for (int i = 1;i <= 6;i++){
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.add(Calendar.MONTH, -i);
            startCalendar.set(Calendar.DAY_OF_MONTH, 1);
            setMinTime(startCalendar);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.add(Calendar.MONTH, -i);
            endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setMaxTime(endCalendar);
            list.add(new Range().setStart(startCalendar.getTime()).setEnd(endCalendar.getTime()));
        }
        return list;
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
        calendar.set(Calendar.MILLISECOND, 0);
    }


    public static class Range {

        private Date start;

        private Date end;


        public Date getStart() {
            return start;
        }

        public Range setStart(Date start) {
            this.start = start;
            return this;
        }

        public Date getEnd() {
            return end;
        }

        public Range setEnd(Date end) {
            this.end = end;
            return this;
        }
    }
}
