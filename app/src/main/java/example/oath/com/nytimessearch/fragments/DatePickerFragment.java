package example.oath.com.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

import example.oath.com.nytimessearch.activities.MainActivity;
import example.oath.com.nytimessearch.models.Filters;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public DatePickerFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static DatePickerFragment newInstance(String title) {
        DatePickerFragment frag = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Filters filters = ((MainActivity)getActivity()).getFilters();
        Calendar beginDate = filters.getBeginDate();
        int year = beginDate.get(Calendar.YEAR);
        int month = beginDate.get(Calendar.MONTH);
        int day = beginDate.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getContext(), this, year, month, day);
    }

    // Defines the listener interface
    public interface DatePickerDialogListener {
        void onFinishPickDateDialog(String date);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String beginDate = new StringBuilder().append(String.format("%02d", month + 1)).append("/").append(String.format("%02d", day)).append("/").append(String.format("%04d", year)).toString();
        DatePickerDialogListener listener = (DatePickerDialogListener) getTargetFragment();
        listener.onFinishPickDateDialog(beginDate);
        dismiss();
    }
}