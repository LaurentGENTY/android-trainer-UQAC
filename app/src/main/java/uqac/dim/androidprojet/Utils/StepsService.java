package uqac.dim.androidprojet.Utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StepsService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;

    private int steps;

    private IBinder myBinder;

    public class myBinderActivity extends Binder{
        public StepsService getService(){
            return StepsService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.myBinder = new myBinderActivity();

        mSensorManager = (SensorManager)
                this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetectorSensor =
                    mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.i("TestStepDetector","ok");
        }

        steps = 0;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int
            startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        steps = 0;
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        steps++;
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("steps.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(Integer.toString(steps));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        Log.i("NOUVEAUPAS",Integer.toString(steps));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public int getSteps(){
        return this.steps;
    }
}