package com.example.ending.uisimple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ending.uisimple.javabean.User;
import com.example.ending.uisimple.utils.postJson;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerpassword;
    private EditText registerpasswordconfirm;
    private EditText email;
    private EditText phonenum;
    private EditText name;
    private EditText number;
    private int flagrp=1;
    private int flagrpc=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void sendRegisterHttp(User user){
        String url = getResources().getString(R.string.registerAddress);//服务器注册地址
        postJson post = new postJson();
        post.httpPostJson(url,user).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String errorMessage = e.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this,"无法注册，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseMessage = response.body().string();
                Log.i("注册页面",responseMessage);
                if (responseMessage.equals("successful")){
                    Intent intent=new Intent(RegisterActivity.this,AfterRegisterActivity.class);
                    intent.putExtra("response",responseMessage);
                    startActivity(intent);
                    RegisterActivity.this.finish();
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this,responseMessage,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.RegisterBack:
            {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
                startActivity(intent);
                break;
            }
            case R.id.RegisterConfirm:
            {
                registerpasswordconfirm = (EditText) findViewById(R.id.RegisterPasswordConfirmEdit);
                registerpassword=(EditText)findViewById(R.id.RegisterPasswordEdit) ;
                email=(EditText)findViewById(R.id.RegisterUsernameEdit) ;
                phonenum=(EditText)findViewById(R.id.RegisterEMEdit);
                name=(EditText)findViewById(R.id.RegisterPNEdit) ;
                number=(EditText)findViewById(R.id.RegisterNumberEdit) ;
                //判断上面5个参数是否符合规范
                if (registerpasswordconfirm.getText().toString().trim().equals("")
                        || registerpassword.getText().toString().trim().equals("")
                        || email.getText().toString().trim().equals("")
                        || phonenum.getText().toString().trim().equals("")
                        || name.getText().toString().trim().equals("")){
                    Toast.makeText(RegisterActivity.this,"注册信息不完整",Toast.LENGTH_SHORT).show();
                    break;
                }
                if (registerpasswordconfirm.getText().toString().equals(registerpassword.getText().toString())){
                    User registerUser = new User();
                    registerUser.setUsername(name.getText().toString().trim());
                    registerUser.setPhone(phonenum.getText().toString().trim());
                    registerUser.setEmail(email.getText().toString().trim());
                    registerUser.setPassword(registerpassword.getText().toString().trim());
                    //这里把上面这五个参数全部发到服务器
                    sendRegisterHttp(registerUser);
                }else {
                    Toast.makeText(RegisterActivity.this,"密码前后不匹配",Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.RegisterPasswordConfirmVisual:
            {
                Button temp1=(Button)findViewById(R.id.RegisterPasswordConfirmVisual) ;
                registerpasswordconfirm=(EditText)findViewById(R.id.RegisterPasswordConfirmEdit);
                if(flagrpc==1) {
                    registerpasswordconfirm.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    temp1.setBackgroundResource(R.mipmap.ic_eye8);
                    flagrpc--;
                }
                else{
                    registerpasswordconfirm.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT );
                    temp1.setBackgroundResource(R.mipmap.ic_eye5);
                    flagrpc++;
                }
                break;
            }
            case R.id.RegisterPasswordVisual:
            {
                Button temp2=(Button)findViewById(R.id.RegisterPasswordVisual) ;
                registerpassword=(EditText)findViewById(R.id.RegisterPasswordEdit);
                if(flagrp==1) {
                    registerpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    temp2.setBackgroundResource(R.mipmap.ic_eye8);
                    flagrp--;
                }
                else{
                    registerpassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT );
                    temp2.setBackgroundResource(R.mipmap.ic_eye5);
                    flagrp++;
                }
                break;
            }
        }
    }
}
