package chimehack.abuseprevention.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Background service that handles listening for triggers and performing actions in the background.
 */
public class ChimeService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private SharedPreferences mPrefs;

    /**
     * Class that we can give to activities in order to interact with the service.
     */
    public class LocalBinder extends Binder {
        void notifySettingsUpdated() {
            readPrefs();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize.
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        readPrefs();
    }

    private void readPrefs() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
