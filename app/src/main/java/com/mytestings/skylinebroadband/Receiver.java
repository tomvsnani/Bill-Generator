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
import java.util.Locale;
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
        int countamountinc = 0;
        int countruntimes = 0;
        public final String TAG = getClass().getSimpleName();

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
        public int onStartCommand(Intent intent, int flags, final int startId) {

            Log.d(TAG, "hellointent" + intent.toUri(0));

            if (intent.getAction() != null && (intent.getAction().equals("stop") || intent.getStringExtra("stop").equals("stop"))) {

                stopForeground(true);

                stopSelf();
            }

            String intentStringExtra = intent.getStringExtra("hello");


            if (intentStringExtra != null && !intentStringExtra.isEmpty())

                createNotification(intentStringExtra);

            final SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.ALARAMSSHAREDPREFERENCES, MODE_PRIVATE);

            Calendar alaramcalender = Calendar.getInstance();

            final String todaysDate = new SimpleDateFormat(AppConstants.dateFormat, Locale.getDefault()).format(alaramcalender.getTimeInMillis());

            final int todaysDay;

            todaysDay = Integer.parseInt(todaysDate.split("/")[0]);

            Log.d(TAG, "todaysDay " + todaysDay);

            sharedPreferences.edit().putBoolean(todaysDate, true).apply();

            skyDatabase = SkyDatabase.getInstance(getApplicationContext());


            try {
                final String lastFired = getSharedPreferences(AppConstants.ALARAMSSHAREDPREFERENCES, MODE_PRIVATE)
                        .getString(AppConstants.LASTFIREDVALUE,
                                todaysDate);


                Date lastFiredDate = new SimpleDateFormat(AppConstants.dateFormat, Locale.getDefault()).parse(lastFired);

                final int lastFiredDay = Integer.parseInt(new SimpleDateFormat("dd", Locale.getDefault()).format(lastFiredDate));
                int lastFiredMonth = Integer.parseInt(new SimpleDateFormat("MM", Locale.getDefault()).format(lastFiredDate));
                int lastFiredYear = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(lastFiredDate));

                Calendar maxDaysCalender = Calendar.getInstance();
                maxDaysCalender.set(Calendar.YEAR,lastFiredYear);
                if(lastFiredMonth>0)
                maxDaysCalender.set(Calendar.MONTH,lastFiredMonth-1);
                final int maxDays =maxDaysCalender.getActualMaximum(Calendar.DATE);
                Date date1 = new SimpleDateFormat(AppConstants.dateFormat, Locale.getDefault()).parse(lastFired);

                Date date2 = new SimpleDateFormat(AppConstants.dateFormat, Locale.getDefault()).parse(todaysDate);

                long differenecInDays = Math.abs(date1.getTime() - date2.getTime());

                final Long differenceInDays = differenecInDays / (1000 * 60 * 60 * 24);

                Log.d(TAG, "diffDays " + String.valueOf(differenceInDays) + "lastFired  " + lastFired);


                if (differenceInDays == 0) {

                    compareDay = Integer.parseInt(new SimpleDateFormat("dd", Locale.getDefault())
                            .format(Calendar.getInstance().getTimeInMillis()));

                    daysToIterate = daysToIterate + 1;
                }


                if (differenceInDays > 0) {

                    daysToIterate = differenceInDays.intValue();
                }

                FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Entity entity = dataSnapshot.getValue(Entity.class);
                        if (differenceInDays > 0) {

                            Log.d(TAG, "lastfired" + lastFired + "maxdays  " + maxDays);



                            if ((maxDays == 30 && lastFiredDay == 30) || (maxDays == 31 && lastFiredDay == 31) || (maxDays == 29 && lastFiredDay == 29))

                                compareDay = 1;

                            else

                                compareDay = lastFiredDay + 1;

                            Log.d(TAG, "lastFired " + lastFired + "dayToCompare " + compareDay);


                        }

                        Log.d(TAG, "counter " + (++countruntimes));

                        //list = skyDatabase.dao().getTotaldataNoLiveData();


                        if (dataSnapshot.getChildrenCount() > 0) {

                            Log.d(TAG, "lastfired" + lastFired + "firebaseChildrenCount   " + dataSnapshot.getChildrenCount());


                            Date dueDateOfPerson = null;

                            try {

                                dueDateOfPerson = new SimpleDateFormat(AppConstants.dateFormat, Locale.getDefault()).parse(entity.getAccountCreatedOn());
                            } catch (ParseException e) {

                                e.printStackTrace();
                            }


                            dueAmount = entity.getAmountDue();


                            if (dueDateOfPerson == null) {

                                Log.d(TAG, "dueDateNull" + entity.getName());
                            }

                            int dueDyOfPerson = Integer.parseInt(new SimpleDateFormat("dd", Locale.getDefault()).format(dueDateOfPerson));


                            sharedPreferences.edit().putString(AppConstants.LASTFIREDVALUE, todaysDate).apply();

                            FirebaseDatabase.getInstance().getReference().child("last").setValue(todaysDate);

                            for (int i = 0; i < daysToIterate; i++) {

                                Log.d(TAG, "compareDay " + String.valueOf(compareDay) + "differenceInDays  " + differenceInDays);

                                if (dueDyOfPerson ==
                                        compareDay) {

                                    Log.d(TAG, "countamountin " + (++countamountinc));

                                    Log.d(TAG, "sueAmount " + dueAmount + "increasedAmount  " + (dueAmount + entity.getNetPayablePrice()));

                                    dueAmount = dueAmount + entity.getNetPayablePrice();

                                    entity.setAmountDue(dueAmount);

                                    entity.setLastUpdatedOn(todaysDate);

                                    Log.d(TAG, "indue");

                                    Log.d(TAG, "dueAmountAfterUpdate " + String.valueOf(dueAmount));

                                    updateEntity(entity);

                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(entity.getFirebaseReferenceKey()).setValue(entity);


                                }
                                if (differenceInDays != 0) {

                                    if (compareDay == maxDays) {
                                        compareDay = 1;
                                    } else
                                        compareDay++;
                                }
//


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
                NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);

                notificationManager.createNotificationChannel(notificationChannel);


                notificationChannel.setDescription("Updating due amount ...");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);

            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "hello");
            builder.setContentText("Updating due amount");
            builder.setContentTitle("click here to remove notification");
            builder.setAutoCancel(true);
            builder.setContentText("hey");

            Intent stopSef = new Intent(this, CountDaysService.class);
            stopSef.setAction("stop");
            stopSef.putExtra("stop", "stop");
            PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSef, 0);
            builder.addAction(R.drawable.ic_search_black_24dp, "stop", pStopSelf);

            builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setColor
                    (ContextCompat.getColor(this, R.color.colorAccent));


            startForeground(1, builder.build());
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
