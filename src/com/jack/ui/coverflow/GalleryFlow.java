package com.jack.ui.coverflow;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

/**
 * @author pengyiming
 * @date 2013-9-30
 * @function 自定义控件
 */
public class GalleryFlow extends Gallery
{
    /* 数据段begin */
    private final String TAG = "GalleryFlow";
    
    // 边缘图片最大旋转角度
    private final float MAX_ROTATION_ANGLE = 45;
    // 中心图片最大前置距离
    private final float MAX_TRANSLATE_DISTANCE = -100;
    // GalleryFlow中心X坐标
    private int mGalleryFlowCenterX;
    // 3D变换Camera
    private Camera mCamera = new Camera();
    /* 数据段end */

    /* 函数段begin */
    public GalleryFlow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        
        // 开启，在滑动过程中，回调getChildStaticTransformation()
        this.setStaticTransformationsEnabled(true);
    }
    
    /**
     * @function 获取GalleryFlow中心X坐标
     * @return
     */
    private int getCenterXOfCoverflow()
    {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }
    
    /**
     * @function 获取GalleryFlow子view的中心X坐标
     * @param childView
     * @return
     */
    private int getCenterXOfView(View childView)
    {
        return childView.getLeft() + childView.getWidth() / 2;
    }
    
    /**
     * @note step1 系统调用measure()方法时，回调此方法；表明此时系统正在计算view的大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        mGalleryFlowCenterX = getCenterXOfCoverflow();
        Log.d(TAG, "onMeasure, mGalleryFlowCenterX = " + mGalleryFlowCenterX);
    }
    
    /**
     * @note step2 系统调用layout()方法时，回调此方法；表明此时系统正在给child view分配空间
     * @note 必定在onMeasure()之后回调，但与onSizeChanged()先后顺序不一定
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        
        mGalleryFlowCenterX = getCenterXOfCoverflow();
        Log.d(TAG, "onLayout, mGalleryFlowCenterX = " + mGalleryFlowCenterX);
    }
    
    /**
     * @note step2 系统调用measure()方法后，当需要绘制此view时，回调此方法；表明此时系统已计算完view的大小
     * @note 必定在onMeasure()之后回调，但与onSizeChanged()先后顺序不一定
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        
        mGalleryFlowCenterX = getCenterXOfCoverflow();
        Log.d(TAG, "onSizeChanged, mGalleryFlowCenterX = " + mGalleryFlowCenterX);
    }
    
    /* gallery每个item每次移动一个会调用该方法
     * @see android.widget.Gallery#getChildStaticTransformation(android.view.View, android.view.animation.Transformation)
     */
    @Override
    protected boolean getChildStaticTransformation(View childView, Transformation t)
    {
    	Log.i(TAG, childView.getTag()+"");
        // 计算旋转角度
        float rotationAngle = calculateRotationAngle(childView);
        
        // 计算前置距离
        float translateDistance = calculateTranslateDistance(childView);
        
        // 开始3D变换
        transformChildView(childView, t, rotationAngle, translateDistance);
        
        return true;
    }
    
    /**
     * @function 计算GalleryFlow子view的旋转角度
     * @note1 位于Gallery中心的图片不旋转
     * @note2 位于Gallery中心两侧的图片按照离中心点的距离旋转
     * @param childView
     * @return
     */
    private float calculateRotationAngle(View childView)
    {
        final int childCenterX = getCenterXOfView(childView);
        float rotationAngle = 0;
        //似乎是通过中心点来计算旋转角度 --taotao
        rotationAngle = (mGalleryFlowCenterX - childCenterX) / (float) mGalleryFlowCenterX * MAX_ROTATION_ANGLE;
        
        if (rotationAngle > MAX_ROTATION_ANGLE)
        {
            rotationAngle = MAX_ROTATION_ANGLE;
        }
        else if (rotationAngle < -MAX_ROTATION_ANGLE)
        {
            rotationAngle = -MAX_ROTATION_ANGLE;
        }
        
        return rotationAngle;
    }
    
    /**
     * @function 计算GalleryFlow子view的前置距离
     * @note1 位于Gallery中心的图片前置
     * @note2 位于Gallery中心两侧的图片不前置
     * @param childView
     * @return
     */
    private float calculateTranslateDistance(View childView)
    {
        final int childCenterX = getCenterXOfView(childView);
        float translateDistance = 00;
        
        if (mGalleryFlowCenterX == childCenterX)
        {
            translateDistance = MAX_TRANSLATE_DISTANCE;
        }
        
        return translateDistance;
    }
    
    /**
     * @function 开始变换GalleryFlow子view
     * @param childView
     * @param t
     * @param rotationAngle
     * @param translateDistance
     */
    private void transformChildView(View childView, Transformation t, float rotationAngle, float translateDistance)
    {
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);
        
        final Matrix imageMatrix = t.getMatrix();
        final int imageWidth = childView.getWidth();
        final int imageHeight = childView.getHeight();
        
        mCamera.save();
        
        /* rotateY */
        // 在Y轴上旋转，位于中心的图片不旋转，中心两侧的图片竖向向里或向外翻转。
//        mCamera.rotateY(rotationAngle);
        mCamera.rotateZ(rotationAngle);
        /* rotateY */
        
        /* translateZ */
        // 在Z轴上前置，位于中心的图片会有放大的效果
        mCamera.translate(0, 0, translateDistance);
        /* translateZ */
        
        // 开始变换（我的理解是：移动Camera，在2D视图上产生3D效果）
        mCamera.getMatrix(imageMatrix);
//        imageMatrix.preTranslate(-imageWidth / 2, -imageHeight / 2);
//        imageMatrix.postTranslate(imageWidth / 2, imageHeight / 2);
        
        mCamera.restore();
    }
    /* 函数段end */
}