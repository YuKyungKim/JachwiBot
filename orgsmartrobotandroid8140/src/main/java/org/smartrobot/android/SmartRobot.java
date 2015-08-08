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

package org.smartrobot.android;

import java.lang.ref.WeakReference;

import org.roboid.robot.Robot;
import org.smartrobot.android.ipc.dc;
import org.smartrobot.android.ipc.dr;
import org.smartrobot.android.ipc.rc;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

/**
 * <p>론처와 연결하거나 연결 해제하는 메소드를 정의한다.
 * <p>로봇을 구성하는 디바이스의 데이터는 {@link Callback#onActivated() onActivated()} 메소드가 호출된 이후부터 {@link Callback#onDeactivated() onDeactivated()} 메소드가 호출되기 전까지 읽거나 쓸 수 있다.
 * </p>
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
public final class SmartRobot
{
	private static final int STATE_NONE = 0;
	private static final int STATE_QUIT = 5;
	
	private static final int MSG_ACK = 1;
	private static final int MSG_STATE = 2;
	private static final int MSG_CHANGE_ROBOT = 3;
	private static final int MSG_CHANGE_NAME = 4;
	
	private WeakReference<Context> mContext;
	Callback mCallback;
	private String mCurrentId;
	AbstractRobot mCurrentRobot;
	private PhysicalRoboid mCurrentRoboid;
	private boolean mActive;
	
	private rc mRobotControllerBinder;
	private final ServiceConnection mRobotControllerConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			mRobotControllerBinder = rc.Stub.asInterface(binder);
			try
			{
				mRobotControllerBinder.a(mSensoryDataChangedCallback);
				mRobotControllerBinder.c(mMotoringDataChangedCallback);
				mRobotControllerBinder.e(mMotoringDataRequestCallback);
			} catch (RemoteException e)
			{
			}
			
			if(mCallback != null)
				mCallback.onActivated();
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mRobotControllerBinder = null;
		}
	};
	private final dc.Stub mSensoryDataChangedCallback = new dc.Stub()
	{
		@Override
		public void a(byte[] a1, long a2) throws RemoteException
		{
			if(a1 != null && mCurrentRoboid != null)
				mCurrentRoboid.handleSensorySimulacrum(a1, a2);
		}

		@Override
		public void b(int b1, int b2, byte[] b3, long b4) throws RemoteException
		{
			if(b3 != null && mCurrentRoboid != null)
			{
				PeripheralRoboid peripheral = mCurrentRoboid.findPeripheral(b1, b2);
				if(peripheral == null)
				{
					peripheral = PeripheralFactory.create(b1, b2);
					if(peripheral == null) return;
					mCurrentRoboid.addPeripheral(peripheral);
				}
				if(peripheral != null)
					peripheral.handleSensorySimulacrum(b3, b4);
			}
		}
	};
	private final dc.Stub mMotoringDataChangedCallback = new dc.Stub()
	{
		@Override
		public void a(byte[] a1, long a2) throws RemoteException
		{
			if(a1 != null && mCurrentRoboid != null)
				mCurrentRoboid.handleMotoringSimulacrum(a1, a2);
			if(mCurrentRobot != null)
				mCurrentRobot.updateDeviceState();
			if(mCallback != null)
				mCallback.onExecute();
		}

		@Override
		public void b(int b1, int b2, byte[] b3, long b4) throws RemoteException
		{
		}
	};
	private final dr.Stub mMotoringDataRequestCallback = new dr.Stub()
	{
		@Override
		public byte[] a() throws RemoteException
		{
			if(mRobotControllerBinder == null) return null;
			if(mCurrentRoboid == null) return null;
			return mCurrentRoboid.encodeMotoringSimulacrum();
		}

		@Override
		public byte[] b(int b1, int b2) throws RemoteException
		{
			return null;
		}
	};
	private BroadcastReceiver mBR;
	private final EventHandler mEventHandler;
	private static SmartRobot INSTANCE = new SmartRobot();
	
	/**
	 * <p>로봇의 생명주기와 이벤트에 따라 호출되는 메소드를 정의한다.
	 * <p>로봇을 구성하는 디바이스의 데이터는 {@link #onActivated()} 메소드가 호출된 이후부터 {@link #onDeactivated()} 메소드가 호출되기 전까지 읽거나 쓸 수 있다.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see SmartRobot
	 */
	public interface Callback
	{
		/**
		 * <p>{@link SmartRobot#activate(Context, Callback) SmartRobot.activate(Context, SmartRobot.Callback)} 메소드를 호출한 후에 {@link org.roboid.robot.Robot Robot} 인스턴스가 생성되거나 {@link org.roboid.robot.Robot Robot} 인스턴스가 변경되었을 때 호출된다.
		 * <p>즉, 로봇의 인스턴스가 새로 생성되었을 때, 론처에서 로봇을 변경하여 다른 로봇 인스턴스로 변경되었을 때 호출된다.
		 * </p>
		 * @param robot 로봇의 인스턴스
		 */
		void onInitialized(Robot robot);
		/**
		 * <p>{@link #onInitialized(Robot)} 메소드가 호출된 후에 애플리케이션이 론처에 연결되었을 때 호출된다.
		 * <p>onActivated() 메소드가 호출된 이후부터 {@link #onDeactivated()} 메소드가 호출되기 전까지 디바이스의 데이터를 읽거나 쓸 수 있으며, 약 20ms마다 주기적으로 {@link #onExecute()} 메소드가 호출된다.
		 */
		void onActivated();
		/**
		 * <p>{@link SmartRobot#deactivate()} 메소드를 호출한 후 또는 론처가 종료되어, 애플리케이션이 론처에서 연결 해제되었을 때 호출된다.
		 * <p>onDeactivated() 메소드가 호출된 이후에는 디바이스의 데이터를 읽거나 쓸 수 없다.
		 */
		void onDeactivated();
		/**
		 * <p>론처가 종료되었을 때 호출된다.
		 * <p>론처가 종료될 때 애플리케이션도 같이 종료되어야 하므로 onDisposed() 메소드에서 액티비티를 닫고 리소스를 해제하는 등의 종료 작업을 해야 한다.
		 */
		void onDisposed();
		/**
		 * <p>{@link #onActivated()} 메소드가 호출된 이후부터 {@link #onDeactivated()} 메소드가 호출되기 전까지 약 20ms마다 주기적으로 호출된다.
		 * <p>onExecute() 메소드에서는 항상 디바이스의 데이터를 읽거나 쓸 수 있다.
		 */
		void onExecute();
		/**
		 * <p>하드웨어 로봇과 블루투스 연결 상태가 변경되었을 때 호출된다.
		 * <p>state가 가질 수 있는 값은 {@link org.roboid.robot.Robot Robot} 인터페이스에 정의되어 있다.
		 * </p>
		 * @param state 블루투스 연결 상태
		 * <p>
		 * @see org.roboid.robot.Robot#STATE_CONNECTING Robot.STATE_CONNECTING
		 * @see org.roboid.robot.Robot#STATE_CONNECTED Robot.STATE_CONNECTED
		 * @see org.roboid.robot.Robot#STATE_CONNECTION_LOST Robot.STATE_CONNECTION_LOST
		 * @see org.roboid.robot.Robot#STATE_DISCONNECTED Robot.STATE_DISCONNECTED
		 */
		void onStateChanged(int state);
		/**
		 * <p>론처에서 현재 연결된 로봇의 이름을 변경했을 때 호출된다.
		 * </p>
		 * @param name 변경된 로봇의 이름
		 */
		void onNameChanged(String name);
	}
	
	private SmartRobot()
	{
		Looper looper;
		if((looper = Looper.myLooper()) != null)
		{
			mEventHandler = new EventHandler(looper);
		}
		else if((looper = Looper.getMainLooper()) != null)
		{
			mEventHandler = new EventHandler(looper);
		}
		else
			mEventHandler = new EventHandler();
	}

	/**
	 * <p>론처와 연결한다.
	 * <p>context와 callback은 null이 아니어야 한다.
	 * </p>
	 * @param context 컨텍스트
	 * @param callback 로봇의 생명주기와 이벤트에 따라 호출되는 콜백
	 * <p>
	 * @return 성공하면 true, 아니면 false
	 */
	public static boolean activate(Context context, Callback callback)
	{
		if(context == null || callback == null) return false;
		INSTANCE.setContext(context);
		INSTANCE.setCallback(callback);
		return INSTANCE.doActivate();
	}
	
	/**
	 * <p>론처와의 연결을 해제한다.
	 * </p>
	 */
	public static void deactivate()
	{
		INSTANCE.doDeactivate();
	}
	
	Context getContext()
	{
		if(mContext == null) return null;
		return mContext.get();
	}
	
	private void setContext(Context context)
	{
		Context applicationContext = context.getApplicationContext();
		if(applicationContext == null)
			mContext = new WeakReference<Context>(context);
		else
			mContext = new WeakReference<Context>(applicationContext);
	}
	
	private void setCallback(Callback callback)
	{
		mCallback = callback;
	}
	
	private boolean doActivate()
	{
		if(mActive) return true;
		Context context = getContext();
		if(context == null) return false;
		mActive = true;
		
		registerBroadcast(context);
		
		Intent intent = new Intent("roboid.intent.action.ROBOT_REQ");
		intent.putExtra("roboid.intent.extra.PACKAGE_NAME", context.getPackageName());
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
				if("roboid.intent.action.ROBOT_ACK".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_ACK);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ROBOT_STATE".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_STATE);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ROBOT_CHANGE".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_CHANGE_ROBOT);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ROBOT_CHANGE_NAME".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_CHANGE_NAME);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("roboid.intent.action.ROBOT_ACK");
		intentFilter.addAction("roboid.intent.action.ROBOT_STATE");
		intentFilter.addAction("roboid.intent.action.ROBOT_CHANGE");
		intentFilter.addAction("roboid.intent.action.ROBOT_CHANGE_NAME");
		context.registerReceiver(mBR, intentFilter);
	}
	
	private void doDeactivate()
	{
		if(mActive == false) return;
		mActive = false;
		
		if(mEventHandler != null)
		{
			mEventHandler.removeMessages(MSG_ACK);
			mEventHandler.removeMessages(MSG_STATE);
			mEventHandler.removeMessages(MSG_CHANGE_ROBOT);
			mEventHandler.removeMessages(MSG_CHANGE_NAME);
		}
		disconnect();
		
		if(mBR != null)
		{
			Context context = getContext();
			if(context != null)
				context.unregisterReceiver(mBR);
			mBR = null;
		}
		mCurrentRobot = null;
		mCurrentRoboid = null;
		mCurrentId = null;
		mCallback = null;
	}
	
	boolean connect(String id, String name)
	{
		Context context = getContext();
		if(context == null) return false;
		if(id == null) return false;
		
		if(!id.equals(mCurrentId))
		{
			if(mCurrentRobot != null)
				mCurrentRobot.clearDeviceDataChangedListener();
			
			mCurrentRobot = RobotFactory.create(id);
			if(mCurrentRobot == null)
			{
				mCurrentId = null;
				mCurrentRoboid = null;
				return false;
			}
			else
			{
				mCurrentRoboid = mCurrentRobot.mRoboid;
				mCurrentRobot.setName(name);
			}
		}
		if(mCurrentRobot == null || mCurrentRoboid == null) return false;
		
		Intent intent = new Intent("roboid.intent.action.ROBOT");
		intent.addCategory(id);
		intent.putExtra("roboid.intent.extra.PACKAGE_NAME", context.getPackageName());
		try
		{
			context.bindService(intent, mRobotControllerConnection, Context.BIND_AUTO_CREATE);
		} catch (SecurityException e)
		{
			return false;
		}

		if(mCallback != null)
			mCallback.onInitialized(mCurrentRobot);
		mCurrentId = id;
		
		return true;
	}
	
	void disconnect()
	{
		try
		{
			if(mRobotControllerBinder != null)
			{
				mRobotControllerBinder.b(mSensoryDataChangedCallback);
				mRobotControllerBinder.d(mMotoringDataChangedCallback);
				mRobotControllerBinder.f(mMotoringDataRequestCallback);
				mRobotControllerBinder = null;
			}
			Context context = getContext();
			if(context != null)
				context.unbindService(mRobotControllerConnection);
		} catch (Exception e)
		{
		}
		
		if(mCallback != null)
			mCallback.onDeactivated();
	}
	
	private class EventHandler extends Handler
	{
		EventHandler()
		{
			super();
		}
		
		EventHandler(Looper looper)
		{
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case MSG_ACK:
				{
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					Context context = getContext();
					if(context == null) return;
					
					String packageName = intent.getStringExtra("roboid.intent.extra.PACKAGE_NAME");
					if(packageName == null || !packageName.equals(context.getPackageName())) return;
					
					String id = intent.getStringExtra("roboid.intent.extra.ROBOT_ID");
					String name = intent.getStringExtra("roboid.intent.extra.ROBOT_NAME");
					connect(id, name);
				}
				break;
			case MSG_STATE:
				{
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					int state = intent.getIntExtra("roboid.intent.extra.ROBOT_STATE", STATE_NONE);
					switch(state)
					{
					case STATE_NONE:
						break;
					case STATE_QUIT:
						{
							if(mCurrentRobot != null)
								mCurrentRobot.clearDeviceDataChangedListener();
							
							disconnect();
							SmartRobot.Callback callback = mCallback;
							if(callback != null)
								callback.onDisposed();
						}
						break;
					default:
						{
							SmartRobot.Callback callback = mCallback;
							if(callback != null)
								callback.onStateChanged(state);
						}
						break;
					}
				}
				break;
			case MSG_CHANGE_ROBOT:
				{
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String id = intent.getStringExtra("roboid.intent.extra.ROBOT_ID");
					String name = intent.getStringExtra("roboid.intent.extra.ROBOT_NAME");
					disconnect();
					connect(id, name);
				}
				break;
			case MSG_CHANGE_NAME:
				{
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String id = intent.getStringExtra("roboid.intent.extra.ROBOT_ID");
					String name = intent.getStringExtra("roboid.intent.extra.ROBOT_NAME");
					if(id != null && id.equals(mCurrentId))
					{
						if(mCurrentRobot != null)
							mCurrentRobot.setName(name);
						
						SmartRobot.Callback callback = mCallback;
						if(callback != null)
							callback.onNameChanged(name);
					}
				}
				break;
			}
		}
	}
}