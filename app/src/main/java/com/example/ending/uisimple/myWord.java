package com.example.ending.uisimple;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ending.uisimple.javabean.Chatter;
import com.example.ending.uisimple.services.WebSocketClientService;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class myWord extends Fragment {

    IntentFilter intentFilter;//注册广播接收器用
    webSocketReceiver mwebSocketReceiver;//广播接收器
    WebSocketClientService service;//service对象，需要里面的方法发送信息
    View view;//后面需要用它来初始化组件
    TextView show;
    EditText editMessage;
    Button send;

    String uid;//用户Id
    String userName;//用户名
    String trueName;//真实姓名
    int classId;//课堂id;

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_my_word,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        show = view.findViewById(R.id.frag_chat_txv);
        editMessage = view.findViewById(R.id.frag_chat_et);
        send = view.findViewById(R.id.frag_chat_bt);

        //初始化用户id和用户名
        SharedPreferences pref = getActivity().getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        uid = pref.getString("uid","");
        userName = pref.getString("userName","");
        trueName = pref.getString("trueName","");
        //trueName = pref.getString("trueName","");

        //注册广播接收器
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.dell.broadcast.WebSocket_BROADCAST");
        mwebSocketReceiver = new webSocketReceiver();
        getActivity().registerReceiver
                (mwebSocketReceiver,intentFilter);

        //获得扫描得到的服务器地址
        String address = getActivity().getIntent().getStringExtra("address");
        classId = getActivity().getIntent().getIntExtra("classId",0);

        Log.i("myWord的服务器地址：",address);
        Log.i("myWord的课堂号：",classId + "");

        //开启服务,并向服务传递服务器地址
        Intent intent = new Intent(getActivity(),WebSocketClientService.class);
        intent.putExtra("address",address);
        intent.putExtra("classId",classId);
        getActivity().bindService
                (intent,mServiceConnection,Context.BIND_AUTO_CREATE);

        //点击发送消息
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editMessage.getText().toString();
                if (message.equals("")){
                    Toast.makeText(getActivity(),"不能发送空消息",Toast.LENGTH_SHORT).show();
                }else {
                    //service.sendMsg(message);
                    Chatter chatter = new Chatter(trueName,"",true,message,classId);
                    String chatterJson = new Gson().toJson(chatter);
                    service.sendMsg(chatterJson);
                    editMessage.setText("");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver
                (mwebSocketReceiver);//注销广播接收器
        //关闭后台服务
        getActivity().unbindService(mServiceConnection);
    }

    //内部类，用于接收webSocket发出的广播
    class webSocketReceiver extends BroadcastReceiver {
        //只要接收到webSocket发出的广播，就会回调此方法
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("newMessage");
            Gson gson = new Gson();
            Chatter chatter = gson.fromJson(message,Chatter.class);
            String name=chatter.getUserName();
            String messager=chatter.getMessage();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(d);
            date=date.substring(11);
            int namelength=name.length();
            int datelength=date.length();
            int messagerlength=messager.length();
            if(messager.equals("进入课堂(系统信息)"))
            {
                Spannable string=new SpannableString(name + messager+ '\n');
                string.setSpan(new ForegroundColorSpan(Color.GRAY), 0, namelength+messagerlength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                show.append(string);
            }
            else if(name.equals(userName))
            {
                Spannable string=new SpannableString(name +"  "+date+'\n'+ messager+ '\n');
                string.setSpan(new ForegroundColorSpan(Color.parseColor("#4CAF50")), 0, namelength+2+datelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                string.setSpan(new StyleSpan(Typeface.BOLD),  0, namelength,  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                string.setSpan(new AbsoluteSizeSpan(55), 0, namelength, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                show.append(string);
            }
            else
            {
                Spannable string=new SpannableString(name +"  "+date+'\n'+ messager+ '\n');
                string.setSpan(new ForegroundColorSpan(Color.parseColor("#03A9F4")), 0, namelength+2+datelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                string.setSpan(new StyleSpan(Typeface.BOLD),  0, namelength,  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                string.setSpan(new AbsoluteSizeSpan(55), 0, namelength, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                show.append(string);
            }
//            show.append(chatter.getUserName() + "："
//                    + chatter.getMessage() + '\n');
        }
    }

    //匿名内部类，用于连接fragment和service
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((WebSocketClientService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };
}
