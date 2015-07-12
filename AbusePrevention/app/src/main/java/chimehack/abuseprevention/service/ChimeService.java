package chimehack.abuseprevention.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

import chimehack.abuseprevention.function.Config;

/**
 * Background service that handles listening for triggers and performing actions in the background.
 */
public class ChimeService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private SharedPreferences mPrefs;
    private Config mConfig;
    private Gson mGson = new Gson();

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
//        mGson.fromJson("", Config.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
