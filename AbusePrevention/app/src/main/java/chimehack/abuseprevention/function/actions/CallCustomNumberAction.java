package chimehack.abuseprevention.function.actions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import chimehack.abuseprevention.R;

/**
 * Calls the police.
 */
public class CallCustomNumberAction implements Action {

    @Override
    public void execute(Context context, SharedPreferences prefs) {
        // Get the custom number, or call the emergency number by default.
        String customNumber = prefs.getString(context.getString(R.string.pref_emergency_number),
                CallPoliceAction.EMERGENCY_NUMBER);
        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + customNumber)));
    }
}
