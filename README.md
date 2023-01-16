# GridImageView
快速简单实现宫格显示ImageView，解决RecyclerView复用卡顿问题，你值得使用

#### 导入方法（一）

- 1，点击下载代码
- 2，下载后解压，复制GridImageView文件夹
- 3，复制GridImageVie文件夹到你的工程Module目录下面
- 4，使用方法看我的工程

#### 导入方法（二）

build.gradle添加依赖

```
implementation 'com.gitee.jdy2002:GridImageView:1.0.2'
```

#### 添加布局

```
<com.yu.gridimageview.GridImageView
    android:layout_width="match_parent"
    android:id="@+id/gridImageView"
    android:padding="10dp"
    android:layout_height="wrap_content"/>
```

#### JAVA中实现

```
GridImageView gridImageView = findViewById(R.id.gridImageView);
        // 设置垂直分割线间隔
        //gridImageView.setVerticalSpacing(10);
        // 设置水平分割线间隔
        //gridImageView.setHorizontalSpacing(10);
        final List<String> mUrls = new ArrayList<>();
        for (int i = 0; i < 5; i ++) {
            mUrls.add("图片链接");
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
```

#### 单张图片问题

显示一张图片由于不知道图片宽高，所以需要设置单张图片所要显示的宽高，如不设置，显示默认大小

```
gridImageView.setSingleImageSize(width, height);
```

#### 图片间隔

```
// 设置垂直分割线间隔
gridImageView.setVerticalSpacing(10);
// 设置水平分割线间隔
gridImageView.setHorizontalSpacing(10);
```

#### 图片圆角

重写LoadListener抽象类中的 **getImageCornerRadius()** 方法

```
new GridImageView.LoadListener() {
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
```


#### RecyclerView Adapter具体实现方法

全部代码请看我的工程

```
/**
     * RecyclerView 适配器
     */
    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private final List<MyBean> mData;
        /**
         *  这个对象尽量这样是，不要直接写在OnBindViewHolder里面
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

```


#### 截图

![输入图片说明](Screenshot_2022-04-24-20-58-29-629_com.yu.gridima.jpg)
![输入图片说明](Screenshot_2022-04-24-20-58-24-541_com.yu.gridima.jpg)
