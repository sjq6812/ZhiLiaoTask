package com.wzvtc.zhiliaotask.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.application.ZhiLiaoApplication;

public class GuidActivity extends AppCompatActivity {
    public final static int RESULT_LOGIN = 0;
    public final static int RESULT_MAIN = 1;
    private static final long SPLASH_DELAY_MILLIS = 500;

    private Handler mHandler = new Handler(msg -> {
        Intent intent = null;
        switch (msg.what) {
            case RESULT_LOGIN:  //0
                intent = new Intent(GuidActivity.this, LoginActivity.class);
                break;
            case RESULT_MAIN:  //1
                intent = new Intent(GuidActivity.this, MainActivity.class);
                break;
            default:
                break;
        }
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guid);
        new Handler().postDelayed(() -> {
        }, 300);
        new StartUpTask().execute();


    }

    private final class StartUpTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            int isLogin = ZhiLiaoApplication.getInstance().getIsLogin();
            return isLogin == 0 ? RESULT_LOGIN : RESULT_MAIN;
        }

        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                case RESULT_LOGIN:
                    mHandler.sendEmptyMessageDelayed(RESULT_LOGIN, SPLASH_DELAY_MILLIS);
                    break;
                case RESULT_MAIN:
                    mHandler.sendEmptyMessageDelayed(RESULT_MAIN, SPLASH_DELAY_MILLIS);
                    break;
                default:
                    break;
            }
            super.onPostExecute(result);
        }
    }

}