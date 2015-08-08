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

package org.smartrobot.android.action;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.roboid.robot.Device.DeviceDataChangedListener;
import org.roboid.robot.impl.RoboidImpl;
import org.smartrobot.android.ipc.ac;
import org.smartrobot.android.ipc.dc;
import org.smartrobot.android.ipc.dr;

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
 * <p>액션 인스턴스를 얻거나 시작, 중지, 폐기하는 메소드를 정의한다.
 * </p>
 * <pre class="prettyprint">
 * public SampleActivity extends Activity
 {
     private Action mAction;

     protected void onCreate(Bundle savedInstanceState)
     {
         super.onCreate(savedInstanceState);
         mAction = Action.obtain(this, Action.Microphone.ID);
     }

     protected void onDestroy()
     {
         super.onDestroy();
         mAction.dispose();
     }

     protected void onStart()
     {
         super.onStart();
         mAction.activate();
     }

     protected void onStop()
     {
         super.onStop();
         mAction.deactivate();
     }
 }</pre>
 * 
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see org.roboid.robot.Roboid Roboid
 * @see org.roboid.robot.Device Device
 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
 */
public abstract class Action extends RoboidImpl
{
	private static final int STATE_NONE = 0;
	/**
	 * <p>액션이 준비된 상태를 나타내는 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 1
	 * </ul>
	 */
	public static final int STATE_PREPARED = 1;
	/**
	 * <p>액션이 시작된 상태를 나타내는 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 2
	 * </ul>
	 */
	public static final int STATE_ACTIVATED = 2;
	/**
	 * <p>액션이 중지된 상태를 나타내는 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 3
	 * </ul>
	 */
	public static final int STATE_DEACTIVATED = 3;
	/**
	 * <p>액션이 폐기된 상태를 나타내는 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: 4
	 * </ul>
	 */
	public static final int STATE_DISPOSED = 4;
	
	private static final int ERROR_NONE = 0;
	/**
	 * <p>액션의 ID가 잘못 되었음을 나타내는 오류 코드 상수.
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
	 * <p>액션 인스턴스를 얻거나 시작, 중지하는 순서가 잘못 되었음을 나타내는 오류 코드 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: -3
	 * </ul>
	 */
	public static final int ERROR_ILLEGAL_STATE = -3;
	/**
	 * <p>지원하지 않는 액션임을 나타내는 오류 코드 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: -4
	 * </ul>
	 */
	public static final int ERROR_NOT_SUPPORTED = -4;
	/**
	 * <p>보안 문제로 액션 실행을 실패하였음을 나타내는 오류 코드 상수.
	 * </p>
	 * <ul>
	 *     <li>상수 값: -5
	 * </ul>
	 */
	public static final int ERROR_SECURITY = -5;
	
