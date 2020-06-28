package com.mytestings.skylinebroadband;



import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TypeConverter {

    @androidx.room.TypeConverter
    public Date fromStringToDate(Long date){
        Log.d("extrass","typeconvdate");
        return    new Date(date);

    }

    @androidx.room.TypeConverter
    public Long fromDateToString(Date date){
        Log.d("extrass","typeconvstring");
       return date.getTime();
    }
}
