package com.jack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jack.ui.coverflow.Gallery3DActivity;

public class MainActivity extends Activity {
	
//	Button btn_viewpager,btn_fallpics,btn_listview;
//	private Button btn_webview;
//	private Button btn_scrollview;
//	private Button btn_showmetheway;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}

	private Map<String, Class> classMap;
	
	/**
	 * 
	 */
	private void initMenuList() {
		classMap = new HashMap<String, Class>();
		classMap.put("ViewPagerActivity", ViewPagerActivity.class);
		classMap.put("ListViewActivity", ListViewActivity.class);
		classMap.put("WebViewActivity", WebViewActivity.class);
		classMap.put("ScrollViewActivity", ScrollViewActivity.class);
		classMap.put("ShowMeTheWayActivity", ShowMeTheWayActivity.class);
		classMap.put("Gallery3DActivity", Gallery3DActivity.class);
		
	}

	private void init() {
		initMenuList();
		LinearLayout mLayout = (LinearLayout) findViewById(R.id.layout_btns);
		Button btn = null;
		Iterator<String> it = classMap.keySet().iterator();
		while(it.hasNext()){
			final String next = it.next();
			btn = new Button(this);
			btn.setText(next);
			btn.setOnClickListener(new View.OnClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void onClick(View v) {
					go(classMap.get(next));
				}
			});
			mLayout.addView(btn);
		}
		
//		btn.performClick();
		
/*//		JackUtils.showProgressDialog(this, "");
		btn_viewpager = (Button)findViewById(R.id.btn_go_viewpager);
//		btn_viewpager.setId(R.id.btn_go_viewpager);
		btn_viewpager.setOnClickListener(this);
		
		btn_fallpics = (Button)findViewById(R.id.btn_go_fall);
		btn_fallpics.setOnClickListener(this);
		
		btn_listview = (Button)findViewById(R.id.btn_go_lv);
		btn_listview.setOnClickListener(this);
		
		btn_webview = (Button)findViewById(R.id.btn_go_wv);
		btn_webview.setOnClickListener(this);
		
		btn_scrollview = (Button)findViewById(R.id.btn_go_sv);
		btn_scrollview.setOnClickListener(this);
		
		btn_showmetheway = (Button)findViewById(R.id.btn_show_me);
		btn_showmetheway.setOnClickListener(this);*/
		
	}

	private <T> void go(Class<T> clazz){
		Intent intent = new Intent();
		intent.setClass(this, clazz);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



}