	/**
	 * <p>폰의 마이크를 통해 음성을 인식하는 액션의 모델 ID와 각 디바이스의 ID에 대한 상수 값을 정의한다.
	 * <p>Action.VoiceRecognition은 7개의 커맨드 디바이스와 1개의 센서 디바이스, 1개의 이벤트 디바이스로 구성되어 있으며, 각 디바이스의 데이터는 스트링 배열 또는 정수형 배열, 실수형 배열로 선언되어 있다.
	 * <p>Action.VoiceRecognition은 음성 인식이 완료되면 {@link Action.OnCompletedListener#onCompleted(Action) onCompleted(Action)} 메소드를 호출한다.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device deviceLanguageModel = action.findDeviceById(Action.VoiceRecognition.COMMAND_LANGUAGE_MODEL);
     deviceLanguageModel.writeString("free_form"); // 언어 모델을 쓴다.

     Device deviceLanguage = action.findDeviceById(Action.VoiceRecognition.COMMAND_LANGUAGE);
     deviceLanguage.writeString("ko"); // 로케일의 언어 코드를 쓴다.

     Device deviceCountry = action.findDeviceById(Action.VoiceRecognition.COMMAND_COUNTRY);
     deviceCountry.writeString("KR"); // 로케일의 국가 코드를 쓴다.

     Device deviceVariant = action.findDeviceById(Action.VoiceRecognition.COMMAND_VARIANT);
     deviceVariant.writeString("POSIX"); // 로케일의 이형(異形) 코드를 쓴다.

     Device deviceVisibility = action.findDeviceById(Action.VoiceRecognition.COMMAND_VISIBILITY);
     deviceVisibility.write(1); // 대화창을 나타나게 한다.

     Device devicePrompt = action.findDeviceById(Action.VoiceRecognition.COMMAND_PROMPT);
     devicePrompt.writeString("말씀하세요"); // 대화창에 표시할 문자열을 쓴다.

     Device deviceMaxResults = action.findDeviceById(Action.VoiceRecognition.COMMAND_MAX_RESULTS);
     deviceMaxResults.write(5); // 인식 결과 후보는 최대 5개로 한다.

     Device deviceMicLevel = action.findDeviceById(Action.VoiceRecognition.SENSOR_MIC_LEVEL);
     float micLevel = deviceMicLevel.readFloat(); // 마이크의 소리 크기 값을 읽는다.

     Device deviceText = action.findDeviceById(Action.VoiceRecognition.EVENT_TEXT);
     if(deviceText.e())
     {
         String[] results = new String[5];
         deviceText.readString(results); // 인식 결과를 읽는다.
     }
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     float micLevel;
     String[] results;
     switch(device.getId())
     {
     case Action.VoiceRecognition.SENSOR_MIC_LEVEL: // 마이크의 소리 크기 값이 갱신되었다.
         micLevel = ((float[])values)[0]; // 마이크의 소리 크기 값을 얻는다.
         break;
     case Action.VoiceRecognition.EVENT_TEXT: // 음성 인식 결과 값이 갱신되었다.
         results = (String[])values; // 음성 인식 결과 값을 얻는다.
         break;
     }
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see Action.OnCompletedListener
	 * @see org.roboid.robot.Device Device
	 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
	 */
	public final class VoiceRecognition
	{
		/**
		 * <p>VoiceRecognition 액션의 모델 ID를 나타내는 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: "org.smartrobot.android.action.voicerecognition"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.voicerecognition";
		/**
		 * <p>언어 모델 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>언어 모델 커맨드 디바이스의 데이터는 음성 인식의 언어 모델을 나타낸다.
		 * "free_form" 또는 "web_search" 중의 하나이다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40100000
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: String [ ]
		 *             <li>배열 크기: 1
		 *             <li>초기 값: "free_form"
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_LANGUAGE_MODEL = 0x40100000;
		/**
		 * <p>언어 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>언어 커맨드 디바이스의 데이터는 음성 인식의 로케일에서 언어 코드를 나타낸다.
		 * 설정하지 않으면 기본 언어 코드를 사용한다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40100001
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: String [ ]
		 *             <li>배열 크기: 1
		 *             <li>초기 값: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_LANGUAGE = 0x40100001;
		/**
		 * <p>국가 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>국가 커맨드 디바이스의 데이터는 음성 인식의 로케일에서 국가 코드를 나타낸다.
		 * 설정하지 않으면 기본 국가 코드를 사용한다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40100002
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: String [ ]
		 *             <li>배열 크기: 1
		 *             <li>초기 값: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_COUNTRY = 0x40100002;
		/**
		 * <p>이형(異形) 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>이형(異形) 커맨드 디바이스의 데이터는 음성 인식의 로케일에서 이형(異形) 코드를 나타낸다.
		 * 설정하지 않으면 기본 이형(異形) 코드를 사용한다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40100003
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: String [ ]
		 *             <li>배열 크기: 1
		 *             <li>초기 값: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_VARIANT = 0x40100003;
		/**
		 * <p>보이기 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>보이기 커맨드 디바이스의 데이터는 음성 인식 대화창을 나타나게 할지 감출지의 여부를 나타낸다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40100004
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: 0 또는 1 (0: 대화창 감추기, 1: 대화창 보이기)
		 *             <li>초기 값: 1
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_VISIBILITY = 0x40100004;
		/**
		 * <p>프롬프트 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>프롬프트 커맨드 디바이스의 데이터는 음성 인식 대화창에 표시할 문자열을 나타낸다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40100005
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: String [ ]
		 *             <li>배열 크기: 1
		 *             <li>초기 값: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_PROMPT = 0x40100005;
		/**
		 * <p>최대 결과 수 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>최대 결과 수 커맨드 디바이스의 데이터는 음성 인식 결과 후보의 최대 개수를 나타낸다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40100006
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: 0 ~ 255
		 *             <li>초기 값: 5
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_MAX_RESULTS = 0x40100006;
		/**
		 * <p>음량 센서 디바이스의 ID를 나타내는 상수.
		 * <p>음량 센서 디바이스의 데이터는 폰의 마이크를 통해 입력된 소리의 크기(RMS)를 데시벨로 나타낸다.
		 * 소리가 커질수록 값이 커진다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40100007
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: float [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: -100 ~ 100 [dB]
		 *             <li>초기 값: -100
		 *         </ul>
		 * </ul>
		 */
		public static final int SENSOR_MIC_LEVEL = 0x40100007;
		/**
		 * <p>문장 이벤트 디바이스의 ID를 나타내는 상수.
		 * <p>문장 이벤트 디바이스의 데이터는 음성 인식된 결과 문자열을 스트링 배열로 나타낸다.
		 * 스트링 배열의 크기는 음성 인식 결과 후보의 최대 개수로 설정된 값과 같다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40100008
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: String [ ]
		 *             <li>배열 크기: -1
		 *             <li>초기 값: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int EVENT_TEXT = 0x40100008;

		/**
		 * <p>마이크 입력에서 음성이 시작된 상태를 나타내는 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 101
		 * </ul>
		 */
		public static final int STATE_BEGINNING_OF_SPEECH = 101;
		/**
		 * <p>마이크 입력에서 음성이 끝난 상태를 나타내는 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 103
		 * </ul>
		 */
		public static final int STATE_END_OF_SPEECH = 103;
		/**
		 * <p>마이크 입력으로 음성을 받아들일 준비가 된 상태를 나타내는 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 105
		 * </ul>
		 */
		public static final int STATE_READY_FOR_SPEECH = 105;

		/**
		 * <p>네트워크 시간 초과를 나타내는 오류 코드 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: -101
		 * </ul>
		 */
		public static final int ERROR_NETWORK_TIMEOUT = -101;
		/**
		 * <p>네트워크에 문제가 있음을 나타내는 오류 코드 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: -102
		 * </ul>
		 */
		public static final int ERROR_NETWORK = -102;
		/**
		 * <p>마이크 입력에 문제가 있음을 나타내는 오류 코드 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: -103
		 * </ul>
		 */
		public static final int ERROR_AUDIO = -103;
		/**
		 * <p>음성 인식 서버 측에 문제가 있음을 나타내는 오류 코드 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: -104
		 * </ul>
		 */
		public static final int ERROR_SERVER = -104;
		/**
		 * <p>애플리케이션 측에 문제가 있음을 나타내는 오류 코드 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: -105
		 * </ul>
		 */
		public static final int ERROR_CLIENT = -105;
		/**
		 * <p>주어진 시간 내에 음성을 입력하지 않았음을 나타내는 오류 코드 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: -106
		 * </ul>
		 */
		public static final int ERROR_SPEECH_TIMEOUT = -106;
		/**
		 * <p>인식 결과가 없음을 나타내는 오류 코드 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: -107
		 * </ul>
		 */
		public static final int ERROR_NO_MATCH = -107;
		/**
		 * <p>처리할 음성 데이터가 너무 많아 입력된 음성 데이터를 처리하지 못함을 나타내는 오류 코드 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: -108
		 * </ul>
		 */
		public static final int ERROR_RECOGNIZER_BUSY = -108;
	}
	
