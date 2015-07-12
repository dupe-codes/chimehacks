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

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("This is an automated report from [our app], sent on behalf of Linda Zheng.\n");
        stringBuilder.append("Home Address: \n");
        stringBuilder.append("Current estimated coordinates: \n");

        for (Config.EmergencyContact contact : service.getConfig().emergencyContacts) {
            stringBuilder.append("Please " + (contact.canCall ? "feel free to " : "DO NOT ") + "call me.\n");
            stringBuilder.append("Please " + (contact.canText ? "feel free to " : "DO NOT ") + "text me.\n");
            stringBuilder.append("Additional notes from Linda: " + customMessages.get(contact.name));
//            smsManager.sendTextMessage(); // TODO (linda)

        }
    }
}
