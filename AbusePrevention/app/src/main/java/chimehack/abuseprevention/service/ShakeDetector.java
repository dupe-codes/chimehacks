package chimehack.abuseprevention.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;

public class ShakeDetector implements SensorEventListener {

    // Settings for shake threshold and such
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 100;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private OnShakeListener mShakeListener;
    private int mShakeCount;
    private long mShakeTimestamp;

    public void setOnShakeListener(OnShakeListener listener) {
        this.mShakeListener = listener;
    }

    public interface OnShakeListener {
        void onShake(int count);
    }

    public void checkShaking(final int currNumber) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currNumber < mShakeCount) {
                    Log.d("ShakeTimer", "More shakes occurred");
                    checkShaking(mShakeCount);
                    return;
                } else {
                    Log.d("ShakeTimer", "No more shakes occurred, processing triggers...");
                    mShakeListener.onShake(mShakeCount);
                    mShakeCount = 0;
                    return;
                }
            }
        }, 2000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mShakeListener != null) {
            float gX = event.values[0]/SensorManager.GRAVITY_EARTH;
            float gY = event.values[1]/SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2]/SensorManager.GRAVITY_EARTH;

            // gForce close to 1 when no movement
            float gForce = FloatMath.sqrt(gX*gX + gY*gY + gZ*gZ);
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();

                // ignore shake events too close to each other (500ms) FIXME: Do this?
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                // Process and reset shake count after 3 seconds of no shakes
                // FIXME: Does this only process once shaking is done like intended?
                /*if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeListener.onShake(mShakeCount);
                    mShakeCount = 0;
                } */

                mShakeTimestamp = now;
                mShakeCount++;

                if (mShakeCount == 1) {
                    // Start timer to wait for shakes to stop
                    checkShaking(mShakeCount);
                }
            }
        }
    }
}
