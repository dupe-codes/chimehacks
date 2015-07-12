package chimehack.abuseprevention.function.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;

import java.util.List;

import chimehack.abuseprevention.function.triggers.Trigger;

/**
 * Action that calls a friend.
 */
public class TextContactsAction implements Action {
    @Override
    public void execute(Context context, SharedPreferences prefs) {
        SmsManager smsManager = SmsManager.getDefault();
        //smsManager.sendTextMessage();
        // TODO(oleg): implement
    }
}
