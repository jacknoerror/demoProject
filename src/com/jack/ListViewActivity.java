package com.jack;


import com.jack.ui.custom.MyListView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListViewActivity extends Activity   {
	
	private ListView mListView;
	private String[] mStringData ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mListView = new MyListView(this);
		setContentView(mListView);
		
		final int COUNT  = 5;
		mStringData = new String[COUNT];
		for(int i=0;i<mStringData.length;i++){
			mStringData[i] = i+":yeah";
		}
		mListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mStringData ));
		mListView.setSelection(1);
	}

}
