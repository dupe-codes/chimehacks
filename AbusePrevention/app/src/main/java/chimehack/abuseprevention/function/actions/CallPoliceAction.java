package chimehack.abuseprevention.function.actions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import chimehack.abuseprevention.function.Config;
import chimehack.abuseprevention.service.ChimeService;

/**
 * Calls the police.
 */
public class CallPoliceAction implements Action {

    // TODO: provide a way to customize this number.
    static final String EMERGENCY_NUMBER = "5083531505";

    @Override
    public void execute(ChimeService service, Config.Statement statement, SharedPreferences prefs) {
        service.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + EMERGENCY_NUMBER)));
    }
}
