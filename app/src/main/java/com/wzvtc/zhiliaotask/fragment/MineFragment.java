package com.wzvtc.zhiliaotask.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.activity.ChangePhoneActivity;
import com.wzvtc.zhiliaotask.activity.LoginActivity;
import com.wzvtc.zhiliaotask.activity.ChangePwdActivity;
import com.wzvtc.zhiliaotask.application.ZhiLiaoApplication;
import com.wzvtc.zhiliaotask.utils.Const;

/**
 * created by Litrainy on 2018-12-03 18:45
 */
public class MineFragment extends Fragment{

    private View mView;
    private ZhiLiaoApplication mApplication;
    private TextView mMineNickname,mMinePhone;
    private Button mResetPwd,mExitLogin,mResetPhone;
    private SharedPreferences mSharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine, container, false);
        initView(mView);
        initData();

        mResetPwd.setOnClickListener(v -> startActivity(new Intent(getActivity(),ChangePwdActivity.class)));
        mResetPhone.setOnClickListener(v -> startActivity(new Intent(getActivity(),ChangePhoneActivity.class)));

        mExitLogin.setOnClickListener(v -> {
            // TODO: 2018/12/15 0015 由于对登录没啥限制退出登录就不走网络请求了
            startActivity(new Intent(getActivity(), LoginActivity.class));
            mApplication.setIsLogin(0);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.clear();
            editor.apply();
        });

        return mView;
    }

    private void initData() {
        mSharedPreferences = getActivity().getSharedPreferences(Const.USER_INFO, Context.MODE_PRIVATE);
        mMineNickname.setText(mSharedPreferences.getString(Const.USER_NICK_NAME, "未登录"));
        mMinePhone.setText(mSharedPreferences.getString(Const.USER_PHONE, "未登录"));
    }

    private void initView(View mView) {
        mMinePhone = mView.findViewById(R.id.mine_phone);
        mMineNickname =  mView.findViewById(R.id.mine_nickname);
        mResetPwd =  mView.findViewById(R.id.reset_pwd);
        mResetPhone = mView.findViewById(R.id.reset_phone);
        mExitLogin =  mView.findViewById(R.id.exit_login);
        mApplication = (ZhiLiaoApplication) getActivity().getApplication();
    }



}
