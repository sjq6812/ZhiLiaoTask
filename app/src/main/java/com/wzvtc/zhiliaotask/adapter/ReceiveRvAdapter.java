package com.wzvtc.zhiliaotask.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wzvtc.zhiliaotask.GetReceiveMessageListQuery;
import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.activity.ReceiveActivity;
import com.wzvtc.zhiliaotask.utils.Const;
import com.wzvtc.zhiliaotask.utils.TimeUtils;

import java.util.Collections;
import java.util.List;

/**
 * created by Litrainy on 2018-12-03 21:06
 */
public class ReceiveRvAdapter extends RecyclerView.Adapter<ReceiveRvAdapter.ViewHolder> {

    private Context mContext;
    private List<GetReceiveMessageListQuery.Content> mList = Collections.emptyList();

    public ReceiveRvAdapter(Context context) {
        mContext = context;
    }

    public void getList(List<GetReceiveMessageListQuery.Content> contentList) {
        mList = contentList;
        this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView name,title,time,isRead;
         ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView= (CardView) itemView;
            name = itemView.findViewById(R.id.receive_rv_name);
            title = itemView.findViewById(R.id.receive_rv_title);
            time = itemView.findViewById(R.id.receive_rv_time);
            isRead = itemView.findViewById(R.id.receive_rv_read);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_fr_rv_receive, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(v->{
            int position = holder.getAdapterPosition();
            GetReceiveMessageListQuery.Content message = mList.get(position);
            Intent intent = new Intent(mContext, ReceiveActivity.class);
            intent.putExtra(Const.MESSAGE_ID, message.id());
            intent.putExtra(Const.SEND_NAME, message.sendUser().nickname());
            intent.putExtra(Const.RECEIVE_TITLE, message.title());
            intent.putExtra(Const.RECEIVE_CONTENT, message.content());
            mContext.startActivity(intent);
        });
        return holder;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        GetReceiveMessageListQuery.Content message = mList.get(i);
        viewHolder.name.setText(message.sendUser().nickname());
        viewHolder.title.setText(message.title());
        if (message.receivers().get(0).readed()) {
            viewHolder.isRead.setText("已读");
            viewHolder.isRead.setTextColor(Color.GREEN);
        } else {
            viewHolder.isRead.setText("未读");
            viewHolder.isRead.setTextColor(Color.RED);
        }
        viewHolder.time.setText(TimeUtils.getTime((Long) message.createtime()));


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
