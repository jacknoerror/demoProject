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
    /* ���ݶ�begin */
    private final String TAG = "Gallery3DActivity";
    private Context mContext;
    
    // ͼƬ���ű��ʣ������Ļ�ߴ����С���ʣ�
    public static final int SCALE_FACTOR = 8;
    
    // ͼƬ��ࣨ���Ƹ�ͼƬ֮��ľ��룩
    private final int GALLERY_SPACING = -50;
    
    // �ؼ�
    private GalleryFlow mGalleryFlow;
    /* ���ݶ�end */

    /* ������begin */
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
        // ͼƬID
        int[] images = {
                R.drawable.egg_b3,
                R.drawable.egg_b3,
                R.drawable.egg_b3,
                R.drawable.egg_b3,
                R.drawable.egg_b3,
                R.drawable.egg_b3,
                 };

        ImageAdapter adapter = new ImageAdapter(mContext, images);
        // ����ͼƬ�Ŀ��
        int[] dimension = BitmapScaleDownUtil.getScreenDimension(getWindowManager().getDefaultDisplay());
        int imageWidth = dimension[0] / SCALE_FACTOR;
        int imageHeight = dimension[1] / SCALE_FACTOR;
        // ��ʼ��ͼƬ
        adapter.createImages(imageWidth, imageHeight);

        // ����Adapter����ʾλ��λ�ڿؼ��м䣬����ʹ�����Ҿ���"����"����
        mGalleryFlow = (GalleryFlow) findViewById(R.id.gallery_flow);
        mGalleryFlow.setSpacing(GALLERY_SPACING);
        mGalleryFlow.setAdapter(adapter);
        mGalleryFlow.setSelection(Integer.MAX_VALUE / 2);
        
        
        
        
    }
    /* ������end */
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