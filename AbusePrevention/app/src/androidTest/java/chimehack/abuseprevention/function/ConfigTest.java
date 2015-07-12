package chimehack.abuseprevention.function;

import android.os.Bundle;

import com.google.gson.Gson;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

import chimehack.abuseprevention.function.Config;

public class ConfigTest extends TestCase {

    public void testGsonSerialization() {
        Gson gson = new Gson();

        Set<Config.EmergencyContact> contacts = new HashSet<>();
        contacts.add(new Config.EmergencyContact("Linda Zheng", "7133675720"));
        contacts.add(new Config.EmergencyContact("Dawsona Botsford", "1234567890"));
        contacts.add(new Config.EmergencyContact("Berta Lovejoy", "1234567890"));

        Set<Config.Statement> statements = new HashSet<>();
        Bundle options = new Bundle();
        statements.add(new Config.Statement(
                Config.Statement.Trigger.SHAKE_ONCE, Config.Statement.Action.CALL_POLICE, options));
        Bundle options2 = new Bundle();
        options2.putString("number", "7897897890");
        statements.add(new Config.Statement(
                Config.Statement.Trigger.STOP_STOP_STOP, Config.Statement.Action.CALL_CUSTOM_NUMBER,
                options2));

        Config config = new Config(contacts, statements);

        assertEquals(gson.toJson(config), "asdfasdf");
    }
}
