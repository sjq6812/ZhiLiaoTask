package com.wzvtc.zhiliaotask.application;

import android.app.Application;
import android.content.SharedPreferences;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.response.CustomTypeAdapter;
import com.apollographql.apollo.response.CustomTypeValue;
import com.wzvtc.zhiliaotask.type.CustomType;
import com.wzvtc.zhiliaotask.utils.Const;

import okhttp3.OkHttpClient;

/**
 * created by Litrainy on 2018-12-10 10:34
 */
public class ZhiLiaoApplication extends Application {

    private static final String BASE_URL = "http://120.79.40.85:9090/graphql";
    private ApolloClient apolloClient;
    private static ZhiLiaoApplication sInstance = null;
    private static SharedPreferences sp;
    private SharedPreferences.Editor edit;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        apolloClient = ApolloClient.builder()
                .addCustomTypeAdapter(CustomType.LONG,new CustomTypeAdapter<Long>() {
                    public Long decode(CustomTypeValue value) {
                        try {
                            return Long.parseLong(value.value.toString());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    public CustomTypeValue encode(Long value) {
                        return CustomTypeValue.fromRawValue(value);
                    }
                })
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build();
    }

    public ApolloClient getApolloClient() {
        return apolloClient;
    }

    public static ZhiLiaoApplication getInstance() {
        return sInstance;
    }

    public void setIsLogin(int isLogin) {
        if (sp == null) {
            sp = getApplicationContext().getSharedPreferences(Const.USER_INFO, MODE_PRIVATE);
        }
        if (edit == null) {
            edit = sp.edit();
        }
        edit.putInt(Const.IS_LOGIN, isLogin);
        edit.apply();
    }

    public int getIsLogin() {
        if (sp == null) {
            sp = getApplicationContext().getSharedPreferences(Const.USER_INFO, MODE_PRIVATE);
        }
        return sp.getInt(Const.IS_LOGIN, 0);
    }

}
