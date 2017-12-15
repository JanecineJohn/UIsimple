package com.example.ending.uisimple;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ending.uisimple.javabean.User;
import com.example.ending.uisimple.utils.postJson;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button loginButton;
    private int flag=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.button:
            {
                username=(EditText) findViewById(R.id.usernameEdit);
                password=(EditText) findViewById(R.id.passwordEdit);
                /**这里把username和password发到服务器检验对错,返回一个boolean参数用来代替下面if的判断
                还要返回学生姓名和学号*/
                if (username.getText().toString().trim().equals("") ||
                        password.getText().toString().trim().equals("")){
                    Toast.makeText(LoginActivity.this,"请输入用户名和密码",Toast.LENGTH_SHORT).show();
                    break;
                }
                User userLogin = new User();
                userLogin.setUsername(username.getText().toString().trim());
                userLogin.setPassword(password.getText().toString().trim());
                sendLoginHttp(userLogin);//调用此方法,将用户名密码发到服务器
                /**test*/
                /*if(username.getText().toString().equals("admin")&&password.getText().toString().equals("admin"))
                {
                    if(language.equals("zh"))
                    {
                        Toast.makeText(this,"登陆成功",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_LONG).show();
                    }
                    Intent intent = new Intent();
                    intent.putExtra("extra_data","num_name");
                    setResult(1,intent);
                    this.finish();
                    break;
                }
                else
                {
                    flag--;
                    if(flag==0)
                    {
                        if(language.equals("zh"))
                        {
                            Toast.makeText(this,"错误三次，假装你已被ban",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(this, "three times wrong,you have been banned", Toast.LENGTH_LONG).show();
                        }
                        loginButton=(Button) findViewById(R.id.button);
                        loginButton.setEnabled(false);
                        loginButton.setBackgroundColor(Color.parseColor("#9E9E9E"));
                        loginButton.setBackgroundResource(R.drawable.false_button_selector);
                        break;
                    }
                    else
                    {
                        if(language.equals("zh"))
                        {
                            Toast.makeText(this,"账号或密码错误，可再尝试"+flag+"次。",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(this, "username or password is wrong.You can try "+(flag)+" more times", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }*/
                break;
            }
            case R.id.LoginBack:
            {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
                startActivity(intent);
                break;
            }
        }
    }
    public void sendRegisterHttp(User user){
        String url = "http://10.243.6.27:8080//UserServlet?method=login";//服务器注册地址
        postJson post = new postJson();
        post.httpPostJson(url,user).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errorMessage = e.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseMessage = response.toString();
                Intent intent=new Intent(LoginActivity.this,AfterRegisterActivity.class);
                intent.putExtra("response",responseMessage);
                startActivity(intent);
            }
        });
    }
    public void sendLoginHttp(User user){
        String url = "http://10.243.6.27:8080//UserServlet?method=login";//服务器登陆接口
        postJson post = new postJson();
        Log.d("发送登陆请求","正在接收返回信息");
        post.httpPostJson(url,user).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errorMessage = e.toString();
                Log.d("无法接收返回信息","某方面出错了");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Log.d("接收到返回信息","用户信息准备写入文件");
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    User userResult = gson.fromJson(jsonObject.toString(),User.class);
                    userResult.getUsername();//获取返回的用户名
                    userResult.getUid();//获取返回的用户ID
                    SharedPreferences.Editor editor =
                            getSharedPreferences("userInfo",MODE_PRIVATE).edit();
                    editor.putString("userID",userResult.getUid());//将用户ID写进文件
                    editor.putString("userName",userResult.getUsername());//将用户名写进文件
                    editor.apply();
                    Log.d("接收到返回信息","用户信息已经写入文件");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Locale locale = getResources().getConfiguration().locale;
                            String language = locale.getLanguage();
                            if(language.equals("zh"))
                            {
                                Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    Intent intent = new Intent();
                    intent.putExtra("extra_data","num_name");
                    setResult(1,intent);
                    LoginActivity.this.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
