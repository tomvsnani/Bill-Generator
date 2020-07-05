package com.mytestings.skylinebroadband;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Database.Entity;
import Database.SkyDatabase;

public class Receiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        Intent intent1 = new Intent(context, CountDaysService.class);
        intent1.putExtra("hello", intent.getStringExtra("hello"));
        Log.d("alaramreceived", "yes");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        }
    }

    public static class CountDaysService extends Service {
        NotificationChannel notificationChannel;
        SkyDatabase skyDatabase;
        List<Entity> list;
        String dueDate;
        int dueAmount;
        long compareDay = 0;
        int daysToIterate = 0;
        int countamountinc=0;
        int countruntimes=0;

        private void updateEntity(final Entity entity) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    skyDatabase.dao().update(entity);
                }
            });
            thread.start();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
             Log.d("hellointent", intent.toUri(0));
            if(intent.getAction()!=null && intent.getAction().equals("stop")) {
                Log.d("cancelservice","ss");
                Log.d("cancelser",intent.getAction());
                stopForeground(true);
                stopSelf();
            }
            String intentStringExtra = intent.getStringExtra("hello");
            final int maxDays = Calendar.getInstance().getActualMaximum(Calendar.DATE);

            if (intentStringExtra != null && !intentStringExtra.isEmpty())
                createNotification(intentStringExtra);
            final SharedPreferences sharedPreferences = getSharedPreferences("alarams", MODE_PRIVATE);
            Calendar alaramcalender = Calendar.getInstance();
            final String string = new SimpleDateFormat("dd/MM/yyyy").format(alaramcalender.getTimeInMillis());

            sharedPreferences.edit().putBoolean(string, true).apply();
            skyDatabase = SkyDatabase.getInstance(getApplicationContext());


            try {
                final String lastFired = getSharedPreferences("alarams", MODE_PRIVATE).getString("lastFired",
                        new SimpleDateFormat("dd/M/yyyy").format(Calendar.getInstance().getTimeInMillis()));
                Date lastFiredDate = new SimpleDateFormat("dd/M/yyyy").parse(lastFired);
                final int lastFiredDay = Integer.parseInt(new SimpleDateFormat("dd").format(lastFiredDate));

                Date date1 = new SimpleDateFormat("dd/M/yyyy").parse(lastFired);
                Date date2 = new SimpleDateFormat("dd/M/yyyy").parse(new SimpleDateFormat("dd/M/yyyy").
                        format(Calendar.getInstance().getTimeInMillis()));
                long differenecInDays = Math.abs(date1.getTime() - date2.getTime());
                final Long days = differenecInDays / (1000 * 60 * 60 * 24);
                Log.d("yessdays", String.valueOf(days) + "  " + lastFired);


             FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(new ChildEventListener() {
                 @Override
                 public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                     Entity entity=dataSnapshot.getValue(Entity.class);
                     Log.d("yessdays","count "+(++countruntimes));
                     list = skyDatabase.dao().getTotaldataNoLiveData();
                     if (days == 0) {
                         compareDay = Integer.valueOf(new SimpleDateFormat("dd")
                                 .format(Calendar.getInstance().getTimeInMillis()));
                         daysToIterate = daysToIterate + 1;
                     }

                     if (dataSnapshot.getChildrenCount() > 0) {

                             Log.d("alaramfired", lastFired+"   "+ dataSnapshot.getChildrenCount());
                             if (days > 0) {
                                 Log.d("alaramfired", lastFired+"  "+entity.getName());
                                 if(maxDays==30)
                                 compareDay = 1;
                                 else
                                     compareDay=lastFiredDay+1;
                                 Log.d("alaramfired", lastFired + " " + compareDay);
                                 daysToIterate = days.intValue();
                             }
                             dueDate = entity.getAccountCreatedOn();
                             dueAmount = entity.getAmountDue();


                         Date date = null;
                         try {
                             date = new SimpleDateFormat("dd/MM/yyyy").parse(dueDate);
                         } catch (ParseException e) {
                             e.printStackTrace();
                         }

                         int day = Integer.parseInt(new SimpleDateFormat("dd").format(date));

                             sharedPreferences.edit().putString("lastFired", string).apply();
                             for (int i = 0; i < daysToIterate; i++) {

                                 Log.d("alaramentered", String.valueOf(compareDay) + "  " + days);

                                 if ( day ==
                                         compareDay) {
                                     Log.d("yessdays","countamountin "+(++countamountinc));
                                     Log.d("dueamountt", dueAmount + "  " + (dueAmount + entity.getNetPayablePrice()));

                                     dueAmount = dueAmount + entity.getNetPayablePrice();

                                     entity.setAmountDue(dueAmount);

                                     entity.setLastUpdatedOn(string);

                                     Log.d("alaramenteredd", "indue");
                                     Log.d("alaramenteredd", String.valueOf(dueAmount));
                                     updateEntity(entity);

                                     FirebaseDatabase.getInstance().getReference("Users")
                                             .child(entity.getFirebaseReferenceKey()).setValue(entity);


                                 } //else stopSelf();
                             //    Log.d("formatt", String.valueOf(entity.getId()) + "  " + list.get(list.size() - 1).getId());

                                 if (days != 0) {
                                     if (compareDay == maxDays)
                                         compareDay = 1;
                                     else
                                         compareDay++;
                                 }
//                                 if () {
//                                     stopSelf();
//                                 }


                             }


                     } else stopSelf();
                 }

                 @Override
                 public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                 }

                 @Override
                 public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                 }

                 @Override
                 public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
            } catch (ParseException e) {
                e.printStackTrace();
            }


            return START_NOT_STICKY;
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

        private void createNotification(String s) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel("hello", "Due date update ", NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.setDescription("Updating due amount ...");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);

            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "hello");
            builder.setContentText("Updating due amount");
            builder.setContentTitle("click here to remove notification");
            builder.setAutoCancel(true);
            builder.setContentText("hey");

            Intent stopSef = new Intent(this, Receiver.class);
            stopSef.setAction("stop");
            PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSef,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_search_black_24dp, "stop", pStopSelf);

            builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setColor
                    (ContextCompat.getColor(this, R.color.colorAccent));


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                startForeground(1, builder.build());
            }
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
