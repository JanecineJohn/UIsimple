package com.example.ending.uisimple.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ending.uisimple.R;
import com.example.ending.uisimple.javabean.AppInfo;

import java.util.List;

/**
 * Created by dell on 2017/12/22.
 */

public class appInfoAdapter extends RecyclerView.Adapter<appInfoAdapter.ViewHolder>{

    private Context mContext;
    private List<AppInfo> appInfoList;

    //获取传递过来的APP详细信息集合
    public appInfoAdapter(List<AppInfo> appInfoList){
        this.appInfoList = appInfoList;
    }

    //为appInfoAdapter创建ViewHolder，作用是容纳组件，并提高效率
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView app_Icon;
        TextView app_Label;
        TextView app_LTS;

        public ViewHolder(View itemView) {
            super(itemView);
            app_Icon = itemView.findViewById(R.id.app_Icon);
            app_Label = itemView.findViewById(R.id.app_Label);
            app_LTS = itemView.findViewById(R.id.app_LTS);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        //为view组件加载xml布局文件,使用父环境(MainActivity)的布局加载器
        View view = LayoutInflater.from(mContext).inflate(R.layout.appinfo_view,
                parent,false);
        final ViewHolder holder = new ViewHolder(view);//利用view创建一个ViewHolder实例
        //可以为ViewHolder及它里面的组件设置点击事件(以下用图标为例)：
        /*holder.app_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取点击的图片位置
                int position = holder.getAdapterPosition();
                AppInfo info = appInfoList.get(position);
            }
        });*/
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppInfo info = appInfoList.get(position);
        holder.app_Icon.setImageDrawable(info.getAppIcon());
        holder.app_Label.setText(info.getAppLabel());
        holder.app_LTS.setText("最近使用：" + info.getAppLTS());
    }

    @Override
    public int getItemCount() {
        return appInfoList.size();
    }

}
