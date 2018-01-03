package com.example.ending.uisimple;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ending.uisimple.adapters.appInfoAdapter;
import com.example.ending.uisimple.javabean.AppInfo;
import com.example.ending.uisimple.javabean.Chatter;
import com.example.ending.uisimple.utils.getAppInfo;
import com.google.gson.Gson;

import java.util.List;


public class mySituation extends Fragment {

    finishActivityReceiver receiver;
    View view;
    RecyclerView recyclerView;
    long startTime;//进入课堂的时间
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_my_situation,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.i("mySituation","onActivityCreate开启");
        startTime = getActivity().getSharedPreferences("userInfo",Context.MODE_PRIVATE).getLong("enterTime",99999999);
        initView();//初始化界面组件
    }

    //据说要用到这个
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.i("mySituation","onResume开启");
        List<AppInfo> appInfoList;//新建一个集合存放getappinfo()返回的app详细信息集合
        getAppInfo get = new getAppInfo();//创建一个getAppInfo实例，里面有获取app详细信息并返回的方法
        appInfoList = get.getappinfo(getActivity(),startTime);//获得App使用情况
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //为各组件设置间距，暂时用不到
        //recyclerView.addItemDecoration(new AppInfoItemDecoration(0));
        recyclerView.setAdapter(new appInfoAdapter(appInfoList));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i("mySituation","onDestroy开启");
        getActivity().unregisterReceiver(receiver);//退出此碎片时，取消注册
    }

    /**初始化各个组件
     * */
    private void initView(){

        //注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.dell.broadcast.mySituation_FINISH");
        receiver = new finishActivityReceiver();
        getActivity().registerReceiver
                (receiver,intentFilter);


        recyclerView = view.findViewById(R.id.appInfo_Rv);
    }

    //内部类，用于接收webSocket发出的广播
    class finishActivityReceiver extends BroadcastReceiver {
        //只要接收到webSocket发出的广播，就会回调此方法
        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().finish();
        }
    }
}
