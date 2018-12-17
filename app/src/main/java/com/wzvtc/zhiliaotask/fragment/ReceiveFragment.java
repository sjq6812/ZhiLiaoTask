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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_receive, container, false);

        initView();
        initData();
        mSwipeRefreshLayout.setOnRefreshListener(this::initData);

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
        new Thread(() -> {
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
        }).start();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCall != null) {
            mCall.cancel();
        }
    }


}
