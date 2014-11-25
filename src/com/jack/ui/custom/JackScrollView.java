package com.jack.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

public class JackScrollView extends ScrollView{
	private final String TAG = getClass().getSimpleName();

	private MyScrollListener mScrollListener;

	public JackScrollView(Context context) {
		super(context);
	}
	
	
	public JackScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public JackScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
//		Log.i(TAG , String.format("(%s)(%s)(%s)(%s)",""+l,""+t,""+oldl,""+oldt));
		if(null!=mScrollListener) mScrollListener.onScroll(t);
	}
	
	public final void setScrollListener(MyScrollListener mScrollListener) {
		this.mScrollListener = mScrollListener;
	}

	public interface MyScrollListener{
		public void onScroll(int top);
	}
}