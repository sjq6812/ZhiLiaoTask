package com.wzvtc.zhiliaotask.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.wzvtc.zhiliaotask.GetUserListQuery;
import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.SetMessageSendMutation;
import com.wzvtc.zhiliaotask.adapter.AddSendAdapter;
import com.wzvtc.zhiliaotask.application.ZhiLiaoApplication;
import com.wzvtc.zhiliaotask.utils.UserUtils;

import org.jetbrains.annotations.NotNull;

public class AddSendActivity extends AppCompatActivity {


    private EditText mAddTitle, mAddContent;
    private ListView mListView;
    private AddSendAdapter mAdapter;
    private Button mSubmit;
    private ZhiLiaoApplication mApplication;
    private GetUserListQuery mQuery;
    private SetMessageSendMutation mMutation;
    private ApolloQueryCall<GetUserListQuery.Data> mQueryApolloCall;
    private ApolloCall<SetMessageSendMutation.Data> mMutationApolloCall;
    private String mTitle,mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_send);
        initView();
        initData();
        initTitle("发送通知");
        mSubmit.setOnClickListener(v -> {
            mTitle = mAddTitle.getText().toString();
            mContent = mAddContent.getText().toString();
            if (TextUtils.isEmpty(mTitle)||TextUtils.isEmpty(mContent)) {
                Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
            } else if (mAdapter.getUserList().isEmpty()) {
                Toast.makeText(this, "发送人不能为空", Toast.LENGTH_SHORT).show();
            } else {
                mMutationThread.start();
            }

        });
    }

    private void initTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
    }

    private void initData() {
        mQueryThread.start();
    }

    private void initView() {
        mAddTitle = findViewById(R.id.add_title);
        mAddContent = findViewById(R.id.add_content);
        mListView = findViewById(R.id.add_list);
        mSubmit = findViewById(R.id.submit);
        mAdapter = new AddSendAdapter(this, R.layout.item_ac_lv_add);
        mListView.setAdapter(mAdapter);
        mApplication = (ZhiLiaoApplication) getApplication();
    }

    Thread mQueryThread = new Thread(() -> {
        mQuery = GetUserListQuery.builder().build();
        mQueryApolloCall = mApplication.getApolloClient().query(mQuery);
        mQueryApolloCall.enqueue(new ApolloCall.Callback<GetUserListQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetUserListQuery.Data> response) {
                mAdapter.getList(response.data().UserList().content());
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
            }
        });

    });

    Thread mMutationThread = new Thread(() -> {
        mMutation = SetMessageSendMutation.builder()
                .title(mAddTitle.getText().toString())
                .content(mAddContent.getText().toString())
                .sendUserId(UserUtils.getUserId(this))
                .receiverUserIdArr(mAdapter.getUserList())
                .build();
        mMutationApolloCall = mApplication.getApolloClient().mutate(mMutation);
        mMutationApolloCall.enqueue(new ApolloCall.Callback<SetMessageSendMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<SetMessageSendMutation.Data> response) {
                    runOnUiThread(()-> Toast.makeText(mApplication, "发送成功", Toast.LENGTH_SHORT).show());
                    finish();
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                runOnUiThread(()-> Toast.makeText(mApplication, "发送失败", Toast.LENGTH_SHORT).show());
            }
        });
    });

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
        if (mMutationApolloCall != null) {
            mMutationApolloCall.cancel();
        }
        if (mQueryApolloCall != null) {
            mQueryApolloCall.cancel();
        }
    }

}
