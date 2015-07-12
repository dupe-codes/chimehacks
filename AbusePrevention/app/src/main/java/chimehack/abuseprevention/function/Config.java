package chimehack.abuseprevention.function;

import android.net.Uri;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import chimehack.abuseprevention.function.actions.CallCustomNumberAction;
import chimehack.abuseprevention.function.actions.CallPoliceAction;
import chimehack.abuseprevention.function.actions.TextContactsAction;
import chimehack.abuseprevention.function.triggers.ShakeOnceTrigger;
import chimehack.abuseprevention.function.triggers.ShakeThriceTrigger;
import chimehack.abuseprevention.function.triggers.StopStopStopTrigger;
import chimehack.abuseprevention.function.triggers.VolumeButtonPatternTrigger;

/**
 * A POJO used to represent the actions and triggers that the user has configured.
 */
public class Config {

    public static class EmergencyContact {
        private String name;
        private String phoneNumber;
        private Uri picture;
        private boolean canText;
        private boolean canCall;

        public EmergencyContact(String name, String phoneNumber, Uri picture, boolean canText,
                                boolean canCall) {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.picture = picture;
            this.canText = canText;
            this.canCall = canCall;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public Uri getPicture() {
            return picture;
        }

        public boolean getCanText() {
            return canText;
        }

        public boolean getCanCall() {
            return canCall;
        }

        public void setCanCall(boolean canCall) {
            this.canCall = canCall;
        }

        public void setCanText(boolean canText) {
            this.canText = canText;
        }
    }

    public static class Statement {

        /**
         * If this...
         */
        public enum Trigger {
            SHAKE_ONCE(ShakeOnceTrigger.class),
            SHAKE_THREE_TIMES(ShakeThriceTrigger.class),
            STOP_STOP_STOP(StopStopStopTrigger.class),
            VOLUME_BUTTON_PATTERN(VolumeButtonPatternTrigger.class);

            private Class<? extends chimehack.abuseprevention.function.triggers.Trigger> clazz;

            Trigger(Class<? extends chimehack.abuseprevention.function.triggers.Trigger> clazz) {
                this.clazz = clazz;
            }

            public Class<? extends chimehack.abuseprevention.function.triggers.Trigger> getClazz() {
                return clazz;
            }
        }

        /**
         * ... then that.
         */
        public enum Action {
            TEXT_CONTACTS(TextContactsAction.class),
            CALL_CUSTOM_NUMBER(CallCustomNumberAction.class),
            CALL_POLICE(CallPoliceAction.class);

            private Class<? extends chimehack.abuseprevention.function.actions.Action> clazz;

            Action(Class<? extends chimehack.abuseprevention.function.actions.Action> clazz) {
                this.clazz = clazz;
            }

            public Class<? extends chimehack.abuseprevention.function.actions.Action> getClazz() {
                return clazz;
            }
        }

        Trigger trigger;
        Action action;
        Map<String, String> options;

        public Statement(Trigger trigger, Action action, Map<String, String> options) {
            this.trigger = trigger;
            this.action = action;
            this.options = options;
        }

        public Trigger getTrigger() {
            return trigger;
        }

        public Action getAction() {
            return action;
        }

        public Map<String, String> getOptions() {
            return options;
        }
    }

    private final List<EmergencyContact> emergencyContacts;
    private final List<Statement> statements;
    private String name;
    private String address;

    public Config(List<EmergencyContact> emergencyContacts, List<Statement> statements, String name,
                  String address) {
        this.emergencyContacts = emergencyContacts;
        this.statements = statements;
        this.name = name;
        this.address = address;
    }

    public List<EmergencyContact> getEmergencyContacts() {
        return Collections.unmodifiableList(emergencyContacts);
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    public void addEmergencyContact(EmergencyContact contact) {
        emergencyContacts.add(contact);
    }

    public void removeEmergencyContact(int contactId) {
        emergencyContacts.remove(contactId);
    }

    public List<Statement> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
