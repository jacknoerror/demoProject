package com.jack;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jack.ui.custom.JackScrollView;
import com.jack.ui.custom.JackScrollView.MyScrollListener;

public class ScrollViewActivity extends Activity {
	private final String TAG = this.getClass().getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroll);
		
		JackScrollView scroll = (JackScrollView) this.findViewById(R.id.scroll);
		MyScrollListener mScrollListener = new MyScrollListener() {
			
			private int floatLayoutHeight;
			private View topLayout;
			private View floatLayout;

			@Override
			public void onScroll(int top) {
				if(floatLayoutHeight<=0){
					initFloatingHeight();
				}else{
					judgeFloat(top);
				}
				
			}

			/**
			 * 决定float层的去留
			 * @param top 
			 */
			private void judgeFloat(int top) {
				if(null==floatLayout) floatLayout = ScrollViewActivity.this.findViewById(R.id.layout_scroll_float);
				floatLayout.setVisibility(top>floatLayoutHeight?View.VISIBLE:View.GONE);
			}

			private void initFloatingHeight() {
				if(null==topLayout)topLayout = ScrollViewActivity.this.findViewById(R.id.layout_scroll_top);
				floatLayoutHeight = topLayout.getMeasuredHeight();
				Log.i(TAG, String.format("(%s)(%s)(%s)","floatlayoutheight",""+floatLayoutHeight,""));
			}
		};
		scroll.setScrollListener(mScrollListener );
		
		
		LinearLayout layout = (LinearLayout)this.findViewById(R.id.layout_list);
		
		for(int i=0;i<50;i++){
			TextView tv = new TextView(this);
			tv.setText("data:"+i);
			layout.addView(tv);
		}
		
		
		
	}
}
