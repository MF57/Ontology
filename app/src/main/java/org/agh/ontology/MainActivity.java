package org.agh.ontology;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import org.agh.ontology.reason.AmbientLight;
import org.agh.ontology.reason.Motion;
import org.agh.ontology.reason.ReasonableService;
import org.agh.ontology.reason.RingtoneVolume;
import org.agh.ontology.reason.ScreenBrightness;
import org.agh.ontology.reason.TimeOfDay;
import org.agh.ontology.reason.VibrationLevel;
import org.agh.ontology.reason.impl.OntologyReasonableService;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mProximity;
    private Sensor gyroscopeSensor;
    private Sensor accelerationSensor;
    private Sensor lightSensor;

    private int brightness = 0;

    private ContentResolver cResolver;


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
                                Calendar rightNow = Calendar.getInstance();
                                int hour = rightNow.get(Calendar.HOUR_OF_DAY);
                                RingtoneVolume volume;
                                if (hour > 0 && hour < 5 || hour >= 22) {
                                    volume = reasonableService.getRingtoneVolumeFor(TimeOfDay.Night);
                                } else if (hour >=6 && hour < 12) {
                                    volume = reasonableService.getRingtoneVolumeFor(TimeOfDay.Morning);
                                } else if (hour >= 12 && hour < 18) {
                                    volume = reasonableService.getRingtoneVolumeFor(TimeOfDay.Noon);
                                } else {
                                    volume = reasonableService.getRingtoneVolumeFor(TimeOfDay.Evening);
                                }


                                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                assert audio != null;

                                switch (volume) {
                                    case OffRingtone:
                                        audio.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI);
                                        break;
                                    case QuietRingtone:
                                        audio.setStreamVolume(AudioManager.STREAM_RING, 5, AudioManager.FLAG_SHOW_UI);
                                        break;
                                    case MediumRingtone:
                                        audio.setStreamVolume(AudioManager.STREAM_RING, 10, AudioManager.FLAG_SHOW_UI);
                                        break;
                                    case LoudRingtone:
                                        audio.setStreamVolume(AudioManager.STREAM_RING, 15, AudioManager.FLAG_SHOW_UI);
                                        break;
                                }


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
            float sensorValue = event.values[2];
            VibrationLevel vibrationLevel;
            if (Math.abs(sensorValue - 9.81) > 1) {
                vibrationLevel = reasonableService.getVibrationLevelFor(Motion.InMotion);
            } else {
                vibrationLevel = reasonableService.getVibrationLevelFor(Motion.Stationary);
            }

            if (vibrationLevel == VibrationLevel.OnVibration) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
            }


            t.setText("Accelerometr Sensor: " + sensorValue);
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            final TextView t = findViewById(R.id.light);
            final float sensorValue = event.values[0];
            int i = 0;

            while (AmbientLight.values()[i].getLimit() < sensorValue && i != 4) {
                i++;
            }

            ScreenBrightness screenBrightness = reasonableService.getScreenBrightnessFor(AmbientLight.values()[i]);
            float brightness = 100 / (float) 255;


            switch (screenBrightness) {
                case LowestScreenBrightness:
                    brightness = 20 / (float) 255;
                    break;
                case LowScreenBrightness:
                    brightness = 40 / (float) 255;
                    break;
                case MediumScreenBrightness:
                    brightness = 60 / (float) 255;
                    break;
                case HighScreenBrightness:
                    brightness = 80 / (float) 255;
                    break;
                case HighestScreenBrightness:
                    brightness = 100 / (float) 255;
                    break;
            }

            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = brightness;
            getWindow().setAttributes(lp);

            t.setText("Light Sensor: " + sensorValue);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
