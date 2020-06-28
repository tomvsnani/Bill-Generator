package com.mytestings.skylinebroadband;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Database.Entity;
import Database.SkyDatabase;

public class Adapter extends ListAdapter implements Filterable {
    MainActivity mainActivity;
    List<Entity> fullList = new ArrayList<>();

    protected Adapter(MainActivity mainActivity) {
        super(Entity.diff);
        this.mainActivity = mainActivity;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewholder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Entity entity = (Entity) getCurrentList().get(position);
        if (entity.getPhoneNumber() != null)
            ((viewholder) (holder)).phnNum.setText(entity.getPhoneNumber());
        if (entity.getAmountDue() < 0)
            ((viewholder) (holder)).dueAmount.setText("excess amount  : " + String.valueOf((-1) * entity.getAmountDue()));
        else
            ((viewholder) (holder)).dueAmount.setText(String.valueOf(entity.getAmountDue()));
        ((viewholder) (holder)).name.setText(entity.getName());
        ((viewholder) (holder)).houseNum.setText(entity.getHouseNumber());

    }

    public void submit(List<Entity> list){
        fullList=list;
        submitList(list);
    }

    @Override
    public void submitList(@Nullable List list) {

        super.submitList(list != null ? new ArrayList<String>(list) : null);
    }

    @Override
    public int getItemCount() {
        return Math.max(getCurrentList().size(), 0);
    }

    private void sendPaymentMessage(EditText editText, Entity entity) throws MalformedURLException {
        String message = "Dear  Customer , You have paid " + editText.getText().toString()
                + " " + "to Skyline Broadband . Thank you";
        String s = "http://198.24.149.4/API/pushsms.aspx?loginID=chirukonda&password=sky@765";
        Uri uri = Uri.parse(s)
                .buildUpon()
                .appendQueryParameter("mobile", entity.getPhoneNumber().toString())
                .appendQueryParameter("text", message)
                .appendQueryParameter("route_id", String.valueOf(2))
                .appendQueryParameter("Unicode", String.valueOf(0))
                .appendQueryParameter("senderid", "SKYLIN")
                .build();

        try {
            URL url = new URL(uri.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();
            Log.d("responses", httpURLConnection.getResponseMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String s = constraint.toString().toLowerCase().trim();
               List<Entity> searchList = new ArrayList<>();
                if (s.length() == 0)
                    searchList .addAll(fullList);
                else{
                for (Object e : fullList) {

                    //     Log.d("phonenn", String.valueOf(e.getPhone_number().compareTo(Long.valueOf(s.toString()))==0));
                    if (((Entity) (e)).getName().toLowerCase().contains(s) ||
                            ((Entity) (e)).getPhoneNumber().contains(s)
                    ) {

                        searchList.add(((Entity) (e)));

                    }

                }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = searchList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                submitList((ArrayList<Entity>) results.values);
            }
        };
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
                    intent.putExtra("lastpaid", entity.getPaidDate());
                    intent.putExtra("createdon", entity.getAccountCreatedOn());
                    intent.putExtra("phn", entity.getPhoneNumber());
                    intent.putExtra("monthlyfee", entity.getNetPayablePrice());
                    intent.putExtra("id", entity.getId());
                    intent.putExtra("referencekey", entity.getFirebaseReferenceKey());
                    intent.putExtra("remarks", entity.getRemarks());
                    intent.putExtra("installation", entity.getInstallationAmount());
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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setHint("Enter the amount paid");


//
                    change.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int amountAfterpaying = 0;
                            if (editText.getText().toString().length() > 0) {


                                if (entity.getAmountDue() >= 0)

                                    amountAfterpaying = entity.getAmountDue() - Integer.parseInt((editText.getText().toString()));

                                else
                                    amountAfterpaying = entity.getAmountDue() - Integer.parseInt((editText.getText().toString()));


                                if (amountAfterpaying >= 0) {


                                    entity.setAmountDue(amountAfterpaying);
                                } else {

                                    entity.setAmountDue(amountAfterpaying);


                                }

                                MainViewModel mainViewModel = ViewModelProviders.of(mainActivity).get(MainViewModel.class);
                                entity.setPaidDate(new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTimeInMillis()));
                                mainViewModel.update(entity);
                                mainViewModel.setVlueinFirebase(entity, "update");
                                TransactionEntity transactionEntity = new TransactionEntity();
                                transactionEntity.setAmountPaid(Integer.parseInt(editText.getText().toString()));
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                transactionEntity.setTransactionId(entity.getHouseNumber() + Calendar.getInstance().getTimeInMillis());
                                transactionEntity.setUsername(entity.getName());
                                transactionEntity.setDatePaid(simpleDateFormat.format(Calendar.getInstance().getTimeInMillis()));
                                Log.d("extrassdate", simpleDateFormat.toString());
                                transactionEntity.setHnum(entity.getHouseNumber());
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            sendPaymentMessage(editText, entity);
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                thread.start();
                                transactionEntity.setPhoneNumber(entity.getPhoneNumber());
                                //  mainViewModel.insertinTransactionEntity(transactionEntity);

                                FirebaseDatabase.getInstance().getReference("transactions").child(entity.getFirebaseReferenceKey())
                                        .push().setValue(transactionEntity);
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
