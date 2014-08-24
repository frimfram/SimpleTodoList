package simpletodolist.codepath.jsrmobile.com.simpletodolist;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class TodoActivity extends Activity implements EditAddDialogFragment.OnFragmentInteractionListener {

    TodoCursorAdapter itemsAdapter;
    ListView lvItems;
    TodoDbData todoDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        todoDb = new TodoDbData(this);

        setTitle(R.string.simpletodo);
        lvItems = (ListView) findViewById(R.id.lvItems);

        String[] columns = new String[] {
                TodoDbData.KEY_TEXT,
                TodoDbData.KEY_PRIORITY,
                TodoDbData.KEY_DUEDATE
        };
        int[] to = new int[] {
                R.id.tvListItemText,
                R.id.tvListItemPriority,
                R.id.tvListItemDueDate
        };
        Cursor cursor = todoDb.fetchAllTodoItems();
        itemsAdapter = new TodoCursorAdapter(this, R.layout.todo_list_item, cursor, columns, to, 0);

        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
        View empty = findViewById(R.id.empty);
        lvItems.setEmptyView(empty);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onDestroy() {
        todoDb.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add) {
            //show fragment to add a new to-do item
            showAddEditDialog(-1, -1);
            return true;
        }else if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddEditDialog(int position, long id) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment newFragment;
        if(position < 0) {
            // add dialog.
            newFragment = EditAddDialogFragment.newInstance(null, null, null, null);

        }else{
            //edit dialog
            Cursor cursor = (Cursor)itemsAdapter.getItem(position);
            String text = cursor.getString(1);
            String priority = cursor.getString(2);
            String dueDate = cursor.getString(3);
            newFragment = EditAddDialogFragment.newInstance(Long.toString(id), text, priority, dueDate);
        }
        newFragment.show(ft, "dialog");
    }

    private void setupListViewListener() {
       lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //doing edit - show the edit dialog and populate it with data
               showAddEditDialog(position, id);
           }
       });
       lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

               boolean deleted = todoDb.delete(Long.toString(id));
               if(deleted) {
                   Cursor newCursor = todoDb.fetchAllTodoItems();
                   itemsAdapter.changeCursor(newCursor);
                   itemsAdapter.notifyDataSetChanged();
               }
               return true;
           }
       });

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View empty = findViewById(R.id.empty);
        if(empty != null && lvItems != null) {
            lvItems.setEmptyView(empty);
        }
    }

    @Override
    public void onFragmentInteraction(ContentValues contents) {
        if(contents == null) return;

        if(contents.getAsString(TodoDbData.C_ID) == null) {
            //inserting new one
            todoDb.insertOrIgnore(contents);
        }else{
            todoDb.update(contents);
        }
        Cursor newCursor = todoDb.fetchAllTodoItems();
        itemsAdapter.changeCursor(newCursor);
        itemsAdapter.notifyDataSetChanged();
    }

    private class TodoCursorAdapter extends SimpleCursorAdapter
    {

        public TodoCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if(position % 2 == 0) {
                view.setBackgroundColor(Color.rgb(238, 233, 233));
            }else{
                view.setBackgroundColor(Color.rgb(255, 255, 255));
            }
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            //set the color of the priority box
            TextView priorityView = (TextView)view.findViewById(R.id.tvListItemPriority);
            String priority = cursor.getString(2);
            if("high".equalsIgnoreCase(priority)) {
                priorityView.setBackgroundColor(Color.rgb(255, 0, 0));
            }else if("medium".equalsIgnoreCase(priority)) {
                priorityView.setBackgroundColor(Color.rgb(255, 255, 0));
            }else if("low".equalsIgnoreCase(priority)) {
                priorityView.setBackgroundColor(Color.rgb(0, 255, 0));
            }else {
                priorityView.setBackgroundColor(Color.rgb(255, 255, 255));
            }
        }
    }

}
