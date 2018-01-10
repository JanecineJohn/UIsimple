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
        TextView app_UTS;

        public ViewHolder(View itemView) {
            super(itemView);
            app_Icon = itemView.findViewById(R.id.app_Icon);
            app_Label = itemView.findViewById(R.id.app_Label);
            app_UTS = itemView.findViewById(R.id.app_UsedTime);
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
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppInfo info = appInfoList.get(position);
        holder.app_Icon.setImageDrawable(info.getAppIcon());
        holder.app_Label.setText(info.getAppLabel());
        long usedTime = info.getAppUsedTime();
        long usedTime_H = usedTime/(60*60*1000);
        long usedTime_M = (usedTime-usedTime_H*(60*60*1000))/(60*1000);
        long usedTime_S = (usedTime-(usedTime_H*60*60*1000)-(usedTime_M*60*1000))/1000;
        holder.app_UTS.setText("使用时长：" + usedTime_H + "时" +
                usedTime_M + "分" + usedTime_S + "秒");
    }

    @Override
    public int getItemCount() {
        return appInfoList.size();
    }

}
