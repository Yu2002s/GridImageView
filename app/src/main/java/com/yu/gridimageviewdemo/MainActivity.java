package com.yu.gridimageviewdemo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * GridImageView
 * @author jdy2002
 * email 2475058223@qq.com
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.normal).setOnClickListener(v ->
                startActivity(new Intent(v.getContext(), NormalActivity.class)));

        findViewById(R.id.recyclerView).setOnClickListener(v ->
                startActivity(new Intent(v.getContext(), RecyclerViewActivity.class)));
    }
}