	/**
	 * <p>폰의 마이크를 통해 음량 등을 측정하는 액션의 모델 ID와 각 디바이스의 ID에 대한 상수 값을 정의한다.
	 * <p>Action.Microphone은 1개의 센서 디바이스로 구성되어 있으며, 각 디바이스의 데이터는 실수형 배열로 선언되어 있다.
	 * <p>Action.Microphone은 {@link Action#activate() activate()} 메소드로 시작하여 {@link Action#deactivate() deactivate()} 메소드로 중지할 때까지 실행된다.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device deviceLevel = action.findDeviceById(Action.Microphone.SENSOR_LEVEL); // 음량 센서 디바이스를 얻는다.
     float level = deviceLevel.readFloat(); // 음량 값을 읽는다.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     float level;
     switch(device.getId())
     {
     case Action.Microphone.SENSOR_LEVEL: // 음량 값이 갱신되었다.
         level = ((float[])values)[0]; // 음량 값을 얻는다.
         break;
     }
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see org.roboid.robot.Device Device
	 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
	 */
	public final class Microphone
	{
		/**
		 * <p>Microphone 액션의 모델 ID를 나타내는 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: "org.smartrobot.android.action.microphone"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.microphone";
		/**
		 * <p>음량 센서 디바이스의 ID를 나타내는 상수.
		 * <p>음량 센서 디바이스의 데이터는 폰의 마이크를 통해 입력된 소리의 파워를 데시벨로 나타낸다.
		 * 파워가 커질수록 값이 커진다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40200000
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: float [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: -100 ~ 0 [dB]
		 *             <li>초기 값: -100
		 *         </ul>
		 * </ul>
		 */
		public static final int SENSOR_LEVEL = 0x40200000;
	}
	
	/**
	 * <p>폰의 마이크로 입력받은 소리를 로봇의 스피커로 내보내는 액션의 모델 ID와 각 디바이스의 ID에 대한 상수 값을 정의한다.
	 * <p>Action.WalkieTalkie는 1개의 이펙터 디바이스로 구성되어 있으며, 각 디바이스의 데이터는 정수형 배열로 선언되어 있다.
	 * <p>Action.WalkieTalkie는 {@link Action#activate() activate()} 메소드로 시작하여 {@link Action#deactivate() deactivate()} 메소드로 중지할 때까지 실행된다.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device deviceSensitivity = action.findDeviceById(Action.WalkieTalkie.EFFECTOR_SENSITIVITY);
     deviceSensitivity.write(20); // 마이크 감도를 20으로 한다.
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see org.roboid.robot.Device Device
	 */
	public final class WalkieTalkie
	{
		/**
		 * <p>WalkieTalkie 액션의 모델 ID를 나타내는 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: "org.smartrobot.android.action.walkietalkie"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.walkietalkie";
		/**
		 * <p>감도 이펙터 디바이스의 ID를 나타내는 상수.
		 * <p>감도 이펙터 디바이스의 데이터는 폰의 마이크를 통해 입력되는 소리의 감도를 나타낸다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40300000
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: 0 ~ 100 [%]
		 *             <li>초기 값: 20
		 *         </ul>
		 * </ul>
		 */
		public static final int EFFECTOR_SENSITIVITY = 0x40300000;
	}
	
