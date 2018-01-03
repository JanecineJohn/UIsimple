package com.example.ending.uisimple;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static com.example.ending.uisimple.utils.clearPreferences.clearAppInfo;
import static com.example.ending.uisimple.utils.clearPreferences.clearUserInfo;

public class StudentOTCActivity extends AppCompatActivity {

    private TabLayout tabLayout;//标签栏
    private ViewPager viewPager;//滑动页面
    private FPAdapter fpAdapter;//适配器，适配Fragment和viewPager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_otc);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        fpAdapter = new FPAdapter(getSupportFragmentManager());//此类负责将Fragment和tab联系起来

        viewPager.setAdapter(fpAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        //实现弹窗询问代码
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.warning)
                .setTitle("警告")
                .setMessage("退出后需要重新扫描二维码才能再次进入课堂！")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clearAppInfo(StudentOTCActivity.this);
                        clearUserInfo(StudentOTCActivity.this);//清除缓存
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }
}
