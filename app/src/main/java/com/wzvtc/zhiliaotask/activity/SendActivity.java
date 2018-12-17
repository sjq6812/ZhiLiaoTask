package com.wzvtc.zhiliaotask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.wzvtc.zhiliaotask.GetReceiversByMessageIdQuery;
import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.adapter.SendLvAdapter;
import com.wzvtc.zhiliaotask.application.ZhiLiaoApplication;
import com.wzvtc.zhiliaotask.utils.Const;

import org.jetbrains.annotations.NotNull;

public class SendActivity extends AppCompatActivity {


    private TextView mSendTitle, mSendContent, mSendReadCount, mSendUnreadCount;
    private ZhiLiaoApplication mApplication;
    private GetReceiversByMessageIdQuery mQuery;
    private ApolloQueryCall<GetReceiversByMessageIdQuery.Data> mCall;

    private ListView mListView;
    private SendLvAdapter mAdapter;
    private String mMessageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        initView();
        initTitle("详情");
        Intent intent = getIntent();
        mMessageId = intent.getStringExtra(Const.MESSAGE_ID);

        initData();
    }

    private void initData() {
        thread.start();
    }

    private void initTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
    }

    Thread thread=new Thread(()->{
        mQuery = GetReceiversByMessageIdQuery.builder()
                .id(mMessageId)
                .build();
        mCall = mApplication.getApolloClient().query(mQuery);
        mCall.enqueue(new ApolloCall.Callback<GetReceiversByMessageIdQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetReceiversByMessageIdQuery.Data> response) {
                GetReceiversByMessageIdQuery.Message message = response.data().Message();
                mAdapter.getList(message.receivers());
                runOnUiThread(()->{
                    mSendTitle.setText(message.title());
                    mSendContent.setText(message.content());
                    int readNum = 0;
                    int unReadNum = 0;
                    for (int j = 0; j <message.receivers().size(); j++) {
                        if (message.receivers().get(j).readed()) {
                            readNum++;
                        } else {
                            unReadNum++;
                        }
                    }
                    mSendReadCount.setText(String.valueOf(readNum));
                    mSendUnreadCount.setText(String.valueOf(unReadNum));
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.d("===========", e.toString());
            }
        });
    });

    private void initView() {
        mSendTitle = findViewById(R.id.send_title);
        mSendContent = findViewById(R.id.send_content);
        mSendReadCount = findViewById(R.id.send_read_count);
        mSendUnreadCount = findViewById(R.id.send_unread_count);
        mListView = findViewById(R.id.send_lv);
        mApplication = (ZhiLiaoApplication) getApplication();
        mAdapter = new SendLvAdapter(this, R.layout.item_ac_lv_send);
        mListView.setAdapter(mAdapter);
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
