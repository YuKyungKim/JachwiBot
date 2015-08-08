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

import org.roboid.robot.Device;
import org.roboid.robot.Robot;

import android.app.Activity;

/**
 * <p>로봇을 생성하고 이벤트를 처리하는 등의 기본적인 작업을 해놓은 액티비티.
 * <p>로봇을 구성하는 디바이스의 데이터는 {@link #onActivated()} 메소드가 호출된 이후부터 {@link #onDeactivated()} 메소드가 호출되기 전까지 읽거나 쓸 수 있다.
 * </p>
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
 * @see org.smartrobot.android.SmartRobot SmartRobot
 * @see org.smartrobot.android.SmartRobot.Callback SmartRobot.Callback
 */
public class RobotActivity extends Activity implements SmartRobot.Callback, Device.DeviceDataChangedListener
{
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
	 * {@inheritDoc}
	 */
	@Override
	public void onInitialized(Robot robot)
	{
		robot.addDeviceDataChangedListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onActivated()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDeactivated()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisposed()
	{
		finish();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onExecute()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStateChanged(int state)
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onNameChanged(String name)
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDeviceDataChanged(Device device, Object values, long timestamp)
	{
	}
}