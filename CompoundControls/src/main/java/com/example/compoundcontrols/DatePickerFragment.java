package com.example.compoundcontrols;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static String tag = "DatePickerFragment";
    private DurationControl parent;
    private int buttonID;

    public DatePickerFragment(DurationControl inParent, int inButtonID){
        parent = inParent;
        buttonID = inButtonID;
        Bundle args = this.getArguments();

        if(args == null){
            args = new Bundle();
        }

        args.putInt("parentid",inParent.getId());
        args.putInt("buttonid",buttonID);
        this.setArguments(args);
    }

    public DatePickerFragment(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar c = Calendar.getInstance();
        int Year = c.get(Calendar.YEAR);
        int Month = c.get(Calendar.MONTH);
        int Day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this,Year,Month,Day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day){
        parent.onDateSet(buttonID, year, month, day);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        Log.d(tag,"Establishing Parent");

        int ParentID = this.getArguments().getInt("parentid");
        buttonID = this.getArguments().getInt("buttonid");

        View x = this.getActivity().findViewById(ParentID);

        if(x == null){
            //throw new RuntimeException("Sorry not able to establish parent on restart");
            return;
        }

        parent = (DurationControl)x;

    }
}
