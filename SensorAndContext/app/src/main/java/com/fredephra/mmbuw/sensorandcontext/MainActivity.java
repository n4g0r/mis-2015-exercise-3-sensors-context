package com.fredephra.mmbuw.sensorandcontext;

import android.content.Context;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



import android.widget.SeekBar;
import android.widget.TextView;

import java.util.LinkedList;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

    private static final String TAG ="fredephra";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float[] accelerationData=new float[3];
    private int pollingrate=0;
    private TextView t;
    private TextView accData;
    private CostumDrawableView costumView;
    private LinkedList<float[]> dataqueue=new LinkedList<float[]>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        costumView = (CostumDrawableView) findViewById(R.id.my_canvas);
        t=(TextView)findViewById(R.id.textView);
        accData=(TextView)findViewById(R.id.accData);
        for(int i=0; i<1024; i++){
            dataqueue.add(accelerationData);
        }
        setupSeekBar();
        setupSensor();
    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }
    @Override
    public final void onSensorChanged(SensorEvent event) {
        accelerationData = event.values;
        //Log.i(TAG, "accdata: "+accelerationData[0]+" "+accelerationData[1]+" " +accelerationData[2]);
        accData.setText(accelerationData[0] + " " + accelerationData[1] + " " +accelerationData[2]);
        dataqueue.add(event.values);
        dataqueue.removeFirst();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "pollingrate on resume: " + pollingrate);
        if(pollingrate!=0)
            mSensorManager.registerListener(MainActivity.this, mSensor, pollingrate);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(MainActivity.this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setPollingrate(int progress){
        Log.i(TAG, "seekbar: " + progress);
        if (progress==0) {
            Log.i(TAG, "pollrate in mikroseconds: ");
            mSensorManager.unregisterListener(MainActivity.this);

        }
        else{
            mSensorManager.unregisterListener(MainActivity.this);
            pollingrate = 1000000 / progress;
            Log.i(TAG, "pollrate in mikroseconds: " + pollingrate);
            mSensorManager.registerListener(MainActivity.this, mSensor, pollingrate);
        }
        t.setText(progress + "Hz");
    }
    public void setupSensor (){
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            Log.i(TAG, "Success! There's a accelerometer.");
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.i(TAG, "accelerometer: " + mSensor.toString());
        }
        else {
            Log.i(TAG, "Failure! There's no accelerometer.");
        }
    }
    public void setupSeekBar(){
        SeekBar mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setPollingrate(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

}
