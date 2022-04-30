package com.yu.gridimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * GridImageView
 * 宫格方式显示ImageView
 * @author jdy2002
 * emial 2475058223@qq.com
 */
public class GridImageView extends ViewGroup {

    private static final int MAX_IMAGE_COUNT = 9;
    private static final int IMAGE_CORNER_RADIUS = 18;

    /**
     * 缓存图片信息的集合
     */
    private final List<ImageInfo> imageInfoList = new ArrayList<>();
    /**
     * 上下文
     */
    private final Context context;
    /**
     * 窗口宽度
     */
    private int windowWidth;
    /**
     * 窗口高度
     */
    private int windowHeight;
    private int verticalSpacing;
    private int horizontalSpacing;
    private LoadListener loadListener;
    private OnImageItemClickListener listener = null;

    public GridImageView(Context context) {
        this(context, null);
    }

    public GridImageView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.GridImageView);
        verticalSpacing = typedArray.getDimensionPixelSize(R.styleable.GridImageView_verticalSpacing, 10);
        horizontalSpacing = typedArray.getDimensionPixelSize(R.styleable.GridImageView_horizontalSpacing, 10);
        typedArray.recycle();

        getWindowSize();
    }

    public void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.listener = listener;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
    }

    public GridImageView setSingleImageSize(int width, int height) {
        if (imageInfoList.isEmpty()) {
            return this;
        }
        final ImageInfo imageInfo = imageInfoList.get(0);
        imageInfo.setWidth(width);
        imageInfo.setHeight(height);
        return this;
    }

    /**
     * 测量 ViewGroup 宽高
     *
     * @param widthMeasureSpec  爸爸尺子信息
     * @param heightMeasureSpec 爸爸尺子信息
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 得到爸爸的 width mode
        /*// int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // int heightMode = MeasureSpec.getMode(heightMeasureSpec);*/
        int widthSize = MeasureSpec.getSize(widthMeasureSpec)
                - getPaddingLeft() - getPaddingEnd();
        int heightSize = 0; //= MeasureSpec.getSize(heightMeasureSpec);
        final int imageCount = imageInfoList.size();
        if (imageCount == 0) {
            // 没有图片直接设置尺寸为 0
            setMeasuredDimension(0, 0);
            return;
        }
        int row = 0;
        for (int i = 0; i < imageCount; i++) {
            final ImageInfo imageInfo = imageInfoList.get(i);
            // 得到图片宽高信息
            if (imageCount != 1) {
                getImageInfo(imageInfo, i, imageCount, widthSize);
            } else {
                // 单个图片单独设置宽高
                loadListener.getSingleImageViewSize(imageInfo);
                // 得到自适应的宽高
                getSingleImageSize(imageInfo);
            }
            final int imageWidth = imageInfo.getWidth();
            final int imageHeight = imageInfo.getHeight();
            final int imageRow = imageInfo.getRow();
            if (row != imageRow) {
                heightSize += imageHeight;
                if (row != 0) {
                    heightSize += verticalSpacing;
                }
                row = imageRow;
            }
            final RoundImageView imageView = (RoundImageView) getChildAt(i);
            final int childWidthMeasureSpec = MeasureSpec
                    .makeMeasureSpec(imageWidth, MeasureSpec.EXACTLY);
            final int childHeightMeasureSpec = MeasureSpec
                    .makeMeasureSpec(imageHeight, MeasureSpec.EXACTLY);

            imageView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
        // 设置测量的到尺寸
        widthSize += getPaddingLeft() + getPaddingRight();
        heightSize += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 对子View进行布局
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right;
        int bottom;
        int row = 1;
        for (int i = 0; i < imageInfoList.size(); i++) {
            final View child = getChildAt(i);
            final ImageInfo imageInfo = imageInfoList.get(i);
            final int imageRow = imageInfo.getRow();
            if (row != imageRow) {
                row = imageRow;
                left = getPaddingLeft();
                top += imageInfoList.get(i - 1).getHeight() + verticalSpacing;
            }
            right = left + imageInfo.getWidth();
            bottom = top + imageInfo.getHeight();
            child.layout(left, top, right, bottom);
            left += imageInfo.getWidth() + horizontalSpacing;
            if (loadListener != null) {
                loadListener.onLoadImage((RoundImageView) child, imageInfo);
            }
        }
    }

    /**
     * 得到 Window 的宽高
     */
    private void getWindowSize() {
        final WindowManager windowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
            final Rect bounds = windowMetrics.getBounds();
            windowWidth = bounds.width();
            windowHeight = bounds.height();
        } else {
            final Display display = windowManager.getDefaultDisplay();
            windowWidth = display.getWidth();
            windowHeight = display.getHeight();
        }
    }

    public void submitData(@Nullable List<String> urls, @NonNull LoadListener listener) {
        submitData(urls, listener, 0, 0);
    }

    public void submitData(@Nullable List<String> urls, @NonNull LoadListener listener,
                           int imageWidth, int imageHeight) {
        if (this.loadListener != listener) {
            this.loadListener = listener;
        }
        // 清除图片信息缓存
        imageInfoList.clear();
        if (urls == null || urls.isEmpty()) {
            // 如果未设置图片URL集合,则不进行显示图片
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        // 添加图片链接到集合
        final int urlCount = urls.size();
        for (int i = 0; i < Math.min(urlCount, loadListener.getMaxImageCount()); i++) {
            final String url = urls.get(i);
            final ImageInfo imageInfo = new ImageInfo();
            imageInfo.setUrl(url);
            if (urlCount == 1) {
                imageInfo.setWidth(imageWidth);
                imageInfo.setHeight(imageHeight);
            }
            imageInfoList.add(imageInfo);
        }
        // 改变或者构建 ImageView
        changedOrCreateImageView(urlCount);
    }

    private void changedOrCreateImageView(int urlCount) {
        final int childCount = getChildCount();
        if (childCount < urlCount) {
            for (int i = 0; i < urlCount; i++) {
                if (i >= childCount) {
                    createImageView(i);
                } else {
                    getChildAt(i).setVisibility(VISIBLE);
                }
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                final RoundImageView imageView = (RoundImageView) getChildAt(i);
                if (i >= urlCount) {
                    imageView.setImageDrawable(null);
                    imageView.setVisibility(GONE);
                } else {
                    imageView.setVisibility(VISIBLE);
                }
            }
        }
    }

    private void createImageView(int position) {
        final RoundImageView imageView = new RoundImageView(context);
        imageView.setRadius(loadListener.getImageCornerRadius());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(androidx.appcompat.R.attr.selectableItemBackground, typedValue, true);
            imageView.setForeground(ContextCompat.getDrawable(context, typedValue.resourceId));
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageItemClick(position, (RoundImageView) v);
            }
        });
        addView(imageView);
    }

    private void getSingleImageSize(ImageInfo imageInfo) {
        int width = imageInfo.getWidth();
        int height = imageInfo.getHeight();
        if (width == 0 || height == 0) {
            // 设置一个默认的高度
            width = windowWidth / 4;
            height = windowHeight / 4;
        } else {
            width = Math.min(width, windowWidth);
            int scaleWidth = imageInfo.getWidth() / windowWidth;
            if (scaleWidth == 0) {
                scaleWidth = 1;
            }
            height = height / scaleWidth;
            int maxHeight = windowWidth / 2;
            if (height > maxHeight) {
                height = maxHeight;
            }
        }
        imageInfo.setWidth(width);
        imageInfo.setHeight(height);
    }

    private void getImageInfo(ImageInfo imageInfo, int position, int count, int width) {
        int row = 0;
        int column;
        int imageSize;
        switch (count) {
            case 2:
            case 4:
                column = 2;
                break;
            case 3:
            case 6:
            case 9:
                column = 3;
                break;
            case 5:
                if (position < 2) {
                    row = 1;
                    column = 2;
                } else {
                    row = 2;
                    column = 3;
                }
                break;
            case 7:
                if (position < 3) {
                    row = 1;
                    column = 3;
                } else {
                    row = 2;
                    column = 4;
                }
                break;
            case 8:
                column = 4;
                break;
            default:
                throw new IllegalStateException("count > 9");
        }
        if (row == 0) {
            row = position / column + 1;
        }
        imageSize = (int) (width / (float) column + 0.5f);
        imageInfo.setRow(row);
        int imageWidth = (int) (imageSize - (column - 1) * horizontalSpacing / (float) column + 0.5f);
        int imageHeight = (int) (imageSize - (column - 1) * verticalSpacing / (float) column + 0.5f);
        imageInfo.setWidth(imageWidth);
        imageInfo.setHeight(imageHeight);
    }

    public interface OnImageItemClickListener {

        void onImageItemClick(int position, RoundImageView imageView);

    }

    public abstract static class LoadListener {

        public abstract void onLoadImage(RoundImageView imageView, ImageInfo imageInfo);

        public void getSingleImageViewSize(ImageInfo imageInfo) {
        }

        public int getMaxImageCount() {
            return MAX_IMAGE_COUNT;
        }

        public int getImageCornerRadius() {
            return IMAGE_CORNER_RADIUS;
        }

    }
}
