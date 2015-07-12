package chimehack.abuseprevention.ui;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chimehack.abuseprevention.R;
import chimehack.abuseprevention.function.Config;

public class AdvancedPrefsActivity extends ListActivity {

    Config mConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfig = getConfig();

        AdvancedPrefsAdapter mAdapter = new AdvancedPrefsAdapter(this, mConfig);
        setListAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        switch (AdvancedPrefsAdapter.RowType.values()[getListAdapter().getItemViewType(position)]) {
            case NAME_ROW:
                break;
            case ADDRESS_ROW:
                break;
            case CONTACT_ROW:
                break;
            case ADD_CONTACT_ROW:
                pickFromContacts();
                break;
            case ACTION_ROW:
                break;
            case ADD_ACTION_ROW:
                break;
        }
    }

    static final int PICK_CONTACT = 1;
    private void pickFromContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    /**
     * Grabbing contacts function ripped from StackOverflow #thankshomies
     */
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch(reqCode) {
            case PICK_CONTACT:
                // Handle the contact they picked here

                Uri contactData = data.getData();
                Cursor c = managedQuery(contactData, null, null, null, null); // Deprecated.. oh well!
                String id = "";
                String cNumber = "";
                String name = "";
                if (c.moveToFirst()) {
                    id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    Log.d("AdvancedPrefsActivity", "Contacts id is " + id);
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null, null
                        );
                        phones.moveToFirst();
                        cNumber = phones.getString(phones.getColumnIndex("data1"));
                        Log.d("AdvancedPrefsActivity", "Contacts number is: " + cNumber);
                    }
                    name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.d("AdvancedPrefsActivity", "Contacts name is: " + name);
                }
                Config.EmergencyContact newContact = new Config.EmergencyContact(name, cNumber);
                mConfig.emergencyContacts.add(newContact);
                //TODO(Oleg) Write new config to preferences
                break;
            default:
                // Nothing to see here
                return;
        }
    }

    private Config getConfig() {
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

        return new Config(contacts, statements);
    }

    private static class AdvancedPrefsAdapter extends BaseAdapter {
        private enum RowType {
            NAME_ROW(R.layout.name_row),
            ADDRESS_ROW(R.layout.address_row),
            CONTACT_ROW(R.layout.contact_row),
            ADD_CONTACT_ROW(R.layout.add_contact_row),
            ACTION_ROW(R.layout.action_row),
            ADD_ACTION_ROW(R.layout.add_action_row);

            int layoutId;

            RowType(int layoutId) {
                this.layoutId = layoutId;
            }

            public int getLayoutId() {
                return layoutId;
            }
        }

        private static class Item {
            private final RowType rowType;
            private final Object data;

            public Item(RowType rowType, Object data) {
                this.rowType = rowType;
                this.data = data;
            }

            public RowType getRowType() {
                return rowType;
            }

            public Object getData() {
                return data;
            }
        }

        private static final int MAX_COUNT = 7;

        private Config mConfig;
        private List<Item> mData = new ArrayList<>();
        private LayoutInflater mInflater;

        public AdvancedPrefsAdapter(Context context, Config config) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mConfig = config;
            populate();
        }

        private void populate() {
            mData.add(new Item(RowType.NAME_ROW, null));
            mData.add(new Item(RowType.ADDRESS_ROW, null));
            for (Config.EmergencyContact contact : mConfig.emergencyContacts) {
                mData.add(new Item(RowType.CONTACT_ROW, contact));
            }
            mData.add(new Item(RowType.ADD_CONTACT_ROW, null));
            for (Config.Statement statement : mConfig.statements) {
                mData.add(new Item(RowType.ACTION_ROW, statement));
            }
            mData.add(new Item(RowType.ADD_ACTION_ROW, null));
            notifyDataSetInvalidated();
        }

        @Override
        public int getItemViewType(int position) {
            return mData.get(position).getRowType().ordinal();
        }

        @Override
        public int getViewTypeCount() {
            return RowType.values().length;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Item getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RowType type = RowType.values()[getItemViewType(position)];
            Log.i("AdvancedPrefs", "getView " + position + " " + convertView + " type = " + type);


            if (convertView == null) {
                switch (type) {
                    case NAME_ROW:
                        convertView = mInflater.inflate(R.layout.name_row, null);
                        break;
                    case ADDRESS_ROW:
                        convertView = mInflater.inflate(R.layout.address_row, null);
                        break;
                    case CONTACT_ROW:
                        convertView = mInflater.inflate(R.layout.contact_row, null);
                        break;
                    case ADD_CONTACT_ROW:
                        convertView = mInflater.inflate(R.layout.add_contact_row, null);
                        break;
                    case ACTION_ROW:
                        convertView = mInflater.inflate(R.layout.action_row, null);
                        break;
                    case ADD_ACTION_ROW:
                        convertView = mInflater.inflate(R.layout.add_action_row, null);
                        break;
                }
            } else {
                switch (type) {
                    case NAME_ROW:
                        TextView name = (TextView) convertView.findViewById(R.id.nameField);
                        name.setText("Linda Zheng");
                        break;
                    case ADDRESS_ROW:
                        TextView address = (TextView) convertView.findViewById(R.id.addressField);
                        address.setText("123 Address Road");
                        break;
                    case CONTACT_ROW:
                        // TODO(linda)
                        break;
                    case ADD_CONTACT_ROW:
                        // No data for this one.
                        break;
                    case ACTION_ROW:
                        // TODO(linda)
                        break;
                    case ADD_ACTION_ROW:
                        // No data for this one.
                        break;
                }
            }
            return convertView;
        }
    }
}
