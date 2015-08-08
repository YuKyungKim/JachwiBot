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
 * <p>로보이드를 구성하는 디바이스를 찾거나 리스너를 등록 및 제거하는 메소드를 정의한다.
 * </p>
 * 
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see Robot
 * @see Device
 * @see Device.DeviceDataChangedListener
 */
public interface Roboid extends NamedElement
{
	/**
	 * <p>로보이드의 모델 ID를 반환한다.
	 * </p>
	 * @return 로보이드의 모델 ID
	 */
	String getId();
	/**
	 * <p>자식 디바이스 중에서 이름이 name인 디바이스를 찾아 인스턴스를 반환한다.
	 * <p>이름이 name인 자식 디바이스가 없으면 null을 반환한다.
	 * 이 때, 자식 로보이드에 포함된 디바이스의 이름은 "자식로보이드.디바이스"와 같이 "."으로 구분된 형식을 사용한다.
	 * 예: "Pen1.Button"
	 * </p>
	 * <pre class="prettyprint">
     * void someMethod(Roboid alpha)
 {
     Device leftWheel = alpha.findDeviceByName("LeftWheel"); // 알파 로봇의 왼쪽 바퀴 이펙터 디바이스를 찾는다.
     Device button = alpha.findDeviceByName("Pen1.Button"); // 1번 펜의 버튼 이벤트 디바이스를 찾는다.
 }</pre>
	 * 
	 * @param name 찾을 디바이스의 이름
	 * <p>
	 * @return 디바이스의 인스턴스 또는 null
	 */
	Device findDeviceByName(String name);
	/**
	 * <p>자식 디바이스 중에서 ID가 deviceId인 디바이스를 찾아 인스턴스를 반환한다.
	 * <p>ID가 deviceId인 자식 디바이스가 없으면 null을 반환한다.
	 * </p>
	 * <pre class="prettyprint">
     * void someMethod(Roboid alpha)
 {
     Device leftWheel = alpha.findDeviceById(Alpha.EFFECTOR_LEFT_WHEEL); // 알파 로봇의 왼쪽 바퀴 이펙터 디바이스를 찾는다.
 }</pre>
	 * 
	 * @param deviceId 찾을 디바이스의 ID
	 * <p>
	 * @return 디바이스의 인스턴스 또는 null
	 */
	Device findDeviceById(int deviceId);
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
