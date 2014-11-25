package com.jack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;

import com.jack.ui.MyImageView;
import com.jack.utils.JackUtils;

public class ViewPagerActivity extends Activity implements ViewPager.OnPageChangeListener ,OnTouchListener{
	private final String TAG = ViewPagerActivity.class.getSimpleName();
	private final String[] FILENAMES = new String[]{"pics/10-24.jpg","pics/10-32.jpg",
			 			"pics/2B-30.jpg","pics/2B-31.jpg","pics/2B-32.jpg"};
	//Arrays.asList(assetManager.list("images"))
	private final int screenWidth = 1080, screenHeight=1920;
	
	ViewPager mViewPager;
	PagerTabStrip mTabStrip;
	MyViewPagerAdapter mAdapter;
	MyBitmapSetHandler mHandler;
	GestureDetector gestureScanner;
	
	List<MyImageView> mImgListBig;
	int curPage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewpager);
		init();
		
		
	}
	
	
	private void init() {
		mHandler = new MyBitmapSetHandler();
		mImgListBig = new ArrayList<MyImageView>();
		
		mViewPager = (ViewPager)findViewById(R.id.vp_viewpager);
		mAdapter = new MyViewPagerAdapter(new ArrayList<View>());
		mViewPager.setAdapter(mAdapter);
		gestureScanner = new GestureDetector(this, new MySimpleGesture());
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setOnTouchListener(this);
		
		initViews();
		requestData();
	}


	private void initViews() {
		if(mAdapter==null) return;
		for(int i=0;i<FILENAMES.length;i++){
			View view = LayoutInflater.from(this).inflate(R.layout.layout_vp_view, null);
			MyImageView img = (MyImageView)view.findViewById(R.id.img_vp_view);
			mImgListBig.add(img);
			mAdapter.addView(view);
		}
		
	}


	private void requestData() {
		// TODO Auto-generated method stub
		
		updateData();
	}


	private void updateData() {
		if(mAdapter==null) return;
		// TODO Auto-generated method stub
		new MyAssetsBitmapThread(FILENAMES).start();
		
		mAdapter.notifyDataSetChanged();
	}
	
	private void setBitmapToImageView(int index, Bitmap bm){
		if(mImgListBig!=null&&mImgListBig.size()>index){
			mImgListBig.get(index).setImageBitmap(bm);
			mAdapter.notifyDataSetChanged();
		}
	}
	private boolean isScaleDirty(MyImageView imageView){
		return imageView.getScale()> imageView.getMiniZoom();
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
//		Log.i(TAG, "onscr::"+arg1+"--a--b--"+arg2);
		/*if(isScaleDirty((MyImageView)mImgListBig.get(curPage)))//TODO 道边
			mViewPager.scrollTo(curPage*screenWidth, 0); //
*/	}
	@Override
	public void onPageSelected(int arg0) {
		curPage = arg0;
//		loadCurPage();
		
	}
	boolean fxxk;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (gestureScanner != null) {
			return gestureScanner.onTouchEvent(event)||isScaleDirty((MyImageView)mImgListBig.get(curPage));
		}
		return false;
	}
	
	public class MyViewPagerAdapter extends PagerAdapter{
		private List<View> mListViews;
		
		public MyViewPagerAdapter(List<View> mListViews) {
			if(mListViews==null) mListViews = new ArrayList<View>();
			this.mListViews = mListViews;//构造方法，参数是我们的页卡，这样比较方便。
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) 	{	
			container.removeView(mListViews.get(position));//删除页卡
		}


		@Override
		public Object instantiateItem(ViewGroup container, int position) {	//这个方法用来实例化页卡		
			 container.addView(mListViews.get(position), 0);//添加页卡
			 return mListViews.get(position);
		}

		@Override
		public int getCount() {			
			return  mListViews.size();//返回页卡的数量
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {			
			return arg0==arg1;//官方提示这样写
		}
		
		public void addView(View view){
			if(view!=null) mListViews.add(view);
		}
	}
	
	public class MyBitmapSetHandler extends Handler{
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.obj!=null)setBitmapToImageView(msg.what, (Bitmap)(msg.obj));
			else{Log.i(TAG, "bitmap nil");}
		}
	}
	
	public class MyAssetsBitmapThread extends Thread{
		private String[] filenames=null;
		public MyAssetsBitmapThread(String... fns){
			if(fns==null) fns = new String[]{};
			this.filenames = fns;
		}
		@Override
		public void run() {
			super.run();
			Looper.prepare();
			List<String> randomList = Arrays.asList(filenames);
			Collections.shuffle(randomList);
			for(int i=0;i<randomList.size();i++){
				Bitmap bm = JackUtils.getbmFromAssetsFile(getResources(), randomList.get(i));
				Message msg = new Message();
				msg.what=i;
				msg.obj=bm;
				mHandler.sendMessage(msg);//多线程？
			}
			Looper.loop();
		}
	}
	
	class MySimpleGesture extends SimpleOnGestureListener {
		// 按两下的第二下Touch down时触发
		@Override
		public boolean onDoubleTap(MotionEvent e) {
				MyImageView imageView = (MyImageView)mImgListBig.get(curPage);
/*				if (imageView.getScale() > imageView.getMiniZoom()) {
					imageView.zoomTo(imageView.getMiniZoom());
				} else {
					imageView.zoomTo(imageView.getMaxZoom());
				}*/				 
//				imageView.setPivotX(e.getX());
//				imageView.setPivotX(e.getY());
//Log.i(TAG, "scale::"+imageView.getScale()+"__"+imageView.getMiniZoom());
//				imageView.setScaleType(ScaleType.MATRIX);
				if(isScaleDirty(imageView)){
					imageView.zoomTo(imageView.getMiniZoom());
//					imageView.setScaleX(1);
				}else{
					imageView.zoomTo(imageView.getMaxZoom());
				}

			return true;
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// Logger.LOG("onSingleTapConfirmed",
			// "onSingleTapConfirmed excute");
			// mTweetShow = !mTweetShow;
			// tweetLayout.setVisibility(mTweetShow ? View.VISIBLE
			// : View.INVISIBLE);
			return true;
		}
		@Override
		public void onShowPress(MotionEvent e) {
			Log.i(TAG, "onshowpress");
			super.onShowPress(e);
		}
		@Override
		public void onLongPress(MotionEvent e) {
//			Log.i(TAG, "longlong");
			super.onLongPress(e);
		}
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
//			Log.i(TAG, "upup");
			return super.onSingleTapUp(e);
		}
		float baseValue;
		float originalScale;
		@Override
		public boolean onDown(MotionEvent e) {
			MyImageView imageView = (MyImageView)mImgListBig.get(curPage);

				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					baseValue = 0;
					originalScale = imageView.getScale();
				}
			return false;
		
		}
		int kEvent = KEY_INVALID; //invalid
		public static final int KEY_INVALID = -1;
		float vvv[] = new float[9];
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			MyImageView imageView = (MyImageView)mImgListBig.get(curPage);
				
				float xdistance = calXdistance(e1, e2);
				float min_distance = screenWidth / 4f;
				if (isScrollingLeft(e1, e2) && xdistance > min_distance) {
					kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
				} else if (!isScrollingLeft(e1, e2) && xdistance > min_distance) {
					kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
				}
				

				Matrix m = imageView.getImageMatrix();
				m.getValues(vvv);
				// 图片实时的上下左右坐标
				float left, right,top,bottom;
				// 图片的实时宽，高
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale() * imageView.getImageHeight();
				if ((int) width <= screenWidth
						&& (int) height <= screenHeight)// 如果图片当前大小<屏幕大小，直接处理滑屏事件
				{
//					super.onScroll(e1, e2, distanceX, distanceY);
					return false;//
				} else {     
					left = vvv[Matrix.MTRANS_X];
					right = left + width;
					bottom = vvv[Matrix.MTRANS_Y];
					top = bottom+height;
					Rect r = new Rect();
					imageView.getGlobalVisibleRect(r);
					Log.i(TAG, r.bottom+"--bt--"+r.top);

					float dx=-distanceX,dy=-distanceY;
//					Log.i(TAG, System.currentTimeMillis()+"_let's see:::"+right+"--rl--"+left);
					if (distanceX > 0)// 向左滑动
					{
						if (r.left > 0 || right < screenWidth) {// 判断当前ImageView是否显示完全
							super.onScroll(e1, e2, distanceX, distanceY);
							dx=0;
						} else {
//							imageView.postTranslate(-distanceX, -distanceY);
						}
					} else if (distanceX < 0)// 向右滑动
					{
						if (r.right < screenWidth || left > 0) {
							super.onScroll(e1, e2, distanceX, distanceY);
							dx=0;
						} else {
//							imageView.postTranslate(-distanceX, -distanceY);//-distanceY
						}
					}
					
					if (distanceY > 0)// 向左滑动
					{
						if (top< screenHeight) {// 判断当前ImageView是否显示完全
							super.onScroll(e1, e2, distanceX, distanceY);
							dy=0;
						} else {
//							imageView.postTranslate(0,-distanceY);
						}
					} else if (distanceY < 0)// 向右滑动
					{
						if (bottom > 0) {
							super.onScroll(e1, e2, distanceX, distanceY);
							dy=0;
						} else {
//							imageView.postTranslate(0,-distanceY);//-distanceY
						}
					}
					imageView.postTranslate(dx, dy);
				}

			return true;//
		
		}
		
		private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
			return e2.getX() > e1.getX();
		}
		
		private float calXdistance(MotionEvent e1, MotionEvent e2) {
			return Math.abs(e2.getX() - e1.getX());
		}
	}

}	
