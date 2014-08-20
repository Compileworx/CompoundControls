package com.example.compoundcontrols;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by bradl_000 on 2014/01/15.
 */
public class DurationControl extends LinearLayout implements View.OnClickListener {
    private static final String tag = "DurationControl";
    private Calendar fromDate = null;
    private Calendar toDate = null;

    private static int ENUM_DAYS = 1;
    private static int ENUM_WEEKS = 1;

    private int durationUnits = 1;

    public long getDuration(){
        if(validate() == false)
        {
            return -1;
        }

        long fromMillis = fromDate.getTimeInMillis();
        long toMillis = toDate.getTimeInMillis();
        long diff = toMillis - fromMillis;
        long day = 24 * 60 * 60 * 1000;
        long diffInDays = diff/day;
        long diffInWeeks = diff / (day * 7);
        if(durationUnits == ENUM_WEEKS){
            return diffInDays;
        }

        return diffInWeeks;
    }

    public boolean validate(){
        if(fromDate == null || toDate == null){
            return false;
        }

        if(toDate.after(fromDate))
        {
            return true;
        }

        return false;
    }

    public DurationControl(Context context){
        super(context);
        initialize(context);
    }

    public DurationControl(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.DurationComponent,0,0);
        durationUnits = t.getInt(R.styleable.DurationComponent_durationUnits,durationUnits);
        t.recycle();
        initialize(context);
    }

    public DurationControl(Context context, AttributeSet attrs){
        this(context);
    }

    private void initialize(Context context)
    {
        LayoutInflater lif = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lif.inflate(R.layout.duration_view_layout, this);
        Button b = (Button)this.findViewById(R.id.fromButton);
        b.setOnClickListener(this);

        b = (Button)this.findViewById(R.id.toButton);
        b.setOnClickListener(this);
        this.setSaveEnabled(true);
    }

    private FragmentManager getFragmentManager(){
        Context c = getContext();
        if(c instanceof Activity)
        {
            return ((Activity) c).getFragmentManager();
        }
        throw new RuntimeException("Activity Context Expected Instead");
    }

    public void onClick(View v){
        Button b = (Button) v;

        if(b.getId() == R.id.fromButton){
            DialogFragment newFragment = new DatePickerFragment(this, R.id.fromButton);
            newFragment.show(getFragmentManager(),"com.example.tags.datepicker");
            return;
        }

        DialogFragment newFragment = new DatePickerFragment(this, R.id.toButton);
        newFragment.show(getFragmentManager(),"com.example.tags.datepicker");
        return;
    }

    public void onDateSet(int ButtonID, int year, int month, int day){
        Calendar c = getDate(year, month, day);
        if(ButtonID == R.id.fromButton){
            setFromDate(c);
            return;
        }

        setToDate(c);
    }

    private void setToDate(Calendar c){
        if(c == null){
            return;
        }

        this.toDate = c;
        TextView tv = (TextView)findViewById(R.id.toDate);
        tv.setText(getDateString(c));
    }

    private void setFromDate(Calendar c){
        if(c == null){
            return;
        }

        this.fromDate = c;
        TextView tv = (TextView)findViewById(R.id.fromDate);
        tv.setText(getDateString(c));
    }

    private Calendar getDate(int Year, int Month, int Day){
        Calendar c = Calendar.getInstance();
        c.set(Year, Month, Day);
        return c;
    }

    public static String getDateString(Calendar c){
        if(c == null){
            return "null";
        }

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        df.setLenient(false);
        String s = df.format(c.getTime());
        return s;
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container){
        //dont call this so that children wont be saved
        //super.dispatchSaveInsttanceState
        //Call your self onsavedinsancestate
        super.dispatchFreezeSelfOnly(container);
        Log.d(tag,"in dispatch save instance state");
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container){
        //dont call for the children super.dispatchrestorestate
        super.dispatchThawSelfOnly(container);
        Log.d(tag, "in dispatch restore instance state");
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state){
        Log.d(tag,"On Restore Instance State");
        if(!(state instanceof SavedState)){
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.setFromDate(ss.fromDate);
        this.setToDate(ss.toDate);
    }

    @Override
    protected Parcelable onSaveInstanceState(){
        Log.d(tag, "in onSaveInstanceState");
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.fromDate = this.fromDate;
        ss.toDate = this.toDate;

        return ss;
    }

    //saved state inner static class
    public static class SavedState extends BaseSavedState{
        private Calendar fromDate;
        private Calendar toDate;

        SavedState(Parcelable superState){
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags){
            super.writeToParcel(out, flags);

            if(fromDate != null){
                out.writeLong(fromDate.getTimeInMillis());
            }
            else{
                out.writeLong(-1L);
            }

            if(toDate != null){
                out.writeLong(toDate.getTimeInMillis());
            }
            else{
                out.writeLong(-1L);
            }
        }

        @Override
        public String toString(){
            StringBuffer sb = new StringBuffer("from Date : " + DurationControl.getDateString(fromDate));
            sb.append("to Date : " + DurationControl.getDateString(toDate));
            return sb.toString();
        }

        @SuppressWarnings("hiding")
        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>(){
            public SavedState createFromParcel(Parcel in){
                return new SavedState(in);
            }

            public SavedState[] newArray(int Size){
                return new SavedState[Size];
            }
        };

        private SavedState(Parcel in){
            super(in);

            long lFromDate = in.readLong();
            if(lFromDate == -1){
                fromDate = null;
            }
            else{
                fromDate = Calendar.getInstance();
                fromDate.setTimeInMillis(lFromDate);
            }

            long lToDate = in.readLong();
            if(lToDate == -1){
                toDate = null;
            }
            else
            {
                toDate = Calendar.getInstance();
                toDate.setTimeInMillis(lToDate);
            }
        }
    }
}
