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

package org.roboid.robot;

import org.roboid.robot.Device.DeviceDataChangedListener;

/**
 * <p>로봇을 구성하는 디바이스를 찾거나 리스너를 등록 및 제거하는 메소드를 정의한다.
 * </p>
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see Roboid
 * @see Device
 * @see Device.DeviceDataChangedListener
 */
public interface Robot extends NamedElement
{
	/**
	 * <p>하드웨어 로봇과 블루투스 통신을 연결 중임을 나타내는 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 1
	 * </ul>
	 */
	int STATE_CONNECTING = 1;
	/**
	 * <p>하드웨어 로봇과 블루투스 통신이 연결되었음을 나타내는 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 2
	 * </ul>
	 */
	int STATE_CONNECTED = 2;
	/**
	 * <p>하드웨어 로봇과 블루투스 통신 연결이 비정상적으로 끊어졌음을 나타내는 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 3
	 * </ul>
	 */
	int STATE_CONNECTION_LOST = 3;
	/**
	 * <p>하드웨어 로봇과 블루투스 통신이 정상적으로 종료되었음을 나타내는 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 4
	 * </ul>
	 */
	int STATE_DISCONNECTED = 4;
	
	/**
	 * <p>로봇의 ID를 반환한다.
	 * </p>
	 * @return 로봇의 ID
	 */
	String getId();
	/**
	 * <p>자식 디바이스 중에서 ID가 deviceId인 디바이스를 찾아 인스턴스를 반환한다.
	 * <p>ID가 deviceId인 자식 디바이스가 없으면 null을 반환한다.
	 * <p>주변기기의 경우에는 {@link #findDeviceById(int, int) findDeviceById(int productId, int deviceId)} 메소드를 사용해야 한다.
	 * </p>
	 * <pre class="prettyprint">
     * void someMethod(Robot robot)
 {
     Device leftWheel = robot.findDeviceById(Alpha.EFFECTOR_LEFT_WHEEL); // 알파 로봇의 왼쪽 바퀴 이펙터 디바이스를 찾는다.
 }</pre>
	 * 
	 * @param deviceId 찾을 디바이스의 ID
	 * <p>
	 * @return 디바이스의 인스턴스 또는 null
	 */
	Device findDeviceById(int deviceId);
	/**
	 * <p>제품 번호가 productId인 주변기기의 자식 디바이스 중에서 ID가 deviceId인 디바이스를 찾아 인스턴스를 반환한다.
	 * <p>ID가 deviceId인 자식 디바이스가 없으면 null을 반환한다.
	 * <p>로봇의 경우에는 {@link #findDeviceById(int) findDeviceById(int deviceId)} 메소드를 사용해야 한다.
	 * </p>
	 * <pre class="prettyprint">
     * void someMethod(Robot robot)
 {
     Device button = robot.findDeviceById(1, Pen.EVENT_BUTTON); // 1번 펜의 버튼 이벤트 디바이스를 찾는다.
 }</pre>
	 * 
	 * @param productId 제품 번호
	 * @param deviceId 찾을 디바이스의 ID
	 * <p>
	 * @return 디바이스의 인스턴스 또는 null
	 */
	Device findDeviceById(int productId, int deviceId);
	/**
	 * <p>디바이스의 데이터가 갱신되었을 때 호출되도록 listener를 등록한다.
	 * </p>
	 * @param listener 등록할 리스너
	 */
	void addDeviceDataChangedListener(DeviceDataChangedListener listener);
	/**
	 * <p>등록된 리스너 목록에서 listener를 제거한다.
	 * </p>
	 * @param listener 제거할 리스너
	 */
	void removeDeviceDataChangedListener(DeviceDataChangedListener listener);
	/**
	 * <p>등록된 모든 리스너를 제거한다.
	 */
	void clearDeviceDataChangedListener();
}
