package com.wzvtc.zhiliaotask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.RegisterUserMutation;
import com.wzvtc.zhiliaotask.application.ZhiLiaoApplication;
import com.wzvtc.zhiliaotask.type.User_;
import com.wzvtc.zhiliaotask.utils.Const;

import org.jetbrains.annotations.NotNull;

public class RegisterActivity extends AppCompatActivity {

    private EditText mRegisterEditName, mRegisterEditPhone, mRegisterEditPwd;
    private Button mRegisterBtnCancel, mRegisterBtnSure;
    private ZhiLiaoApplication mZhiLiaoApplication;
    private RegisterUserMutation mMutation;
    private ApolloCall<RegisterUserMutation.Data> mCall;
    private String mNickName, mPhone, mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initTitle("注册");
        mRegisterBtnCancel.setOnClickListener(v -> finish());

        mRegisterBtnSure.setOnClickListener(v -> {
            mNickName = mRegisterEditName.getText().toString().trim();
            mPhone = mRegisterEditPhone.getText().toString().trim();
            mPassword = mRegisterEditPwd.getText().toString().trim();
            if (!TextUtils.isEmpty(mNickName) && !TextUtils.isEmpty(mPhone) && !TextUtils.isEmpty(mPassword)) {
                if (mPassword.length() < 6 || mPassword.length() > 11) {
                    Toast.makeText(this, "电话必须在6-11位", Toast.LENGTH_SHORT).show();
                } else {
                    mThread.start();
                }
            } else {
                Toast.makeText(this, "请填写完整资料", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        mZhiLiaoApplication = (ZhiLiaoApplication) getApplication();
        mRegisterEditName = findViewById(R.id.register_edit_name);
        mRegisterEditPhone = findViewById(R.id.register_edit_phone);
        mRegisterBtnCancel = findViewById(R.id.register_btn_cancel);
        mRegisterEditPwd = findViewById(R.id.change_pwd_edit_pwd);
        mRegisterBtnSure = findViewById(R.id.register_btn_sure);
    }

    private void initTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
    }


    Thread mThread = new Thread(() -> {

        mMutation = RegisterUserMutation.builder()
                .user(User_.builder()
                        .nickname(mNickName)
                        .phone(mPhone)
                        .password(mPassword)
                        .build())
                .build();
        mCall = mZhiLiaoApplication.getApolloClient().mutate(mMutation);
        mCall.enqueue(new ApolloCall.Callback<RegisterUserMutation.Data>() {

            private Intent mIntent;

            @Override
            public void onResponse(@NotNull Response<RegisterUserMutation.Data> response) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show());
                mIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                mIntent.putExtra(Const.USER_PHONE, mPhone);
                mIntent.putExtra(Const.USER_PWD, mPassword);
                startActivity(mIntent);
                finish();
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
            }
        });
    });

    /**
     * 左上角返回点击
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCall != null) {
            mCall.cancel();
        }
    }

}
