package com.wzvtc.zhiliaotask.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * created by Litrainy on 2018-12-14 8:31
 */
public class TimeUtils {

    @TargetApi(Build.VERSION_CODES.O)
    public static String getTime(Long time) {
        SimpleDateFormat simpleDateFormat;
        String result;
        Date date;
        if (isToday(time)) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
            date = new Date(time);
            result = simpleDateFormat.format(date);
            return result;
        } else {
            simpleDateFormat = new SimpleDateFormat("MM-dd");
            date = new Date(time);
            result = simpleDateFormat.format(date);
            return result;
        }
    }


    private static boolean isToday(Long inputTime) {
        Date inputJudgeDate = new Date(inputTime);
        boolean flag = false;
        //获取当前系统时间
        long longDate = System.currentTimeMillis();
        Date nowDate = new Date(longDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(nowDate);
        String subDate = format.substring(0, 10);
        //定义每天的24h时间范围
        String beginTime = subDate + " 00:00:00";
        String endTime = subDate + " 23:59:59";
        Date paseBeginTime = null;
        Date paseEndTime = null;
        try {
            paseBeginTime = dateFormat.parse(beginTime);
            paseEndTime = dateFormat.parse(endTime);

        } catch (ParseException e) {
            Log.e(TextUtils.class.getSimpleName(), "error "+e.getMessage() );
        }
        if(inputJudgeDate.after(paseBeginTime) && inputJudgeDate.before(paseEndTime)) {
            flag = true;
        }
        return flag;
    }



}
