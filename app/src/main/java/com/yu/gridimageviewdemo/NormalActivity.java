package com.yu.gridimageviewdemo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.yu.gridimageview.GridImageView;
import com.yu.gridimageview.ImageInfo;
import com.yu.gridimageview.RoundImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示在普通布局上
 */
public class NormalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);

        GridImageView gridImageView = findViewById(R.id.gridImageView);
        // 设置垂直分割线间隔
        //gridImageView.setVerticalSpacing(10);
        // 设置水平分割线间隔
        //gridImageView.setHorizontalSpacing(10);
        final List<String> mUrls = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mUrls.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2F1115%2F101021113337%2F211010113337-6-1200.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1653365879&t=8335d41125d5a35328cfd9937254a38a");
        }
        // 设置图片链接集合
        gridImageView
                // 设置单张图片显示的宽高，如果可能显示一张图片，则需要设置这个
                // .setSingleImageSize()
                // 开始提交
                .submitData(mUrls, new GridImageView.LoadListener() {
                    @Override
                    public void onLoadImage(RoundImageView imageView, ImageInfo imageInfo) {
                        Glide.with(imageView.getContext())
                                .load(imageInfo.getUrl())
                                // 这里记得设置宽高，对图片进行裁剪显示
                                .override(imageInfo.getWidth(), imageInfo.getHeight())
                                .into(imageView);
                    }
                });

        gridImageView.setOnImageItemClickListener(new GridImageView.OnImageItemClickListener() {
            @Override
            public void onImageItemClick(int position, RoundImageView imageView) {
                Toast.makeText(imageView.getContext(), "position: " + position, Toast.LENGTH_LONG).show();
            }
        });
    }
}