	/**
	 * <p>폰의 진동을 발생시키는 액션의 모델 ID와 각 디바이스의 ID에 대한 상수 값을 정의한다.
	 * <p>Action.Vibration은 3개의 커맨드 디바이스로 구성되어 있으며, 각 디바이스의 데이터는 정수형 배열로 선언되어 있다.
	 * <p>Action.Vibration은 {@link Action#activate() activate()} 메소드로 시작하여 {@link Action#deactivate() deactivate()} 메소드로 중지할 때까지 실행된다.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device deviceTime = action.findDeviceById(Action.Vibration.COMMAND_TIME);
     deviceTime.write(1000); // 1초간 진동을 출력한다.

     Device devicePattern = action.findDeviceById(Action.Vibration.COMMAND_PATTERN);
     int[] pattern = new int[] { 1000, 2000, 3000, 4000, 5000, 6000 }; // 1초 후에 2초간 진동, 3초 쉬고 4초간 진동, 5초 쉬고 6초간 진동
      devicePattern.write(pattern); // 진동 패턴을 쓴다.

     Device deviceRepeat = action.findDeviceById(Action.Vibration.COMMAND_REPEAT);
     deviceRepeat.write(2); // 진동 패턴의 인덱스 2부터 끝까지를 반복한다.
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see org.roboid.robot.Device Device
	 */
	public final class Vibration
	{
		/**
		 * <p>Vibration 액션의 모델 ID를 나타내는 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: "org.smartrobot.android.action.vibration"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.vibration";
		/**
		 * <p>시간 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>시간 커맨드 디바이스의 데이터는 진동의 지속 시간(ms)을 나타낸다.
		 * 시간 커맨드 디바이스에 데이터를 쓰면 패턴 커맨드 디바이스와 반복 커맨드 디바이스의 데이터는 무시된다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40400000
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: 0 ~ 65535 [ms]
		 *             <li>초기 값: 0
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_TIME = 0x40400000;
		/**
		 * <p>패턴 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>패턴 커맨드 디바이스의 데이터는 진동을 켜거나 끄는 시간 구간으로 구성된 패턴을 나타낸다.
		 * 패턴을 표시하는 정수형 배열에서 첫 번째 값은 진동을 켜기 전에 기다리는 시간(ms)이다.
		 * 두 번째 값부터 진동을 켠 상태를 유지하는 시간(ms)과 진동을 끈 상태를 유지하는 시간(ms)을 번갈아 입력한다.
		 * 패턴 커맨드 디바이스에 데이터를 쓰면 시간 커맨드 디바이스의 데이터는 무시된다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40400001
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: -1
		 *             <li>값의 범위: 0 ~ 65535 [ms]
		 *             <li>초기 값: 0
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_PATTERN = 0x40400001;
		/**
		 * <p>반복 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>반복 커맨드 디바이스의 데이터는 진동 패턴을 나타내는 정수형 배열에서 반복할 부분의 시작 인덱스를 나타낸다.
		 * 반복하지 않을 경우에는 -1을 입력하면 된다.
		 * 반복 커맨드 디바이스에 데이터를 쓰면 시간 커맨드 디바이스의 데이터는 무시된다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40400002
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: -1 ~ 32767
		 *             <li>초기 값: -1
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_REPEAT = 0x40400002;
	}
	
	/**
	 * <p>문장을 음성으로 출력하는 액션의 모델 ID와 각 디바이스의 ID에 대한 상수 값을 정의한다.
	 * <p>Action.Tts는 7개의 커맨드 디바이스로 구성되어 있으며, 각 디바이스의 데이터는 스트링 배열 또는 정수형 배열로 선언되어 있다.
	 * <p>Action.Tts는 음성 합성 출력이 완료되면 {@link Action.OnCompletedListener#onCompleted(Action) onCompleted(Action)} 메소드를 호출한다.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device deviceLanguage = action.findDeviceById(Action.Tts.COMMAND_LANGUAGE);
     deviceLanguage.writeString("ko"); // 로케일의 언어 코드를 쓴다.

     Device deviceCountry = action.findDeviceById(Action.Tts.COMMAND_COUNTRY);
     deviceCountry.writeString("KR"); // 로케일의 국가 코드를 쓴다.

     Device deviceVariant = action.findDeviceById(Action.Tts.COMMAND_VARIANT);
     deviceVariant.writeString("POSIX"); // 로케일의 이형(異形) 코드를 쓴다.

     Device devicePitch = action.findDeviceById(Action.Tts.COMMAND_PITCH);
     devicePitch.write(100); // 음 높이 값을 쓴다.

     Device deviceSpeechRate = action.findDeviceById(Action.Tts.COMMAND_SPEECH_RATE);
     deviceSpeechRate.write(100); // 음성 출력 속도 값을 쓴다.

     Device deviceText = action.findDeviceById(Action.Tts.COMMAND_TEXT);
     deviceText.writeString("안녕하세요"); // 음성 합성할 문자열을 쓴다.
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see Action.OnCompletedListener
	 * @see org.roboid.robot.Device Device
	 */
	public final class Tts
	{
		/**
		 * <p>Tts 액션의 모델 ID를 나타내는 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: "org.smartrobot.android.action.tts"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.tts";
		/**
		 * <p>엔진 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>엔진 커맨드 디바이스의 데이터는 TTS 엔진의 패키지 이름을 나타낸다.
		 * 설정하지 않으면 기본 TTS 엔진을 사용한다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40500000
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: String [ ]
		 *             <li>배열 크기: 1
		 *             <li>초기 값: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_ENGINE = 0x40500000;
		/**
		 * <p>언어 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>언어 커맨드 디바이스의 데이터는 TTS의 로케일에서 언어 코드를 나타낸다.
		 * 설정하지 않으면 기본 언어 코드를 사용한다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40500001
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: String [ ]
		 *             <li>배열 크기: 1
		 *             <li>초기 값: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_LANGUAGE = 0x40500001;
		/**
		 * <p>국가 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>국가 커맨드 디바이스의 데이터는 TTS의 로케일에서 국가 코드를 나타낸다.
		 * 설정하지 않으면 기본 국가 코드를 사용한다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40500002
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: String [ ]
		 *             <li>배열 크기: 1
		 *             <li>초기 값: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_COUNTRY = 0x40500002;
		/**
		 * <p>이형(異形) 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>이형(異形) 커맨드 디바이스의 데이터는 TTS의 로케일에서 이형(異形) 코드를 나타낸다.
		 * 설정하지 않으면 기본 이형(異形) 코드를 사용한다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * <ul>
		 *     <li>상수 값: 0x40500003
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: String [ ]
		 *             <li>배열 크기: 1
		 *             <li>초기 값: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_VARIANT = 0x40500003;
		/**
		 * <p>음 높이 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>음 높이 커맨드 디바이스의 데이터는 출력되는 음성의 음 높이를 나타낸다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * <ul>
		 *     <li>상수 값: 0x40500004
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: 0 ~ 300 [%]
		 *             <li>초기 값: 100
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_PITCH = 0x40500004;
		/**
		 * <p>속도 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>속도 커맨드 디바이스의 데이터는 음성의 출력 속도를 나타낸다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * <ul>
		 *     <li>상수 값: 0x40500005
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: 0 ~ 300 [%]
		 *             <li>초기 값: 100
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_SPEECH_RATE = 0x40500005;
		/**
		 * <p>문장 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>문장 커맨드 디바이스의 데이터는 음성 합성할 문자열을 나타낸다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40500006
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: String [ ]
		 *             <li>배열 크기: -1
		 *             <li>초기 값: ""
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_TEXT = 0x40500006;
		/**
		 * <p>지원하지 않는 언어임을 나타내는 오류 코드 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: -100
		 * </ul>
		 */
		public static final int ERROR_LANG_NOT_AVAILABLE = -100;
	}

