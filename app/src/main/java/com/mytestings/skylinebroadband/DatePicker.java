package com.mytestings.skylinebroadband;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    DatesetInterface datesetInterface;
    public DatePicker(DatesetInterface datesetInterface) {
        super();
        this.datesetInterface=datesetInterface;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar=Calendar.getInstance();

        return new DatePickerDialog(getContext(), (DatePickerDialog.OnDateSetListener) this,
                calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
      datesetInterface.dateset(year,month,dayOfMonth);
    }

}
interface DatesetInterface{
  public  void dateset(int year,int month,int dayofmonth);
}