package com.wzvtc.zhiliaotask.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.wzvtc.zhiliaotask.activity.LoginActivity;

/**
 * created by Litrainy on 2018-12-14 10:34
 */
public class UserUtils {



    public static String getUserId(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Const.USER_INFO, Context.MODE_PRIVATE);
        String sUserId = sharedPreferences.getString(Const.USER_ID, "");
        if ("".equals(sUserId)) {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
        return sUserId;
    }
}
