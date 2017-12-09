package com.huaweichallenge.app.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.huaweichallenge.app.Constants;

import java.util.ArrayList;
import java.util.HashMap;

import uk.me.berndporr.iirj.Butterworth;

public class SensorService extends IntentService implements SensorEventListener {

    public static final String SEND_DATA="SEND_DATA";

    private SensorManager mSensorManager;
    private Sensor mLinearAccelerationSensor;
    private Sensor mGyroscopeSensor;
    private Butterworth butterworth = new Butterworth();

    private ArrayList<float[]> accelerationValues = new ArrayList<>();
    private ArrayList<float[]> gyroscopicValues = new ArrayList<>();
    private ArrayList<Float> accelerationMagnitudes = new ArrayList<>();
    private ArrayList<Float> gyroscopicMagnitudes = new ArrayList<>();

    private HashMap<String, Float> sentData = new HashMap<>();

    public SensorService() {
        super("SensorService");
    }

    public static void startActionGetSensorValues(Context context) {
        Intent intent = new Intent(context, SensorService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            handleListenSensorValues();
        }
    }

    public void handleListenSensorValues() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLinearAccelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mLinearAccelerationSensor, 100000);
        mSensorManager.registerListener(this, mGyroscopeSensor, 100000);

        butterworth.lowPass(2, 100, 15);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;

        if( accelerationValues.size() == Constants.WINDOW_SIZE && gyroscopicValues.size() == Constants.WINDOW_SIZE) {

            setDataHashMapInBundle();

            accelerationValues = new ArrayList<>();
            gyroscopicValues = new ArrayList<>();
            accelerationMagnitudes = new ArrayList<>();
            gyroscopicMagnitudes = new ArrayList<>();
        }

        else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION && accelerationValues.size() < Constants.WINDOW_SIZE) {
            float accelerationAxisX = event.values[0];
            float accelerationAxisY = event.values[1];
            float accelerationAxisZ = event.values[2];

            float accelerationMagnitude = (float) Math.sqrt(
                    accelerationAxisX * accelerationAxisX +
                            accelerationAxisY * accelerationAxisY +
                            accelerationAxisZ * accelerationAxisZ
            );

            final float[] acceleration = {accelerationAxisX, accelerationAxisY, accelerationAxisZ};

            accelerationValues.add(acceleration);
            accelerationMagnitudes.add(accelerationMagnitude);

            if (accelerationValues.size() == Constants.WINDOW_SIZE) {
                new Thread() {
                    public void run() {
                        ArrayList<Float> filteredAccelerationValues = getFilteredValues(accelerationValues);

                        float accelerationMean = 0f;
                        for(float value : filteredAccelerationValues) {
                            accelerationMean += value;
                        }

                        float accelerationMagnitudeMean = 0f;
                        for (float magnitudeValue : accelerationMagnitudes) {
                            accelerationMagnitudeMean += magnitudeValue;
                        }

                        accelerationMean = accelerationMean/Constants.WINDOW_SIZE;
                        accelerationMagnitudeMean = accelerationMagnitudeMean/Constants.WINDOW_SIZE;

                        float accelerationStd = 0f;
                        for(float value : filteredAccelerationValues) {
                            accelerationStd += (value-accelerationMean)*(value-accelerationMean);
                        }
                        accelerationStd = (float)Math.sqrt(accelerationStd/Constants.WINDOW_SIZE);

                        sentData.put("accelerationMean", accelerationMean);
                        sentData.put("accelerationMagnitudeMean", accelerationMagnitudeMean);
                        sentData.put("accelerationStd", accelerationStd);
                    }
                }.start();
            }

            /*System.out.println("Acceleration X : " + accelerationAxisX);
            System.out.println("Acceleration Y : " + accelerationAxisY);
            System.out.println("Acceleration Z : " + accelerationAxisZ);
            System.out.println("Magnitude acceleration : " + accelerationMagnitude);*/

        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE && gyroscopicValues.size() < Constants.WINDOW_SIZE) {

            float gyroscopicAxisX = event.values[0];
            float gyroscopicAxisY = event.values[1];
            float gyroscopicAxisZ = event.values[2];

            final float gyroscopicMagnitude = (float) Math.sqrt(
                    gyroscopicAxisX * gyroscopicAxisX +
                            gyroscopicAxisY * gyroscopicAxisY +
                            gyroscopicAxisZ * gyroscopicAxisZ
            );

            final float[] gyroscopic = {gyroscopicAxisX, gyroscopicAxisY, gyroscopicAxisZ};

            gyroscopicValues.add(gyroscopic);
            gyroscopicMagnitudes.add(gyroscopicMagnitude);

            if (gyroscopicValues.size() == Constants.WINDOW_SIZE) {
                new Thread() {
                    public void run() {
                        ArrayList<Float> filteredGyroscopicValues = getFilteredValues(gyroscopicValues);

                        float gyroscopicMean = 0f;
                        for(float value : filteredGyroscopicValues) {
                            gyroscopicMean += value;
                        }

                        float gyroscopicMagnitudeMean = 0f;
                        for (float magnitudeValue : gyroscopicMagnitudes) {
                            gyroscopicMagnitudeMean += magnitudeValue;
                        }

                        gyroscopicMean = gyroscopicMean/Constants.WINDOW_SIZE;
                        gyroscopicMagnitudeMean = gyroscopicMagnitudeMean/Constants.WINDOW_SIZE;

                        float gyroscopicStd = 0f;
                        for(float value : filteredGyroscopicValues) {
                            gyroscopicStd += (value-gyroscopicMean)*(value-gyroscopicMean);
                        }
                        gyroscopicStd = (float)Math.sqrt(gyroscopicStd/Constants.WINDOW_SIZE);

                        sentData.put("gyroscopicMean", gyroscopicMean);
                        sentData.put("gyroscopicMagnitudeMean", gyroscopicMagnitudeMean);
                        sentData.put("gyroscopicStd", gyroscopicStd);
                    }
                }.start();
            }

            /*System.out.println("Gyro X : " + gyroscopicAxisX);
            System.out.println("Gyro Y : " + gyroscopicAxisY);
            System.out.println("Gyro Z : " + gyroscopicAxisZ);
            System.out.println("Gyro magnitude : " + gyroscopicMagnitude);*/
        }

    }

    private void setDataHashMapInBundle() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        Log.i("SENDING ", "DATA DFHGSDHSDFGSDGS");
        HashMap<String, Float> toto = new HashMap<>();
        bundle.putSerializable("sensorDataMap", sentData);
        intent.setAction(SEND_DATA);
        intent.putExtras(bundle);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(intent);

        sentData = new HashMap<>();
    }

    @NonNull
    private ArrayList<Float> getFilteredValues(ArrayList<float[]> valuesToFilter) {
        ArrayList<Float> filteredGyroscopicValues = new ArrayList<>();
        for (int i = 0; i < valuesToFilter.size(); i++) {
            filteredGyroscopicValues.add(
                    (float)butterworth.filter(
                            valuesToFilter.get(i)[0] +
                                    valuesToFilter.get(i)[1] +
                                    valuesToFilter.get(i)[2])
            );
        }
        return filteredGyroscopicValues;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
