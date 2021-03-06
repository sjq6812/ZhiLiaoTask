package com.wzvtc.zhiliaotask.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.wzvtc.zhiliaotask.GetReceiveMessageListQuery;
import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.adapter.ReceiveRvAdapter;
import com.wzvtc.zhiliaotask.application.ZhiLiaoApplication;
import com.wzvtc.zhiliaotask.type.Qfilter;
import com.wzvtc.zhiliaotask.type.QueryFilterOperator;
import com.wzvtc.zhiliaotask.utils.UserUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

/**
 * created by Litrainy on 2018-12-03 18:45
 */
public class ReceiveFragment extends Fragment {


    private View mView;
    private RecyclerView mRecyclerView;
    private ReceiveRvAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ZhiLiaoApplication mApplication;
    private GetReceiveMessageListQuery mQuery;
    private ApolloCall<GetReceiveMessageListQuery.Data> mCall;
    private Timer mTimer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_receive, container, false);

        initView();
        initData();
        refresh();
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);

        return mView;
    }


    private void initView() {
        mRecyclerView = mView.findViewById(R.id.receive_rv);
        mSwipeRefreshLayout = mView.findViewById(R.id.receive_refresh);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mAdapter = new ReceiveRvAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mApplication = (ZhiLiaoApplication) getActivity().getApplication();

    }


    private void initData() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                query();
            }
        }, 10000);
    }

    public void refresh() {
        new Thread(this::query).start();
    }

    public void query(){
        if (getActivity() != null) {
            mQuery = GetReceiveMessageListQuery.builder()
                    .qfilter(Qfilter.builder()
                            .key("receivers.user.id")
                            .operator(QueryFilterOperator.EQUEAL)
                            .value(UserUtils.getUserId(getActivity()))
                            .build())
                    .build();
            mCall = mApplication.getApolloClient().query(mQuery);
            mCall.enqueue(new ApolloCall.Callback<GetReceiveMessageListQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<GetReceiveMessageListQuery.Data> response) {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTimer.cancel();
        if (mCall != null) {
            mCall.cancel();
        }
    }

}
