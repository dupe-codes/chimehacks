package chimehack.abuseprevention.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import chimehack.abuseprevention.R;

public class PrefsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
