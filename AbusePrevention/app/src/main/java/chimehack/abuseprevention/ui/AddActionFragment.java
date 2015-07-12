package chimehack.abuseprevention.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import chimehack.abuseprevention.R;
import chimehack.abuseprevention.function.Config;

/**
 * Fragment for adding a new action.
 */
public class AddActionFragment extends DialogFragment {

    public static AddActionFragment newInstance() {
        return new AddActionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_action, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(R.string.add_action_title);
        getDialog().setCanceledOnTouchOutside(false);

        final Config config = ((AdvancedPrefsActivity) getActivity()).getConfig();

        // TODO: don't hardcode.
        final Spinner chooseTrigger = (Spinner) view.findViewById(R.id.add_action_trigger);
        final Spinner chooseAction = (Spinner) view.findViewById(R.id.add_action_action);
        final ListView contacts = (ListView) view.findViewById(R.id.add_action_contacts);
        final TextView contactsLabel = (TextView) view.findViewById(R.id.add_action_contacts_label);
        final EditText customMessage = (EditText) view.findViewById(R.id.add_action_custom_message);
        final TextView customMessageLabel = (TextView) view.findViewById(R.id.add_action_custom_message_label);
        final EditText customNumber = (EditText) view.findViewById(R.id.add_action_custom_number);
        final TextView customNumberLabel = (TextView) view.findViewById(R.id.add_action_custom_number_label);
        chooseAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Config.Statement.Action action =
                        Config.Statement.Action.values()[chooseAction.getSelectedItemPosition()];
                switch (action) {
                    case CALL_POLICE:
                        customMessage.setVisibility(View.GONE);
                        customMessageLabel.setVisibility(View.GONE);
                        contacts.setVisibility(View.GONE);
                        contactsLabel.setVisibility(View.GONE);
                        customMessage.setVisibility(View.GONE);
                        customMessageLabel.setVisibility(View.GONE);
                        break;
                    case CALL_CUSTOM_NUMBER:
                        customMessage.setVisibility(View.GONE);
                        customMessageLabel.setVisibility(View.GONE);
                        contacts.setVisibility(View.GONE);
                        contactsLabel.setVisibility(View.GONE);
                        customMessage.setVisibility(View.VISIBLE);
                        customMessageLabel.setVisibility(View.VISIBLE);
                        break;
                    case TEXT_CONTACTS:
                        customMessage.setVisibility(View.VISIBLE);
                        customMessageLabel.setVisibility(View.VISIBLE);
                        contacts.setVisibility(View.VISIBLE);
                        contactsLabel.setVisibility(View.VISIBLE);
                        customMessage.setVisibility(View.GONE);
                        customMessageLabel.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing yet.
            }
        });

        // Select contacts.
        // TODO handle contacts with similar names.
        ArrayList<String> contactsArray = new ArrayList<>();
        for (Config.EmergencyContact contact : config.getEmergencyContacts()) {
            contactsArray.add(contact.getName());
        }
        contacts.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, contactsArray));

        // Add button.
        final Button save = (Button) view.findViewById(R.id.add_action_ok);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO(oleg): implement
                Config.Statement.Trigger trigger =
                        Config.Statement.Trigger.values()[chooseTrigger.getSelectedItemPosition()];
                Config.Statement.Action action =
                        Config.Statement.Action.values()[chooseAction.getSelectedItemPosition()];
                Map<String, String> options = new HashMap<String, String>();

                switch (action) {
                    case CALL_POLICE:
                        break;
                    case CALL_CUSTOM_NUMBER:
                        options.put("number", customNumber.getText().toString());
                        break;
                    case TEXT_CONTACTS:
                        SparseBooleanArray array = contacts.getCheckedItemPositions();
                        for (int i = 0; i < array.size(); i++) {
                            int pos = array.keyAt(i);
                            if (array.get(pos)) {
                                options.put(config.getEmergencyContacts().get(pos).getName(),
                                        customMessage.getText().toString());
                            }
                        }
                        break;
                }
                Config.Statement statement = new Config.Statement(trigger, action, options);
                config.addStatement(statement);

                ((AdvancedPrefsActivity) getActivity()).reloadData();
            }
        });

    }
}
