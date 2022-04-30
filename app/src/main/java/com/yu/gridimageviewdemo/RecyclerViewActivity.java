package com.yu.gridimageviewdemo;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yu.gridimageview.GridImageView;
import com.yu.gridimageview.ImageInfo;
import com.yu.gridimageview.RoundImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 显示在RecyclerView中
 */
public class RecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                // super.getItemOffsets(outRect, view, parent, state);
                outRect.set(32, 32, 32, 32);
            }
        });

        String url1 = "https://img1.baidu.com/it/u=2535256966,3819121136&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500";
        String url2 = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202106%2F15%2F20210615152332_36903.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1653378104&t=0d26a481e12f08655e05c1484a0ffa10";

        final List<MyBean> mData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            MyBean myBean = new MyBean();
            myBean.setTitle("我是标题" + i + ", 哈哈哈!!!!!");
            myBean.setMessage("我是冬日暖雨，欢迎来到酷安关注我，哈哈哈哈");
            // 随机生成图片链接
            final List<String> images = new ArrayList<>();
            Random random = new Random();
            int count = random.nextInt(9);
            for (int j = 0; j < count; j++) {
                if (count % 2 == 0) {
                    images.add(url1);
                } else {
                    images.add(url2);
                }
            }
            myBean.setImages(images);
            mData.add(myBean);
        }

        recyclerView.setAdapter(new MyAdapter(mData));

    }

    /**
     * RecyclerView 适配器
     */
    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private final List<MyBean> mData;
        /**
         * 这个对象尽量这样是，不要直接写在OnBindViewHolder里面
         */
        private final GridImageView.LoadListener loadListener = new GridImageView.LoadListener() {
            @Override
            public void onLoadImage(RoundImageView imageView, ImageInfo imageInfo) {
                Glide.with(imageView.getContext())
                        .load(imageInfo.getUrl())
                        // 记得设置这个大小，对图片进行裁剪一下再显示
                        .override(imageInfo.getWidth(), imageInfo.getHeight())
                        .into(imageView);
            }

            /**
             * 圆角大小
             * @return 圆角半径
             */
            @Override
            public int getImageCornerRadius() {
                return 28;
            }
        };

        public MyAdapter(List<MyBean> mData) {
            this.mData = mData;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false));
            viewHolder.gridImageView.setOnImageItemClickListener(new GridImageView.OnImageItemClickListener() {
                @SuppressLint({"WrongConstant", "ShowToast"})
                @Override
                public void onImageItemClick(int position, RoundImageView imageView) {
                    // 这里你可以继续进行回调到Activity里面去
                    final int adapterPosition = viewHolder.getAdapterPosition();
                    Toast.makeText(parent.getContext(), "url: " + mData.get(adapterPosition).getImages().get(position), 500).show();
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.title.setText(mData.get(position).getTitle());
            holder.message.setText(mData.get(position).getMessage());
            // 提交数据，并设置单张图片应该显示的宽高
            // 这个LoadListener记得写成全局变量，防止频繁创建
            holder.gridImageView.submitData(mData.get(position).getImages(), loadListener, 200, 150);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            TextView title;
            TextView message;
            GridImageView gridImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                message = itemView.findViewById(R.id.message);
                gridImageView = itemView.findViewById(R.id.gridImageView);
                // 设置间隔
               /*// gridImageView.setVerticalSpacing(10);
                //gridImageView.setHorizontalSpacing(10);*/

            }

        }

    }

    static class MyBean {

        private String title;
        private String message;
        private List<String> images;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

    }
}
