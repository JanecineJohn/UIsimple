package com.example.ending.uisimple.utils;

import android.util.Log;

import com.example.ending.uisimple.javabean.User;
import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by xin on 2017/12/9.
 */

public class postJson {
    Gson gson = new Gson();
    //此方法接收http服务器接口，还有要post的数据
    public Call httpPostJson(String url, User user){
        String jsonUser = gson.toJson(user);//将接收的user类对象转为json字符串
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");//设置发送格式为json
        OkHttpClient client = new OkHttpClient();//创建一个okhttp实例
        RequestBody body = RequestBody.create(JSON,jsonUser);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        return call;
    }
}
