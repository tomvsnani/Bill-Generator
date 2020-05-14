package com.mytestings.skylinebroadband;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Database.Entity;
import Database.SkyDatabase;

public class Receiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("hello", "hello");
        Intent intent1 = new Intent(context, CountDaysService.class);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intent1);
//        } else {
//            context.startService(intent);
//        }
        CountDaysService.enqueueWork(context,intent1);
    }

    public static class CountDaysService extends JobIntentService {
        NotificationChannel notificationChannel;
        SkyDatabase skyDatabase;
        List<Entity> list;
        String dueDate;
        int dueAmount;


        public static void enqueueWork(Context context, Intent intent) {
            enqueueWork(context, CountDaysService.class, 1, intent);
        }

        @Override
        protected void onHandleWork(@NonNull Intent intent) {
            Log.d("hello", "hai");
            createNotification();
            skyDatabase = SkyDatabase.getInstance(getApplicationContext());
            list = skyDatabase.dao().getTotaldataNoLiveData();
            if (list.size() > 0) {
                for (Entity entity : list) {
                    dueDate = entity.getAccount_created_on();
                    dueAmount = entity.getAmountDue();
                    if (entity.getAmountDue() != 0 &&
                            new SimpleDateFormat("dd : MM : yyyy").format(Calendar.getInstance().getTimeInMillis())
                                    .equals(dueDate)) {
                        dueAmount = dueAmount + entity.getNetPayablePrice();
                        entity.setAmountDue(dueAmount);
                        updateEntity(entity);

                        if (entity.getId() == (list.get(list.size() - 1).getId())) {
                            stopSelf();
                        }
                    } else stopSelf();

                }
            } else stopSelf();
        }

        private void updateEntity(final Entity entity) {
           Thread thread=new Thread(new Runnable() {
               @Override
               public void run() {
                   skyDatabase.dao().update(entity);
               }
           });
           thread.start();
        }


//        private void calculatedays() {
//            if (check == 0) {
//                calenderFrom.set(Calendar.YEAR, calenderFrom.get(Calendar.YEAR));
//                calenderFrom.set(Calendar.MONTH, calenderFrom.get(Calendar.MONTH));
//                calenderFrom.set(Calendar.DAY_OF_MONTH, dueDate);
//                if (calenderFrom.get(Calendar.MONTH) == Calendar.DECEMBER) {
//
//                    calenderto.set(Calendar.YEAR, calenderto.get(Calendar.YEAR) + 1);
//                    calenderto.set(Calendar.MONTH, Calendar.JANUARY);
//                } else {
//                    calenderto.set(Calendar.YEAR, calenderto.get(Calendar.YEAR));
//                    calenderto.set(Calendar.MONTH, calenderto.get(Calendar.MONTH) + 1);
//                }
//                calenderto.set(Calendar.DAY_OF_MONTH, dueDate);
//                diffDays = calenderFrom.getTimeInMillis() - calenderto.getTimeInMillis();
//                days = TimeUnit.MILLISECONDS.toDays(diffDays);
//                check = 1;
//            }
        //  }

        private void createNotification() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel("hello", "ha", NotificationManager.IMPORTANCE_DEFAULT);

            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "hello");
            builder.setContentText("adding date");
            builder.setContentTitle("Changing Date");
            builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
            builder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(notificationChannel);
            }

            startForeground(100, builder.build());
        }
    }
}
