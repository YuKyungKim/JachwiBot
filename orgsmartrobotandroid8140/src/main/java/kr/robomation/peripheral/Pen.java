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

package kr.robomation.peripheral;

/**
 * <p>펜의 모델 ID와 펜을 구성하는 각 디바이스의 ID에 대한 상수 값을 정의한다.
 * <p>펜은 2개의 센서 디바이스와 2개의 이벤트 디바이스로 구성되어 있으며, 각 디바이스의 데이터는 정수형 배열로 선언되어 있다.
 * <p>펜의 디바이스 ID 값은 실제 제품을 구분하지 않는다.
 * 예를 들어, 1번 펜과 2번 펜이 있다고 했을 때 펜의 디바이스 ID만으로는 어느 펜의 디바이스인지 구분할 수 없다.
 * 실제 제품을 구분하고자 하는 경우에는 제품에 대한 추가적인 정보, 즉 제품 번호가 필요하다.
 * 주변기기의 제품 번호는 론처에서 주변기기를 등록할 때 표시되며, {@link org.roboid.robot.Device Device} 클래스의 {@link org.roboid.robot.Device#getProductId() getProductId()} 메소드를 사용하여 얻을 수 있다.
 * <p>이와 같이 제품을 구분하는 경우는 주변기기에만 해당된다.
 * 로봇이나 액션의 경우에는 구분할 제품이 없으므로 해당되지 않는다.
 * <p>펜을 구성하는 디바이스의 데이터를 읽기 위해서는 우선 {@link org.roboid.robot.Robot Robot} 클래스의 {@link org.roboid.robot.Robot#findDeviceById(int, int) findDeviceById(int productId, int deviceId)} 메소드를 사용하여 디바이스 객체의 레퍼런스를 얻어야 한다.
 * productId에는 펜의 제품 번호를 입력하고 deviceId에는 디바이스의 ID를 입력한다.
 * 이렇게 얻어진 디바이스 객체에 대해 read 메소드를 사용하여 디바이스의 데이터를 읽을 수 있다.
 * <p>애플리케이션에서 사용하는 펜을 특정 펜으로 제한해야 할 이유가 없다면 제품을 구분하지 않고 어떤 펜이든지 상관없도록 작업하는 것이 더 좋다.
 * 사용자가 어떤 펜을 사용할지 알 수 없기 때문이다.
 * 이 경우에는 {@link org.roboid.robot.Device.DeviceDataChangedListener#onDeviceDataChanged(org.roboid.robot.Device, Object, long) onDeviceDataChanged} 콜백 메소드를 통해 디바이스의 데이터를 얻는 것이 더 간단하다.
 * <p>이에 반해, 펜을 여러 개 사용하는 애플리케이션의 경우에는 애플리케이션과 펜들을 패키지로 제공하여 특정 펜(빨간 펜, 파란 펜, 토끼 그림 펜, 과일 그림 펜)으로 제한하고 사용자가 쉽게 구분할 수 있게 해야 하며, 반드시 아래와 같이 제품 번호로 각 펜을 구분해야 한다.
 * </p>
 * <pre class="prettyprint">
 * void someMethod(Robot robot)
 {
     Device device = robot.findDeviceById(1, Pen.EVENT_OID); // 1번 펜의 OID 이벤트 디바이스를 얻는다.
     int oid;
     if(device.e()) // 1번 펜의 OID 값이 갱신되었는지 확인한다.
         oid = device.read(); // 1번 펜의 OID 값을 읽는다.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int oid, oid1, oid2;
     // 1번 펜과 2번 펜을 구분하지 않는 경우
      switch(device.getId())
     {
     case Pen.EVENT_OID: // 사용자가 사용하는 펜의 OID 값이 갱신되었다.
         oid = ((int[])values)[0];
         break;
     }

     // 1번 펜과 2번 펜을 구분하는 경우
      switch(device.getId())
     {
     case Pen.EVENT_OID:
         if(device.getProductId() == 1) // 1번 펜의 OID 값이 갱신되었다.
             oid1 = ((int[])values)[0];
         else if(device.getProductId() == 2) // 2번 펜의 OID 값이 갱신되었다.
             oid2 = ((int[])values)[0];
         break;
     }
 }</pre>
 *
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see org.roboid.robot.Robot Robot
 * @see org.roboid.robot.Device Device
 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
 */
public final class Pen
{
	/**
	 * <p>펜의 모델 ID를 나타내는 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: "kr.robomation.peripheral.pen"
	 * </ul>
	 */
	public static final String ID = "kr.robomation.peripheral.pen";
	
	/**
	 * <p>신호 세기 센서 디바이스의 ID를 나타내는 상수.
	 * <p>신호 세기 센서 디바이스의 데이터는 펜과 로봇 본체 간의 지그비 무선 통신의 신호 세기를 나타낸다.
	 * 신호의 세기가 커질수록 값이 커진다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x80100000
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: 0 ~ 255
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_SIGNAL = 0x80100000;
	/**
	 * <p>배터리 센서 디바이스의 ID를 나타내는 상수.
	 * <p>배터리 센서 디바이스의 데이터는 펜의 배터리 잔량을 %로 나타낸다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x80100001
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: 0 ~ 100 [%]
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_BATTERY = 0x80100001;
	/**
	 * <p>OID 이벤트 디바이스의 ID를 나타내는 상수.
	 * <p>OID 이벤트 디바이스의 데이터는 펜의 OID 값을 나타낸다.
	 * OID 값을 읽지 못한 경우에는 -1의 값을 가진다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x80100002
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: -1 ~ 65535 (-1: 유효하지 않은 값)
	 *             <li>초기 값: -1 (유효하지 않은 값)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_OID = 0x80100002;
	/**
	 * <p>버튼 이벤트 디바이스의 ID를 나타내는 상수.
	 * <p>버튼 이벤트 디바이스의 데이터는 펜의 버튼 상태를 나타낸다.
	 * 버튼을 누르면 1, 누르지 않으면 0의 값을 가진다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x80100003
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: 0 또는 1 (0: 버튼을 누르지 않은 상태, 1: 버튼을 누른 상태)
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_BUTTON = 0x80100003;
}