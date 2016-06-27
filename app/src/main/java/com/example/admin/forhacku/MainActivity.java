package com.example.admin.forhacku;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private SensorManager sm;
    private LocationManager lm;
    float[] datas = new float[3];
    long start,end;
    TextView time,locate,prov;
    boolean vibra = false;
    int location_min_time = 0,location_min_distance = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //アプリ開始時のいろいろな初期設定
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        lm = (LocationManager)getSystemService(Service.LOCATION_SERVICE);
        start = System.currentTimeMillis();
        datas[0] = 0;
        datas[1] = 0;
        datas[2] = 0;
        time = (TextView)findViewById(R.id.acceleText);
        locate = (TextView)findViewById(R.id.locationText);
        prov = (TextView)findViewById(R.id.providerText);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String m = "";
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                if (Math.abs(datas[0]-event.values[0])>1 || Math.abs(datas[1]-event.values[1])>1 || Math.abs(datas[2]-event.values[2])>1) {
                    //加速度が一定以上の変化があった場合
                    datas[0] = event.values[0];
                    datas[1] = event.values[1];
                    datas[2] = event.values[2];
                    start = System.currentTimeMillis();
                    vibra = false;
                }
                end = System.currentTimeMillis();
        }
        //情報表示用の処理
        m += event.values[0]+"\n";
        m += event.values[1]+"\n";
        m += event.values[2]+"\n";
        m += ((end-start)/1000)+"秒";
        time.setText(m);
        if ((end-start)/1000 >= 10 && !vibra) {
            //もし一定以上加速度が変わらなかった場合、バイブレーションを起動する
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(new long[]{500,200,500,200},-1);
            vibra = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onStop() {
        //スリープ時にも動かしていたいので、センサマネージャは解放しない
        super.onStop();
    }
    @Override
    protected void onResume() {
        //センサマネージャ等の設定
        super.onResume();
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            Sensor s = sensors.get(0);
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
        boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isNetworkEnabled) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,location_min_time,location_min_distance,this);
        }
    }
    @Override
    protected void onDestroy() {
        //センサマネージャの解放
        super.onDestroy();
        sm.unregisterListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        String m = "";
        //位置情報の表示
        m += "緯度 : "+location.getLongitude()+"\n";//緯度の取得
        m += "経度 : "+location.getLatitude()+"\n";//経度の取得
        locate.setText(m);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                prov.setText(provider+"が圏外になっていて利用できません");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                prov.setText("一時的に"+provider+"が利用できません");
                break;
            case LocationProvider.AVAILABLE:
                prov.setText(provider+"が利用できます");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        prov.setText(provider+" : 有効になりました");
    }

    @Override
    public void onProviderDisabled(String provider) {
        prov.setText(provider+" : 無効になりました");
    }
}
