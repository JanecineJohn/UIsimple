package com.example.ending.uisimple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TeacherOTCActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_otc);
    }
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.EndTheClass:{

                Intent intent=new Intent(TeacherOTCActivity.this,TeacherACActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
