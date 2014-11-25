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
		mHeaderTextView.setText("初始状态");
		mHeaderLinearLayout.addView(mHeaderTextView);
		
		
		setSelection(1);
		setOnScrollListener(this); 
		
		measureView(mHeaderLinearLayout);
		mHeaderHeight = mHeaderLinearLayout.getMeasuredHeight();
	}

	/**
	 * 因为是在构造函数里测量高度，应该先measure一下
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

	private final static int NONE_PULL_REFRESH = 0;   //正常状态
	private final static int ENTER_PULL_REFRESH = 1;  //进入下拉刷新状态
	private final static int OVER_PULL_REFRESH = 2;   //进入松手刷新状态
	private final static int EXIT_PULL_REFRESH = 3;     //松手后反弹后加载状态
	private int mPullRefreshState = 0;                         //记录刷新状态
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	    if (mCurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL
	            && firstVisibleItem == 0
	            && (mHeaderLinearLayout.getBottom() >= 0 && mHeaderLinearLayout.getBottom() < mHeaderHeight)) {
	        //进入且仅进入下拉刷新状态
	        if (mPullRefreshState == NONE_PULL_REFRESH) {
	            mPullRefreshState = ENTER_PULL_REFRESH;
	        }
	    } else if (mCurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL
	            && firstVisibleItem == 0
	            && (mHeaderLinearLayout.getBottom() >= mHeaderHeight)) {
	        //下拉达到界限，进入松手刷新状态
	        if (mPullRefreshState == ENTER_PULL_REFRESH || mPullRefreshState == NONE_PULL_REFRESH) {
	            mPullRefreshState = OVER_PULL_REFRESH;
	            //下面是进入松手刷新状态需要做的一个显示改变
	            mDownY = mMoveY;//用于后面的下拉特殊效果
	            mHeaderTextView.setText("松手刷新");
	            /*mHeaderPullDownImageView.setVisibility(View.GONE);
	            mHeaderReleaseDownImageView.setVisibility(View.VISIBLE);*/
	        }
	    } else if (mCurrentScrollState ==SCROLL_STATE_TOUCH_SCROLL && firstVisibleItem != 0) {
	        //不刷新了
	        if (mPullRefreshState == ENTER_PULL_REFRESH) {
	            mPullRefreshState = NONE_PULL_REFRESH;
	        }
	    } else if (mCurrentScrollState == SCROLL_STATE_FLING && firstVisibleItem == 0) {
	        //飞滑状态，不能显示出header，也不能影响正常的飞滑
	        //只在正常情况下才纠正位置
	        if (mPullRefreshState == NONE_PULL_REFRESH) {
	            setSelection(1);
	        }
	    }
	}
	
	private float mDownY;
	private float mMoveY;
	/* 特殊处理，当header完全显示后，下拉只按下拉1/3的距离下拉，给用户一种艰难下拉，该松手的弹簧感觉。
	 * 这个在onTouchEvent里处理比较方便
	 * @see android.widget.AbsListView#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
	    switch (ev.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	            //记下按下位置
	            //改变
	            mDownY = ev.getY();
	            break;
	        case MotionEvent.ACTION_MOVE:
	            //移动时手指的位置
	            mMoveY = ev.getY();
	            if (mPullRefreshState == OVER_PULL_REFRESH) {
	                //注意下面的mDownY在onScroll的第二个else中被改变了
	                mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(),
	                        (int)((mMoveY - mDownY)/3), //1/3距离折扣
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
	                                sleep(5);//慢一点反弹，别一下子就弹回去了
	                            } catch (InterruptedException e) {
	                                e.printStackTrace();
	                            }
	                        }
	                        msg = mHandler.obtainMessage();
	                        if (mPullRefreshState == OVER_PULL_REFRESH) {
	                            msg.what = REFRESH_BACED;//加载数据完成，结束返回
	                        } else {
	                            msg.what = REFRESH_RETURN;//未达到刷新界限，直接返回
	                        }
	                        mHandler.sendMessage(msg);
	                    };
	                }.start();
	            }
	            break;
	    }
	    return super.onTouchEvent(ev);
	}
	
	//因为涉及到handler数据处理，为方便我们定义如下常量
	private final static int REFRESH_BACKING = 0;      //反弹中
	private final static int REFRESH_BACED = 1;        //达到刷新界限，反弹结束后
	private final static int REFRESH_RETURN = 2;       //没有达到刷新界限，返回
	private final static int REFRESH_DONE = 3;         //加载数据结束
	
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
	            mHeaderTextView.setText("正在加载...");
	            /* mHeaderProgressBar.setVisibility(View.VISIBLE);
	            mHeaderPullDownImageView.setVisibility(View.GONE);
	            mHeaderReleaseDownImageView.setVisibility(View.GONE);*/
	            mPullRefreshState = EXIT_PULL_REFRESH;
	            new Thread() {
	                public void run() {
	                    SystemClock.sleep(2000);//处理后台加载数据
	                    Message msg = mHandler.obtainMessage();
	                    msg.what = REFRESH_DONE;
	                    //通知主线程加载数据完成
	                    mHandler.sendMessage(msg);
	                };
	            }.start();
	            break;
	        case REFRESH_RETURN:
	            //未达到刷新界限，返回
	            mHeaderTextView.setText("下拉刷新");
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
	            //刷新结束后，恢复原始默认状态
	            mHeaderTextView.setText("下拉刷新");
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
