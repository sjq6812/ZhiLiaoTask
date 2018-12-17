package com.wzvtc.zhiliaotask.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wzvtc.zhiliaotask.GetReceiversByMessageIdQuery;
import com.wzvtc.zhiliaotask.GetUserListQuery;
import com.wzvtc.zhiliaotask.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * created by Litrainy on 2018-12-11 20:10
 */
public class AddSendAdapter extends ArrayAdapter<GetReceiversByMessageIdQuery.Receiver> {

    private Context mContext;
    private int mResource;
    private List<GetUserListQuery.Content> mList = Collections.emptyList();
    private List<String> mUserList = new ArrayList<>();

    public AddSendAdapter(@NonNull Context context, int resource) {
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
            viewHolder.username = view.findViewById(R.id.add_lv_user);
            viewHolder.checked = view.findViewById(R.id.add_lv_check);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        GetUserListQuery.Content message = mList.get(position);
        viewHolder.username.setText(message.nickname());
        viewHolder.checked.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mUserList.add(message.id());
            } else {
                mUserList.remove(message.id());
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public void getList(List<GetUserListQuery.Content> contentList) {
        mList = contentList;
    }

    public List<String> getUserList() {
        return mUserList;
    }

    class ViewHolder {
        TextView username;
        CheckBox checked;
    }

}

