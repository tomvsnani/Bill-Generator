package com.mytestings.skylinebroadband;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Database.Entity;
import Database.SkyDatabase;

public class EditDetailsActivity extends AppCompatActivity implements DatesetInterface {
    AlertDialog alertDialog;
    Handler handler;
    TextView name;
    TextView hNum;
    TextView phnNum;
    TextView due;
    TextView monthlyfee;
    TextView createdDate;
    TextView lastPaidOn;
    ImageView edithNum;
    ImageView editPhnNUm;
    ImageView editDue;
    ImageView editLastPaidOn;
    ImageView editmonthlyfee;
    Entity entity;
    MainViewModel mainViewModel;
    TransactionRowAdapter transactionRowAdapter;
    EditText editText;
    FrameLayout frameLayout;
    TextView viewTransactions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editdetailslayout);
        name = findViewById(R.id.editName);
        hNum = findViewById(R.id.newHouseNumber);
        phnNum = findViewById(R.id.newphnNumber);
        due = findViewById(R.id.newDue);
        lastPaidOn = findViewById(R.id.newAmountPaidDate);
        frameLayout = findViewById(R.id.container);
        editDue = findViewById(R.id.editDue);
        edithNum = findViewById(R.id.editHouseNum);
        editmonthlyfee = findViewById(R.id.editMonthlyfee);
        editLastPaidOn = findViewById(R.id.editLastPaidOn);
        editPhnNUm = findViewById(R.id.editPhoneNumber);
        monthlyfee = findViewById(R.id.payableAmount);
        createdDate = findViewById(R.id.accountcreatedon);
        viewTransactions = findViewById(R.id.display_transactions);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        transactionRowAdapter = new TransactionRowAdapter();
        entity = new Entity();
        handler = new Handler();
        String intentName = getIntent().getStringExtra("name");
        Long intentphnNum = getIntent().getLongExtra("phn", -1);
        int intentDue = getIntent().getIntExtra("due", -1);
        String intentHnum = getIntent().getStringExtra("hnum");
        String intentlastPaidOn = getIntent().getStringExtra("lastpaid");
        String account_CreatedOn = getIntent().getStringExtra("createdon");
        int monthlyFee = getIntent().getIntExtra("monthlyfee", -1);
        Long id = getIntent().getLongExtra("id", -1);
        entity.setHouseNumber(intentHnum);
        entity.setAmountDue(intentDue);
        entity.setPaid_date(intentlastPaidOn);
        entity.setPhone_number(intentphnNum);
        entity.setName(intentName);
        entity.setId(id);
        entity.setFirebaseReferenceKey(getIntent().getStringExtra("referencekey"));
        entity.setAccount_created_on(account_CreatedOn);
        entity.setNetPayablePrice(monthlyFee);
        name.setText(intentName);
        hNum.setText(intentHnum);
        phnNum.setText(intentphnNum.toString());
        due.setText(String.valueOf(intentDue));
        lastPaidOn.setText(String.valueOf(intentlastPaidOn));
        monthlyfee.setText(String.valueOf(monthlyFee));
        createdDate.setText(account_CreatedOn);
        SharedPreferences settings = getSharedPreferences("transactions", MODE_PRIVATE);
        if(settings.getBoolean(entity.getHouseNumber(),true)) {
            FirebaseDatabase.getInstance().getReference("transactions").child(entity.getFirebaseReferenceKey())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Log.d("datasnapshott", dataSnapshot1.toString());
                                TransactionEntity transactionEntity = dataSnapshot1.getValue(TransactionEntity.class);
                                mainViewModel.insertinTransactionEntity(transactionEntity);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        settings.edit().putBoolean(entity.getHouseNumber(),false).apply();
    }

    @Override
    protected void onStart() {
        super.onStart();


        editPhnNUm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog("phn");
            }
        });
        editDue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog("due");
            }
        });
        edithNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog("hnum");
            }
        });
        createdDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog("create");
            }
        });
        editmonthlyfee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createDialog("fee");
            }
        });
        editLastPaidOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePicker datePicker = new DatePicker(EditDetailsActivity.this);
                datePicker.show(getSupportFragmentManager(), "datepicker");
            }
        });

        viewTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(EditDetailsActivity.this,DialogFragmentt.class);
