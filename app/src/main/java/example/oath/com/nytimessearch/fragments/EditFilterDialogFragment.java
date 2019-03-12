package example.oath.com.nytimessearch.fragments;

import android.icu.text.SimpleDateFormat;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Iterator;

import example.oath.com.nytimessearch.activities.MainActivity;
import example.oath.com.nytimessearch.fragments.DatePickerFragment.DatePickerDialogListener;
import example.oath.com.nytimessearch.R;
import example.oath.com.nytimessearch.models.Filters;

public class EditFilterDialogFragment extends DialogFragment implements DatePickerDialogListener {

    TextView tvDate;
    Button btnSave;
    Spinner spinnerSortOrder;

    public EditFilterDialogFragment() {        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditFilterDialogFragment newInstance(String title) {
        EditFilterDialogFragment frag = new EditFilterDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.8);
        int height = getResources().getDisplayMetrics().heightPixels;
        getDialog().getWindow().setLayout(width, height);
        View view = inflater.inflate(R.layout.filter_setting, container);
        tvDate = (TextView) view.findViewById(R.id.tvBeginDate);
        btnSave = (Button) view.findViewById(R.id.btn_save);
        spinnerSortOrder = (Spinner) view.findViewById(R.id.spinnerSortOrder);
        CheckBox checkArts = (CheckBox) view.findViewById(R.id.checkbox_arts);
        CheckBox checkFashion = (CheckBox) view.findViewById(R.id.checkbox_fashion);
        CheckBox checkSports = (CheckBox) view.findViewById(R.id.checkbox_sports);

        Filters filters = ((MainActivity)getActivity()).getFilters();
        if (filters.getBeginDate() == null) {
            filters.setBeginDate(Calendar.getInstance());
        }
        tvDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(filters.getBeginDate().getTime()));
        tvDate.setOnClickListener(v -> showDatePickerDialog());

        if (filters.getSortOrder() != null && !filters.getSortOrder().isEmpty()) {
            setSpinnerToValue(spinnerSortOrder, filters.getSortOrder());
        }

        if (filters.getNewsDesk() != null & filters.getNewsDesk().size() > 0) {
            Iterator it = filters.getNewsDesk().iterator();
            while (it.hasNext()) {
                String currentNewsDesk = it.next().toString();
                if (currentNewsDesk.equals(getString(R.string.news_desk_arts))) {
                    checkArts.setChecked(true);
                } else if (currentNewsDesk.equals(getString(R.string.news_desk_fashion))) {
                    checkFashion.setChecked(true);
                } else if (currentNewsDesk.equals(getString(R.string.news_desk_sports))) {
                    checkSports.setChecked(true);
                }
            }
        }

        btnSave.setOnClickListener(v -> saveFilters());

        return view;
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }

    // call this method to launch a date picker dialog
    public void showDatePickerDialog() {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance("Pick start date");
        // SETS the target fragment for use later when sending results
        datePickerFragment.setTargetFragment(EditFilterDialogFragment.this, 300);
        datePickerFragment.show(fm, "fragment_datepicker");
    }

    public void saveFilters() {
        Filters filters = new Filters();
        TextView tvBeginDate = (TextView) getDialog().findViewById(R.id.tvBeginDate);
        CheckBox checkArts = (CheckBox) getDialog().findViewById(R.id.checkbox_arts);
        CheckBox checkFashion = (CheckBox) getDialog().findViewById(R.id.checkbox_fashion);
        CheckBox checkSports = (CheckBox) getDialog().findViewById(R.id.checkbox_sports);

        Calendar calendar = Calendar.getInstance();
        String beginDate = tvBeginDate.getText().toString().trim();
        int year = Integer.parseInt(beginDate.substring(6));
        int month = Integer.parseInt(beginDate.substring(0, 2)) - 1;
        int date = Integer.parseInt(beginDate.substring(3, 5));
        calendar.set(year, month, date);
        filters.setBeginDate(calendar);

        filters.setSortOrder(spinnerSortOrder.getSelectedItem().toString());

        if (checkArts.isChecked()) {
            filters.addNewsDesk(checkArts.getText().toString());
        }
        if (checkFashion.isChecked()) {
            filters.addNewsDesk(checkFashion.getText().toString());
        }
        if (checkSports.isChecked()) {
            filters.addNewsDesk(checkSports.getText().toString());
        }

        ((MainActivity)getActivity()).setFilters(filters);
        dismiss();
    }

    // This is called when the dialog is completed and the results have been passed
    @Override
    public void onFinishPickDateDialog(String date) {
        tvDate = (TextView) getDialog().findViewById(R.id.tvBeginDate);
        tvDate.setText(date);
    }
}
