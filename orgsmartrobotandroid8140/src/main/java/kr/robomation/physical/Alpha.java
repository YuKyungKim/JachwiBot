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

package kr.robomation.physical;

/**
 * <p>알파 로봇의 모델 ID와 알파 로봇을 구성하는 각 디바이스의 ID에 대한 상수 값을 정의한다.
 * <p>알파 로봇은 8개의 센서 디바이스와 8개의 이펙터 디바이스, 1개의 커맨드 디바이스, 2개의 이벤트 디바이스로 구성되어 있으며, 각 디바이스의 데이터는 정수형 배열로 선언되어 있다.
 * <p>알파 로봇을 구성하는 디바이스의 데이터를 읽거나 쓰기 위해서는 우선 {@link org.roboid.robot.Robot Robot} 클래스의 {@link org.roboid.robot.Robot#findDeviceById(int) findDeviceById(int deviceId)} 메소드를 사용하여 디바이스 객체의 레퍼런스를 얻어야 한다.
 * 이렇게 얻어진 디바이스 객체에 대해 read 또는 write 메소드를 사용하여 디바이스의 데이터를 읽거나 쓸 수 있다.
 * {@link org.roboid.robot.Device.DeviceDataChangedListener#onDeviceDataChanged(org.roboid.robot.Device, Object, long) onDeviceDataChanged} 콜백 메소드를 통해 디바이스의 데이터를 얻을 수도 있다.
 * </p>
 * <pre class="prettyprint">
 * void someMethod(Robot robot)
 {
     Device deviceLip = robot.findDeviceById(Alpha.EFFECTOR_LIP); // 입 이펙터 디바이스를 얻는다.
     int lip = deviceLip.read(); // 입 크기 값을 읽는다.

     Device deviceLeftProximity = robot.findDeviceById(Alpha.SENSOR_LEFT_PROXIMITY); // 왼쪽 근접 센서 디바이스를 얻는다.
     int[] leftProximity = new int[4];
     deviceLeftProximity.read(leftProximity); // 왼쪽 근접 센서 값을 읽는다.

     Device devicePosition = robot.findDeviceById(Alpha.SENSOR_POSITION); // 위치 센서 디바이스를 얻는다.
     int positionX = devicePosition.read(0); // 위치 데이터의 X 좌표 값을 읽는다.
     int positionY = devicePosition.read(1); // 위치 데이터의 Y 좌표 값을 읽는다.

     Device deviceFrontOID = robot.findDeviceById(Alpha.EVENT_FRONT_OID); // 앞쪽 OID 이벤트 디바이스를 얻는다.
     if(deviceFrontOID.e()) // 앞쪽 OID 값이 갱신되었는지 확인한다.
     {
         int frontOID = deviceFrontOID.read(); // 앞쪽 OID 값을 읽는다.
     }

     Device deviceLeftWheel = robot.findDeviceById(Alpha.EFFECTOR_LEFT_WHEEL); // 왼쪽 바퀴 이펙터 디바이스를 얻는다.
     deviceLeftWheel.write(25); // 왼쪽 바퀴 속도 값을 쓴다.

     Device deviceLeftEye = robot.findDeviceById(Alpha.EFFECTOR_LEFT_EYE); // 왼쪽 눈 이펙터 디바이스를 얻는다.
     int[] leftEye = new int[] { 255, 0, 0 };
     deviceLeftEye.write(leftEye); // 왼쪽 눈 색상 값을 쓴다.

     Device devicePadSize = robot.findDeviceById(Alpha.COMMAND_PAD_SIZE); // 패드 크기 커맨드 디바이스를 얻는다.
     devicePadSize.write(0, 108); // 패드의 폭 값을 쓴다.
     devicePadSize.write(1, 76); // 패드의 높이 값을 쓴다.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int leftWheel, padWidth, padHeight, frontOID;
     int[] leftProximity = new int[4];
     switch(device.getId())
     {
     case Alpha.EFFECTOR_LEFT_WHEEL: // 왼쪽 바퀴 속도 값을 얻는다.
         leftWheel = ((int[])values)[0];
         break;
     case Alpha.COMMAND_PAD_SIZE: // 패드 크기 값을 얻는다.
         padWidth = ((int[])values)[0];
         padHeight = ((int[])values)[1];
         break;
     case Alpha.SENSOR_LEFT_PROXIMITY: // 왼쪽 근접 센서 값을 얻는다.
         System.arraycopy((int[])values, 0, leftProximity, 0, 4);
         break;
     case Alpha.EVENT_FRONT_OID: // 앞쪽 OID 값을 얻는다.
         frontOID = ((int[])values)[0];
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
public final class Alpha
{
	/**
	 * <p>알파 로봇의 모델 ID를 나타내는 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: "kr.robomation.physical.alpha"
	 * </ul>
	 */
	public static final String ID = "kr.robomation.physical.alpha";
	
	/**
	 * <p>스피커 이펙터 디바이스의 ID를 나타내는 상수.
	 * <p>스피커 이펙터 디바이스의 데이터는 로봇의 스피커로 출력되는 소리(PCM)를 나타낸다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100000
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 480
	 *             <li>값의 범위: -32768 ~ 32767
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_SPEAKER = 0x00100000;
	/**
	 * <p>소리 크기 이펙터 디바이스의 ID를 나타내는 상수.
	 * <p>소리 크기 이펙터 디바이스의 데이터는 로봇의 스피커로 출력되는 소리의 크기를 나타낸다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100001
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: 0 ~ 300 [%]
	 *             <li>초기 값: 100
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_VOLUME = 0x00100001;
	/**
	 * <p>입 이펙터 디바이스의 ID를 나타내는 상수.
	 * <p>입 이펙터 디바이스의 데이터는 로봇의 입의 크기를 나타낸다.
	 * 알파 로봇의 입은 실제 하드웨어 장치에는 없는 가상의 장치이며, 이 값을 이용하여 화면에 그래픽을 표시하거나 LED를 깜박이도록 하여 입을 움직이는 것과 비슷한 효과를 나타낼 수 있다.
	 * 클립 파일을 재생할 때 이 값을 얻을 수 있다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100002
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
	public static final int EFFECTOR_LIP = 0x00100002;
	/**
	 * <p>왼쪽 바퀴 이펙터 디바이스의 ID를 나타내는 상수.
	 * <p>왼쪽 바퀴 이펙터 디바이스의 데이터는 왼쪽 바퀴의 속도를 나타낸다.
	 * 양수 값은 전진 방향으로의 회전을, 음수 값은 후진 방향으로의 회전을 의미한다.
	 * 부호를 제외한 절대치가 클수록 속도가 빨라진다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100003
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: -100 ~ 100 [%]
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_LEFT_WHEEL = 0x00100003;
	/**
	 * <p>오른쪽 바퀴 이펙터 디바이스의 ID를 나타내는 상수.
	 * <p>오른쪽 바퀴 이펙터 디바이스의 데이터는 오른쪽 바퀴의 속도를 나타낸다.
	 * 양수 값은 전진 방향으로의 회전을, 음수 값은 후진 방향으로의 회전을 의미한다.
	 * 부호를 제외한 절대치가 클수록 속도가 빨라진다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100004
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: -100 ~ 100 [%]
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_RIGHT_WHEEL = 0x00100004;
	/**
	 * <p>왼쪽 눈 이펙터 디바이스의 ID를 나타내는 상수.
	 * <p>왼쪽 눈 이펙터 디바이스의 데이터는 왼쪽 눈의 LED 색상을 나타낸다.
	 * 크기 3의 데이터 배열에서 첫 번째 값은 RGB 성분의 R 값, 두 번째 값은 G 값, 세 번째 값은 B 값이다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100005
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 3
	 *             <li>값의 범위: 0 ~ 255
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_LEFT_EYE = 0x00100005;
	/**
	 * <p>오른쪽 눈 이펙터 디바이스의 ID를 나타내는 상수.
	 * <p>오른쪽 눈 이펙터 디바이스의 데이터는 오른쪽 눈의 LED 색상을 나타낸다.
	 * 크기 3의 데이터 배열에서 첫 번째 값은 RGB 성분의 R 값, 두 번째 값은 G 값, 세 번째 값은 B 값이다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100006
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 3
	 *             <li>값의 범위: 0 ~ 255
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_RIGHT_EYE = 0x00100006;
	/**
	 * <p>버저 이펙터 디바이스의 ID를 나타내는 상수.
	 * <p>버저 이펙터 디바이스의 데이터는 버저 소리의 음 높이를 나타낸다.
	 * 버저 소리를 끄기 위해서는 0을 입력하면 된다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100007
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: 0 ~ 2500 [Hz] (0: off)
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_BUZZER = 0x00100007;
	/**
	 * <p>패드 크기 커맨드 디바이스의 ID를 나타내는 상수.
	 * <p>패드 크기 커맨드 디바이스의 데이터는 내비게이션 패드의 크기를 나타낸다.
	 * 크기 2의 데이터 배열에서 첫 번째 값은 패드의 가로 크기, 두 번째 값은 패드의 세로 크기이다.
	 * 가로 크기와 세로 크기의 곱(면적)은 40000을 넘을 수 없다.
	 * 즉, 가로 크기가 1인 경우에는 세로 크기가 1 ~ 40000까지 가능하고, 가로 크기가 200인 경우에는 세로 크기가 1 ~ 200까지 가능하다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100008
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 2
	 *             <li>값의 범위: 0 ~ 40000 (0: 유효하지 않은 값)
	 *             <li>초기 값: 0 (유효하지 않은 값)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int COMMAND_PAD_SIZE = 0x00100008;
	/**
	 * <p>왼쪽 근접 센서 디바이스의 ID를 나타내는 상수.
	 * <p>왼쪽 근접 센서 디바이스의 데이터는 왼쪽 근접 센서 값을 나타낸다.
	 * 근접 센서는 약 10ms마다 측정되는데 애플리케이션으로 약 40ms마다 전달되므로 센서 값을 4개씩 모아서 전달한다.
	 * 10ms 간격으로 측정된 센서 값 4개가 크기 4의 데이터 배열에 차례대로 기록되어 전달된다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100009
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 4
	 *             <li>값의 범위: 0 ~ 255
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_LEFT_PROXIMITY = 0x00100009;
	/**
	 * <p>오른쪽 근접 센서 디바이스의 ID를 나타내는 상수.
	 * <p>오른쪽 근접 센서 디바이스의 데이터는 오른쪽 근접 센서 값을 나타낸다.
	 * 근접 센서는 약 10ms마다 측정되는데 애플리케이션으로 약 40ms마다 전달되므로 센서 값을 4개씩 모아서 전달한다.
	 * 10ms 간격으로 측정된 센서 값 4개가 크기 4의 데이터 배열에 차례대로 기록되어 전달된다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x0010000a
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 4
	 *             <li>값의 범위: 0 ~ 255
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_RIGHT_PROXIMITY = 0x0010000a;
	/**
	 * <p>가속도 센서 디바이스의 ID를 나타내는 상수.
	 * <p>가속도 센서 디바이스의 데이터는 로봇의 3축 가속도 센서 값을 나타낸다.
	 * 크기 3의 데이터 배열에서 첫 번째 값은 X축, 두 번째 값은 Y축, 세 번째 값은 Z축의 가속도 값이다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x0010000b
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
	public static final int SENSOR_ACCELERATION = 0x0010000b;
	/**
	 * <p>위치 센서 디바이스의 ID를 나타내는 상수.
	 * <p>위치 센서 디바이스의 데이터는 내비게이션 패드 위에서 로봇의 위치를 나타낸다.
	 * 크기 2의 데이터 배열에서 첫 번째 값은 X축 좌표, 두 번째 값은 Y축 좌표 값이다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x0010000c
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 2
	 *             <li>값의 범위: -1 ~ 39999 (-1: 유효하지 않은 값)
	 *             <li>초기 값: -1 (유효하지 않은 값)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_POSITION = 0x0010000c;
	/**
	 * <p>방향 센서 디바이스의 ID를 나타내는 상수.
	 * <p>방향 센서 디바이스의 데이터는 내비게이션 패드 위에서 로봇의 방향을 나타낸다.
	 * 내비게이션 패드의 X축 방향이 0도이다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x0010000d
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: -179 ~ 180[<sup>o</sup>] 또는 -200[<sup>o</sup>] (-200: 유효하지 않은 값)
	 *             <li>초기 값: -200 (유효하지 않은 값)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_ORIENTATION = 0x0010000d;
	/**
	 * <p>조도 센서 디바이스의 ID를 나타내는 상수.
	 * <p>조도 센서 디바이스의 데이터는 로봇의 조도 센서 값을 나타낸다.
	 * 밝을 수록 값이 커진다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x0010000e
	 *     <li>디바이스의 데이터 배열
	 *         <ul>
	 *             <li>데이터 형: int [ ]
	 *             <li>배열 크기: 1
	 *             <li>값의 범위: 0 ~ 65535
	 *             <li>초기 값: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_LIGHT = 0x0010000e;
	/**
	 * <p>온도 센서 디바이스의 ID를 나타내는 상수.
	 * <p>온도 센서 디바이스의 데이터는 로봇 내부의 온도를 나타낸다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x0010000f
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
	public static final int SENSOR_TEMPERATURE = 0x0010000f;
	/**
	 * <p>배터리 센서 디바이스의 ID를 나타내는 상수.
	 * <p>배터리 센서 디바이스의 데이터는 로봇의 배터리 잔량을 %로 나타낸다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100010
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
	public static final int SENSOR_BATTERY = 0x00100010;
	/**
	 * <p>앞쪽 OID 이벤트 디바이스의 ID를 나타내는 상수.
	 * <p>앞쪽 OID 이벤트 디바이스의 데이터는 로봇의 바닥 면에 있는 앞쪽 OID 값을 나타낸다.
	 * OID 값을 읽지 못한 경우에는 -1의 값을 가진다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100011
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
	public static final int EVENT_FRONT_OID = 0x00100011;
	/**
	 * <p>뒤쪽 OID 이벤트 디바이스의 ID를 나타내는 상수.
	 * <p>뒤쪽 OID 이벤트 디바이스의 데이터는 로봇의 바닥 면에 있는 뒤쪽 OID 값을 나타낸다.
	 * OID 값을 읽지 못한 경우에는 -1의 값을 가진다.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 0x00100012
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
	public static final int EVENT_BACK_OID = 0x00100012;
}