package chimehack.abuseprevention.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashSet;

import chimehack.abuseprevention.Constants;
import chimehack.abuseprevention.R;
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
        void updateSettings() {
            mConfig = readConfigFromPrefs();
        }

        Config getSettings() {
            return mConfig;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.TAG, "Service started");

        // Initialize.
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mConfig = readConfigFromPrefs();
    }

    public Config getConfig() {
        return mConfig;
    }

    private Config readConfigFromPrefs() {
        String config = mPrefs.getString(getString(R.string.pref_config), "");
        if (TextUtils.isEmpty(config)) {
            return new Config(new HashSet<Config.EmergencyContact>(),
                    new HashSet<Config.Statement>());
        } else {
            return mGson.fromJson(config, Config.class);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
