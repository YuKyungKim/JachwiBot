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

package org.smartrobot.android.web;

import org.roboid.robot.Device;
import org.roboid.robot.Robot;
import org.smartrobot.android.SmartRobot;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * <p>{@link org.smartrobot.android.web.RobotWebView RobotWebView}를 전체 화면으로 표시하고, 로봇의 생명주기와 이벤트를 처리하는 등의 기본적인 작업을 해놓은 액티비티.
 * <p>로봇을 구성하는 디바이스의 데이터는 {@link #onActivated()} 메소드가 호출된 이후부터 {@link #onDeactivated()} 메소드가 호출되기 전까지 읽거나 쓸 수 있다.
 * </p>
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
 * @see org.smartrobot.android.SmartRobot SmartRobot
 * @see org.smartrobot.android.SmartRobot.Callback SmartRobot.Callback
 */
public class RobotWebActivity extends Activity implements SmartRobot.Callback, Device.DeviceDataChangedListener
{
	private RobotWebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mWebView = new RobotWebView(this);
        setContentView(mWebView);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		SmartRobot.activate(getApplicationContext(), this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		SmartRobot.deactivate();
	}

	/**
	 * <p>포함된 웹뷰를 반환한다.
	 * <p>웹뷰는 onCreate 메소드 내에서 생성된다.
	 * </p>
	 * @return 웹뷰
	 */
	protected WebView getWebView()
	{
		return mWebView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onInitialized(Robot robot)
	{
		robot.addDeviceDataChangedListener(this);
		mWebView.onInitialized(robot);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onActivated()
	{
		mWebView.onActivated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDeactivated()
	{
		mWebView.onDeactivated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisposed()
	{
		mWebView.onDisposed();
		finish();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onExecute()
	{
		mWebView.onExecute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStateChanged(int state)
	{
		mWebView.onStateChanged(state);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onNameChanged(String name)
	{
		mWebView.onNameChanged(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDeviceDataChanged(Device device, Object values, long timestamp)
	{
	}
}