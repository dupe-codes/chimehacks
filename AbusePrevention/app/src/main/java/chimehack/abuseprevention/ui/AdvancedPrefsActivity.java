package chimehack.abuseprevention.ui;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeSet;

import chimehack.abuseprevention.R;

public class AdvancedPrefsActivity extends ListActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.advanced_prefs);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdvancedPrefsAdapter mAdapter = new AdvancedPrefsAdapter();
        setListAdapter(mAdapter);
    }

    private class AdvancedPrefsAdapter extends BaseAdapter {

        private static final int TYPE_NAME_HEADER_ROW = 0;
        private static final int TYPE_NAME_ROW = 1;
        private static final int TYPE_ADDRESS_ROW = 2;
        private static final int TYPE_CONTACT_ROW = 3;
        private static final int TYPE_ADD_CONTACT_ROW = 4;
        private static final int TYPE_ACTION_ROW = 5;
        private static final int TYPE_ADD_ACTION_ROW = 6;

//        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private ArrayList<String> mData = new ArrayList<>();
        private LayoutInflater mInflater;

        private TreeSet mSeparatorsSet = new TreeSet();

        public AdvancedPrefsAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final String item) {
            mData.add(item);
            // save separator position
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
//            return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 7;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            int type = getItemViewType(position);
            Log.i("AdvancedPrefs", "getView " + position + " " + convertView + " type = " + type);
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_NAME_HEADER_ROW:
                        convertView = mInflater.inflate(R.layout.header_row, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.nameHeader);
                        break;
                    case TYPE_NAME_ROW:
                        convertView = mInflater.inflate(R.layout.name_row, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.nameField);
                        break;
                    case TYPE_ADDRESS_ROW:
                        convertView = mInflater.inflate(R.layout.address_row, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.addressField);
                        break;
//                    case TYPE_CONTACT_ROW:
//                        convertView = mInflater.inflate(R.layout.contact_row, null);
//                        holder.textView = (TextView)convertView.findViewById(R.id.addressField);
//                        break;
//                    case TYPE_ADD_CONTACT_ROW:
//                        convertView = mInflater.inflate(R.layout.add_contact_row, null);
//                        holder.textView = (TextView)convertView.findViewById(R.id);
//                        break;
//                    case TYPE_ACTION_ROW:
//                        convertView = mInflater.inflate(R.layout.action_row, null);
//                        holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
//                        break;
//                    case TYPE_ADD_ACTION_ROW:
//                        convertView = mInflater.inflate(R.layout.add_action_row, null);
//                        holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
//                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.textView.setText(mData.get(position));
            return convertView;
        }

    }

    public static class ViewHolder {
        public TextView textView;
    }

}
