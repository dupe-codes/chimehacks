package chimehack.abuseprevention.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

import chimehack.abuseprevention.R;
import chimehack.abuseprevention.function.Config;
import chimehack.abuseprevention.service.ChimeService;

public class AdvancedPrefsActivity extends Activity {

    private Config mConfig;
    private ChimeService.LocalBinder mBinder;
    private boolean mIsBound = false;

    SwipeMenuListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_prefs);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.advanced_prefs);
        }

        mListView = (SwipeMenuListView) findViewById(android.R.id.list);
        SwipeMenuCreator swipeToDelete = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu swipeMenu) {
                if (swipeMenu.getViewType() == AdvancedPrefsAdapter.RowType.CONTACT_ROW.ordinal()
                        || swipeMenu.getViewType() == AdvancedPrefsAdapter.RowType.ACTION_ROW.ordinal()) {
                    // create "delete" item
                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                            0x3F, 0x25)));
                    // set item width
                    deleteItem.setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90,
                            getResources().getDisplayMetrics()));
                    // set a icon
                    deleteItem.setIcon(android.R.drawable.ic_delete);
                    // add to menu
                    swipeMenu.addMenuItem(deleteItem);
                }
            }
        };
        mListView.setMenuCreator(swipeToDelete);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int i1) {
                mConfig.removeEmergencyContact(i - 5);
                mBinder.updateConfig(mConfig);
                reloadData();
                return true;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (AdvancedPrefsAdapter.RowType.values()[parent.getAdapter().getItemViewType(position)]) {
                    case ADD_CONTACT_ROW:
                        pickFromContacts();
                        break;
                    case ADD_ACTION_ROW:
                        addAction();
                        break;
                }
            }
        });

        bindService(new Intent(this, ChimeService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
}

    private void addAction() {
        AddActionFragment fragment = AddActionFragment.newInstance();
        fragment.show(getFragmentManager(), "add_action");
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
                if (data == null) {
                    // user canceled
                    return;
                }

                Uri contactData = data.getData();
                //noinspection deprecation
                Cursor c = managedQuery(contactData, null, null, null, null); // Deprecated.. oh well!
                String cNumber;
                String name;
                final Uri picture;
                if (c.moveToFirst()) {
                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    Log.d("AdvancedPrefsActivity", "Contacts id is " + id);
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (!hasPhone.equalsIgnoreCase("1")) {
                        return;
                    }
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null
                    );
                    phones.moveToFirst();
                    cNumber = phones.getString(phones.getColumnIndex("data1"));
                    name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                            Long.parseLong(id));
                    picture = Uri.withAppendedPath(person,
                            ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                    Log.d("AdvancedPrefsActivity", "Contacts name is: " + name);
                    phones.close();
                    Log.d("AdvancedPrefsActivity", "Contacts number is: " + cNumber);
                    Log.d("AdvancedPrefsActivity", "Contacts picture is: " + picture.toString());
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
                            Config.EmergencyContact newContact = new Config.EmergencyContact(
                                    contactName, contactNum, picture, allowTexting, allowCalling);

                            mConfig.addEmergencyContact(newContact);
                            mBinder.updateConfig(mConfig);
                            reloadData();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
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
            reloadData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
        }
    };

    void reloadData() {
        AdvancedPrefsAdapter adapter = new AdvancedPrefsAdapter(AdvancedPrefsActivity.this,
                mConfig, mBinder);
        mListView.setAdapter(adapter);
    }

    public Config getConfig() {
        return mConfig;
    }

    private static class AdvancedPrefsAdapter extends BaseAdapter {
        private enum RowType {
            NAME_HEADER_ROW(R.layout.name_header_row),
            NAME_ROW(R.layout.name_row),
            ADDRESS_HEADER_ROW(R.layout.address_header_row),
            ADDRESS_ROW(R.layout.address_row),
            CONTACTS_HEADER_ROW(R.layout.header_row),
            CONTACT_ROW(R.layout.contact_row),
            ADD_CONTACT_ROW(R.layout.add_row),
            ACTIONS_HEADER_ROW(R.layout.header_row),
            ACTION_ROW(R.layout.action_row),
            ADD_ACTION_ROW(R.layout.add_row);
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
        private ChimeService.LocalBinder mBinder;
        private List<Item> mData = new ArrayList<>();
        private LayoutInflater mInflater;
        private SharedPreferences mPrefs;

        public AdvancedPrefsAdapter(Context context, Config config, ChimeService.LocalBinder binder) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            mConfig = config;
            mBinder = binder;

            populate();
        }

        private void populate() {
            mData.add(new Item(RowType.NAME_HEADER_ROW, null));
            mData.add(new Item(RowType.NAME_ROW, null));
            mData.add(new Item(RowType.ADDRESS_HEADER_ROW, null));
            mData.add(new Item(RowType.ADDRESS_ROW, null));
            mData.add(new Item(RowType.CONTACTS_HEADER_ROW, R.string.header_emergency_contacts));
            for (Config.EmergencyContact contact : mConfig.getEmergencyContacts()) {
                mData.add(new Item(RowType.CONTACT_ROW, contact));
            }
            mData.add(new Item(RowType.ADD_CONTACT_ROW, R.string.add_contact));
            mData.add(new Item(RowType.ACTIONS_HEADER_ROW, R.string.header_actions));
            for (Config.Statement statement : mConfig.getStatements()) {
                mData.add(new Item(RowType.ACTION_ROW, statement));
            }
            mData.add(new Item(RowType.ADD_ACTION_ROW, R.string.add_action));
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
                                    mBinder.updateConfig(mConfig);
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
                                    mBinder.updateConfig(mConfig);
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
                                    mBinder.updateConfig(mConfig);
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
                                    mBinder.updateConfig(mConfig);
                                }
                            }
                        });
                        break;
                    case CONTACT_ROW:
                        final View finalConvertView1 = convertView;
                        ((ToggleButton) convertView.findViewById(R.id.contact_can_call)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                for (Config.EmergencyContact contact : mConfig.getEmergencyContacts()) {
                                    if (contact.getName().equals(((TextView) finalConvertView1.findViewById(R.id.contact_name)).getText())) {
                                        contact.setCanCall(isChecked);
                                        break;
                                    }
                                }
                            }
                        });
                        final View finalConvertView = convertView;
                        ((ToggleButton) convertView.findViewById(R.id.contact_can_message)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                for (Config.EmergencyContact contact : mConfig.getEmergencyContacts()) {
                                    if (contact.getName().equals(((TextView) finalConvertView.findViewById(R.id.contact_name)).getText())) {
                                        contact.setCanText(isChecked);
                                        break;
                                    }
                                }
                            }
                        });
                }
            }

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
                    Config.EmergencyContact contact = mConfig.getEmergencyContacts().get(position - 5);
                    TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
                    contactName.setText(contact.getName());
                    TextView contactNumber = (TextView) convertView.findViewById(R.id.contact_phone);
                    contactNumber.setText(contact.getPhoneNumber());
                    ((ToggleButton) convertView.findViewById(R.id.contact_can_call)).setChecked(contact.getCanCall());
                    ((ToggleButton) convertView.findViewById(R.id.contact_can_message)).setChecked(contact.getCanText());
                    if (contact.getPicture() != null) {
                        ((ImageView) convertView.findViewById(R.id.contact_picture)).setImageURI(contact.getPicture());
                    } else {
                        ((ImageView) convertView.findViewById(R.id.contact_picture)).setImageResource(android.R.drawable.gallery_thumb);
                    }
                    Log.i("TOGGLE CALL", contact.getCanCall() ? "YES" : "NO");
                    Log.i("TOGGLE TEXT", contact.getCanText() ? "YES" : "NO");

                    Log.i("CONTACT NAME", contact.getName());
                    Log.i("CONTACT PHONE", contact.getPhoneNumber());

                    break;
                case ACTION_ROW:
                    int numContacts = mConfig.getEmergencyContacts().size();
                    Config.Statement action = mConfig.getStatements().get(position - 7 - numContacts);
                    TextView actionView = (TextView)convertView.findViewById(R.id.action_message);

                    Config.Statement.Trigger trigger = action.getTrigger();
                    String triggerStr = "";
                    if (trigger != null) {
                        triggerStr = mContext.getResources().getStringArray(R.array.triggers)[trigger.ordinal()];
                    }

                    Config.Statement.Action response = action.getAction();
                    String respStr = "";
                    if (response != null) {
                        respStr = mContext.getResources().getStringArray(R.array.actions)[response.ordinal()];
                    }

                    String message = "When\n" + triggerStr + "\nDo\n" + respStr;
                    actionView.setText(message);
                    break;
                case ADD_ACTION_ROW:
                case ADD_CONTACT_ROW:
                case ACTIONS_HEADER_ROW:
                case CONTACTS_HEADER_ROW:
                    TextView text = (TextView) convertView.findViewById(android.R.id.title);
                    text.setText((Integer) mData.get(position).getData());
                    break;
            }
            return convertView;
        }
    }
}
