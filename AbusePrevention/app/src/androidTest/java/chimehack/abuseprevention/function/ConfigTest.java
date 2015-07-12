package chimehack.abuseprevention.function;

import com.google.gson.Gson;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigTest extends TestCase {

    public void testGsonSerialization() {
        Gson gson = new Gson();

        List<Config.EmergencyContact> contacts = new ArrayList<>();
        contacts.add(new Config.EmergencyContact("Linda Zheng", "7133675720"));
        contacts.add(new Config.EmergencyContact("Dawsona Botsford", "1234567890"));
        contacts.add(new Config.EmergencyContact("Berta Lovejoy", "1234567890"));

        List<Config.Statement> statements = new ArrayList<>();
        Map<String, String> options = new HashMap<>();
        statements.add(new Config.Statement(
                Config.Statement.Trigger.SHAKE_ONCE, Config.Statement.Action.CALL_POLICE, options));
        Map<String, String> options2 = new HashMap<>();
        options2.put("number", "7897897890");
        statements.add(new Config.Statement(
                Config.Statement.Trigger.STOP_STOP_STOP, Config.Statement.Action.CALL_CUSTOM_NUMBER,
                options2));

        Config config = new Config(contacts, statements, "Linda", "123 Road Rd");

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
