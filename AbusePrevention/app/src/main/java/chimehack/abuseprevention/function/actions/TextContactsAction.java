package chimehack.abuseprevention.function.actions;

import android.content.SharedPreferences;
import android.telephony.SmsManager;

import java.util.Map;

import chimehack.abuseprevention.function.Config;
import chimehack.abuseprevention.service.ChimeService;

/**
 * Action that calls a friend.
 */
public class TextContactsAction implements Action {
    @Override
    public void execute(ChimeService service, Config.Statement statement, SharedPreferences prefs) {
        SmsManager smsManager = SmsManager.getDefault();

        // Map from "ID" <=> "Custom message"
        Map<String, String> customMessages = statement.getOptions();

        smsManager.sendTextMessage();
    }
}
