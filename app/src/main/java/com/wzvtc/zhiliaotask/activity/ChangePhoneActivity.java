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
import com.wzvtc.zhiliaotask.ModifyPhoneMutation;
import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.application.ZhiLiaoApplication;
import com.wzvtc.zhiliaotask.utils.UserUtils;

import org.jetbrains.annotations.NotNull;

public class ChangePhoneActivity extends AppCompatActivity{

    private EditText mChangePhoneEditPwd,mChangePhoneEditPwdCheck;
    private Button mChangePhoneBtnSure,mChangePhoneBtnCancel;
    private String mNewPhone,mNewPhoneCheck;
    private ModifyPhoneMutation mMutation;
    private ApolloCall<ModifyPhoneMutation.Data> mCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        initView();

        initTitle("修改手机");
        mChangePhoneBtnCancel.setOnClickListener(v -> finish());
        mChangePhoneBtnSure.setOnClickListener(v -> {
            mNewPhone = mChangePhoneEditPwd.getText().toString().trim();
            mNewPhoneCheck = mChangePhoneEditPwdCheck.getText().toString().trim();
            if (!TextUtils.isEmpty(mNewPhone) && !TextUtils.isEmpty(mNewPhoneCheck)) {
                if (mNewPhone.equals(mNewPhoneCheck)) {
                    mThread.start();
                } else {
                    Toast.makeText(this, "两次号码不一致", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "填写不能为空", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {
        mChangePhoneEditPwd = findViewById(R.id.change_phone_edit_pwd);
        mChangePhoneEditPwdCheck =  findViewById(R.id.change_phone_edit_pwd_check);
        mChangePhoneBtnSure = findViewById(R.id.change_phone_btn_sure);
        mChangePhoneBtnCancel =  findViewById(R.id.change_phone_btn_cancel);
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
        mMutation = ModifyPhoneMutation.builder()
                .userId(UserUtils.getUserId(this))
                .phone(mNewPhoneCheck)
                .build();
        mCall = ZhiLiaoApplication.getInstance().getApolloClient().mutate(mMutation);
        mCall.enqueue(new ApolloCall.Callback<ModifyPhoneMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<ModifyPhoneMutation.Data> response) {
                runOnUiThread(() -> Toast.makeText(ChangePhoneActivity.this, "修改成功", Toast.LENGTH_SHORT).show());
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
    }


}
