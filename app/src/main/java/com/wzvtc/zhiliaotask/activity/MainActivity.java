package com.wzvtc.zhiliaotask.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.adapter.MainViewPagerAdapter;
import com.wzvtc.zhiliaotask.fragment.MineFragment;
import com.wzvtc.zhiliaotask.fragment.ReceiveFragment;
import com.wzvtc.zhiliaotask.fragment.SendFragment;
import com.wzvtc.zhiliaotask.view.ColorChangeView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Fragment> mFragmentList;
    private ViewPager viewPager;
    private List<ColorChangeView> mTabs = new ArrayList<>();
    private ColorChangeView btnSend, btnReceive, btnMine;

    private static boolean isExit = false;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        btnSend.setOnClickListener(v -> viewPager.setCurrentItem(0));
        btnReceive.setOnClickListener(v -> viewPager.setCurrentItem(1));
        btnMine.setOnClickListener(v -> viewPager.setCurrentItem(2));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (v > 0) {
                    ColorChangeView left = mTabs.get(i);
                    ColorChangeView right = mTabs.get(i + 1);
                    left.setDirection(1);
                    right.setDirection(0);
                    left.setProgress(1 - v);
                    right.setProgress(v);
                }
            }
            @Override
            public void onPageSelected(int i) {

            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    private void initView() {
        viewPager = findViewById(R.id.viewpager);
        btnSend = findViewById(R.id.frag_send);
        btnReceive = findViewById(R.id.frag_receive);
        btnMine = findViewById(R.id.frag_mine);

        mTabs.add(btnSend);
        mTabs.add(btnReceive);
        mTabs.add(btnMine);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new SendFragment());
        mFragmentList.add(new ReceiveFragment());
        mFragmentList.add(new MineFragment());
        viewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager(), mFragmentList));
        viewPager.setCurrentItem(0);

    }




}
