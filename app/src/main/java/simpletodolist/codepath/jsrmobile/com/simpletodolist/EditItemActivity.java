package simpletodolist.codepath.jsrmobile.com.simpletodolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import simpletodolist.codepath.jsrmobile.com.simpletodolist.R;

public class EditItemActivity extends Activity {
    EditText etItemEdit;
    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        setTitle(R.string.edititem);
        etItemEdit = (EditText)findViewById(R.id.etItemEdit);
        String text = getIntent().getStringExtra("text");
        position = getIntent().getIntExtra("position",-1);
        etItemEdit.setText(text);
        etItemEdit.setSelection(text.length());
        if(etItemEdit.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void onEditItemSave(View view) {
        String newText = etItemEdit.getText().toString();
        Intent data = new Intent();
        data.putExtra("text", newText);
        data.putExtra("position", position);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
