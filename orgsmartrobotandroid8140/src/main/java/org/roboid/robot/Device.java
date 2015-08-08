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

/**
 * <p>디바이스의 데이터를 읽고 쓰는 메소드를 정의한다.
 * <p>디바이스의 데이터는 배열로 선언되어 있으며, 배열의 데이터 형과 크기, 값의 범위는 디바이스에 따라 다르다.
 * </p> 
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see Robot
 * @see Roboid
 * @see org.smartrobot.android.action.Action Action
 * @see Device.DeviceDataChangedListener
 */
public interface Device extends NamedElement
{
	/**
	 * <p>디바이스의 데이터가 갱신되었을 때 호출되는 메소드를 정의한다.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Robot
	 * @see Roboid
	 * @see Device
	 */
	public interface DeviceDataChangedListener
	{
		/**
		 * <p>디바이스의 데이터가 갱신되었을 때 호출된다.
		 * <p>values는 정수형 배열(int[]) 또는 실수형 배열(float[]), 스트링 배열(String[])의 데이터 형을 가진다.
		 * </p>
		 * @param device 데이터가 갱신된 디바이스
		 * @param values 디바이스의 데이터 배열
		 * @param timestamp 데이터가 갱신된 시간 (System.nanoTime() 메소드로 측정된 시간)
		 */
		void onDeviceDataChanged(Device device, Object values, long timestamp);
	}

