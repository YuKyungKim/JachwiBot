/*
 * Copyright (C) 2011 SmartRobot.ORG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartrobot.android.clip;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

/**
 * <p>로보이드 스튜디오로 만든 클립 파일을 열거나 닫고, 재생 및 중지하는 메소드를 정의한다.
 * </p>
 * <pre class="prettyprint">
 * public SampleActivity extends Activity
 {
     private ClipPlayer mClipPlayer;

     protected void onCreate(Bundle savedInstanceState)
     {
         super.onCreate(savedInstanceState);
         mClipPlayer = ClipPlayer.obtain(this, 0);
     }

     protected void onStart()
     {
         super.onStart();
         mClipPlayer.open("org.smartrobot.sample", R.raw.sample);
     }

     protected void onStop()
     {
         super.onStop();
         mClipPlayer.close();
     }

     public void onClick(View v)
     {
         switch(v.getId())
         {
         case R.id.play:
             mClipPlayer.play();
             break;
         case R.id.stop:
             mClipPlayer.stop();
             break;
         }
     }
 }</pre>
 * 
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
public final class ClipPlayer
{
	private static final int ERROR_NONE = 0;
	/**
	 * <p>클립 재생기의 ID가 잘못 되었음을 나타내는 오류 코드 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: -1
	 * </ul>
	 */
	public static final int ERROR_INVALID_ID = -1;
	/**
	 * <p>컨텍스트가 잘못 되었음을 나타내는 오류 코드 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: -2
	 * </ul>
	 */
	public static final int ERROR_INVALID_CONTEXT = -2;
	/**
	 * <p>클립 파일의 URL이 잘못 되었음을 나타내는 오류 코드 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: -3
	 * </ul>
	 */
	public static final int ERROR_INVALID_URL = -3;
	/**
	 * <p>패키지 이름이 잘못 되었음을 나타내는 오류 코드 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: -4
	 * </ul>
	 */
	public static final int ERROR_INVALID_PACKAGE = -4;
	/**
	 * <p>클립 파일의 리소스 ID 또는 리소스 이름이 잘못 되었음을 나타내는 오류 코드 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: -5
	 * </ul>
	 */
	public static final int ERROR_INVALID_RESOURCE = -5;
	/**
	 * <p>잘못된 클립임을 나타내는 오류 코드 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: -6
	 * </ul>
	 */
	public static final int ERROR_INVALID_CLIP = -6;
	/**
	 * <p>클립 파일이 잘못 되었음을 나타내는 오류 코드 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: -7
	 * </ul>
	 */
	public static final int ERROR_INVALID_FILE = -7;
	/**
	 * <p>클립 파일을 열거나 재생, 중지하는 순서가 잘못 되었음을 나타내는 오류 코드 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: -8
	 * </ul>
	 */
	public static final int ERROR_ILLEGAL_STATE = -8;
	
	private static final int MSG_COMPLETION = 1;
	private static final int MSG_ERROR = 2;
	
	/**
	 * <p>클립 파일의 재생이 완료되었을 때 호출되는 메소드를 정의한다.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see ClipPlayer
	 */
	public interface OnCompletedListener
	{
		/**
		 * <p>클립 파일의 재생이 완료되었을 때 호출된다.
		 * </p>
		 * @param clipPlayer 재생이 완료된 클립 재생기
		 */
		void onCompleted(ClipPlayer clipPlayer);
	}
	
	/**
	 * <p>클립 파일을 열거나 재생, 중지하는 과정에서 오류가 발생했을 때 호출되는 메소드를 정의한다.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see ClipPlayer
	 */
	public interface OnErrorListener
	{
		/**
		 * <p>클립 파일을 열거나 재생, 중지하는 과정에서 오류가 발생했을 때 호출된다.
		 * <p>오류 코드 값은 {@link ClipPlayer} 클래스에 정의되어 있다.
		 * </p>
		 * @param clipPlayer 오류가 발생한 클립 재생기
		 * @param errorCode 오류 코드 값
		 */
		void onError(ClipPlayer clipPlayer, int errorCode);
	}
	
	private WeakReference<Context> mContext;
	OnCompletedListener mOnCompletedListener;
	OnErrorListener mOnErrorListener;
	static final SparseArray<ClipPlayer> mClipPlayers = new SparseArray<ClipPlayer>();
	final String mRequestCode;
	private final int mID;
	private boolean mOpen;
	boolean mPlaying;
	private BroadcastReceiver mBR;
	private final EventHandler mEventHandler;
	
	private ClipPlayer(String packageName, int id)
	{
		mID = id;
		mRequestCode = packageName + "_" + id;
		Looper looper;
		if((looper = Looper.myLooper()) != null)
		{
			mEventHandler = new EventHandler(this, looper);
		}
		else if((looper = Looper.getMainLooper()) != null)
		{
			mEventHandler = new EventHandler(this, looper);
		}
		else
			mEventHandler = new EventHandler(this);
	}
	
	/**
	 * <p>주어진 ID에 대한 클립 재생기의 인스턴스를 얻는다.
	 * <p>context가 null이면 null을 반환한다.
	 * clipPlayerId는 클립 재생기를 구분하기 위해 사용자가 지정하는 ID 값이다.
	 * clipPlayerId에 대한 클립 재생기 인스턴스가 이미 존재하는 경우에는 기존의 클립 재생기 인스턴스를 반환하고, 존재하지 않으면 새로 생성하여 반환한다.
	 * </p>
	 * @param context 컨텍스트
	 * @param clipPlayerId 클립 재생기의 ID
	 * <p>
	 * @return 클립 재생기의 인스턴스 또는 null
	 */
	public static ClipPlayer obtain(Context context, int clipPlayerId)
	{
		if(context == null) return null;
		String packageName = context.getPackageName();
		if(packageName == null) return null;
		ClipPlayer clipPlayer = null;
		synchronized(mClipPlayers)
		{
			clipPlayer = mClipPlayers.get(clipPlayerId);
			if(clipPlayer == null)
			{
				clipPlayer = new ClipPlayer(packageName, clipPlayerId);
				mClipPlayers.put(clipPlayerId, clipPlayer);
			}
		}
		clipPlayer.setContext(context);
		return clipPlayer;
	}
	
	/**
	 * <p>열려 있는 모든 클립 파일을 닫는다.
	 * <p>클립 파일을 닫은 후에는 {@link #play()} 혹은 {@link #stop()} 메소드를 호출하여 클립 파일을 재생하거나 중지할 수 없다.
	 * 애플리케이션이 종료되기 전에 반드시 생성된 모든 클립 재생기에 대해 {@link #close()} 혹은 ClipPlayer.closeAll() 메소드를 호출하여 클립 파일을 닫아야 한다.
	 */
	public static void closeAll()
	{
		synchronized(mClipPlayers)
		{
			int sz = mClipPlayers.size();
			for(int i = 0; i < sz; ++i)
			{
				mClipPlayers.valueAt(i).release();
			}
			mClipPlayers.clear();
		}
	}
	
	/**
	 * <p>클립 재생기의 ID를 반환한다.
	 * </p>
	 * @return 클립 재생기의 ID
	 */
	public int getId()
	{
		return mID;
	}
	
	private Context getContext()
	{
		if(mContext == null) return null;
		return mContext.get();
	}
	
	void setContext(Context context)
	{
		Context applicationContext = context.getApplicationContext();
		if(applicationContext == null)
			mContext = new WeakReference<Context>(context);
		else
			mContext = new WeakReference<Context>(applicationContext);
	}
	
	/**
	 * <p>클립 파일의 재생이 완료되었을 때 호출되도록 listener를 설정한다.
	 * </p>
	 * @param listener 설정할 리스너
	 */
	public void setOnCompletedListener(OnCompletedListener listener)
	{
		mOnCompletedListener = listener;
	}
	
	/**
	 * <p>클립 파일을 열거나 재생, 중지하는 과정에서 오류가 발생했을 때 호출되도록 listener를 설정한다.
	 * </p>
	 * @param listener 설정할 리스너
	 */
	public void setOnErrorListener(OnErrorListener listener)
	{
		mOnErrorListener = listener;
	}
	
	/**
	 * <p>클립 파일을 연다.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(ClipPlayer clipPlayer)
 {
     clipPlayer.open("http://www.sample.org/sample.mcs");
 }</pre>
	 * 
	 * @param url 클립 파일의 URL
	 * <p>
	 * @return 성공하면 true, 아니면 false
	 */
	public boolean open(String url)
	{
		if(url == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_URL);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mOpen) close();
		mOpen = true;
		
		synchronized(mClipPlayers)
		{
			ClipPlayer player = mClipPlayers.get(getId());
			if(player == null)
				mClipPlayers.put(getId(), this);
		}
		
		registerBroadcast(context);
		
		Intent intent = new Intent("roboid.intent.action.CLIP_OPEN");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		intent.putExtra("roboid.intent.extra.CLIP_URL", url);
		context.sendBroadcast(intent);
		return true;
	}
	
	/**
	 * <p>클립 파일을 연다.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(ClipPlayer clipPlayer)
 {
     clipPlayer.open("org.smartrobot.sample", R.raw.sample);
 }</pre>
	 * 
	 * @param packageName 클립 파일이 있는 패키지의 이름
	 * @param resid 클립 파일의 리소스 ID
	 * <p>
	 * @return 성공하면 true, 아니면 false
	 */
	public boolean open(String packageName, int resid)
	{
		if(packageName == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_PACKAGE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mOpen) close();
		mOpen = true;
		
		synchronized(mClipPlayers)
		{
			ClipPlayer player = mClipPlayers.get(getId());
			if(player == null)
				mClipPlayers.put(getId(), this);
		}
		
		registerBroadcast(context);
		
		Intent intent = new Intent("roboid.intent.action.CLIP_OPEN");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		intent.putExtra("roboid.intent.extra.CLIP_PACKAGE_NAME", packageName);
		intent.putExtra("roboid.intent.extra.CLIP_RESOURCE_ID", resid);
		context.sendBroadcast(intent);
		return true;
	}
	
	/**
	 * <p>클립 파일을 연다.
	 * <p>클립 파일(mcs 파일)은 raw 폴더에 있어야 하며, resName은 클립 파일의 확장자를 제외한 파일 이름이다.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(ClipPlayer clipPlayer)
 {
     clipPlayer.open("org.smartrobot.sample", "sample");
 }</pre>
	 * 
	 * @param packageName 클립 파일이 있는 패키지의 이름
	 * @param resName 클립 파일의 리소스 이름
	 * <p>
	 * @return 성공하면 true, 아니면 false
	 */
	public boolean open(String packageName, String resName)
	{
		if(packageName == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_PACKAGE);
			return false;
		}
		if(resName == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_RESOURCE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mOpen) close();
		mOpen = true;
		
		synchronized(mClipPlayers)
		{
			ClipPlayer player = mClipPlayers.get(getId());
			if(player == null)
				mClipPlayers.put(getId(), this);
		}
		
		registerBroadcast(context);
		
		Intent intent = new Intent("roboid.intent.action.CLIP_OPEN");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		intent.putExtra("roboid.intent.extra.CLIP_PACKAGE_NAME", packageName);
		intent.putExtra("roboid.intent.extra.CLIP_RESOURCE_ID", resName);
		context.sendBroadcast(intent);
		return true;
	}
	
	private void registerBroadcast(Context context)
	{
		if(mBR != null) return;
		mBR = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				String action = intent.getAction();
				if("roboid.intent.action.CLIP_COMPLETION".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_COMPLETION);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.CLIP_ERROR".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_ERROR);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("roboid.intent.action.CLIP_COMPLETION");
		intentFilter.addAction("roboid.intent.action.CLIP_ERROR");
		context.registerReceiver(mBR, intentFilter);
	}
	
	/**
	 * <p>클립 파일을 닫는다.
	 * <p>클립 파일을 닫은 후에는 {@link #play()} 혹은 {@link #stop()} 메소드를 호출하여 클립 파일을 재생하거나 중지할 수 없다.
	 * 애플리케이션이 종료되기 전에 반드시 생성된 모든 클립 재생기에 대해 close() 혹은 {@link #ClipPlayer.closeAll() ClipPlayer.closeAll()} 메소드를 호출하여 클립 파일을 닫아야 한다.
	 */
	public void close()
	{
		release();
		synchronized(mClipPlayers)
		{
			mClipPlayers.remove(getId());
		}
	}
	
	private void release()
	{
		if(mPlaying)
			stop();
		mOpen = false;

		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
		}
		else
		{
			if(mRequestCode != null)
			{
				Intent intent = new Intent("roboid.intent.action.CLIP_CLOSE");
				intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
				context.sendBroadcast(intent);
			}
			if(mBR != null)
			{
				context.unregisterReceiver(mBR);
				mBR = null;
			}
		}
	}
	
	/**
	 * <p>클립 파일을 재생한다.
	 * <p>{@link #close()} 혹은 {@link #ClipPlayer.closeAll() ClipPlayer.closeAll()} 메소드를 호출하여 클립 파일을 닫은 후에는 클립 파일이 재생되지 않고 false를 반환한다.
	 * </p>
	 * @return 성공하면 true, 아니면 false
	 */
	public boolean play()
	{
		if(mOpen == false)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mPlaying)
			stop();
		mPlaying = true;
		
		Intent intent = new Intent("roboid.intent.action.CLIP_PLAY");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		context.sendBroadcast(intent);
		return true;
	}
	
	/**
	 * <p>클립 파일의 재생을 중지한다.
	 * <p>{@link #close()} 혹은 {@link #ClipPlayer.closeAll() ClipPlayer.closeAll()} 메소드를 호출하여 클립 파일을 닫은 후에는 false를 반환한다.
	 * </p>
	 * @return 성공하면 true, 아니면 false
	 */
	public boolean stop()
	{
		if(mOpen == false)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		mPlaying = false;
		
		Intent intent = new Intent("roboid.intent.action.CLIP_STOP");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		context.sendBroadcast(intent);
		return true;
	}
	
	private static class EventHandler extends Handler
	{
		private final ClipPlayer mClipPlayer;
		
		EventHandler(ClipPlayer clipPlayer)
		{
			super();
			mClipPlayer = clipPlayer;
		}
		
		EventHandler(ClipPlayer clipPlayer, Looper looper)
		{
			super(looper);
			mClipPlayer = clipPlayer;
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case MSG_COMPLETION:
				{
					if(mClipPlayer == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String requestCode = intent.getStringExtra("roboid.intent.extra.CLIP_REQUEST_CODE");
					if(requestCode == null || !requestCode.equals(mClipPlayer.mRequestCode))
						return;
					
					mClipPlayer.mPlaying = false;
					ClipPlayer.OnCompletedListener listener = mClipPlayer.mOnCompletedListener;
					if(listener != null)
						listener.onCompleted(mClipPlayer);
				}
				break;
			case MSG_ERROR:
				{
					if(mClipPlayer == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String requestCode = intent.getStringExtra("roboid.intent.extra.CLIP_REQUEST_CODE");
					if(requestCode == null || !requestCode.equals(mClipPlayer.mRequestCode))
						return;
					
					int errorCode = intent.getIntExtra("roboid.intent.extra.CLIP_ERROR", ERROR_NONE);
					if(errorCode != ERROR_NONE)
					{
						ClipPlayer.OnErrorListener listener = mClipPlayer.mOnErrorListener;
						if(listener != null)
							listener.onError(mClipPlayer, errorCode);
					}
				}
				break;
			}
		}
	}
}
