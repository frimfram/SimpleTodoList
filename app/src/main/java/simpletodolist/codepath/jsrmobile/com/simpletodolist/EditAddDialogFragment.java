package simpletodolist.codepath.jsrmobile.com.simpletodolist;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A  {@link android.app.DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditAddDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditAddDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class EditAddDialogFragment extends DialogFragment {

    private ContentValues mContentValues;

    private OnFragmentInteractionListener mListener;

    Spinner prioritySpinner;
    EditText etText;
    EditText etDueDate;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditAddDialogFragment.
     */
    public static EditAddDialogFragment newInstance(String id, String text, String priority, String dueDate) {
        EditAddDialogFragment fragment = new EditAddDialogFragment();
        if(id != null) {
            Bundle args = new Bundle();
            args.putString(TodoDbData.C_ID, id);
            args.putString(TodoDbData.KEY_TEXT, text);
            args.putString(TodoDbData.KEY_PRIORITY, priority);
            args.putString(TodoDbData.KEY_DUEDATE, dueDate);
            fragment.setArguments(args);
        }
        return fragment;
    }
    public EditAddDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContentValues = new ContentValues();
            mContentValues.put(TodoDbData.C_ID, getArguments().getString(TodoDbData.C_ID));
            mContentValues.put(TodoDbData.KEY_TEXT, getArguments().getString(TodoDbData.KEY_TEXT));
            mContentValues.put(TodoDbData.KEY_PRIORITY, getArguments().getString(TodoDbData.KEY_PRIORITY));
            mContentValues.put(TodoDbData.KEY_DUEDATE, getArguments().getString(TodoDbData.KEY_DUEDATE));
        }else{
            mContentValues = null;
        }
        int style = DialogFragment.STYLE_NORMAL,
            theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_add_dialog, container, false);
        prioritySpinner = (Spinner)v.findViewById(R.id.spinnerPriority);
        List<String> priorityList = new ArrayList<String>();
        priorityList.add("high");
        priorityList.add("medium");
        priorityList.add("low");
        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_item, priorityList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(dataAdapter);

        etText = (EditText)v.findViewById(R.id.etItemText);
        etDueDate = (EditText)v.findViewById(R.id.etDueDate);

        if(mContentValues == null) {
            getDialog().setTitle(R.string.add_new);
            etText.setHint(R.string.enter_hint);
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            Date today = Calendar.getInstance().getTime();
            String todayStr = df.format(today);
            etDueDate.setText(todayStr);
            prioritySpinner.setSelection(2);
        }else{
            getDialog().setTitle(R.string.edititem);
            etText.setText(mContentValues.getAsString(TodoDbData.KEY_TEXT));
            etDueDate.setText(mContentValues.getAsString(TodoDbData.KEY_DUEDATE));
            String priority = mContentValues.getAsString(TodoDbData.KEY_PRIORITY);
            if("high".equalsIgnoreCase(priority)) {
                prioritySpinner.setSelection(0);
            }else if("medium".equalsIgnoreCase(priority)) {
                prioritySpinner.setSelection(1);
            }else{
                prioritySpinner.setSelection(2);
            }
        }

        Button saveButton = (Button)v.findViewById(R.id.save_button);
        Button cancelButton = (Button)v.findViewById(R.id.cancel_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    //save
                    if(mContentValues == null) {
                        mContentValues = new ContentValues();
                    }
                    mContentValues.put(TodoDbData.KEY_PRIORITY, (String)prioritySpinner.getSelectedItem());
                    mContentValues.put(TodoDbData.KEY_DUEDATE, etDueDate.getText().toString());
                    mContentValues.put(TodoDbData.KEY_TEXT, etText.getText().toString());
                    save(mContentValues);
                    dismiss();
                }else{
                    //show error Toast
                    Toast t = Toast.makeText(getActivity(), "Please enter due date or todo text.", Toast.LENGTH_LONG);
                    t.show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

    public void save(ContentValues content) {
        if (mListener != null) {
            mListener.onFragmentInteraction(content);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(ContentValues content);
    }

    private boolean validate() {
        //validate the data before saving
        if(etDueDate.getText() == null || etDueDate.getText().toString().trim().length() < 1) {
            return false;
        }
        if(etText.getText() == null || etText.getText().toString().trim().length() < 1) {
            return false;
        }
        return true;
    }

}
