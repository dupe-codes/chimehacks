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

import java.util.ArrayList;
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
        public void updateConfig(Config config) {
            mConfig = config;
            writeConfigToPrefs();
        }

        public Config getConfig() {
            return mConfig;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.TAG, "Service started. Initializing...");

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize.
        readConfigFromPrefs();
        Log.i(Constants.TAG, "... done.");
    }

    public Config getConfig() {
        return mConfig;
    }

    private void readConfigFromPrefs() {
        String config = mPrefs.getString(getString(R.string.pref_config), "");
        if (TextUtils.isEmpty(config)) {
            mConfig = new Config(new ArrayList<Config.EmergencyContact>(),
                    new ArrayList<Config.Statement>(), "", "");
        } else {
            mConfig = mGson.fromJson(config, Config.class);
        }
    }

    private void writeConfigToPrefs() {
        mPrefs.edit().putString(getString(R.string.pref_config), mGson.toJson(mConfig)).apply();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
