package com.wzvtc.zhiliaotask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.SetUserReadedMutation;
import com.wzvtc.zhiliaotask.application.ZhiLiaoApplication;
import com.wzvtc.zhiliaotask.utils.Const;
import com.wzvtc.zhiliaotask.utils.UserUtils;

import org.jetbrains.annotations.NotNull;

// TODO: 2018/12/11 0011 返回的时候调用刷新数据
public class ReceiveActivity extends AppCompatActivity {

    private TextView mReceiveTitle, mReceiveContent;
    private Button mSetRemind, mSetRead;
    private String messageId;
    private ZhiLiaoApplication mApplication;
    private ApolloCall<SetUserReadedMutation.Data> mCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        initView();
        Intent intent = getIntent();
        initTitle(intent);
        mReceiveTitle.setText(intent.getStringExtra(Const.RECEIVE_TITLE));
        mReceiveContent.setText(intent.getStringExtra(Const.RECEIVE_CONTENT));

        messageId = intent.getStringExtra(Const.MESSAGE_ID);

        mSetRemind.setOnClickListener(v -> Toast.makeText(this, "此功能正在测试中", Toast.LENGTH_SHORT).show());

        mSetRead.setOnClickListener(v -> new Thread(()->{
            SetUserReadedMutation setUserReadedMutation = SetUserReadedMutation.builder()
                    .messageId(messageId)
                    .userId(UserUtils.getUserId(this))
                    .build();
            mCall = mApplication.getApolloClient().mutate(setUserReadedMutation);
            mCall.enqueue(new ApolloCall.Callback<SetUserReadedMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<SetUserReadedMutation.Data> response) {
                    if (response.data().messages_userReaded()) {
                        runOnUiThread(() -> Toast.makeText(ReceiveActivity.this, "已读", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    Log.d("-------", "onFailure: "+e.toString());
                }
            });
        }).start());

    }

    private void initTitle(Intent intent) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(intent.getStringExtra(Const.SEND_NAME));
    }


    private void initView() {
        mReceiveTitle = findViewById(R.id.receive_title);
        mReceiveContent = findViewById(R.id.receive_content);
        mSetRemind = findViewById(R.id.set_remind);
        mSetRead = findViewById(R.id.set_read);
        mApplication = (ZhiLiaoApplication) getApplication();
    }

    /**
     *左上角返回点击
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
