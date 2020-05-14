package com.mytestings.skylinebroadband;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Database.Entity;

public class Adapter extends ListAdapter {
    MainActivity mainActivity;

    protected Adapter(MainActivity mainActivity) {
        super(Entity.diff);
        this.mainActivity = mainActivity;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Entity entity = (Entity) getCurrentList().get(position);
        ((viewholder) (holder)).phnNum.setText(entity.getPhone_number().toString());
        ((viewholder) (holder)).dueAmount.setText(String.valueOf(entity.getAmountDue()));
        ((viewholder) (holder)).name.setText(entity.getName());
        ((viewholder) (holder)).houseNum.setText(entity.getHouseNumber());

    }

    @Override
    public void submitList(@Nullable List list) {
        Log.d("searchdd", String.valueOf(list.size()));

        super.submitList(list != null ? new ArrayList<String>(list) : null);
    }

    @Override
    public int getItemCount() {
        return Math.max(getCurrentList().size(), 0);
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView houseNum;
        TextView dueAmount;
        TextView phnNum;
        TextView name;
        Button paynow;
        AlertDialog alertDialog;
        ConstraintLayout constraintLayout;

        viewholder(@NonNull View itemView) {
            super(itemView);
            houseNum = itemView.findViewById(R.id.createHouseNumber);
            dueAmount = itemView.findViewById(R.id.createDue);
            phnNum = itemView.findViewById(R.id.createphnNumber);
            name = itemView.findViewById(R.id.editName);
            paynow = itemView.findViewById(R.id.paynow);
            constraintLayout = itemView.findViewById(R.id.constraintRowLayoutRoot);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mainActivity, EditDetailsActivity.class);
                    Entity entity = (Entity) getCurrentList().get(getAdapterPosition());
                    intent.putExtra("name", entity.getName());
                    intent.putExtra("due", entity.getAmountDue());
                    intent.putExtra("hnum", entity.getHouseNumber());
                    intent.putExtra("lastpaid", entity.getPaid_date());
                    intent.putExtra("createdon", entity.getAccount_created_on());
                    intent.putExtra("phn", entity.getPhone_number());
                    intent.putExtra("monthlyfee", entity.getNetPayablePrice());
                    intent.putExtra("id", entity.getId());
                    intent.putExtra("referencekey", entity.getFirebaseReferenceKey());
                    mainActivity.startActivity(intent);
                }
            });

            paynow.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onClick(View v) {
                    final Entity entity = (Entity) getCurrentList().get(getAdapterPosition());
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    View view = mainActivity.getLayoutInflater().inflate(R.layout.dialoglayout, null);
                    builder.setView(view);
                    final EditText editText = (EditText) view.findViewById(R.id.editTextDialog);
                    final Button dismiss = view.findViewById(R.id.dismiss);
                    Button change = view.findViewById(R.id.changeDialog);
                    builder.setCancelable(false);
                    editText.requestFocus();
                    try {
                        Thread.sleep(100);
                        InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    } catch (InterruptedException e ) {
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setHint("Enter the amount paid");


//
                    change.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (editText.getText().toString().length() > 0) {
                                entity.setAmountPaid(Integer.parseInt(editText.getText().toString()));
                                if (entity.getAmountDue() - entity.getAmountPaid() >= 0)
                                    entity.setAmountDue(entity.getAmountDue() - entity.getAmountPaid());
                                else
                                    entity.setAmountDue(entity.getAmountPaid());
                                ViewModelProviders.of(mainActivity).get(MainViewModel.class).update(entity);
                                TransactionEntity transactionEntity = new TransactionEntity();
                                transactionEntity.setAmountPaid(entity.getAmountPaid());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd : MM : yyyy");
                                transactionEntity.setDatePaid(simpleDateFormat.format(Calendar.getInstance().getTimeInMillis()));
                                transactionEntity.setHnum(entity.getHouseNumber());
                                transactionEntity.setPhoneNumber(entity.getPhone_number());
                                ViewModelProviders.of(mainActivity).get(MainViewModel.class).insertinTransactionEntity(transactionEntity);
                                notifyItemChanged(getAdapterPosition());
                                alertDialog.dismiss();
                            }
                        }
                    });
                    dismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog = builder.show();

                }
            });
        }
    }
}
