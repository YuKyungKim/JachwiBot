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
 * <p>디바이스의 종류 중에서 이벤트 디바이스를 나타낸다.
 * <p>하드웨어 장치에서 사건이 발생하였을 때 보내어지는 데이터를 다루며, 애플리케이션으로 반드시 전달되어야 하는 데이터를 표현할 때 사용한다.
 * 기호를 다루는 심볼릭 디바이스(Symbolic Device)이며, 데이터가 숫자로 표현된다고 하더라도 기호로서의 숫자를 의미한다.
 * 하드웨어 장치에서 사건이 발생했을 때에만 데이터가 전송된다.
 * </p>
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see Device
 * @see SensoryDevice
 */
public interface Event extends SensoryDevice
{
}
