package com.demo.haisheng.loggerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import utils.Logger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.d(TAG,"打印debug");
        Logger.i(TAG,"打印info");
        Logger.e(TAG,"打印error");
    }
}
