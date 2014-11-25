package com.jack.ui.coverflow;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jack.R;

/**
 * @author pengyiming
 * @date 2013-9-30
 */
public class Gallery3DActivity extends Activity
{
    /* 数据段begin */
    private final String TAG = "Gallery3DActivity";
    private Context mContext;
    
    // 图片缩放倍率（相对屏幕尺寸的缩小倍率）
    public static final int SCALE_FACTOR = 8;
    
    // 图片间距（控制各图片之间的距离）
    private final int GALLERY_SPACING = -50;
    
    // 控件
    private GalleryFlow mGalleryFlow;
    /* 数据段end */

    /* 函数段begin */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        
        setContentView(R.layout.gallery_3d_activity_layout);
        initGallery();
    }
    
    private void initGallery()
    {
        // 图片ID
        int[] images = {
                R.drawable.egg_b3,
                R.drawable.egg_b3,
                R.drawable.egg_b3,
                R.drawable.egg_b3,
                R.drawable.egg_b3,
                R.drawable.egg_b3,
                 };

        ImageAdapter adapter = new ImageAdapter(mContext, images);
        // 计算图片的宽高
        int[] dimension = BitmapScaleDownUtil.getScreenDimension(getWindowManager().getDefaultDisplay());
        int imageWidth = dimension[0] / SCALE_FACTOR;
        int imageHeight = dimension[1] / SCALE_FACTOR;
        // 初始化图片
        adapter.createImages(imageWidth, imageHeight);

        // 设置Adapter，显示位置位于控件中间，这样使得左右均可"无限"滑动
        mGalleryFlow = (GalleryFlow) findViewById(R.id.gallery_flow);
        mGalleryFlow.setSpacing(GALLERY_SPACING);
        mGalleryFlow.setAdapter(adapter);
        mGalleryFlow.setSelection(Integer.MAX_VALUE / 2);
        
        
        
        
    }
    /* 函数段end */
    class SimpleImageAdapter extends BaseAdapter{
    	
    	public SimpleImageAdapter(){
    	}

    	
		@Override
		public int getCount() {
			return Integer.MAX_VALUE/2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
    	
    }
}