package com.jack.ui.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MyListView extends ListView implements AbsListView.OnScrollListener {

	LinearLayout mHeaderLinearLayout ;
	private TextView mHeaderTextView;
	private int mHeaderHeight;
	private int mCurrentScrollState;
	
	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyListView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		
		mHeaderLinearLayout  = new LinearLayout(context);
		addHeaderView(mHeaderLinearLayout);
		mHeaderTextView = new TextView(context);
		mHeaderTextView.setText("��ʼ״̬");
		mHeaderLinearLayout.addView(mHeaderTextView);
		
		
		setSelection(1);
		setOnScrollListener(this); 
		
		measureView(mHeaderLinearLayout);
		mHeaderHeight = mHeaderLinearLayout.getMeasuredHeight();
	}

	/**
	 * ��Ϊ���ڹ��캯��������߶ȣ�Ӧ����measureһ��
	 * @param child
	 */
	private void measureView(View child) {
	    ViewGroup.LayoutParams p = child.getLayoutParams();
	    if (p == null) {
	        p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT);
	    }
	 
	    int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
	    int lpHeight = p.height;
	    int childHeightSpec;
	    if (lpHeight > 0) {
	        childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
	                MeasureSpec.EXACTLY);
	    } else {
	        childHeightSpec = MeasureSpec.makeMeasureSpec(0,
	                MeasureSpec.UNSPECIFIED);
	    }
	    child.measure(childWidthSpec, childHeightSpec);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		mCurrentScrollState = scrollState;
		
	}

	private final static int NONE_PULL_REFRESH = 0;   //����״̬
	private final static int ENTER_PULL_REFRESH = 1;  //��������ˢ��״̬
	private final static int OVER_PULL_REFRESH = 2;   //��������ˢ��״̬
	private final static int EXIT_PULL_REFRESH = 3;     //���ֺ󷴵������״̬
	private int mPullRefreshState = 0;                         //��¼ˢ��״̬
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	    if (mCurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL
	            && firstVisibleItem == 0
	            && (mHeaderLinearLayout.getBottom() >= 0 && mHeaderLinearLayout.getBottom() < mHeaderHeight)) {
	        //�����ҽ���������ˢ��״̬
	        if (mPullRefreshState == NONE_PULL_REFRESH) {
	            mPullRefreshState = ENTER_PULL_REFRESH;
	        }
	    } else if (mCurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL
	            && firstVisibleItem == 0
	            && (mHeaderLinearLayout.getBottom() >= mHeaderHeight)) {
	        //�����ﵽ���ޣ���������ˢ��״̬
	        if (mPullRefreshState == ENTER_PULL_REFRESH || mPullRefreshState == NONE_PULL_REFRESH) {
	            mPullRefreshState = OVER_PULL_REFRESH;
	            //�����ǽ�������ˢ��״̬��Ҫ����һ����ʾ�ı�
	            mDownY = mMoveY;//���ں������������Ч��
	            mHeaderTextView.setText("����ˢ��");
	            /*mHeaderPullDownImageView.setVisibility(View.GONE);
	            mHeaderReleaseDownImageView.setVisibility(View.VISIBLE);*/
	        }
	    } else if (mCurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL && firstVisibleItem != 0) {
	        //��ˢ����
	        if (mPullRefreshState == ENTER_PULL_REFRESH) {
	            mPullRefreshState = NONE_PULL_REFRESH;
	        }
	    } else if (mCurrentScrollState == SCROLL_STATE_FLING && firstVisibleItem == 0) {
	        //�ɻ�״̬��������ʾ��header��Ҳ����Ӱ�������ķɻ�
	        //ֻ����������²ž���λ��
	        if (mPullRefreshState == NONE_PULL_REFRESH) {
	            setSelection(1);
	        }
	    }
	}
	
	private float mDownY;
	private float mMoveY;
	/* ���⴦����header��ȫ��ʾ������ֻ������1/3�ľ������������û�һ�ּ��������������ֵĵ��ɸо���
	 * �����onTouchEvent�ﴦ��ȽϷ���
	 * @see android.widget.AbsListView#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
	    switch (ev.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	            //���°���λ��
	            //�ı�
	            mDownY = ev.getY();
	            break;
	        case MotionEvent.ACTION_MOVE:
	            //�ƶ�ʱ��ָ��λ��
	            mMoveY = ev.getY();
	            if (mPullRefreshState == OVER_PULL_REFRESH) {
	                //ע�������mDownY��onScroll�ĵڶ���else�б��ı���
	                mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
	                        (int)((mMoveY - mDownY)/3), //1/3�����ۿ�
	                        mHeaderLinearLayout.getPaddingRight(),
	                        mHeaderLinearLayout.getPaddingBottom());
	            }
	            break;
	        case MotionEvent.ACTION_UP:
	        	 //when you action up, it will do these:
	            //1. roll back util header topPadding is 0
	            //2. hide the header by setSelection(1)
	            if (mPullRefreshState == OVER_PULL_REFRESH || mPullRefreshState == ENTER_PULL_REFRESH) {
	                new Thread() {
	                    public void run() {
	                        Message msg;
	                        while(mHeaderLinearLayout.getPaddingTop() > 1) {
	                            msg = mHandler.obtainMessage();
	                            msg.what = REFRESH_BACKING;
	                            mHandler.sendMessage(msg);
	                            try {
	                                sleep(5);//��һ�㷴������һ���Ӿ͵���ȥ��
	                            } catch (InterruptedException e) {
	                                e.printStackTrace();
	                            }
	                        }
	                        msg = mHandler.obtainMessage();
	                        if (mPullRefreshState == OVER_PULL_REFRESH) {
	                            msg.what = REFRESH_BACED;//����������ɣ���������
	                        } else {
	                            msg.what = REFRESH_RETURN;//δ�ﵽˢ�½��ޣ�ֱ�ӷ���
	                        }
	                        mHandler.sendMessage(msg);
	                    };
	                }.start();
	            }
	            break;
	    }
	    return super.onTouchEvent(ev);
	}
	
	//��Ϊ�漰��handler���ݴ���Ϊ�������Ƕ������³���
	private final static int REFRESH_BACKING = 0;      //������
	private final static int REFRESH_BACED = 1;        //�ﵽˢ�½��ޣ�����������
	private final static int REFRESH_RETURN = 2;       //û�дﵽˢ�½��ޣ�����
	private final static int REFRESH_DONE = 3;         //�������ݽ���
	
	private Handler mHandler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        case REFRESH_BACKING:
	            mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
	                    (int) (mHeaderLinearLayout.getPaddingTop()*0.75f),
	                    mHeaderLinearLayout.getPaddingRight(),
	                    mHeaderLinearLayout.getPaddingBottom());
	            break;
	        case REFRESH_BACED:
	            mHeaderTextView.setText("���ڼ���...");
	            /* mHeaderProgressBar.setVisibility(View.VISIBLE);
	            mHeaderPullDownImageView.setVisibility(View.GONE);
	            mHeaderReleaseDownImageView.setVisibility(View.GONE);*/
	            mPullRefreshState = EXIT_PULL_REFRESH;
	            new Thread() {
	                public void run() {
	                    SystemClock.sleep(2000);//�����̨��������
	                    Message msg = mHandler.obtainMessage();
	                    msg.what = REFRESH_DONE;
	                    //֪ͨ���̼߳����������
	                    mHandler.sendMessage(msg);
	                };
	            }.start();
	            break;
	        case REFRESH_RETURN:
	            //δ�ﵽˢ�½��ޣ�����
	            mHeaderTextView.setText("����ˢ��");
	            /*mHeaderProgressBar.setVisibility(View.INVISIBLE);
	            mHeaderPullDownImageView.setVisibility(View.VISIBLE);
	            mHeaderReleaseDownImageView.setVisibility(View.GONE);*/
	            mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
	                    0,
	                    mHeaderLinearLayout.getPaddingRight(),
	                    mHeaderLinearLayout.getPaddingBottom());
	            mPullRefreshState = NONE_PULL_REFRESH;
	            setSelection(1);
	            break;
	        case REFRESH_DONE:
	            //ˢ�½����󣬻ָ�ԭʼĬ��״̬
	            mHeaderTextView.setText("����ˢ��");
	            /*mHeaderProgressBar.setVisibility(View.INVISIBLE);
	            mHeaderPullDownImageView.setVisibility(View.VISIBLE);
	            mHeaderReleaseDownImageView.setVisibility(View.GONE);
	            mHeaderUpdateText.setText(getContext().getString(R.string.app_list_header_refresh_last_update, 
	                    mSimpleDateFormat.format(new Date())));*/
	            mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
	                    0,
	                    mHeaderLinearLayout.getPaddingRight(),
	                    mHeaderLinearLayout.getPaddingBottom());
	            mPullRefreshState = NONE_PULL_REFRESH;
	            setSelection(1);
	            break;
	        default:
	            break;
	        }
	    }
	};
}
