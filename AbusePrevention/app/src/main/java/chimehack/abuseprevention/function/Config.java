package chimehack.abuseprevention.function;

import java.util.Map;
import java.util.Set;

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
        public String name;
        public String phoneNumber;

        public EmergencyContact(String name, String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
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
            CALL_POLICE(CallPoliceAction.class),
            CALL_CUSTOM_NUMBER(CallCustomNumberAction.class),
            TEXT_CONTACTS(TextContactsAction.class);

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
    }

    public Set<EmergencyContact> emergencyContacts;
    public Set<Statement> statements;

    public Config(Set<EmergencyContact> emergencyContacts, Set<Statement> statements) {
        this.emergencyContacts = emergencyContacts;
        this.statements = statements;
    }
}
