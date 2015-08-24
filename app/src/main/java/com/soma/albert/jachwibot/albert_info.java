package com.soma.albert.jachwibot;

import org.roboid.robot.Device;
import org.roboid.robot.Robot;
import org.smartrobot.android.RobotActivity;

import kr.robomation.physical.Albert;

/**
 * Created by josh on 15. 8. 23..
 */
public class Albert_info extends RobotActivity {

    private Device mBatteryDevice;
    private Device mTemperatureDevice;
    public int battery_info(){
        return mBatteryDevice.read();
    }
    public int temperature_info(){
        return mTemperatureDevice.read();
    }
    @Override
    public void onInitialized(Robot robot)
    {
        mTemperatureDevice = robot.findDeviceById(Albert.SENSOR_TEMPERATURE);
        mBatteryDevice = robot.findDeviceById(Albert.SENSOR_BATTERY);
        mTemperatureDevice = robot.findDeviceById(Albert.SENSOR_TEMPERATURE);
    }

}
