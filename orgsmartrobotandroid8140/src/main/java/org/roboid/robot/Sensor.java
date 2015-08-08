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
 * <p>디바이스의 종류 중에서 센서 디바이스를 나타낸다.
 * <p>거리 센서, 조도 센서, 온도 센서 등과 같이 거리, 빛, 온도 등의 물리량을 수치 데이터로 변환하는 입력 장치를 나타낸다.
 * 연산이 가능한 수치를 다루는 뉴메리컬 디바이스(Numerical Device)이며, 데이터 전송 시 데이터 손실이 일어날 경우에도 심각한 문제를 야기하지 않는 디바이스를 대상으로 한다.
 * 이때 손실된 데이터는 무시되거나, 보간법으로 보충되거나, 기타 방법으로 애플리케이션 쪽에서 만들어질 수 있다.
 * 하드웨어 장치에서 감지된 데이터가 일정한 주기로 계속 전송된다.
 * </p>
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see Device
 * @see SensoryDevice
 */
public interface Sensor extends SensoryDevice
{
}