	/**
	 * <p>로봇을 원하는 위치와 방향으로 이동시키는 액션의 모델 ID와 각 디바이스의 ID에 대한 상수 값을 정의한다.
	 * <p>Action.Navigation은 6개의 커맨드 디바이스로 구성되어 있으며, 각 디바이스의 데이터는 정수형 배열로 선언되어 있다.
	 * <p>Action.Navigation은 목표 위치와 방향에 도달하면 {@link Action.OnCompletedListener#onCompleted(Action) onCompleted(Action)} 메소드를 호출한다.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device devicePadSize = action.findDeviceById(Action.Navigation.COMMAND_PAD_SIZE);
     devicePadSize.write(0, 108); // 패드의 폭 값을 쓴다.
     devicePadSize.write(1, 76); // 패드의 높이 값을 쓴다.

     Device deviceInitialPosition = action.findDeviceById(Action.Navigation.COMMAND_INITIAL_POSITION);
     deviceInitialPosition.write(0, 10); // 초기 위치의 X 좌표 값을 쓴다.
     deviceInitialPosition.write(1, 20); // 초기 위치의 Y 좌표 값을 쓴다.

     Device deviceWaypoints = action.findDeviceById(Action.Navigation.COMMAND_WAYPOINTS);
     int[] waypoints = new int[] { 50, 50, 90, 50 }; // 초기 위치에서 (50, 50) 위치를 경유하여 (90, 50) 위치로 이동한다.
     deviceWaypoints.write(waypoints); // 중간 경로를 포함하여 로봇이 이동할 목표 위치 값을 쓴다.

     Device deviceFinalOrientation = action.findDeviceById(Action.Navigation.COMMAND_FINAL_ORIENTATION);
     deviceFinalOrientation.write(30); // 마지막 위치에서 로봇의 최종 방향 값을 쓴다.

     Device deviceMaxSpeed = action.findDeviceById(Action.Navigation.COMMAND_MAX_SPEED);
     deviceMaxSpeed.write(50); // 최대 이동 속도 값을 쓴다.

     Device deviceCurvature = action.findDeviceById(Action.Navigation.COMMAND_CURVATURE);
     deviceCurvature.write(50); // 이동 경로의 곡률 값을 쓴다.
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see Action.OnCompletedListener
	 * @see org.roboid.robot.Device Device
	 */
	public final class Navigation
	{
		/**
		 * <p>Navigation 액션의 모델 ID를 나타내는 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: "org.smartrobot.android.action.navigation"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.navigation";
		/**
		 * <p>패드 크기 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>패드 크기 커맨드 디바이스의 데이터는 내비게이션 패드의 크기를 나타낸다.
		 * 크기 2의 데이터 배열에서 첫 번째 값은 패드의 가로 크기, 두 번째 값은 패드의 세로 크기이다.
		 * 가로 크기와 세로 크기의 곱(면적)은 40000을 넘을 수 없다.
		 * 즉, 가로 크기가 1인 경우에는 세로 크기가 1 ~ 40000까지 가능하고, 가로 크기가 200인 경우에는 세로 크기가 1 ~ 200까지 가능하다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40600005
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 2
		 *             <li>값의 범위: 0 ~ 40000 (0: 유효하지 않은 값)
		 *             <li>초기 값: 0 (유효하지 않은 값)
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_PAD_SIZE = 0x40600005;
		/**
		 * <p>초기 위치 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>초기 위치 커맨드 디바이스의 데이터는 내비게이션 패드 위에서 로봇의 초기 위치를 나타낸다.
		 * 크기 2의 데이터 배열에서 첫 번째 값은 X축 좌표, 두 번째 값은 Y축 좌표 값이다.
		 * 설정하지 않으면 로봇의 현재 위치를 초기 위치로 사용한다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40600000
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 2
		 *             <li>값의 범위: 0 ~ 39999
		 *             <li>초기 값: 0
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_INITIAL_POSITION = 0x40600000;
		/**
		 * <p>경로점 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>경로점 커맨드 디바이스의 데이터는 로봇이 이동하는 중간 경로와 마지막 위치를 나타낸다.
		 * 데이터 배열의 크기는 2의 배수이어야 하며, 홀수 번째 값은 X축 좌표, 짝수 번째 값은 Y축 좌표 값이다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40600001
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: -1
		 *             <li>값의 범위: 0 ~ 39999
		 *             <li>초기 값: 0
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_WAYPOINTS = 0x40600001;
		/**
		 * <p>최종 방향 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>최종 방향 커맨드 디바이스의 데이터는 마지막 위치에서 로봇의 최종 방향을 나타낸다.
		 * 내비게이션 패드의 X축 방향이 0도이다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40600002
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: -179 ~ 180 [<sup>o</sup>]
		 *             <li>초기 값: 0
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_FINAL_ORIENTATION = 0x40600002;
		/**
		 * <p>최대 속도 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>최대 속도 커맨드 디바이스의 데이터는 로봇이 경로를 따라 이동하는 최대 속도를 나타낸다.
		 * 값이 클수록 이동 속도가 빨라진다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * <p>버전 1.3.0까지는 지원하지 않는다. 이후 버전에서 지원할 예정이다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40600003
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: 0 ~ 100 [%]
		 *             <li>초기 값: 50
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_MAX_SPEED = 0x40600003;
		/**
		 * <p>곡률 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>곡률 커맨드 디바이스의 데이터는 로봇이 이동하는 경로의 굽은 정도를 나타낸다.
		 * 값이 클수록 방향 전환을 급격하게 하고, 값이 작을수록 방향 전환을 부드럽게 한다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * <p>버전 1.3.0까지는 지원하지 않는다. 이후 버전에서 지원할 예정이다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40600004
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: 0 ~ 100 [%]
		 *             <li>초기 값: 50
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_CURVATURE = 0x40600004;
	}
	
	/**
	 * <p>내비게이션 패드 위에서 OID 코드 값을 위치 값으로 변환하는 액션의 모델 ID와 각 디바이스의 ID에 대한 상수 값을 정의한다.
	 * <p>Action.Localization은 1개의 센서 디바이스와 2개의 커맨드 디바이스로 구성되어 있으며, 각 디바이스의 데이터는 정수형 배열로 선언되어 있다.
	 * <p>Action.Localization은 {@link Action#activate() activate()} 메소드로 시작하여 {@link Action#deactivate() deactivate()} 메소드로 중지할 때까지 실행된다.
	 * </p>
	 * <pre class="prettyprint">
	 * void someMethod(Action action)
 {
     Device devicePadSize = action.findDeviceById(Action.Localization.COMMAND_PAD_SIZE); // 패드 크기 커맨드 디바이스를 얻는다.
     devicePadSize.write(0, 108); // 패드의 폭 값을 쓴다.
     devicePadSize.write(1, 76); // 패드의 높이 값을 쓴다.

     Device deviceOID = action.findDeviceById(Action.Localization.COMMAND_OID); // OID 커맨드 디바이스를 얻는다.
     deviceOID.write(12345); // OID 값을 쓴다.

     Device devicePosition = action.findDeviceById(Action.Localization.SENSOR_POSITION); // 위치 센서 디바이스를 얻는다.
     int positionX = devicePosition.read(0); // 위치 데이터의 X 좌표 값을 읽는다.
     int positionY = devicePosition.read(1); // 위치 데이터의 Y 좌표 값을 읽는다.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int x, y;
     switch(device.getId())
     {
     case Action.Localization.SENSOR_POSITION: // 위치 값을 얻는다.
         x = ((int[])values)[0];
         y = ((int[])values)[1];
         break;
     }
 }</pre>
	 * 
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 * @see org.roboid.robot.Device Device
	 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
	 */
	public final class Localization
	{
		/**
		 * <p>Localization 액션의 모델 ID를 나타내는 상수.
		 * </p>
		 * <ul>
		 *     <li>상수 값: "org.smartrobot.android.action.localization"
		 * </ul>
		 */
		public static final String ID = "org.smartrobot.android.action.localization";
		/**
		 * <p>패드 크기 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>패드 크기 커맨드 디바이스의 데이터는 내비게이션 패드의 크기를 나타낸다.
		 * 크기 2의 데이터 배열에서 첫 번째 값은 패드의 가로 크기, 두 번째 값은 패드의 세로 크기이다.
		 * 가로 크기와 세로 크기의 곱(면적)은 40000을 넘을 수 없다.
		 * 즉, 가로 크기가 1인 경우에는 세로 크기가 1 ~ 40000까지 가능하고, 가로 크기가 200인 경우에는 세로 크기가 1 ~ 200까지 가능하다.
		 * <p>{@link Action} 클래스의 {@link Action#activate() activate()} 메소드를 호출하기 전에 설정해야 한다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40700000
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 2
		 *             <li>값의 범위: 0 ~ 40000 (0: 유효하지 않은 값)
		 *             <li>초기 값: 0 (유효하지 않은 값)
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_PAD_SIZE = 0x40700000;
		/**
		 * <p>OID 커맨드 디바이스의 ID를 나타내는 상수.
		 * <p>OID 커맨드 디바이스의 데이터는 내비게이션 패드의 OID 코드 값을 나타낸다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40700001
		 *     <li>디바이스의 데이터 배열
		 *         <ul>
		 *             <li>데이터 형: int [ ]
		 *             <li>배열 크기: 1
		 *             <li>값의 범위: -1 ~ 65535 (-1: 유효하지 않은 값)
		 *             <li>초기 값: -1 (유효하지 않은 값)
		 *         </ul>
		 * </ul>
		 */
		public static final int COMMAND_OID = 0x40700001;
		/**
		 * <p>위치 센서 디바이스의 ID를 나타내는 상수.
		 * <p>위치 센서 디바이스의 데이터는 내비게이션 패드 위에서 OID 코드 값에 해당하는 위치를 나타낸다.
		 * 크기 2의 데이터 배열에서 첫 번째 값은 X축 좌표, 두 번째 값은 Y축 좌표 값이다.
		 * </p>
		 * <ul>
		 *     <li>상수 값: 0x40700002
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
		public static final int SENSOR_POSITION = 0x40700002;
	}
	
	/**
	 * <p>액션의 상태가 변경되었을 때 호출되는 메소드를 정의한다.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 */
	public interface OnStateChangedListener
	{
		/**
		 * <p>액션의 상태가 변경되었을 때 호출된다.
		 * <p>공통 상태 값은 {@link Action} 클래스를 참고하고, 액션에 따라 다른 상태 값은 각 액션의 클래스를 참고하기 바란다.
		 * </p>
		 * @param action 상태가 변경된 액션
		 * @param state 액션의 상태 값
		 */
		void onStateChanged(Action action, int state);
	}
	
