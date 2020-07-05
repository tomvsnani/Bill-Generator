package com.mytestings.skylinebroadband;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import Database.Entity;
import Database.SkyDatabase;

public class CreateNewUser extends AppCompatActivity {
    EditText name;
    EditText hNum;
    EditText phnNum;
    EditText due;
    EditText monthlyfee;
    EditText installationAmount;
    EditText remarks;

    Button createUserButton;
    Entity entity;
    MainViewModel mainViewModel;
    ScrollView scrollView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        name = findViewById(R.id.createUsername);
        hNum = findViewById(R.id.createHouseNumber);
        phnNum = findViewById(R.id.createphnNumber);
        due = findViewById(R.id.createDue);
        monthlyfee = findViewById(R.id.createMonthlyfee);
        createUserButton = findViewById(R.id.createNewUserButton);
        scrollView = findViewById(R.id.scrollview);
        installationAmount=findViewById(R.id.installationfee);
        remarks=findViewById(R.id.remarks);
        entity = new Entity();
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        phnNum.setInputType(InputType.TYPE_CLASS_PHONE);

        due.setInputType(InputType.TYPE_CLASS_NUMBER);

        hNum.setInputType(InputType.TYPE_CLASS_TEXT);
        monthlyfee.setInputType(InputType.TYPE_CLASS_NUMBER);
        installationAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString().length() > 0 && hNum.getText().toString().length() > 0
                        && phnNum.getText().toString().length() > 0 && monthlyfee.getText().toString().length() > 0
                 && installationAmount.getText().toString().length()>0) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            entity.setName(name.getText().toString());
                            entity.setAccountCreatedOn(getDateandTime());
                            entity.setHouseNumber(hNum.getText().toString());
                            entity.setNetPayablePrice(Integer.parseInt(monthlyfee.getText().toString()));
                            entity.setPhoneNumber(Long.valueOf(phnNum.getText().toString()));
                            entity.setInstallationAmount(Integer.valueOf(installationAmount.getText().toString()));
                            entity.setAmountDue(Integer.parseInt(due.getText().toString()));
                            entity.setRemarks(remarks.getText().toString());
                            if (entity.getAmountDue() == 0) {
                                TransactionEntity transactionEntity = new TransactionEntity();
                                transactionEntity.setAmountPaid(entity.getNetPayablePrice());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                transactionEntity.setDatePaid(simpleDateFormat.format(Calendar.getInstance().getTimeInMillis()));
                                transactionEntity.setHnum(entity.getHouseNumber());
                                transactionEntity.setTransactionId(entity.getHouseNumber()+Calendar.getInstance().getTimeInMillis());
                                transactionEntity.setPhoneNumber(entity.getPhoneNumber());
                                mainViewModel.insertinTransactionEntity(transactionEntity);
                                 }
                            mainViewModel.insert(entity);
                            Long id = mainViewModel.getEntityid();
                            entity.setId(id);

                            if (id != null) {
                                mainViewModel.setVlueinFirebase(entity, "insert");
                                Log.d("entityid", String.valueOf(id));
                            }
                            finish();
                        }
                    });
                    thread.start();

                } else {
                    Toast.makeText(CreateNewUser.this, "Please enter all the required details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getDateandTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd : MM : yyyy");
        return simpleDateFormat.format(Calendar.getInstance().getTimeInMillis());
    }

    private void scrolldown() {
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
                int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
                int sy = scrollView.getScrollY();
                int sh = scrollView.getHeight();
                int delta = bottom - (sy + sh);

                scrollView.smoothScrollTo(0, delta);
            }
        }, 10);
    }

    public void initiateScroll(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //   if(event.getAction()==MotionEvent.ACTION_DOWN)
                //scrolldown();
                return false;
            }
        });
    }
}
