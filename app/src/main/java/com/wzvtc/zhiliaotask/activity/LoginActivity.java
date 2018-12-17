package com.wzvtc.zhiliaotask.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.application.ZhiLiaoApplication;
import com.wzvtc.zhiliaotask.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Button mLoginBtnLogin, mLoginBtnRegister;
    private EditText mLoginEditAccount, mLoginEditPwd;
    private OkHttpClient mOkHttpClient;
    private String mAccount, mPassword;
    private Intent mIntent;
    private SharedPreferences.Editor mEditor;
    private JSONObject mJsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initTitle("用户登录");
        mLoginBtnLogin.setOnClickListener(v -> {
            mAccount = mLoginEditAccount.getText().toString().trim();
            mPassword = mLoginEditPwd.getText().toString().trim();
            if (!TextUtils.isEmpty(mAccount) && !TextUtils.isEmpty(mPassword)) {
                mLoginThread.start();
            } else {
                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        mLoginBtnRegister.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, RegisterActivity.class), 1);
        });

    }

    private void initView() {
        mLoginBtnRegister = findViewById(R.id.login_btn_register);
        mLoginBtnLogin = findViewById(R.id.login_btn_login);
        mLoginEditPwd = findViewById(R.id.login_edit_pwd);
        mLoginEditAccount = findViewById(R.id.login_edit_account);
    }

    private void initTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
    }

    Thread mLoginThread = new Thread(() -> {
        mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://120.79.40.85:9090/login")
                .post(new FormBody.Builder()
                        .add("username", mAccount)
                        .add("password", mPassword)
                        .build())
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "网络出现了问题", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // TODO: 2018/12/15 0015 token暂无用处先不处理了
                    try {
                        mJsonObject = new JSONObject(response.body().string());
                        mEditor = getSharedPreferences(Const.USER_INFO, MODE_PRIVATE).edit();
                        mEditor.putString(Const.USER_ID, mJsonObject.getString("id"));
                        mEditor.putString(Const.USER_NICK_NAME, mJsonObject.getString("nickname"));
                        mEditor.putString(Const.USER_PHONE, mJsonObject.getString("phone"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show());
                    ZhiLiaoApplication.getInstance().setIsLogin(1);
                    mEditor.apply();
                    mIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mIntent);
                    finish();
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show());
                }
            }
        });
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                        mLoginEditAccount.setText(data.getStringExtra(Const.USER_PHONE));
                        mLoginEditPwd.setText(data.getStringExtra(Const.USER_PWD));
                }
                break;
            default:
                break;
        }
    }
}
