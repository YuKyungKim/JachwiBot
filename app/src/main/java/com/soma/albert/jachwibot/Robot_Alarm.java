package com.soma.albert.jachwibot;

import org.roboid.robot.Device;
import org.roboid.robot.Robot;

import kr.robomation.physical.Albert;

/**
 * Created by wlsdn on 2015-08-10.
 */
public class Robot_Alarm implements Runnable
{
    private Thread main_thread;
    private CallBackEvent callBackEvent;

    private Device RightProximity;
    private Device LeftProximity;
    private Device Acceleration;
    private Device RightWheel;
    private Device LeftWheel;

    private boolean Alram_Start_Flag = false;

    public interface CallBackEvent
    {
        void Robot_Alram_Stop();
    }
    public Robot_Alarm(Robot robot)
    {
        RightProximity = robot.findDeviceById(Albert.SENSOR_RIGHT_PROXIMITY);
        LeftProximity = robot.findDeviceById(Albert.SENSOR_LEFT_PROXIMITY);
        Acceleration = robot.findDeviceById(Albert.SENSOR_ACCELERATION);
        RightWheel = robot.findDeviceById(Albert.EFFECTOR_RIGHT_WHEEL);
        LeftWheel = robot.findDeviceById(Albert.EFFECTOR_LEFT_WHEEL);
    }
    public void setCallBackEvent(CallBackEvent callback)
    {
        callBackEvent = callback;
    }

    public void set_Alram_status(boolean status)
    {
        if(main_thread == null || main_thread.getState() == Thread.State.TERMINATED)
        {
            main_thread = new Thread(this);
            main_thread.start();
        }
        Alram_Start_Flag = status;
    }
    public boolean get_Alram_status()
    {
        return Alram_Start_Flag;
    }


    @Override
    public void run()
    {
        boolean obstacle_flag = false;
        while (Alram_Start_Flag)
        {
            try
            {
                Thread.sleep(200); //200ms delay

                int accelerationZ = Acceleration.read(2); // Z축 가속도 값을 읽는다.
                int leftProximity = LeftProximity.read();
                int rightProximity = RightProximity.read();
                if((leftProximity > 50) || ( rightProximity > 50) || obstacle_flag)
                {
                    if((leftProximity > 35) && (rightProximity > 35))
                    {
                        obstacle_flag = true;
                        if(leftProximity > rightProximity)
                        {
                            LeftWheel.write(100);
                            RightWheel.write(-100);
                        }
                        else
                        {
                            LeftWheel.write(-100);
                            RightWheel.write(100);
                        }
                    }
                    else
                    {
                        obstacle_flag = false;
                    }
                }
                else
                {
                    LeftWheel.write(100);
                    RightWheel.write(100);
                }

                //넘어짐 감지
                if(accelerationZ > 0)
                {
                    Alram_Start_Flag = false;
                    LeftWheel.write(0);
                    RightWheel.write(0);
                    if(callBackEvent != null)
                    {
                        callBackEvent.Robot_Alram_Stop();
                    }
                }

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}
