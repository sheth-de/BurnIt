package com.example.projectteam23mobiledev;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RunActivity extends AppCompatActivity implements SensorEventListener {

    TextView steps;
    TextView distance;
    SensorManager sensorManager;
    boolean isRunning = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        steps = (TextView) findViewById(R.id.steps);
        distance = (TextView) findViewById(R.id.miles);
        sensorManager =  (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(this, "Sensor does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (isRunning) {
            steps.setText(String.valueOf(sensorEvent.values[0]));
            float distanceTravelled = (float)(sensorEvent.values[0] * 78) / (float)100000;
            distance.setText(String.valueOf(distanceTravelled));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