	/**
	 * <p>액션의 실행이 완료되었을 때 호출되는 메소드를 정의한다.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 */
	public interface OnCompletedListener
	{
		/**
		 * <p>액션의 실행이 완료되었을 때 호출된다.
		 * <p>액션에 따라 {@link Action.Navigation}과 같이 주어진 명령을 완수하여 실행이 완료되는 액션도 있고,
		 * {@link Action.Microphone}과 같이 {@link Action#deactivate() deactivate()} 메소드를 호출할 때까지 계속 실행되는 액션도 있다.
		 * {@link Action#deactivate() deactivate()} 메소드를 호출할 때까지 계속 실행되는 액션의 경우에는 onCompleted(Action) 메소드가 호출되지 않고, 실행이 완료되는 액션의 경우에만 주어진 명령을 완수하였을 때 onCompleted(Action) 메소드가 호출된다.
		 * onCompleted(Action) 메소드의 호출 여부는 각 액션의 클래스를 참고하기 바란다.
		 * </p>
		 * @param action 실행이 완료된 액션
		 */
		void onCompleted(Action action);
	}
	
	/**
	 * <p>액션 인스턴스를 얻거나 시작, 중지, 폐기하는 과정에서 오류가 발생했을 때 호출되는 메소드를 정의한다.
	 * </p>
	 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
	 * <p>
	 * @see Action
	 */
	public interface OnErrorListener
	{
		/**
		 * <p>액션 인스턴스를 얻거나 시작, 중지, 폐기하는 과정에서 오류가 발생했을 때 호출된다.
		 * <p>공통 오류 코드 값은 {@link Action} 클래스를 참고하고, 액션에 따라 다른 오류 코드 값은 각 액션의 클래스를 참고하기 바란다.
		 * </p>
		 * @param action 오류가 발생한 액션
		 * @param errorCode 오류 코드 값
		 */
		void onError(Action action, int errorCode);
	}

	private static final int MSG_ACK = 1;
	private static final int MSG_STATE = 2;
	private static final int MSG_COMPLETION = 3;
	private static final int MSG_ERROR = 4;
	
