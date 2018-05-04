package org.agh.ontology;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.agh.ontology.reason.AmbientLight;
import org.agh.ontology.reason.ReasonableService;
import org.agh.ontology.reason.impl.OntologyReasonableService;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mProximity;
    private Sensor gyroscopeSensor;
    private Sensor accelerationSensor;
    private Sensor lightSensor;

    private int brightness = 0;

    private ReasonableService reasonableService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.reasonableService = new OntologyReasonableService(
                    getAssets().open("domain.owl"),
                    getAssets().open("default-logic.owl"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        final TextView t = findViewById(R.id.dateTime);
        final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        t.setText(dateFormat.format(date));

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                Date date = new Date();
                                t.setText(dateFormat.format(date));

                                //todo: remove this line
                                t.setText(reasonableService.getScreenBrightnessFor(AmbientLight.values()[new Random().nextInt(5)]).toString());
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        final TextView b = findViewById(R.id.brightness);
        try {
            brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            float perc = (brightness / (float) 255) * 100;
            b.setText("Brightness: " + perc);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        Thread thread2 = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final TextView b = findViewById(R.id.brightness);
                                try {
                                    int newBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);

                                    if (newBrightness != brightness) {
                                        brightness = newBrightness;
                                        float perc = (brightness / (float) 255) * 100;
                                        b.setText("Brightness: " + perc);
                                    }


                                } catch (Settings.SettingNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread2.start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            final TextView t = findViewById(R.id.proximity);
            t.setText("Proximity Sensor: " + event.values[0]);
            if (event.values[0] < mProximity.getMaximumRange()) {
                // Detected something nearby
                getWindow().getDecorView().setBackgroundColor(Color.RED);
            } else {
                // Nothing is nearby
                getWindow().getDecorView().setBackgroundColor(Color.GREEN);
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            final TextView t = findViewById(R.id.gyroscope);
            t.setText("Gyroscope Sensor: " + event.values[2]);
            if (event.values[2] > 0.5f) { // anticlockwise
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            } else if (event.values[2] < -0.5f) { // clockwise
                getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final TextView t = findViewById(R.id.accelerometr);
            t.setText("Accelerometr Sensor: " + event.values[2]);
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            final TextView t = findViewById(R.id.light);
            t.setText("Light Sensor: " + event.values[0]);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
