package com.example.ending.uisimple.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.ending.uisimple.javabean.Chatter;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClientService extends Service {
    WebSocketClient mWebSocketClient;
    //final String address = "ws://10.243.6.27:8080/websocket/onClass/84";
    String address;//服务器地址
    String trueName;//姓名
    String schoolId;//学号
    int classId;//课堂id

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Service的onBind：","启动");
        address = intent.getStringExtra("address");//获得服务器地址
        classId = intent.getIntExtra("classId",0);//获得课堂id
        Log.i("Service的服务器地址：",address);
        Log.i("Service的课堂号：",classId + "");
        if (classId == 0){
            Toast.makeText(WebSocketClientService.this,"无法获得课堂Id",Toast.LENGTH_SHORT).show();
        }else {
            connect();//用得到的服务器地址进行后台连接
            Log.i("已经得到课堂号","准备进行webSocket连接");
        }
        return new MyBinder();
    }

    //内部类，里面提供方法，返回此service对象
    public class MyBinder extends Binder {
        public WebSocketClientService getService(){
            return WebSocketClientService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service的onCreate：","启动");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeConnect();
    }

    //连接服务器
    private void connect(){
        new Thread(){
            @Override
            public void run() {
                try {
                    mWebSocketClient = new WebSocketClient(new URI(address)) {
                        @Override
                        public void onOpen(ServerHandshake serverHandshake) {
                            Log.d("webSocket相关信息---->","onOpen，建立webSocket连接");
                            SharedPreferences pref = getSharedPreferences("userInfo",MODE_PRIVATE);
                            trueName = pref.getString("trueName","");
                            schoolId = pref.getString("schoolId","无");
                            Chatter chatter = new Chatter(trueName,schoolId,true,"进入课堂(系统信息)",classId);
                            String chatterJson = new Gson().toJson(chatter);
                            sendMsg(chatterJson);
                        }

                        @Override
                        public void onMessage(String s) {
                            //当有服务端发送来消息的时候，回调此函数
                            Log.d("webSocket相关信息---->","onMessage，服务端发送过来的信息为：" + s);
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                boolean isClose = jsonObject.getString("message").equals("close");
                                boolean isStudent = jsonObject.getBoolean("isStudent");
                                if (isClose && !isStudent){
                                    //如果isClose为真，isStudent为假，代表这条信息是老师发的关闭课堂消息
                                    Chatter closeChat = new Chatter("系统信息","",false,"已断开连接",classId);//构建系统信息
                                    String closeJson = new Gson().toJson(closeChat);
                                    Intent intent = new Intent
                                            ("com.example.dell.broadcast.WebSocket_BROADCAST");
                                    intent.putExtra("newMessage",closeJson);
                                    sendBroadcast(intent);
                                    mWebSocketClient.onClose(404,"客户端主动断开",false);//主动断开连接
                                }else {
                                    Intent intent = new Intent
                                            ("com.example.dell.broadcast.WebSocket_BROADCAST");
                                    intent.putExtra("newMessage",s);
                                    sendBroadcast(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onClose(int i, String s, boolean b) {
                            //连接断开
                            Log.d("webSocket相关信息---->","onClose，连接断开"+ i + "/" + s + "/" + b);
                            closeConnect();
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("webSocket相关信息---->","onError，出错：" + e);
                        }
                    };
                    mWebSocketClient.connect();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    //断开连接
    private void closeConnect(){
        try {
            mWebSocketClient.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mWebSocketClient = null;
        }
    }
    //发送信息
    public void sendMsg(String msg){
        mWebSocketClient.send(msg);
    }
}
