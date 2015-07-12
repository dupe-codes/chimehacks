package chimehack.abuseprevention.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeDetector implements SensorEventListener {

    //TODO: Add settings for shake threshold and such here

    private OnShakeListener mShakeListener;
    private SensorManager mSensorMgr;
    private int mShakeCount;

    public void setOnShakeListener(OnShakeListener listener) {
        this.mShakeListener = listener;
    }

    public interface OnShakeListener {
        public void onShake(int count);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO: Do math here to decide if motion should be considered shake
        Log.d("ShakeSensor", "Shake triggered");

        // Uncomment this when onShake has been implemented
        mShakeListener.onShake(mShakeCount);
    }
}
