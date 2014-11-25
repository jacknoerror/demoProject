package com.jack;


import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jack.ui.MyOnTouchListener;
import com.jack.utils.CampassHelper;

public class ShowMeTheWayActivity extends Activity {
	private final String TAG = getClass().getSimpleName();
	private ImageView imageView;


	private TextView aText;
	private CampassHelper camHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showmetheway);

		imageView = (ImageView) this.findViewById(R.id.img_showmetheway);
		imageView.setKeepScreenOn(true);// ÆÁÄ»¸ßÁÁ
		camHelper = new CampassHelper(this, imageView);

		aText = (TextView) this.findViewById(R.id.tv_sm);
		Button btn1, btn2, btn3;
		Button btn4, btn5, btn6;
		btn1 = (Button) this.findViewById(R.id.btn_sm1);
		btn2 = (Button) this.findViewById(R.id.btn_sm2);
		btn3 = (Button) this.findViewById(R.id.btn_sm3);
		btn4 = (Button) this.findViewById(R.id.btn_sm4);
		btn5 = (Button) this.findViewById(R.id.btn_sm5);
		btn6 = (Button) this.findViewById(R.id.btn_sm6);
		OnTouchListener tl = new MyOnTouchListener();
		btn1.setOnTouchListener(tl);
		btn2.setOnTouchListener(tl);
		btn3.setOnTouchListener(tl);
		btn4.setOnTouchListener(tl);
		btn5.setOnTouchListener(tl);
		btn6.setOnTouchListener(tl);
	}

	@Override
	protected void onResume() {
		if(null!=camHelper) camHelper.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		if(null!=camHelper) camHelper.onPause();
		super.onPause();
	}
}
