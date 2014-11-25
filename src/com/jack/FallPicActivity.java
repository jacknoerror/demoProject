package com.jack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.huewu.pla.lib.MultiColumnPullToRefreshListView;
import com.huewu.pla.lib.internal.PLA_HeaderViewListAdapter;
import com.jack.utils.JackUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;

public class FallPicActivity extends Activity {
	
	MultiColumnPullToRefreshListView listview;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_pull_to_refresh_sample);
		
		initView();
		
	}

	private void initView() {
		listview = (MultiColumnPullToRefreshListView)findViewById(R.id.list);
		
		MyAdapter adapter = new MyAdapter(getData());
		listview.setAdapter(new PLA_HeaderViewListAdapter(null, null, adapter));
		adapter.notifyDataSetChanged();
	}
	
	private List<FallItem> getData(){
		List<FallItem> itemList=new ArrayList<FallItem>();
		//test
		try {
			List<String> image_filenames = Arrays.asList(getAssets().list("images"));
			for(String name: image_filenames){
				FallItem fi = new FallItem();
				fi.imgPath="images/"+name;
				fi.desc = "jack is a genius __"+name;
				itemList.add(fi);
				System.out.println(name+"||||file");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return itemList;
	}
	
	
	public class MyAdapter extends BaseAdapter{
		List<FallItem> contentList;
		SparseArray<View> viewMap;
		
		public MyAdapter(List<FallItem> list){
			if(list==null) list = new ArrayList<FallItem>();
			contentList = list;
			viewMap = new SparseArray<View>();
		}
		
		@Override
		public int getCount() {
			return contentList.size();
		}

		@Override
		public Object getItem(int position) {
			return contentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = viewMap.get(position);
			ViewHolder holder = new ViewHolder();
			
			if(view==null){
				view = LayoutInflater.from(FallPicActivity.this).inflate(R.layout.layout_fall_pic, null);
				   
                holder = new ViewHolder();
                holder.image = (ImageView)view.findViewById(R.id.img_fall_in_layout);
                holder.text = (TextView)view.findViewById(R.id.tv_fall_in_layout);
   
   
                viewMap.put(position, view);
   
                view.setTag(holder);
			}else{
				view = viewMap.get(position);
                holder = (ViewHolder)view.getTag();
            }
            

            //哟啊不要换地方？
			FallItem ssi = contentList.get(position);
//            JackImageLoader.justSetMeImage(ssi.shopLogoImage, holder.icimg);
			Bitmap bm = JackUtils.getbmFromAssetsFile(getResources(), ssi.imgPath);
			holder.image.setImageBitmap(bm);
            holder.text.setText(ssi.desc);
            
			return view;
		}
		
	}
	static class ViewHolder{
		ImageView image;
		TextView text;
	}
	public class FallItem{
		public String imgPath;
		public String desc;
	}
}
