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
 * <p>디바이스의 종류 중에서 이펙터 디바이스를 나타낸다.
 * <p>모터, 스피커, LED 등과 같이 수치 데이터를 속도, 소리, 빛 등의 물리량으로 변환하는 출력 장치를 나타낸다.
 * 연산이 가능한 수치를 다루는 뉴메리컬 디바이스(Numerical Device)이며, 데이터 전송 시 데이터 손실이 일어날 경우에도 심각한 문제를 야기하지 않는 디바이스를 대상으로 한다.
 * 이때 손실된 데이터는 무시되거나, 보간법으로 보충되거나, 기타 방법으로 하드웨어 장치 쪽에서 만들어질 수 있다.
 * 애플리케이션에서 쓴 데이터가 일정한 주기로 계속 전송된다.
 * </p>
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see Device
 * @see MotoringDevice
 */
public interface Effector extends MotoringDevice
{
}
