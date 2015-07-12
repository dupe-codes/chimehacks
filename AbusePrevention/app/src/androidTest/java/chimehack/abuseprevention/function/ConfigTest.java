package chimehack.abuseprevention.function;

import com.google.gson.Gson;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigTest extends TestCase {

    public void testGsonSerialization() {
        Gson gson = new Gson();

        Set<Config.EmergencyContact> contacts = new HashSet<>();
        contacts.add(new Config.EmergencyContact("Linda Zheng", "7133675720"));
        contacts.add(new Config.EmergencyContact("Dawsona Botsford", "1234567890"));
        contacts.add(new Config.EmergencyContact("Berta Lovejoy", "1234567890"));

        Set<Config.Statement> statements = new HashSet<>();
        Map<String, String> options = new HashMap<>();
        statements.add(new Config.Statement(
                Config.Statement.Trigger.SHAKE_ONCE, Config.Statement.Action.CALL_POLICE, options));
        Map<String, String> options2 = new HashMap<>();
        options2.put("number", "7897897890");
        statements.add(new Config.Statement(
                Config.Statement.Trigger.STOP_STOP_STOP, Config.Statement.Action.CALL_CUSTOM_NUMBER,
                options2));

        Config config = new Config(contacts, statements);

        assertEquals(gson.toJson(config), "{\"emergencyContacts\":[" +
                "{\"name\":\"Berta Lovejoy\",\"phoneNumber\":\"1234567890\"}," +
                "{\"name\":\"Dawsona Botsford\",\"phoneNumber\":\"1234567890\"}," +
                "{\"name\":\"Linda Zheng\",\"phoneNumber\":\"7133675720\"}]," +
                "\"statements\":[" +
                "{\"action\":\"CALL_CUSTOM_NUMBER\",\"options\":{\"number\":\"7897897890\"}," +
                "\"trigger\":\"STOP_STOP_STOP\"}," +
                "{\"action\":\"CALL_POLICE\",\"options\":{}," +
                "\"trigger\":\"SHAKE_ONCE\"}]}");
    }
}