	private static final HashMap<String, Action> mActions = new HashMap<String, Action>();
	private WeakReference<Context> mContext;
	OnStateChangedListener mOnStateChangedListener;
	OnCompletedListener mOnCompletedListener;
	OnErrorListener mOnErrorListener;
	private boolean mPrepared;
	private boolean mDisposed;
	boolean mActive;
	private final ArrayList<Intent> mIntents = new ArrayList<Intent>();
	private BroadcastReceiver mBR;
	private final EventHandler mEventHandler;
	
	private ac mActionBinder;
	private final ServiceConnection mActionConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			mActionBinder = ac.Stub.asInterface(binder);
			try
			{
				mActionBinder.a(mDataChangedCallback);
				mActionBinder.c(mDataRequestCallback);
			} catch (RemoteException e)
			{
			}
			
			mPrepared = true;
			Context context = getContext();
			if(context == null)
			{
				if(mOnErrorListener != null)
					mOnErrorListener.onError(Action.this, Action.ERROR_INVALID_CONTEXT);
			}
			else
			{
				for(Intent i : mIntents)
					context.sendBroadcast(i);
			}
			mIntents.clear();
			if(mOnStateChangedListener != null)
				mOnStateChangedListener.onStateChanged(Action.this, Action.STATE_PREPARED);
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mActionBinder = null;
		}
	};
	private final dc.Stub mDataChangedCallback = new dc.Stub()
	{
		@Override
		public void a(byte[] a1, long a2) throws RemoteException
		{
			if(a1 != null)
				handleSimulacrum(a1, a2);
			updateDeviceState();
		}

		@Override
		public void b(int b1, int b2, byte[] b3, long b4) throws RemoteException
		{
		}
	};
	private final dr.Stub mDataRequestCallback = new dr.Stub()
	{
		@Override
		public byte[] a() throws RemoteException
		{
			if(mActionBinder == null) return null;
			return encodeSimulacrum();
		}

		@Override
		public byte[] b(int b1, int b2) throws RemoteException
		{
			return null;
		}
	};
	
	Action(int size, int tag)
	{
		super(size, tag);
		
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
	 * <p>액션 ID에 대한 액션 인스턴스를 얻는다.
	 * <p>context 또는 actionId가 null이면 null을 반환한다.
	 * actionId는 각 액션에 정의된 ID 값이다.
	 * actionId에 대한 액션 인스턴스가 이미 존재하는 경우에는 기존의 액션 인스턴스를 반환하고, 존재하지 않으면 새로 생성하여 반환한다.
	 * 액션 생성을 실패하면 null을 반환한다.
	 * </p>
	 * @param context 컨텍스트
	 * @param actionId 액션의 ID
	 * <p>
	 * @return 액션 인스턴스 또는 null
	 */
	public static Action obtain(Context context, String actionId)
	{
		if(context == null || actionId == null) return null;
		Action action = null;
		synchronized(mActions)
		{
			action = mActions.get(actionId);
			if(action == null)
			{
				action = ActionFactory.create(actionId);
				if(action != null)
				{
					mActions.put(actionId, action);
					action.setContext(context);
					action.registerBroadcast();
					Intent intent = new Intent("roboid.intent.action.ACTION_REQ");
					intent.putExtra("roboid.intent.extra.PACKAGE_NAME", context.getPackageName());
					intent.putExtra("roboid.intent.extra.ACTION_ID", actionId);
					context.sendBroadcast(intent);
				}
			}
			else
				action.setContext(context);
		}
		return action;
	}
	
	/**
	 * <p>생성된 모든 액션을 폐기한다.
	 * <p>액션을 폐기한 이후에는 {@link #activate()} 혹은 {@link #deactivate()} 메소드를 호출하여 액션을 시작하거나 중지할 수 없다.
	 * 애플리케이션이 종료되기 전에 반드시 생성된 모든 액션에 대해 {@link #dispose()} 혹은 Action.disposeAll() 메소드를 호출하여 생성된 액션을 폐기하여야 한다.
	 */
	public static void disposeAll()
	{
		synchronized(mActions)
		{
			for(Action action : mActions.values())
				action.release();
			mActions.clear();
		}
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
	
	/**
	 * <p>액션의 상태가 변경되었을 때 호출되도록 listener를 설정한다.
	 * </p>
	 * @param listener 설정할 리스너
	 */
	public void setOnStateChangedListener(OnStateChangedListener listener)
	{
		mOnStateChangedListener = listener;
	}
	
	/**
	 * <p>액션의 실행이 완료되었을 때 호출되도록 listener를 설정한다.
	 * </p>
	 * @param listener 설정할 리스너
	 */
	public void setOnCompletedListener(OnCompletedListener listener)
	{
		mOnCompletedListener = listener;
	}
	
	/**
	 * <p>액션 인스턴스를 얻거나 시작, 중지, 폐기하는 과정에서 오류가 발생했을 때 호출되도록 listener를 설정한다.
	 * </p>
	 * @param listener 설정할 리스너
	 */
	public void setOnErrorListener(OnErrorListener listener)
	{
		mOnErrorListener = listener;
	}
	
	private void registerBroadcast()
	{
		if(mBR != null) return;
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_INVALID_CONTEXT);
			return;
		}

		mBR = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				String action = intent.getAction();
				if("roboid.intent.action.ACTION_ACK".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_ACK);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ACTION_STATE".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_STATE);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ACTION_COMPLETION".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_COMPLETION);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ACTION_ERROR".equals(action))
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
		intentFilter.addAction("roboid.intent.action.ACTION_ACK");
		intentFilter.addAction("roboid.intent.action.ACTION_STATE");
		intentFilter.addAction("roboid.intent.action.ACTION_COMPLETION");
		intentFilter.addAction("roboid.intent.action.ACTION_ERROR");
		context.registerReceiver(mBR, intentFilter);
	}
	
