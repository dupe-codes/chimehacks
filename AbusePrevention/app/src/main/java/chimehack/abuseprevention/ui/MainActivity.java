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

import java.io.IOException;
import java.util.List;
import android.content.Context;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

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

        try {
            // TODO: Load up todos saved in json file on the phone
            String content = new Scanner(new File(this.getFilesDir(), "todos.json")).useDelimiter("\\Z").next();
            try {
                JSONArray tasks = new JSONArray(new JSONTokener(content));
                ArrayList<JSONObject> todos = new ArrayList<JSONObject>();
                for(int i = 0; i < tasks.length(); i++){
                    todos.add(tasks.getJSONObject(i));
                }
                todoAdapter = new TodoAdapter(this, todos);
                Log.d("MainActivity", "Loaded tasks");
            } catch (org.json.JSONException e) {
                Log.e("MainActivity", "Error loading todo file");
                todoAdapter = new TodoAdapter(this, new ArrayList<JSONObject>());
            }
        } catch (IOException e) {
            // react
            Log.d("MainActivity", "Failed to load tasks");
            todoAdapter = new TodoAdapter(this, new ArrayList<JSONObject>());
        }
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

                        JSONObject newTodo = new JSONObject();
                        try {
                            newTodo.put("task", task);
                        } catch (org.json.JSONException e) {
                            Log.d("MainActivity", "Failed to add task");
                        }
                        todoAdapter.add(newTodo);

                        // Save current list of tasks
                        String currTasks = todoAdapter.mTodos.toString();
                        saveToFile(currTasks);
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

    public void saveToFile(String toWrite) {
        try {
            File saveFile = new File((this).getFilesDir(), "todos.json");
            if (!saveFile.exists()) saveFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
            writer.write(toWrite);
            writer.close();
            Log.d("MainActivity", "Wrote out to file");
        } catch (IOException e) {
            Log.e("MainActivity", "Unable to write out todos save file");
        }
    }
}
