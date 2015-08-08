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
 * <p>주사위의 모델 ID와 주사위를 구성하는 각 디바이스의 ID에 대한 상수 값을 정의한다.
 * <p>주사위는 4개의 센서 디바이스와 2개의 이벤트 디바이스로 구성되어 있으며, 각 디바이스의 데이터는 정수형 배열로 선언되어 있다.
 * <p>주사위의 디바이스 ID 값은 실제 제품을 구분하지 않는다.
 * 예를 들어, 1번 주사위와 2번 주사위가 있다고 했을 때 주사위의 디바이스 ID만으로는 어느 주사위의 디바이스인지 구분할 수 없다.
 * 실제 제품을 구분하고자 하는 경우에는 제품에 대한 추가적인 정보, 즉 제품 번호가 필요하다.
 * 주변기기의 제품 번호는 론처에서 주변기기를 등록할 때 표시되며, {@link org.roboid.robot.Device Device} 클래스의 {@link org.roboid.robot.Device#getProductId() getProductId()} 메소드를 사용하여 얻을 수 있다.
 * <p>이와 같이 제품을 구분하는 경우는 주변기기에만 해당된다.
 * 로봇이나 액션의 경우에는 구분할 제품이 없으므로 해당되지 않는다.
 * <p>주사위를 구성하는 디바이스의 데이터를 읽기 위해서는 우선 {@link org.roboid.robot.Robot Robot} 클래스의 {@link org.roboid.robot.Robot#findDeviceById(int, int) findDeviceById(int productId, int deviceId)} 메소드를 사용하여 디바이스 객체의 레퍼런스를 얻어야 한다.
 * productId에는 주사위의 제품 번호를 입력하고 deviceId에는 디바이스의 ID를 입력한다.
 * 이렇게 얻어진 디바이스 객체에 대해 read 메소드를 사용하여 디바이스의 데이터를 읽을 수 있다.
 * <p>애플리케이션에서 사용하는 주사위를 특정 주사위로 제한해야 할 이유가 없다면 제품을 구분하지 않고 어떤 주사위든지 상관없도록 작업하는 것이 더 좋다.
 * 사용자가 어떤 주사위를 사용할지 알 수 없기 때문이다.
 * 이 경우에는 {@link org.roboid.robot.Device.DeviceDataChangedListener#onDeviceDataChanged(org.roboid.robot.Device, Object, long) onDeviceDataChanged} 콜백 메소드를 통해 디바이스의 데이터를 얻는 것이 더 간단하다.
 * <p>이에 반해, 주사위를 여러 개 사용하는 애플리케이션의 경우에는 애플리케이션과 주사위들을 패키지로 제공하여 특정 주사위(빨간 주사위, 파란 주사위, 토끼 그림 주사위, 과일 그림 주사위)로 제한하고 사용자가 쉽게 구분할 수 있게 해야 하며, 반드시 아래와 같이 제품 번호로 각 주사위를 구분해야 한다.
 * </p>
 * <pre class="prettyprint">
 * void someMethod(Robot robot)
 {
     Device device = robot.findDeviceById(1, Dice.EVENT_VALUE); // 1번 주사위의 눈 이벤트 디바이스를 얻는다.
     int dice;
     if(device.e()) // 1번 주사위의 눈 값이 갱신되었는지 확인한다.
         dice = device.read(); // 1번 주사위의 눈 값을 읽는다.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int dice, dice1, dice2;
     // 1번 주사위와 2번 주사위를 구분하지 않는 경우
      switch(device.getId())
     {
     case Dice.EVENT_VALUE: // 사용자가 사용하는 주사위의 눈 값이 갱신되었다.
         dice = ((int[])values)[0];
         break;
     }

     // 1번 주사위와 2번 주사위를 구분하는 경우
      switch(device.getId())
     {
     case Dice.EVENT_VALUE:
         if(device.getProductId() == 1) // 1번 주사위의 눈 값이 갱신되었다.
             dice1 = ((int[])values)[0];
         else if(device.getProductId() == 2) // 2번 주사위의 눈 값이 갱신되었다.
             dice2 = ((int[])values)[0];
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
public final class Dice
{
	/**
	 * <p>주사위의 모델 ID를 나타내는 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: "kr.robomation.peripheral.dice"
	 * </ul>
	 */
	public static final String ID = "kr.robomation.peripheral.dice";
	
	/**
	 * <p>신호 세기 센서 디바이스의 ID를 나타내는 상수.
	 * <p>신호 세기 센서 디바이스의 데이터는 주사위와 로봇 본체 간의 지그비 무선 통신의 신호 세기를 나타낸다.
	 * 신호의 세기가 커질수록 값이 커진다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x80200000
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
	public static final int SENSOR_SIGNAL = 0x80200000;
	/**
	 * <p>온도 센서 디바이스의 ID를 나타내는 상수.
	 * <p>온도 센서 디바이스의 데이터는 주사위 내부의 온도를 나타낸다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x80200001
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: -40 ~ 88 [<sup>o</sup>C]
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_TEMPERATURE = 0x80200001;
	/**
	 * <p>배터리 센서 디바이스의 ID를 나타내는 상수.
	 * <p>배터리 센서 디바이스의 데이터는 주사위의 배터리 잔량을 %로 나타낸다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x80200002
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
	public static final int SENSOR_BATTERY = 0x80200002;
	/**
	 * <p>가속도 센서 디바이스의 ID를 나타내는 상수.
	 * <p>가속도 센서 디바이스의 데이터는 주사위의 3축 가속도 센서 값을 나타낸다.
	 * 크기 3의 데이터 배열에서 첫 번째 값은 X축, 두 번째 값은 Y축, 세 번째 값은 Z축의 가속도 값이다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x80200003
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 3
	 *             <li>값의 범위: -8192 ~ 8191
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_ACCELERATION = 0x80200003;
	/**
	 * <p>자유 낙하 이벤트 디바이스의 ID를 나타내는 상수.
	 * <p>자유 낙하 이벤트 디바이스의 데이터는 주사위의 자유 낙하 값을 나타낸다.
	 * 주사위를 공중으로 던질 때마다 값이 1씩 증가한다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x80200004
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: -1 ~ 255 (-1: 유효하지 않은 값)
	 *             <li>초기 값: -1 (유효하지 않은 값)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_FALL = 0x80200004;
	/**
	 * <p>주사위 눈 이벤트 디바이스의 ID를 나타내는 상수.
	 * <p>주사위 눈 이벤트 디바이스의 데이터는 주사위의 눈을 나타낸다.
	 * 주사위의 윗면이 수직으로 위를 향하고 있을 때는 1 ~ 6의 값을 가지고, 주사위가 기울어져 있는 경우에는 -1 ~ -6의 값을 가진다.
	 * 음수 값인 경우 (주사위가 기울어져 있는 경우) 부호를 제외한 절대치는 보다 가까운 쪽의 눈을 표시한다.
	 * 즉, 주사위의 윗면이 3인 경우 윗면이 수직으로 위를 향하고 있을 때는 3, 기울어져 있을 때는 -3의 값을 가진다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x80200005
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: -6 ~ 6 (0: 유효하지 않은 값)
	 *             <li>초기 값: 0 (유효하지 않은 값)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_VALUE = 0x80200005;
}