package chimehack.abuseprevention.function.actions;

import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.Map;

import chimehack.abuseprevention.Constants;
import chimehack.abuseprevention.R;
import chimehack.abuseprevention.function.Config;
import chimehack.abuseprevention.service.ChimeService;

/**
 * Action that calls a friend.
 */
public class TextContactsAction implements Action {
    @Override
    public void execute(ChimeService service, Config.Statement statement, SharedPreferences prefs) {
        SmsManager smsManager = SmsManager.getDefault();

        // Map from "First Last" <=> "Custom message"
        Map<String, String> customMessages = statement.getOptions();

        for (Config.EmergencyContact contact : service.getConfig().getEmergencyContacts()) {
            String appName = service.getString(R.string.real_app_name);
            String fullName = service.getConfig().getName();
            String homeAddress = service.getConfig().getAddress();
            String currentLocation = "TODO";
            String canCall = contact.getCanCall() ? service.getString(R.string.message_template_do) :
                    service.getString(R.string.message_template_do_not);
            String canText = contact.getCanCall() ? service.getString(R.string.message_template_do) :
                    service.getString(R.string.message_template_do_not);
            String firstName = fullName.split("\\s")[0];
            String additionalMessage = customMessages.get(contact.getName());
            String message = String.format(service.getString(R.string.message_template),
                    appName, fullName, homeAddress, currentLocation, canCall, canText, firstName,
                    additionalMessage);
            Log.i(Constants.TAG, "Sending text message: " + message);
            smsManager.sendTextMessage(contact.getPhoneNumber(), null, message, null, null);
//            smsManager.sendTextMessage(); // TODO (linda)
//=======
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("This is an automated report from [our app], sent on behalf of Linda Zheng.\n");
//        stringBuilder.append("Home Address: \n");
//        stringBuilder.append("Current estimated coordinates: \n");
//
//        for (Config.EmergencyContact contact : service.getConfig().emergencyContacts) {
//            stringBuilder.append("Please " + (contact.canCall ? "feel free to " : "DO NOT ") + "call me.\n");
//            stringBuilder.append("Please " + (contact.canText ? "feel free to " : "DO NOT ") + "text me.\n");
//            stringBuilder.append("Additional notes from Linda: " + customMessages.get(contact.name));
//            smsManager.sendTextMessage(contact.phoneNumber, null, stringBuilder.toString(), null, null); // TODO (linda)
//>>>>>>> finished message sending

        }
    }
}
