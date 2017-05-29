package hakito.carclient;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import hakito.carclient.views.BigSeekBar;

public class AccelerometerFragment extends BaseSteeringFragment implements SensorEventListener {
    private static final double MAX_SEEK_BAR_VALUE = 100;
    private static final double G = 9.81;

    BigSeekBar throttleSeekBar;
    double steer;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accelerometer, container, false);
        throttleSeekBar = (BigSeekBar) view.findViewById(R.id.seekThrottle);
        throttleSeekBar.setOnTouchListener(new SeekResetter(50));
        throttleSeekBar.setHorizontal(false);
        return view;
    }

    @Override
    double getSteer() {
        return steer;
    }

    @Override
    double getThrottle() {
        return normalize(throttleSeekBar.getProgress());
    }

    private double normalize(int value) {
        return value / MAX_SEEK_BAR_VALUE * 2 - 1;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] vals = sensorEvent.values;
        steer = vals[1]/(G*0.7);
        Log.d("qaz", Arrays.toString(vals));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //none
    }
}
