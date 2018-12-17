package com.wzvtc.zhiliaotask.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wzvtc.zhiliaotask.GetSendMessageListQuery;
import com.wzvtc.zhiliaotask.R;
import com.wzvtc.zhiliaotask.activity.SendActivity;
import com.wzvtc.zhiliaotask.utils.Const;
import com.wzvtc.zhiliaotask.utils.TimeUtils;

import java.util.Collections;
import java.util.List;

/**
 * created by Litrainy on 2018-12-03 20:51
 */
public class SendRvAdapter extends RecyclerView.Adapter<SendRvAdapter.ViewHolder> {

    private Context mContext;
    private List<GetSendMessageListQuery.Content> mList = Collections.emptyList();

    public SendRvAdapter(Context context) {
        mContext = context;
    }

    public void getList(List<GetSendMessageListQuery.Content> contentList) {
        mList = contentList;
        this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, time, unreadCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.send_rv_title);
            content = itemView.findViewById(R.id.send_rv_content);
            time = itemView.findViewById(R.id.send_rv_time);
            unreadCount = itemView.findViewById(R.id.send_rv_count);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_fr_rv_send, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            GetSendMessageListQuery.Content message = mList.get(position);
            Intent intent = new Intent(mContext, SendActivity.class);
            intent.putExtra(Const.MESSAGE_ID, message.id());
            mContext.startActivity(intent);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        GetSendMessageListQuery.Content message = mList.get(i);
        viewHolder.title.setText(message.title());
        viewHolder.content.setText(message.content());
        viewHolder.time.setText(TimeUtils.getTime((Long) message.createtime()));
        viewHolder.unreadCount.setText(String.valueOf(message.receivers().size()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


}
