package com.example.admin.forhacku;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sm;
    float[] datas = new float[3];
    long start,end;
    TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        start = System.currentTimeMillis();
        datas[0] = 0;
        datas[1] = 0;
        datas[2] = 0;
        time = (TextView)findViewById(R.id.Data);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String m = "";
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                if (Math.abs(datas[0]-event.values[0])>1 || Math.abs(datas[1]-event.values[1])>1 || Math.abs(datas[2]-event.values[2])>1) {
                    datas[0] = event.values[0];
                    datas[1] = event.values[1];
                    datas[2] = event.values[2];
                    start = System.currentTimeMillis();
                }
                end = System.currentTimeMillis();
        }
        m += event.values[0]+"\n";
        m += event.values[1]+"\n";
        m += event.values[2]+"\n";
        m += ((end-start)/1000)+"秒";
        time.setText(m);
        if ((end-start)/1000 >= 10) {
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //アプリを閉じたときにマネージャーを解放する
    @Override
    protected void onStop() {
        super.onStop();
        sm.unregisterListener(this);
    }
    //
    @Override
    protected void onResume() {
        super.onResume();
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            Sensor s = sensors.get(0);
            //センサーをマネージャーに管理してもらう
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        }
    }
}
