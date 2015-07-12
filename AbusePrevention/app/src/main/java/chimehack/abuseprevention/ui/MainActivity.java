package chimehack.abuseprevention.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chimehack.abuseprevention.R;

public class MainActivity extends Activity {

    private SwipeMenuListView mTodoList;
    private TodoAdapter todoAdapter;
    private List<JSONObject> mTodos;
    private static int[] COLORS = {0xFF1CB17B, 0xFF3A85AB, 0xFFF26960, 0xFF93CBF0, 0xFF59C4AC,
            0xFFF0AF44};

    private static class TodoAdapter extends ArrayAdapter<JSONObject> {

        final Context mContext;
        List<JSONObject> mTodos;

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

            TextView task = (TextView) convertView.findViewById(R.id.taskTextView);
            View border = convertView.findViewById(R.id.rightHandBorder);

            JSONObject todo = mTodos.get(position);
            task.setText(todo.optString("task", "Unknown"));
            border.setBackgroundColor(COLORS[position % COLORS.length]);

            return convertView;
        }

        public void update(List<JSONObject> todos) {
            mTodos = todos;
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTodoList = (SwipeMenuListView) findViewById(R.id.todoList);

        mTodos = new ArrayList<JSONObject>();
        try {
            String content = new Scanner(new File(this.getFilesDir(), "todos.json")).useDelimiter("\\Z").next();
            try {
                JSONArray tasks = new JSONArray(new JSONTokener(content));
                for (int i = 0; i < tasks.length(); i++) {
                    mTodos.add(tasks.getJSONObject(i));
                }
                Log.d("MainActivity", "Loaded tasks");
            } catch (org.json.JSONException e) {
                Log.e("MainActivity", "Error loading todo file");
            }
        } catch (IOException e) {
            // react
            Log.d("MainActivity", "Failed to load tasks, it didn't exist");
            String[] prePopulate = new String[]{
                    "Pick up groceries",
                    "Check bank account balance",
                    "Check the mail"
            };
            for (String todo : prePopulate) {
                JSONObject newTask = new JSONObject();
                try {
                    newTask.put("task", todo);
                } catch (org.json.JSONException ex) {
                    Log.e("MainActivity", "Error prepopulating tasks");
                }
                mTodos.add(newTask);
            }
        }
        todoAdapter = new TodoAdapter(this, mTodos);
//        mTodoList.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
//            @Override
//            public void onSwipeStart(int i) {
//                // Nothing.
//            }
//
//            @Override
//            public void onSwipeEnd(int i) {
//                mTodos.remove(i);
//                todoAdapter.update(mTodos);
//                saveToFile(mTodos.toString());
//            }
//        });
//        mTodoList.setOpenInterpolator(new LinearInterpolator(this, null));
        SwipeMenuCreator swipeToDelete = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu swipeMenu) {
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
        };
        mTodoList.setAdapter(todoAdapter);
        mTodoList.setMenuCreator(swipeToDelete);
        mTodoList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int i1) {
                mTodos.remove(i);
                todoAdapter.update(mTodos);
                saveToFile(mTodos.toString());
                return true;
            }
        });
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

    /**
     * Saves the current tasks to persistent file storage
     */
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