	/**
	 * <p>디바이스의 ID를 반환한다.
	 * <p>주변기기의 경우에는 실제 제품을 구분하지 않는다.
	 * </p>
	 * <pre class="prettyprint">
     * public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int accelerationY, dice;
     switch(device.getId()) // 디바이스의 ID를 얻는다.
     {
     case Alpha.SENSOR_ACCELERATION: // 알파 로봇의 가속도 센서 값이 갱신되었다.
         accelerationY = ((int[])values)[1];
         break;
     case Dice.EVENT_VALUE: // 주사위의 눈 값이 갱신되었다. 어느 주사위인지는 구분하지 않는다.
         dice = ((int[])values)[0];
         break;
     }
 }</pre>
	 * 
	 * @return 디바이스의 ID
	 * <p>
	 * @see Device.DeviceDataChangedListener
	 */
	int getId();
	/**
	 * <p>주변기기의 제품 번호를 반환한다.
	 * <p>로봇이나 액션의 경우에는 0을 반환한다.
	 * </p>
	 * <pre class="prettyprint">
     * public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int dice1, dice2;
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
	 * @return 주변기기의 제품 번호
	 * <p>
	 * @see Device.DeviceDataChangedListener
	 * @see kr.robomation.peripheral.Pen Pen
	 * @see kr.robomation.peripheral.Dice Dice
	 */
	int getProductId();
	/**
	 * <p>디바이스의 데이터 형을 반환한다.
	 * <p>디바이스의 데이터 형에 대한 상수 값은 {@link DataType} 클래스에 정의되어 있다.
	 * </p>
	 * <pre class="prettyprint">
     * void someMethod(Robot robot)
 {
     Device device = robot.findDeviceById(Alpha.EFFECTOR_LEFT_WHEEL);
     if(device.getDataType() == DataType.INTEGER) // 데이터 형이 정수형 배열(int[])임을 확인한다.
     {
         ...
     }
 }</pre>
     * 
	 * @return 디바이스의 데이터 형
	 * <p>
	 * @see DataType#INTEGER
	 * @see DataType#FLOAT
	 * @see DataType#STRING
	 */
	int getDataType();
	/**
	 * <p>디바이스의 데이터 배열 크기를 반환한다.
	 * <p>여기서의 데이터 배열 크기는 모델에서 지정한 배열 크기를 의미한다.
	 * 배열 크기가 -1로 지정되어 있는 경우는 데이터 배열 크기가 가변적이라는 의미이다.
	 * 이 경우에는 -1을 반환한다.
	 * </p>
	 * @return 디바이스의 데이터 배열 크기. 데이터 배열 크기가 가변적인 경우에는 -1
	 */
	int getDataSize();
	/**
	 * <p>디바이스의 데이터가 갱신되었는지 확인한다.
	 * <p>센서 또는 이펙터 디바이스는 데이터가 계속 갱신되기 때문에 확인할 필요가 없지만, 커맨드 또는 이벤트 디바이스는 데이터를 읽기 전에 반드시 e() 메소드를 사용하여 데이터가 갱신되었는지 확인해야 한다.
	 * 그렇지 않으면 커맨드 또는 이벤트가 발생한 시점을 알 수가 없기 때문에 현재 발생한 커맨드 또는 이벤트의 데이터가 아니라 과거에 발생하였던 커맨드 또는 이벤트의 데이터를 읽을 수도 있다.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Robot robot)
 {
     Device device = robot.findDeviceById(Alpha.EVENT_FRONT_OID); // 앞쪽 OID 이벤트 디바이스를 얻는다.
     int oid;
     if(device.e()) // 앞쪽 OID 값이 갱신되었는지 확인한다.
         oid = device.read(); // 앞쪽 OID 값을 읽는다.
 }</pre>
	 *
	 * @return 디바이스의 데이터가 갱신되었으면 true, 아니면 false
	 */
	boolean e();
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스 0의 위치에 있는 데이터를 정수 값으로 반환한다.
	 * <p>디바이스의 데이터 배열 크기가 0인 경우에는 0을 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 0을 반환한다.
	 * </p>
	 * @return 디바이스의 데이터 또는 0
	 */
	int read();
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스가 index인 위치에 있는 데이터를 정수 값으로 반환한다.
	 * <p>index가 디바이스의 데이터 배열 크기보다 크거나 같은 경우, 혹은 index가 음수인 경우에는 0을 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 0을 반환한다.
	 * </p>
	 * @param index 디바이스 데이터 배열의 인덱스
	 * <p>
	 * @return 디바이스의 데이터 또는 0
	 */
	int read(int index);
	/**
	 * <p>디바이스의 데이터 배열을 정수형 배열 data에 복사하고, 복사한 데이터의 개수를 반환한다.
	 * <p>디바이스의 데이터 배열 크기가 data 배열의 크기보다 큰 경우에는 data 배열의 크기만큼만 복사한다.
	 * 디바이스의 데이터 배열 크기가 data 배열의 크기보다 작은 경우에는 디바이스의 데이터 배열을 모두 복사한 후 data 배열의 나머지 부분은 0으로 채운다.
	 * data 배열이 null인 경우, data 배열의 크기가 0인 경우, 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 복사하지 않고 0을 반환한다.
	 * </p>
	 * @param data 디바이스의 데이터 배열을 복사할 정수형 배열
	 * <p>
	 * @return 복사한 데이터의 개수
	 */
	int read(int[] data);
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스 0의 위치에 있는 데이터를 실수 값으로 반환한다.
	 * <p>디바이스의 데이터 배열 크기가 0인 경우에는 0.0을 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 0.0을 반환한다.
	 * </p>
	 * @return 디바이스의 데이터 또는 0.0
	 */
	float readFloat();
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스가 index인 위치에 있는 데이터를 실수 값으로 반환한다.
	 * <p>index가 디바이스의 데이터 배열 크기보다 크거나 같은 경우, 혹은 index가 음수인 경우에는 0.0을 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 0.0을 반환한다.
	 * </p>
	 * @param index 디바이스 데이터 배열의 인덱스
	 * <p>
	 * @return 디바이스의 데이터 또는 0.0
	 */
	float readFloat(int index);
	/**
	 * <p>디바이스의 데이터 배열을 실수형 배열 data에 복사하고, 복사한 데이터의 개수를 반환한다.
	 * <p>디바이스의 데이터 배열 크기가 data 배열의 크기보다 큰 경우에는 data 배열의 크기만큼만 복사한다.
	 * 디바이스의 데이터 배열 크기가 data 배열의 크기보다 작은 경우에는 디바이스의 데이터 배열을 모두 복사한 후 data 배열의 나머지 부분은 0.0으로 채운다.
	 * data 배열이 null인 경우, data 배열의 크기가 0인 경우, 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 복사하지 않고 0을 반환한다.
	 * </p>
	 * @param data 디바이스의 데이터 배열을 복사할 실수형 배열
	 * <p>
	 * @return 복사한 데이터의 개수
	 */
	int readFloat(float[] data);
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스 0의 위치에 있는 데이터를 스트링으로 반환한다.
	 * <p>디바이스의 데이터 배열 크기가 0인 경우에는 ""을 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}이 아닌 경우에는 ""을 반환한다.
	 * </p>
	 * @return 디바이스의 데이터 또는 ""
	 */
	String readString();
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스가 index인 위치에 있는 데이터를 스트링으로 반환한다.
	 * <p>index가 디바이스의 데이터 배열 크기보다 크거나 같은 경우, 혹은 index가 음수인 경우에는 ""을 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}이 아닌 경우에는 ""을 반환한다.
	 * </p>
	 * @param index 디바이스 데이터 배열의 인덱스
	 * <p>
	 * @return 디바이스의 데이터 또는 ""
	 */
	String readString(int index);
	/**
	 * <p>디바이스의 데이터 배열을 스트링 배열 data에 복사하고, 복사한 데이터의 개수를 반환한다.
	 * <p>디바이스의 데이터 배열 크기가 data 배열의 크기보다 큰 경우에는 data 배열의 크기만큼만 복사한다.
	 * 디바이스의 데이터 배열 크기가 data 배열의 크기보다 작은 경우에는 디바이스의 데이터 배열을 모두 복사한 후 data 배열의 나머지 부분은 ""으로 채운다.
	 * data 배열이 null인 경우, data 배열의 크기가 0인 경우, 디바이스의 데이터 형이 {@link DataType#STRING}이 아닌 경우에는 복사하지 않고 0을 반환한다.
	 * </p>
	 * @param data 디바이스의 데이터 배열을 복사할 스트링 배열
	 * <p>
	 * @return 복사한 데이터의 개수
	 */
	int readString(String[] data);
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스 0의 위치에 정수 값 data를 쓴다.
	 * <p>디바이스의 데이터 배열 크기가 0인 경우에는 쓰지 않고 false를 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 쓰지 않고 false를 반환한다.
	 * </p>
	 * @param data 디바이스에 쓸 정수 값 데이터
	 * <p>
	 * @return 성공하면 true, 실패하면 false
	 */
	boolean write(int data);
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스가 index인 위치에 정수 값 data를 쓴다.
	 * <p>index가 디바이스의 데이터 배열 크기보다 크거나 같은 경우, 혹은 index가 음수인 경우에는 쓰지 않고 false를 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 쓰지 않고 false를 반환한다.
	 * </p>
	 * @param index 디바이스 데이터 배열의 인덱스
	 * @param data 디바이스에 쓸 정수 값 데이터
	 * <p>
	 * @return 성공하면 true, 실패하면 false
	 */
	boolean write(int index, int data);
	/**
	 * <p>정수형 배열 data를 디바이스의 데이터 배열에 복사하고, 복사한 데이터의 개수를 반환한다.
	 * <p>data 배열의 크기가 디바이스의 데이터 배열 크기보다 큰 경우에는 디바이스의 데이터 배열 크기만큼만 복사한다.
	 * data 배열의 크기가 디바이스의 데이터 배열 크기보다 작은 경우에는 data 배열을 모두 복사한 후 디바이스 데이터 배열의 나머지 부분은 0으로 채운다.
	 * data 배열이 null인 경우, data 배열의 크기가 0인 경우, 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 복사하지 않고 0을 반환한다.
	 * 디바이스의 데이터 배열 크기가 가변적인 경우(데이터 배열 크기가 -1로 지정된 경우)에는 data 배열의 크기만큼 디바이스의 데이터 배열을 새로 만들어서 data 배열을 복사한다.
	 * </p>
	 * @param data 디바이스의 데이터 배열에 복사할 정수형 배열
	 * <p>
	 * @return 복사한 데이터의 개수
	 */
	int write(int[] data);
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스 0의 위치에 실수 값 data를 쓴다.
	 * <p>디바이스의 데이터 배열 크기가 0인 경우에는 쓰지 않고 false를 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 쓰지 않고 false를 반환한다.
	 * </p>
	 * @param data 디바이스에 쓸 실수 값 데이터
	 * <p>
	 * @return 성공하면 true, 실패하면 false
	 */
	boolean writeFloat(float data);
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스가 index인 위치에 실수 값 data를 쓴다.
	 * <p>index가 디바이스의 데이터 배열 크기보다 크거나 같은 경우, 혹은 index가 음수인 경우에는 쓰지 않고 false를 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 쓰지 않고 false를 반환한다.
	 * </p>
	 * @param index 디바이스 데이터 배열의 인덱스
	 * @param data 디바이스에 쓸 실수 값 데이터
	 * <p>
	 * @return 성공하면 true, 실패하면 false
	 */
	boolean writeFloat(int index, float data);
	/**
	 * <p>실수형 배열 data를 디바이스의 데이터 배열에 복사하고, 복사한 데이터의 개수를 반환한다.
	 * <p>data 배열의 크기가 디바이스의 데이터 배열 크기보다 큰 경우에는 디바이스의 데이터 배열 크기만큼만 복사한다.
	 * data 배열의 크기가 디바이스의 데이터 배열 크기보다 작은 경우에는 data 배열을 모두 복사한 후 디바이스 데이터 배열의 나머지 부분은 0.0으로 채운다.
	 * data 배열이 null인 경우, data 배열의 크기가 0인 경우, 디바이스의 데이터 형이 {@link DataType#STRING}인 경우에는 복사하지 않고 0을 반환한다.
	 * 디바이스의 데이터 배열 크기가 가변적인 경우(데이터 배열 크기가 -1로 지정된 경우)에는 data 배열의 크기만큼 디바이스의 데이터 배열을 새로 만들어서 data 배열을 복사한다.
	 * </p>
	 * @param data 디바이스의 데이터 배열에 복사할 실수형 배열
	 * <p>
	 * @return 복사한 데이터의 개수
	 */
	int writeFloat(float[] data);
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스 0의 위치에 스트링 data를 쓴다.
	 * <p>디바이스의 데이터 배열 크기가 0인 경우에는 쓰지 않고 false를 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}이 아닌 경우에는 쓰지 않고 false를 반환한다.
	 * </p>
	 * @param data 디바이스에 쓸 스트링 데이터
	 * <p>
	 * @return 성공하면 true, 실패하면 false
	 */
	boolean writeString(String data);
	/**
	 * <p>디바이스의 데이터 배열에서 인덱스가 index인 위치에 스트링 data를 쓴다.
	 * <p>index가 디바이스의 데이터 배열 크기보다 크거나 같은 경우, 혹은 index가 음수인 경우에는 쓰지 않고 false를 반환한다.
	 * 디바이스의 데이터 형이 {@link DataType#STRING}이 아닌 경우에는 쓰지 않고 false를 반환한다.
	 * </p>
	 * @param index 디바이스 데이터 배열의 인덱스
	 * @param data 디바이스에 쓸 스트링 데이터
	 * <p>
	 * @return 성공하면 true, 실패하면 false
	 */
	boolean writeString(int index, String data);
	/**
	 * <p>스트링 배열 data를 디바이스의 데이터 배열에 복사하고, 복사한 데이터의 개수를 반환한다.
	 * <p>data 배열의 크기가 디바이스의 데이터 배열 크기보다 큰 경우에는 디바이스의 데이터 배열 크기만큼만 복사한다.
	 * data 배열의 크기가 디바이스의 데이터 배열 크기보다 작은 경우에는 data 배열을 모두 복사한 후 디바이스 데이터 배열의 나머지 부분은 ""으로 채운다.
	 * data 배열이 null인 경우, data 배열의 크기가 0인 경우, 디바이스의 데이터 형이 {@link DataType#STRING}이 아닌 경우에는 복사하지 않고 0을 반환한다.
	 * 디바이스의 데이터 배열 크기가 가변적인 경우(데이터 배열 크기가 -1로 지정된 경우)에는 data 배열의 크기만큼 디바이스의 데이터 배열을 새로 만들어서 data 배열을 복사한다.
	 * </p>
	 * @param data 디바이스의 데이터 배열에 복사할 스트링 배열
	 * <p>
	 * @return 복사한 데이터의 개수
	 */
	int writeString(String[] data);
}