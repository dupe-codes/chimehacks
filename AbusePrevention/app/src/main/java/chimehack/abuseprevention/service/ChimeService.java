package chimehack.abuseprevention.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

import chimehack.abuseprevention.Constants;
import chimehack.abuseprevention.R;
import chimehack.abuseprevention.function.Config;
import chimehack.abuseprevention.function.actions.CallCustomNumberAction;
import chimehack.abuseprevention.function.actions.CallPoliceAction;
import chimehack.abuseprevention.function.actions.TextContactsAction;

/**
 * Background service that handles listening for triggers and performing actions in the background.
 */
public class ChimeService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private SharedPreferences mPrefs;
    private Config mConfig;
    private Gson mGson;

    // Tools for handling shake events
    private SensorManager mSensorMgr;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

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
        mGson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriSerializer())
                .registerTypeAdapter(Uri.class, new UriDeserializer()).create();

        // Initialize.
        readConfigFromPrefs();
        Log.i(Constants.TAG, "... done.");

        // Set up shake detection
        mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                Log.d("OnShake", count + " shakes detected, processing triggers...");
                processTriggers(count);
            }
        });

        mSensorMgr.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
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

    private void processTriggers(int shakeCount) {

        for (Config.Statement statement : mConfig.getStatements()) {
            Log.d("processTriggers", "Checking statement... " + statement.getTrigger());
            switch (statement.getTrigger()) {
                case SHAKE_ONCE:
                    if (shakeCount == 1 || shakeCount == 2) {
                        launchAction(statement);
                    }
                    break;
                case SHAKE_THREE_TIMES:
                    if (shakeCount >= 3) {
                        launchAction(statement);
                    }
                    break;
                default:
                    // Nothing to see here...
                    break;
            }
        }
    }

    private void launchAction(Config.Statement statement) {
        Log.d("launchAction", "Launching action: " + statement.getAction());

        switch (statement.getAction()) {
            case CALL_POLICE:
                // Call da popo
                CallPoliceAction callPolice = new CallPoliceAction();
                callPolice.execute(this, statement, mPrefs);
                break;
            case CALL_CUSTOM_NUMBER:
                // Call dat number
                CallCustomNumberAction callCustom = new CallCustomNumberAction();
                callCustom.execute(this, statement, mPrefs);
                break;
            case TEXT_CONTACTS:
                // Send out dat text
                TextContactsAction textContacts = new TextContactsAction();
                textContacts.execute(this, statement, mPrefs);
                break;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class UriSerializer implements JsonSerializer<Uri> {
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            String string = src.toString();
            if (TextUtils.isEmpty(string)) {
                string = "";
            }
            return new JsonPrimitive(string);
        }
    }

    public class UriDeserializer implements JsonDeserializer<Uri> {
        @Override
        public Uri deserialize(final JsonElement src, final Type srcType,
                               final JsonDeserializationContext context) throws JsonParseException {
            String string = src.toString();
            if (!TextUtils.isEmpty(string)) {
                return Uri.parse(string);
            } else {
                return null;
            }
        }
    }
}
