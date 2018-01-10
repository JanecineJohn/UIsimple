package com.example.ending.uisimple.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ending.uisimple.R;
import com.example.ending.uisimple.javabean.OnClassInfo;

import java.util.List;

/**
 * Created by dell on 2018/1/10.
 */

public class onClassInfoAdapter extends RecyclerView.Adapter<onClassInfoAdapter.ViewHolder>{

    private Context mContext;
    private List<OnClassInfo> onClassInfoList;

    public onClassInfoAdapter(List<OnClassInfo> onClassInfoList){
        this.onClassInfoList = onClassInfoList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linear;
        TextView stuInfo_tv;
        RecyclerView appInfo_rv;

        public ViewHolder(View itemView) {
            super(itemView);
            linear = itemView.findViewById(R.id.linear);
            stuInfo_tv = itemView.findViewById(R.id.studentInfo_TV);
            appInfo_rv = itemView.findViewById(R.id.appInfo_RV);
        }
    }
    @Override
    public onClassInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }

        //为view组件加载xml布局文件
        View view = LayoutInflater.from(mContext).inflate(R.layout.onclassinfo_view,
                parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final onClassInfoAdapter.ViewHolder holder, int position) {

        final OnClassInfo onClassInfo = onClassInfoList.get(position);
        holder.stuInfo_tv.setText(onClassInfo.getTrueName() + "("
                + onClassInfo.getSchoolID() + ")：");
        holder.appInfo_rv.setLayoutManager(new LinearLayoutManager(mContext));
        holder.appInfo_rv.setAdapter(new appInfoAdapter(onClassInfo.getAppList()));
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,holder.stuInfo_tv.getText(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return onClassInfoList.size();
    }

//    public void refreshAdapter(List<OnClassInfo> onClassInfoList){
//        this.onClassInfoList.addAll(onClassInfoList);//将传入的集合元素全部添加到adapter的集合中
//        notifyDataSetChanged();
//    }
}

