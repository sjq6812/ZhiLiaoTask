package com.wzvtc.zhiliaotask.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wzvtc.zhiliaotask.GetReceiversByMessageIdQuery;
import com.wzvtc.zhiliaotask.R;

import java.util.Collections;
import java.util.List;

/**
 * created by Litrainy on 2018-12-11 20:10
 */
public class SendLvAdapter extends ArrayAdapter<GetReceiversByMessageIdQuery.Receiver> {

    private Context mContext;
    private int mResource;
    private List<GetReceiversByMessageIdQuery.Receiver> mList = Collections.emptyList();

    public SendLvAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mContext = context;
        mResource = resource;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.btn = view.findViewById(R.id.send_lv_sms_notice);
            viewHolder.nickName = view.findViewById(R.id.send_lv_name);
            viewHolder.isRead = view.findViewById(R.id.send_lv_read);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        GetReceiversByMessageIdQuery.Receiver message = mList.get(position);
        viewHolder.nickName.setText(message.user().nickname());
        if (message.readed()) {
            viewHolder.isRead.setText("已读");
            viewHolder.isRead.setTextColor(Color.GREEN);
        } else {
            viewHolder.isRead.setText("未读");
            viewHolder.isRead.setTextColor(Color.RED);
        }
        viewHolder.btn.setOnClickListener(v -> Toast.makeText(mContext, position + "此功能下版本开放", Toast.LENGTH_SHORT).show());

        return view;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public void getList(List<GetReceiversByMessageIdQuery.Receiver> contentList) {
        mList = contentList;
    }

    class ViewHolder{
        TextView nickName;
        TextView isRead;
        Button btn;
    }
}

