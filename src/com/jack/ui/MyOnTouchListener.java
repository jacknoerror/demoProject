package com.jack.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import com.jack.R;
import com.jack.R.id;

import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MyOnTouchListener implements OnTouchListener {
	private final String TAG = getClass().getSimpleName();
	
	private static final int MAX_FINGERS = 2;
	static final List<Integer> availableZone = new ArrayList<Integer>();
	static final Queue<Integer> waitingZone = new LinkedList<Integer>();
	/**
	 * key:btnId; value:btnValue
	 */
	static final SparseArray<Integer> contrastMap = new SparseArray<Integer>();

	/*final int COMPOUNDMASK_1 = 0x110000;
	final int COMPOUNDMASK_2 = 0x011000;
	final int COMPOUNDMASK_3 = 0x000110;
	final int COMPOUNDMASK_4 = 0x000011;*/
	final int[] CP_MASKS = new int[]{0x110000,0x011000,0x000110,0x000011};

	public MyOnTouchListener() {
		contrastMap.put(R.id.btn_sm1, 0x100000);
		contrastMap.put(R.id.btn_sm2, 0x010000);
		contrastMap.put(R.id.btn_sm3, 0x001000);
		contrastMap.put(R.id.btn_sm4, 0x000100);
		contrastMap.put(R.id.btn_sm5, 0x000010);
		contrastMap.put(R.id.btn_sm6, 0x000001);
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {

		int id = v.getId();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int order = 0;
			Integer intToAdd = contrastMap.get(id);//得到btnValue
			if(addToAvailable(intToAdd)){//添加有效区成功
				order = getAvailableValue(intToAdd);
			}else{
				addToWaiting(intToAdd);
			}
			if(order>0){
				send(order);
			}
			break;
		case MotionEvent.ACTION_UP:
			Integer intToRemove = contrastMap.get(id);
//			if(availableZone.contains(intToRemove))stop();
			removeElement(intToRemove);
			if(availableZone.size()<2&&waitingZone.size()>0){
				addToAvailable(waitingZone.poll());
			}
			if (availableZone.size() > 0)
				send(getAvailableValue(availableZone.get(0)));// 松开一个手指后补发按下的
			else
				stop();
			break;
		default:
			break;
		}
		return false;
	}


	/**
	 * 终止指令
	 */
	public void stop() {
		// TODO Auto-generated method stub
		Log.i(TAG, "stop--");
	}


	/**
	 * 发送指令
	 * @param order
	 */
	public void send(int order) {
		// TODO Auto-generated method stub
		if(order==0&&availableZone.size()>0) order = availableZone.get(0);//松开第三颗手指而剩下两个有冲突时
		
		logHex(order);
	}


	/**
	 * test
	 * @param order 
	 */
	void logHex(int order) {
		String output;
		output = Integer.toHexString(order);
//		  System.out.printf("%010d\n",Integer.parseInt(s,10));
		Log.i(TAG, "send:0x"+output);
	}

	/**
	 * 
		 * @param intToAdd
		 * @return 组合键value，单个按键value，0
		 */
		private int getAvailableValue(Integer intToAdd) {
			int order = intToAdd;
			if(availableZone.size()>1){
				int checkCompound = checkCompound();
				if(checkCompound>0){
					order = checkCompound;
				}else{
					removeElement(intToAdd);
					addToWaiting(intToAdd);
					order=0; //如果不能组成组合键，那这个按钮是无效的，加入等待队列中
				}
			}
			return order;
		}


	private int checkCompound() {
		if(availableZone.size()<MAX_FINGERS) return 0;
		int result = 0;
		Integer a0 = availableZone.get(0);
		Integer a1 = availableZone.get(1);
		int com = a0 | a1;
		for(int mask:CP_MASKS){
			if(com==mask) {
				result=mask;
			break;
			}
		}
//		Log.i(TAG , "check:"+result);
		return result;
	}
	
	boolean addToAvailable(Integer intToAdd) {
		if (availableZone.size() >= 2)
			return false;
		availableZone.add(intToAdd);
		return true;
	}

	boolean addToWaiting(Integer intToAdd) {
		waitingZone.add(intToAdd);
//		Log.i(TAG , "addwait:"+intToAdd);
		return true;
	}

	/**
	 * 从有效区或等待区删除
	 * 
	 * @param intToRemove
	 * @return
	 */
	boolean removeElement(Integer intToRemove) {
		if (availableZone.contains(intToRemove)) {
			availableZone.remove(intToRemove);
		} else if (waitingZone.contains(intToRemove)) {
			waitingZone.remove(intToRemove);
		} else {
			return false;
		}
//		Log.i(TAG , "remove:"+intToRemove);
		return true;
	}

}