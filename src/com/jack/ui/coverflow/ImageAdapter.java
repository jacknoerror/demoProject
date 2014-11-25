package com.jack.ui.coverflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * @author pengyiming
 * @date 2013-9-30
 * @function GalleryFlow适配器
 */
public class ImageAdapter extends BaseAdapter
{
    /* 数据段begin */
    private final String TAG = "ImageAdapter";
    private Context mContext;
    
    //图片数组
    private int[] mImageIds ;
    //图片控件数组
    private ImageView[] mImages;
    //图片控件LayoutParams
    private GalleryFlow.LayoutParams mImagesLayoutParams;
    /* 数据段end */

    /* 函数段begin */
    public ImageAdapter(Context context, int[] imageIds)
    {
        mContext = context;
        mImageIds = imageIds;
        mImages = new ImageView[mImageIds.length];
        mImagesLayoutParams = new GalleryFlow.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.WRAP_CONTENT);
    }
    
    /**
     * @function 根据指定宽高创建待绘制的Bitmap，并绘制到ImageView控件上
     * @param imageWidth
     * @param imageHeight
     * @return void
     */
    public void createImages(int imageWidth, int imageHeight)
    {
        // 原图与倒影的间距5px
        final int gapHeight = 5;
        
        int index = 0;
        for (int imageId : mImageIds)
        {
            /* step1 采样方式解析原图并生成倒影 */
            // 解析原图，生成原图Bitmap对象
//            Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(), imageId);
            Bitmap originalImage = BitmapScaleDownUtil.decodeSampledBitmapFromResource(mContext.getResources(), imageId, imageWidth, imageHeight);
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            
            // Y轴方向反向，实质就是X轴翻转
            Matrix matrix = new Matrix();
            matrix.setScale(1, -1);
            // 且仅取原图下半部分创建倒影Bitmap对象
            Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);
            
            /* step2 绘制 */
            // 创建一个可包含原图+间距+倒影的新图Bitmap对象
            Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + gapHeight + height / 2), Config.ARGB_8888);
            // 在新图Bitmap对象之上创建画布
            Canvas canvas = new Canvas(bitmapWithReflection);
            // 抗锯齿效果
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG));
            // 绘制原图
            canvas.drawBitmap(originalImage, 0, 0, null);
            // 绘制间距
            Paint gapPaint = new Paint();
            gapPaint.setColor(0xFFCCCCCC);
            canvas.drawRect(0, height, width, height + gapHeight, gapPaint);
            // 绘制倒影
            canvas.drawBitmap(reflectionImage, 0, height + gapHeight, null);
            
            /* step3 渲染 */
            // 创建一个线性渐变的渲染器用于渲染倒影
            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(0, height, 0, (height + gapHeight + height / 2), 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
            // 设置画笔渲染器
            paint.setShader(shader);
            // 设置图片混合模式
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            // 渲染倒影+间距
            canvas.drawRect(0, height, width, (height + gapHeight + height / 2), paint);
            
            /* step4 在ImageView控件上绘制 */
            ImageView imageView = new ImageView(mContext);
            imageView.setImageBitmap(bitmapWithReflection);
            imageView.setLayoutParams(mImagesLayoutParams);
            // 打log
            imageView.setTag(index);
            
            /* step5 释放heap */
            originalImage.recycle();
            reflectionImage.recycle();
//          bitmapWithReflection.recycle();
            
            mImages[index++] = imageView;
        }
    }

    @Override
    public int getCount()
    {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Object getItem(int position)
    {
        return mImages[position];
    }
    
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return mImages[position % mImages.length];
    }
    /* 函数段end */
}