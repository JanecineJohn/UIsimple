package com.example.ending.uisimple.services;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.ending.uisimple.R;
import com.example.ending.uisimple.javabean.AppInfo;
import com.example.ending.uisimple.javabean.Chatter;
import com.example.ending.uisimple.utils.getAppInfo;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.ending.uisimple.utils.clearPreferences.clearAppInfo;
import static com.example.ending.uisimple.utils.clearPreferences.clearUserInfo;
import static com.example.ending.uisimple.utils.mBase64.DrawableToString;

public class WebSocketTeacher extends Service {
    WebSocketClient mWebSocketClient;
    Handler handler;
    //final String address = "ws://10.243.6.27:8080/websocket/onClass/84";
    String address;//服务器地址
    int classId;//课堂id


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Log.i("Service的onBind：","启动");
        address = intent.getStringExtra("address");//获得服务器地址
        classId = intent.getIntExtra("classId",0);//获得课堂id
        //Log.i("Service的服务器地址：",address);
        //Log.i("Service的课堂号：",classId + "");
        if (classId == 0){
            //Toast.makeText(WebSocketTeacher.this,"无法获得课堂Id",Toast.LENGTH_SHORT).show();
            //Log.i("教师界面","匹配不到课堂号");
        }else {
            connect();//用得到的服务器地址进行后台连接
            //Log.i("已经得到课堂号","准备进行webSocket连接");
        }
        return new MyBinder();
    }

    //内部类，里面提供方法，返回此service对象
    public class MyBinder extends Binder {
        public WebSocketTeacher getService(){
            return WebSocketTeacher.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.i("Service的onCreate：","启动");
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0){
                    Toast.makeText(WebSocketTeacher.this,"创建场景成功",Toast.LENGTH_SHORT).show();
                }else if (msg.what == 1){
                    Toast.makeText(WebSocketTeacher.this,"已退出场景",Toast.LENGTH_SHORT).show();
                }
            }
        };

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
                            handler.sendEmptyMessage(0);//0表示连接正常
                        }

                        @Override
                        public void onMessage(String s) {
                        }

                        @Override
                        public void onClose(int i, String s, boolean b) {
                            //连接断开
                            Log.d("webSocket相关信息---->","onClose，连接断开"+ i + "/" + s + "/" + b);
                            handler.sendEmptyMessage(1);
                            closeConnect();
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("webSocket相关信息---->","onError，出错：" + e);
                            handler.sendEmptyMessage(1);
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
        //断线重连
        if (mWebSocketClient == null){
            connect();
        }
        mWebSocketClient.send(msg);
    }
}
