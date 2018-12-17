package com.wzvtc.zhiliaotask.activity;

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
import com.wzvtc.zhiliaotask.ModifyPasswordMutation;
import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.application.ZhiLiaoApplication;
import com.wzvtc.zhiliaotask.utils.UserUtils;

import org.jetbrains.annotations.NotNull;


public class ChangePwdActivity extends AppCompatActivity {

    private EditText mChangePwdEditPwd, mChangePwdEditPwdCheck;
    private Button mChangePwdBtnSure, mChangePwdBtnCancel;
    private String mNewPwd,mNewPwdCheck;
    private ModifyPasswordMutation mMutation;
    private ApolloCall<ModifyPasswordMutation.Data> mCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepwd);
        initView();
        initTitle("修改密码");
        mChangePwdBtnCancel.setOnClickListener(v -> finish());
        mChangePwdBtnSure.setOnClickListener(v -> {
            mNewPwd = mChangePwdEditPwd.getText().toString().trim();
            mNewPwdCheck = mChangePwdEditPwdCheck.getText().toString().trim();
            if (!TextUtils.isEmpty(mNewPwd) && !TextUtils.isEmpty(mNewPwdCheck)) {
                if (mNewPwd.equals(mNewPwdCheck)) {
                    mThread.start();
                } else {
                    Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "填写不能为空", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        mChangePwdEditPwd = findViewById(R.id.change_pwd_edit_pwd);
        mChangePwdEditPwdCheck = findViewById(R.id.change_pwd_edit_pwd_check);
        mChangePwdBtnSure = findViewById(R.id.change_pwd_btn_sure);
        mChangePwdBtnCancel = findViewById(R.id.change_pwd_btn_cancel);
    }

    private void initTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
    }


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

    Thread mThread=new Thread(()->{
        mMutation = ModifyPasswordMutation.builder()
                .id(UserUtils.getUserId(this))
                .password(mNewPwdCheck)
                .build();
        mCall = ZhiLiaoApplication.getInstance().getApolloClient().mutate(mMutation);
        mCall.enqueue(new ApolloCall.Callback<ModifyPasswordMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<ModifyPasswordMutation.Data> response) {
                runOnUiThread(() -> Toast.makeText(ChangePwdActivity.this, "修改成功", Toast.LENGTH_SHORT).show());
                finish();
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
            }
        });
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCall != null) {
            mCall.cancel();
        }
        if (mCall != null) {
            mCall.cancel();
        }
    }

}