//                intent.putExtra("param1",entity.getHouseNumber());
//                startActivity(intent);


                AlertDialog.Builder builder = new AlertDialog.Builder(EditDetailsActivity.this);
                View view = getLayoutInflater().inflate(R.layout.transactionlayout, null);
                builder.setView(view);
                final RecyclerView recyclerView = view.findViewById(R.id.transactionrecyclerview);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EditDetailsActivity.this, RecyclerView.VERTICAL, false);
                transactionRowAdapter = new TransactionRowAdapter();
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setHasFixedSize(true);
                Log.d("hellok", "1");


                mainViewModel.getFullTransactionData(entity.getHouseNumber(), entity.getPhone_number()).observeForever(new Observer<List<TransactionEntity>>() {
                    @Override
                    public void onChanged(List<TransactionEntity> transactionEntities) {
                        Log.d("hellok", "2");
                        if (transactionEntities.size() > 0) {
                            recyclerView.setAdapter(transactionRowAdapter);
                            transactionRowAdapter.submitList(transactionEntities);

                        }
                    }
                });


                builder.show();


            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    void createDialog(final String type) {
        Log.d("hhhh", "hhh");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialoglayout, null);
        builder.setView(v);
        editText = (EditText) v.findViewById(R.id.editTextDialog);
        final Button dismiss = v.findViewById(R.id.dismiss);
        Button change = v.findViewById(R.id.changeDialog);
        builder.setCancelable(false);
        editText.requestFocus();

        changeInputTypeofEditText(type, editText);
        try {
            Thread.sleep(100);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


//
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString() != null && !editText.getText().toString().equals("")) {
                    switch (type) {

                        case "phn":
                            editText.setInputType(InputType.TYPE_CLASS_PHONE);
                            entity.setPhone_number(Long.valueOf(editText.getText().toString()));
                            mainViewModel.update(entity);
                            mainViewModel.setVlueinFirebase(entity, "update");
                            phnNum.setText(String.valueOf(entity.getPhone_number()));
                            alertDialog.dismiss();
                            break;


                        case "due":
//
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            int amountAfterpaying = entity.getAmountDue() - Integer.parseInt((editText.getText().toString()));

                            if (amountAfterpaying >= 0)
                                entity.setAmountDue(amountAfterpaying);
                            else
                                entity.setAmountDue(Integer.parseInt((editText.getText().toString())));

                            entity.setPaid_date(new SimpleDateFormat("dd:MM:yyyy").format(Calendar.getInstance().getTimeInMillis()));
                            mainViewModel.update(entity);
                            mainViewModel.setVlueinFirebase(entity, "update");
                            due.setText(String.valueOf(entity.getAmountDue()));
                            lastPaidOn.setText(entity.getPaid_date());
                            Toast.makeText(EditDetailsActivity.this, "Due is updated", Toast.LENGTH_SHORT).show();
                            sendPaymentMessage(editText);

                            TransactionEntity transactionEntity = new TransactionEntity();
                            transactionEntity.setAmountPaid(Integer.parseInt(editText.getText().toString()));
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd : MM : yyyy");
                            transactionEntity.setDatePaid(simpleDateFormat.format(Calendar.getInstance().getTimeInMillis()));
                            transactionEntity.setHnum(entity.getHouseNumber());
                            transactionEntity.setPhoneNumber(entity.getPhone_number());
                            mainViewModel.insertinTransactionEntity(transactionEntity);
                            FirebaseDatabase.getInstance().getReference("transactions").child(entity.getFirebaseReferenceKey())
                                    .push().setValue(transactionEntity);
//                           Log.d("transactionnn",key);
//                            FirebaseDatabase.getInstance().getReference("Users").child(entity.getFirebaseReferenceKey())
//                                    .child("transaction").child(key).setValue(transactionEntity);


                            alertDialog.dismiss();
                            break;


                        case "hnum":
                            editText.setInputType(InputType.TYPE_CLASS_TEXT);
                            entity.setHouseNumber(editText.getText().toString());
                            mainViewModel.update(entity);
                            mainViewModel.setVlueinFirebase(entity, "update");
                            alertDialog.dismiss();
                            break;


                        case "fee":
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            entity.setNetPayablePrice(Integer.parseInt(editText.getText().toString()));
                            mainViewModel.update(entity);
                            mainViewModel.setVlueinFirebase(entity, "update");
                            monthlyfee.setText(editText.getText().toString());
                            alertDialog.dismiss();

                            break;
                    }
                }

            }
        });


        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.show();

    }

    private void sendPaymentMessage(EditText editText) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(String.valueOf(entity.getPhone_number()), null,
                    "Dear Customer , You have paid " + editText.getText().toString() + " to Skyline BroadBand ."
                    , null, null);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            sendPaymentMessage(editText);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void changeInputTypeofEditText(String type, EditText editText) {
        switch (type) {
            case "phn":

                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                editText.setHint("Enter the new phone number");

                break;
            case "due":

                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setHint("Enter the Amount paid");

                break;
            case "hnum":

                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setHint("Enter the new House number");


                break;
            case "fee":

                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setHint("Enter the new monthly fees");


                break;
        }
    }

    @Override
    public void dateset(int year, int month, int dayofmonth) {
        if (entity != null) {
            Date simpleDateFormat = new Date();
            try {
                simpleDateFormat = new SimpleDateFormat("dd:MM:yyyy").parse(dayofmonth + ":" + (month + 1) + ":" + year);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            entity.setPaid_date(new SimpleDateFormat("dd:MM:yyyy").format(simpleDateFormat));
            mainViewModel.update(entity);
            mainViewModel.setVlueinFirebase(entity, "update");
            lastPaidOn.setText(new SimpleDateFormat("dd:MM:yyyy").format(simpleDateFormat));

        }
    }
}
