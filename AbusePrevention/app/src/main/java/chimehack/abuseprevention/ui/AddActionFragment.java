package chimehack.abuseprevention.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

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

        // TODO: don't hardcode.
        Spinner spinner = (Spinner) view.findViewById(R.id.add_action_trigger);

        // Select contacts.
        ListView contacts = (ListView) view.findViewById(R.id.add_action_contacts);

        // Add button.
        Button save = (Button) view.findViewById(R.id.add_action_ok);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO(oleg): implement
                Config config = ((AdvancedPrefsActivity) getActivity()).getConfig();
                Config.Statement statement = new Config.Statement(null, null, null);
                config.addStatement(statement);

                ((AdvancedPrefsActivity) getActivity()).reloadData();
            }
        });

    }
}
