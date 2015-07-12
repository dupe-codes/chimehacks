package chimehack.abuseprevention.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import chimehack.abuseprevention.R;
import chimehack.abuseprevention.function.Config;
import chimehack.abuseprevention.service.ChimeService;

public class AdvancedPrefsActivity extends ListActivity {

    private Config mConfig;
    private ChimeService.LocalBinder mBinder;
    private boolean mIsBound = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            getActionBar().setDisplayShowHomeEnabled(true);
        }

        bindService(new Intent(this, ChimeService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
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
        switch (reqCode) {
            case PICK_CONTACT:
                // Handle the contact they picked here

                Uri contactData = data.getData();
                //noinspection deprecation
                Cursor c = managedQuery(contactData, null, null, null, null); // Deprecated.. oh well!
                String cNumber = "";
                String name = "";
                if (c.moveToFirst()) {
                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
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
                        phones.close();
                        Log.d("AdvancedPrefsActivity", "Contacts number is: " + cNumber);
                    }
                    name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.d("AdvancedPrefsActivity", "Contacts name is: " + name);
                }
                final String contactName = name;
                final String contactNum = cNumber;

                // Ask if the contact can call or text
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set Policy");
                builder.setMessage("Set what " + name + " should do in an emergency.");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final ToggleButton canText = new ToggleButton(this);
                canText.setChecked(true);
                canText.setText("OK to text me");
                canText.setTextOn("OK to text me");
                canText.setTextOff("Not OK to text me");
                final ToggleButton canCall = new ToggleButton(this);
                canCall.setChecked(true);
                canCall.setText("OK to call me");
                canCall.setTextOn("OK to call me");
                canCall.setTextOff("Not OK to call me");

                layout.addView(canText);
                layout.addView(canCall);
                builder.setView(layout);

                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean allowTexting = false;
                        if (canText.isChecked()) {
                            Log.d("AdvancedPrefsActivity", "Contact " + contactName + " can text");
                            allowTexting = true;
                        }
                        boolean allowCalling = false;
                        if (canCall.isChecked()) {
                            Log.d("AdvancedPrefsActivity", "Contact " + contactName + " can call");
                            allowCalling = true;
                        }
                        Config.EmergencyContact newContact =
                                new Config.EmergencyContact(contactName, contactNum);
                        newContact.canText = allowTexting;
                        newContact.canCall = allowCalling;
                        mConfig.addEmergencyContact(newContact);
                        mBinder.updateConfig(mConfig);
                        setListAdapter(new AdvancedPrefsAdapter(AdvancedPrefsActivity.this,
                                mConfig));
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
                break;
            default:
                // Nothing to see here
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsBound) {
            unbindService(mServiceConnection);
            mServiceConnection = null;
            mIsBound = false;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (ChimeService.LocalBinder) service;
            mConfig = mBinder.getConfig();
            mIsBound = true;
            // TODO maybe put in some loading thing before?
            AdvancedPrefsAdapter mAdapter = new AdvancedPrefsAdapter(AdvancedPrefsActivity.this,
                    mConfig);
            setListAdapter(mAdapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
        }
    };

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

        private Context mContext;
        private Config mConfig;
        private List<Item> mData = new ArrayList<>();
        private LayoutInflater mInflater;
        private SharedPreferences mPrefs;

        public AdvancedPrefsAdapter(Context context, Config config) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            mConfig = config;
            populate();
        }

        private void populate() {
            mData.add(new Item(RowType.NAME_ROW, null));
            mData.add(new Item(RowType.ADDRESS_ROW, null));
            for (Config.EmergencyContact contact : mConfig.getEmergencyContacts()) {
                mData.add(new Item(RowType.CONTACT_ROW, contact));
            }
            mData.add(new Item(RowType.ADD_CONTACT_ROW, null));
            for (Config.Statement statement : mConfig.getStatements()) {
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
                convertView = mInflater.inflate(type.getLayoutId(), parent, false);

                switch (type) {
                    case NAME_ROW:
                        final TextView name = (TextView) convertView.findViewById(R.id.nameField);
                        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    mConfig.setName(name.getText().toString());
                                    return true;
                                }
                                return false;
                            }
                        });
                        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    mConfig.setName(name.getText().toString());
                                }
                            }
                        });
                        break;
                    case ADDRESS_ROW:
                        final TextView address = (TextView) convertView.findViewById(R.id.addressField);
                        address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    mConfig.setAddress(address.getText().toString());
                                    return true;
                                }
                                return false;
                            }
                        });
                        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    mConfig.setAddress(address.getText().toString());
                                }
                            }
                        });
                        break;
                    case CONTACT_ROW:
                        int id = position - 2;
                        Config.EmergencyContact contact = mConfig.getEmergencyContacts().get(id);

                        break;
                    case ACTION_ROW:
                        break;
                }
            } else {
                switch (type) {
                    case NAME_ROW:
                        TextView name = (TextView) convertView.findViewById(R.id.nameField);
                        name.setText(mConfig.getName());
                        break;
                    case ADDRESS_ROW:
                        TextView address = (TextView) convertView.findViewById(R.id.addressField);
                        address.setText(mConfig.getAddress());
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
