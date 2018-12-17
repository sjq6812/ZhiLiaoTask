package com.wzvtc.zhiliaotask.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.wzvtc.zhiliaotask.GetSendMessageListQuery;
import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.activity.AddSendActivity;
import com.wzvtc.zhiliaotask.adapter.SendRvAdapter;
import com.wzvtc.zhiliaotask.application.ZhiLiaoApplication;
import com.wzvtc.zhiliaotask.type.Qfilter;
import com.wzvtc.zhiliaotask.type.QueryFilterOperator;
import com.wzvtc.zhiliaotask.utils.UserUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

/**
 * created by Litrainy on 2018-12-03 18:44
 */
public class SendFragment extends Fragment {

    private View mView;
    private SendRvAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddMessage;
    private ZhiLiaoApplication mApplication;
    private GetSendMessageListQuery mQuery;
    private ApolloCall<GetSendMessageListQuery.Data> mCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_send, container, false);
        initView(mView);
        refresh();
        initData();

        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        mAddMessage.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddSendActivity.class)));

        return mView;
    }


    private void initData() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                query();
            }
        }, 5000);
    }

    public void refresh() {
        new Thread(this::query).start();
    }


    private void initView(View mView) {
        mRecyclerView = mView.findViewById(R.id.rv_send);
        mAddMessage = mView.findViewById(R.id.add_message);
        mSwipeRefreshLayout = mView.findViewById(R.id.send_refresh);
        mApplication = (ZhiLiaoApplication) getActivity().getApplication();
        mAdapter = new SendRvAdapter(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void query() {
        mQuery = GetSendMessageListQuery.builder()
                .qfilter(Qfilter.builder()
                        .key("sendUser.id")
                        .operator(QueryFilterOperator.EQUEAL)
                        .value(UserUtils.getUserId(getActivity()))
                        .build())
                .build();
        mCall = mApplication.getApolloClient().query(mQuery);
        mCall.enqueue(new ApolloCall.Callback<GetSendMessageListQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetSendMessageListQuery.Data> response) {
                getActivity().runOnUiThread(() -> {
                    mAdapter.getList(response.data().MessageList().content());
                    mSwipeRefreshLayout.setRefreshing(false);
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.d("=============: ", e.toString());
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
