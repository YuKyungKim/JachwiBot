package com.soma.albert.jachwibot;

import org.roboid.robot.Device;
import org.roboid.robot.Robot;
import java.util.Random;

import kr.robomation.physical.Albert;

/**
 * Created by wlsdn on 2015-08-10.
 */
public class Robot_Alarm implements Runnable {
    private Thread main_thread;
    private CallBackEvent callBackEvent;

    private Device RightProximity, LeftProximity;
    private Device RightWheel, LeftWheel;
    private Device RightEye, LeftEye;
    private Device Acceleration;

    private boolean Alram_Start_Flag = false;

    public interface CallBackEvent {
        void Robot_Alram_Stop();
    }

    public Robot_Alarm(Robot robot) {
        RightProximity = robot.findDeviceById(Albert.SENSOR_RIGHT_PROXIMITY);
        LeftProximity = robot.findDeviceById(Albert.SENSOR_LEFT_PROXIMITY);
        Acceleration = robot.findDeviceById(Albert.SENSOR_ACCELERATION);
        RightWheel = robot.findDeviceById(Albert.EFFECTOR_RIGHT_WHEEL);
        LeftWheel = robot.findDeviceById(Albert.EFFECTOR_LEFT_WHEEL);
        RightEye = robot.findDeviceById(Albert.EFFECTOR_RIGHT_EYE);
        LeftEye = robot.findDeviceById(Albert.EFFECTOR_LEFT_EYE);
    }

    public void setCallBackEvent(CallBackEvent callback) {
        callBackEvent = callback;
    }

    public void set_Alram_status(boolean status) {
        if (main_thread == null || main_thread.getState() == Thread.State.TERMINATED) {
            main_thread = new Thread(this);
            main_thread.start();
        }
        Alram_Start_Flag = status;
    }

    public boolean get_Alram_status() {
        return Alram_Start_Flag;
    }


    @Override
    public void run() {
        Random random = new Random();
        boolean obstacle_flag = false; //장애물을 피하는 중인지 여부

        boolean increment_flag = true;
        int leftEye_value[] = {LeftEye.read(0),  LeftEye.read(1), LeftEye.read(2)}; //왼쪽눈의 RGB값
        int rightEye_value[] = {RightEye.read(0),  RightEye.read(1), RightEye.read(2)}; //오른쪽눈의 RGB값

        while (Alram_Start_Flag) try {
            Thread.sleep(200); //200ms delay

            int accelerationZ = Acceleration.read(2); // Z축 가속도 값을 읽는다.
            int leftProximity = LeftProximity.read(); //왼쪽 거리센서
            int rightProximity = RightProximity.read(); //오른쪽 거리센서
            int random1 = random.nextInt(3);
            int random2 = random.nextInt(3);

            //전진 및 장애물 회피
            if ((leftProximity > 50) || (rightProximity > 50) || obstacle_flag) {
                if ((leftProximity > 35) && (rightProximity > 35)) {
                    obstacle_flag = true;
                    if (leftProximity > rightProximity) {
                        LeftWheel.write(100);
                        RightWheel.write(-100);
                    } else {
                        LeftWheel.write(-100);
                        RightWheel.write(100);
                    }
                } else {
                    obstacle_flag = false;
                }
            } else {
                LeftWheel.write(100);
                RightWheel.write(100);
            }

            //LED색 변경
            if (increment_flag) {
                leftEye_value[random1] += 50;
                rightEye_value[random2] += 50;
            } else {
                leftEye_value[random1] -= 50;
                rightEye_value[random2] -= 50;
            }
            if ((leftEye_value[random1] > 250) || (rightEye_value[random2] > 250) ||
                    (leftEye_value[random1] == 0) || (rightEye_value[random2] == 0)) {
                increment_flag ^= true;
            }
            LeftEye.write(leftEye_value);
            RightEye.write(rightEye_value);

            //넘어짐 감지
            if (accelerationZ > 0) {
                LeftWheel.write(0);
                RightWheel.write(0);
                LeftEye.write(new int[]{0, 0, 0});
                RightEye.write(new int[]{0, 0, 0});
                set_Alram_status(false);
                if (callBackEvent != null) {
                    callBackEvent.Robot_Alram_Stop();
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
