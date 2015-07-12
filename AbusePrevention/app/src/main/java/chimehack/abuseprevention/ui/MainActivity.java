package chimehack.abuseprevention.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.util.List;
import android.content.Context;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;

import org.json.JSONObject;

import chimehack.abuseprevention.R;

public class MainActivity extends Activity {

    ListView mTodoList;
    TodoAdapter todoAdapter;

    private static class TodoAdapter extends ArrayAdapter<JSONObject> {

        final Context mContext;
        final List<JSONObject> mTodos;

        TodoAdapter(Context context, List<JSONObject> todos) {
            super(context, R.layout.task_view, todos);
            mContext = context;
            mTodos = todos;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.task_view, parent, false);
            }

            TextView task = (TextView)convertView.findViewById(R.id.taskTextView);
            //Button done = (Button)convertView.findViewById(R.id.doneButton);

            JSONObject todo = mTodos.get(position);
            task.setText(todo.optString("task", "Unknown"));

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTodoList = (ListView) findViewById(R.id.todoList);

        // TODO: Load up todos saved in json file on the phone
        ArrayList<JSONObject> todos = new ArrayList<JSONObject>();
        JSONObject test = new JSONObject();
        try {
            test.put("task", "this is a test");
        } catch (org.json.JSONException e) {
            Log.d("MainActivity", "Oops, exception in test");
        }
        todos.add(test);
        todoAdapter = new TodoAdapter(this, todos);
        mTodoList.setAdapter(todoAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_task:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add a task");
                builder.setMessage("What do you want to do");
                final EditText inputField = new EditText(this);
                builder.setView(inputField);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String task = inputField.getText().toString();
                        Log.d("MainActivity", task);


                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
                return true;
            case R.id.action_settings:
                Log.d("MainActivity", "Settings");
                startActivity(new Intent(this, PrefsActivity.class));
                return true;
            default:
                return false;
        }
    }
}