	boolean connect(Intent intent)
	{
		if(intent == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_NOT_SUPPORTED);
			return false;
		}
		
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_INVALID_CONTEXT);
			return false;
		}
		
		try
		{
			context.bindService(intent, mActionConnection, Context.BIND_AUTO_CREATE);
		} catch (SecurityException e)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_SECURITY);
		}
		return true;
	}

	void disconnect()
	{
		if(mActionBinder == null) return;
		
		try
		{
			mActionBinder.b(mDataChangedCallback);
			mActionBinder.d(mDataRequestCallback);
			mActionBinder = null;
			Context context = getContext();
			if(context == null)
			{
				if(mOnErrorListener != null)
					mOnErrorListener.onError(this, Action.ERROR_INVALID_CONTEXT);
			}
			else
				context.unbindService(mActionConnection);
		} catch (RemoteException e)
		{
		}
	}
	
	/**
	 * <p>액션을 폐기한다.
	 * <p>액션을 폐기한 이후에는 {@link #activate()} 혹은 {@link #deactivate()} 메소드를 호출하여 액션을 시작하거나 중지할 수 없다.
	 * 애플리케이션이 종료되기 전에 반드시 생성된 모든 액션에 대해 dispose() 혹은 {@link #Action.disposeAll() Action.disposeAll()} 메소드를 호출하여 생성된 액션을 폐기하여야 한다.
	 */
	public void dispose()
	{
		release();
		synchronized(mActions)
		{
			mActions.remove(getId());
		}
	}
	
	private void release()
	{
		if(mActive)
			deactivate();
		
		if(mEventHandler != null)
		{
			mEventHandler.removeMessages(MSG_ACK);
			mEventHandler.removeMessages(MSG_STATE);
			mEventHandler.removeMessages(MSG_COMPLETION);
			mEventHandler.removeMessages(MSG_ERROR);
		}
		disconnect();
		
		if(mBR != null)
		{
			Context context = getContext();
			if(context == null)
			{
				if(mOnErrorListener != null)
					mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			}
			else
				context.unregisterReceiver(mBR);
			mBR = null;
		}
		mPrepared = false;
		mDisposed = true;
		mIntents.clear();
		
		if(mOnStateChangedListener != null)
			mOnStateChangedListener.onStateChanged(this, STATE_DISPOSED);
		
		mOnStateChangedListener = null;
		mOnCompletedListener = null;
		mOnErrorListener = null;
	}
	
	/**
	 * <p>액션의 실행을 시작한다.
	 * <p>{@link #dispose()} 혹은 {@link #disposeAll() Action.disposeAll()} 메소드를 호출하여 액션이 폐기된 후에는 액션이 실행되지 않고 false를 반환한다.
	 * </p>
	 * @return 성공하면 true, 아니면 false
	 */
	public boolean activate()
	{
		if(mDisposed)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mActive)
			deactivate();
		mActive = true;
		
		Intent intent = new Intent("roboid.intent.action.ACTION_ACTIVATE");
		intent.putExtra("roboid.intent.extra.ACTION_ID", getId());
		if(mPrepared)
			context.sendBroadcast(intent);
		else
			mIntents.add(intent);
		return true;
	}
	
	/**
	 * <p>액션의 실행을 중지한다.
	 * <p>{@link #dispose()} 혹은 {@link #disposeAll() Action.disposeAll()} 메소드를 호출하여 액션이 폐기된 후에는 false를 반환한다.
	 * </p>
	 * @return 성공하면 true, 아니면 false
	 */
	public boolean deactivate()
	{
		if(mDisposed)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		mActive = false;
		
		Intent intent = new Intent("roboid.intent.action.ACTION_DEACTIVATE");
		intent.putExtra("roboid.intent.extra.ACTION_ID", getId());
		if(mPrepared)
			context.sendBroadcast(intent);
		else
			mIntents.add(intent);
		return true;
	}
	
	void handleSimulacrum(byte[] simulacrum, long timestamp)
	{
		if(decodeSimulacrum(simulacrum))
		{
			synchronized(mListeners)
			{
				for(DeviceDataChangedListener listener : mListeners)
					notifyDataChanged(listener, timestamp);
			}
		}
	}
	
	abstract boolean decodeSimulacrum(byte[] simulacrum);
	abstract void notifyDataChanged(DeviceDataChangedListener listener, long timestamp);
	
	byte[] encodeSimulacrum()
	{
		return null;
	}
	
	private static class EventHandler extends Handler
	{
		private final Action mAction;
		
		EventHandler(Action action)
		{
			super();
			mAction = action;
		}
		
		EventHandler(Action action, Looper looper)
		{
			super(looper);
			mAction = action;
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case MSG_ACK:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					Context context = mAction.getContext();
					if(context == null)
					{
						Action.OnErrorListener listener = mAction.mOnErrorListener;
						if(listener != null)
							listener.onError(mAction, Action.ERROR_INVALID_CONTEXT);
						return;
					}
					
					String packageName = intent.getStringExtra("roboid.intent.extra.PACKAGE_NAME");
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(packageName == null || !packageName.equals(context.getPackageName())) return;
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					Intent i = intent.getParcelableExtra("roboid.intent.extra.ACTION");
					mAction.connect(i);
				}
				break;
			case MSG_STATE:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					int state = intent.getIntExtra("roboid.intent.extra.ACTION_STATE", STATE_NONE);
					if(state != STATE_NONE)
					{
						Action.OnStateChangedListener listener = mAction.mOnStateChangedListener;
						if(listener != null)
							listener.onStateChanged(mAction, state);
					}
				}
				break;
			case MSG_COMPLETION:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					mAction.mActive = false;
					Action.OnCompletedListener listener = mAction.mOnCompletedListener;
					if(listener != null)
						listener.onCompleted(mAction);
				}
				break;
			case MSG_ERROR:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					int errorCode = intent.getIntExtra("roboid.intent.extra.ACTION_ERROR", ERROR_NONE);
					if(errorCode != ERROR_NONE)
					{
						Action.OnErrorListener listener = mAction.mOnErrorListener;
						if(listener != null)
							listener.onError(mAction, errorCode);
					}
				}
				break;
			}
		}
	}
}