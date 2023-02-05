package com.hjq.gson.factory.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TitleBar titleBar = findViewById(R.id.tb_main_bar);
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onTitleClick(TitleBar titleBar) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(titleBar.getTitle().toString()));
                startActivity(intent);
            }
        });
    }
}