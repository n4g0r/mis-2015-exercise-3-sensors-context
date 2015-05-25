package com.fredephra.mmbuw.sensorandcontext;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

    private static final String TAG ="fredephra";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float[] accelerationData=new float[4];
    private int pollingrate=0;
    private TextView t;
    private TextView t2;
    private TextView accData;
    private LinkedList<float[]> dataqueue=new LinkedList<float[]>();
    private CostumDrawableView costumView;
    private boolean reading_sensor =false;
    long previousTime;
    private int fftlength=64;//power of 2
    private int fftcounter=0;
    private FFT fft;
    private double[] gravity =new double[3];
    private int mId;
    private NotificationManager mNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        costumView = (CostumDrawableView) findViewById(R.id.my_canvas);

        t=(TextView)findViewById(R.id.textView);
        t2=(TextView)findViewById(R.id.textView2);
        accData=(TextView)findViewById(R.id.accData);
        for(int i=0; i<1080; i++){
            dataqueue.add(accelerationData);
        }
        setupSensor();
        setupSeekBar();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        costumView.setQueue(dataqueue);
    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }
    @Override
    public final void onSensorChanged(SensorEvent event) {
        long temptime = System.currentTimeMillis();
        //Log.i(TAG,Long.toString(temptime - previousTime));
        if (previousTime != 0)
        {
            accData.setText(Long.toString(temptime - previousTime));//+"/"+1/(temptime - previousTime)+"Hz");
        }
        else
        {
            accData.setText("0");
        }
        previousTime = temptime;

        //removal of gravity http://developer.android.com/reference/android/hardware/SensorEvent.html
        final float alpha = 0.8f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        float[] linear_acceleration= new float[3];

        linear_acceleration[0] = event.values[0] - (float)gravity[0];
        linear_acceleration[1] = event.values[1] - (float)gravity[1];
        linear_acceleration[2] = event.values[2] - (float)gravity[2];

        //Log.i(TAG, "accdata: "+accelerationData[0]+" "+accelerationData[1]+" " +accelerationData[2]);
        //accData.setText(accelerationData[0] + " " + accelerationData[1] + " " + accelerationData[2]);
        float magnitude=Math.abs(linear_acceleration[0])+Math.abs(linear_acceleration[1])+Math.abs(linear_acceleration[2]);
        System.out.println(magnitude);
        float[] temp= new float[]{event.values[0],event.values[1],event.values[2],magnitude};
        dataqueue.add(temp);
        dataqueue.remove();
        fftcounter++;
        if (true){//(fftcounter>=fftlength){
            fft();
            fftcounter=0;
        }
        costumView.setQueue(dataqueue);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "pollingrate on resume: " + pollingrate);
        if(pollingrate!=0) {
            Log.i(TAG, "resuming and setting pollingrate to: " + pollingrate);
            mSensorManager.registerListener(this, mSensor, pollingrate);
            reading_sensor=true;
        }
        mNotificationManager.cancel(mId);

    }

    @Override
    protected void onPause() {
        if(reading_sensor) {
            mSensorManager.unregisterListener(this, mSensor);
            reading_sensor = false;
        }
        setup_notifications();
        super.onPause();
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
        if (reading_sensor) {
            mSensorManager.unregisterListener(this,mSensor);
            reading_sensor = false;
        }
        if (progress!=0){
            pollingrate = 1000000 / progress;
            Log.i(TAG, "pollrate (mikroseconds): " + pollingrate);
            mSensorManager.registerListener(this, mSensor, pollingrate);
            reading_sensor=true;
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
        mSeekBar.setProgress(50);
        mSeekBar.setMax(200);
        SeekBar mSeekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fftlength=(int)Math.pow(2,progress+1);
                t2.setText(Integer.toString(fftlength));
                fft =new FFT(fftlength);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mSeekBar2.setProgress(6);
        mSeekBar2.setMax(9);
    }
    public void fft(){
        double[] x=new double[fftlength];
        //ListIterator it = dataqueue.listIterator(dataqueue.size());
        for(int i = 0; i < fftlength; i++){
            x[i]= dataqueue.get(dataqueue.size()-i-1)[3];
        }
        double[] y=new double[fftlength];

        //System.out.println("x: " + Arrays.toString(x));
        //System.out.println("y: " + Arrays.toString(y));

        fft.fft(x, y);

        //System.out.println("x: " + Arrays.toString(x));
        //System.out.println("y: " + Arrays.toString(y));
        double[] fftresult= new double[fftlength/2];
        for(int i = 0; i < fftlength/2; i++){
            fftresult[i]=Math.sqrt(Math.pow(x[i],2)+Math.pow(y[i],2));
        }
        //System.out.println("result: " + Arrays.toString(fftresult));
        costumView.setFFT(fftresult);
    }
    public void setup_notifications(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.my_image)
                        .setContentTitle("Sensor and Context")
                        .setContentText("no activity detected!(not implemented)");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

// mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
