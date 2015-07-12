package chimehack.abuseprevention.function.actions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import chimehack.abuseprevention.R;
import chimehack.abuseprevention.function.Config;
import chimehack.abuseprevention.service.ChimeService;

/**
 * Calls the police.
 */
public class CallCustomNumberAction implements Action {

    @Override
    public void execute(ChimeService service, Config.Statement statement, SharedPreferences prefs) {
        // Get the custom number, or call the emergency number by default.
        String customNumber = prefs.getString(service.getString(R.string.pref_emergency_number),
                CallPoliceAction.EMERGENCY_NUMBER);
        service.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + customNumber)));
    }
}
