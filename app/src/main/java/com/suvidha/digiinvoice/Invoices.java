package com.suvidha.digiinvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifImageView;

public class Invoices extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Context context = this;
    List<ItemInvoice> itemInvoiceList;
    RecyclerView recyclerView_invoices;
    RVAdapter_Invoice rvAdapter_invoice;
    LinearLayoutManager linearLayoutManager;
    Button button_add_invoices,button_select_date;
    TextView textView_date;
    String pwd_dbms;
    GifImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //showShopDialog();
        Intent intent = getIntent();
        pwd_dbms = intent.getStringExtra("pwdDBMS");
        Log.e("Tag",pwd_dbms);

        firebaseDatabase = FirebaseDatabase.getInstance();
        itemInvoiceList = new ArrayList<>();
        recyclerView_invoices = findViewById(R.id.invoices_rv);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_invoices.setLayoutManager(linearLayoutManager);
        button_add_invoices = findViewById(R.id.invoices_btn_add);
        button_select_date = findViewById(R.id.invoice_btn_select_date);
        textView_date = findViewById(R.id.invoice_et_date);
        imageView = findViewById(R.id.invoices_iv_cart);

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        }, PackageManager.PERMISSION_GRANTED);

        button_select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog;
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, final int y, final int m, final int d) {
                        final String[] months = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                        String dateToday = new SimpleDateFormat("dd MM yyyy").format(Calendar.getInstance().getTime());
                        String dateSelected = d + " " + Integer.toString((m+1)) +" " + y;
                        long days = getDifferenceDays(dateToday,dateSelected);
                        if(days<=7) {
                            String iDate = d + " " + months[m] + " " + y;
                            textView_date.setText(iDate);
                            databaseReference = firebaseDatabase.getReference("Invoices").child(iDate);
                            itemInvoiceList.clear();
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    imageView.setVisibility(View.INVISIBLE);
                                    itemInvoiceList.clear();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        ItemInvoice itemInvoice = dataSnapshot.getValue(ItemInvoice.class);
                                        itemInvoiceList.add(itemInvoice);
                                    }
                                    rvAdapter_invoice = new RVAdapter_Invoice(itemInvoiceList, context);
                                    recyclerView_invoices.setAdapter(rvAdapter_invoice);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else
                        {
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_pwd);
                            final EditText editText_pwd;
                            Button button_login;
                            editText_pwd = dialog.findViewById(R.id.dialog_et_pwd);
                            button_login = dialog.findViewById(R.id.dialog_btn_login);

                            button_login.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(!editText_pwd.getText().toString().trim().isEmpty())
                                    {
                                        if(editText_pwd.getText().toString().trim().equals(pwd_dbms))
                                        {
                                            dialog.dismiss();
                                            String iDate = d + " " + months[m] + " " + y;
                                            textView_date.setText(iDate);
                                            databaseReference = firebaseDatabase.getReference("Invoices").child(iDate);
                                            itemInvoiceList.clear();
                                            databaseReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    itemInvoiceList.clear();
                                                    imageView.setVisibility(View.INVISIBLE);
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        ItemInvoice itemInvoice = dataSnapshot.getValue(ItemInvoice.class);
                                                        itemInvoiceList.add(itemInvoice);
                                                    }
                                                    rvAdapter_invoice = new RVAdapter_Invoice(itemInvoiceList, context);
                                                    recyclerView_invoices.setAdapter(rvAdapter_invoice);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                        else
                                        {
                                            dialog.dismiss();
                                            Toast.makeText(context,"You are Not Authorised",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });

                            dialog.show();

                        }
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
        button_add_invoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkConnection())
                startActivity(new Intent(Invoices.this,Home.class));
                else
                    Toast.makeText(context,"Turn on Internet First",Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
    public static long getDifferenceDays(String d1, String d2) {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        Date date1 = null;
        try {
            date1 = myFormat.parse(d1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = myFormat.parse(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = date1.getTime() - date2.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    final int shopDialogStart = 100;
    TableRow.LayoutParams layoutParams;
    TableRow.LayoutParams layoutParamsCenter;
    TableLayout tableLayout_shopDetails;
    List<ItemShop> itemShopList = new ArrayList<>();
    private void showShopDialog()
    {

        final Dialog dialogShop = new Dialog(context);
        dialogShop.setContentView(R.layout.dialog_shop_details);
        dialogShop.setCanceledOnTouchOutside(false);

        layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
        layoutParamsCenter = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
        layoutParamsCenter.setMargins(6,0,6,12);

        tableLayout_shopDetails = dialogShop.findViewById(R.id.dialog_shop_table_details);
        tableLayout_shopDetails.setStretchAllColumns(true);

        TableRow tableRow_heading = new TableRow(context);
        tableRow_heading.setBackgroundResource(R.color.colorPrimary);
        tableRow_heading.setLayoutParams(layoutParams);

        TextView textView_heading_action,textView_heading_shopname,textView_heading_landmark;

        textView_heading_action = new TextView(context);
        textView_heading_action.setLayoutParams(layoutParams);
        textView_heading_action.setText("Action");
        textView_heading_action.setGravity(Gravity.CENTER);
        textView_heading_action.setTextColor(Color.WHITE);
        //textView_heading_action.setBackgroundResource(R.color.colorPrimary);
        textView_heading_action.setPadding(12,36,12,36);

        textView_heading_shopname = new TextView(context);
        textView_heading_shopname.setLayoutParams(layoutParamsCenter);
        textView_heading_shopname.setText("Shop Name");
        textView_heading_shopname.setGravity(Gravity.CENTER);
        textView_heading_shopname.setTextColor(Color.WHITE);
        //textView_heading_shopname.setBackgroundResource(R.color.colorPrimary);
        textView_heading_shopname.setPadding(12,36,12,36);


        textView_heading_landmark = new TextView(context);
        textView_heading_landmark.setLayoutParams(layoutParams);
        textView_heading_landmark.setText("Landmark");
        textView_heading_landmark.setGravity(Gravity.LEFT);
        textView_heading_landmark.setTextColor(Color.WHITE);
        //textView_heading_landmark.setBackgroundResource(R.color.colorPrimary);
        textView_heading_landmark.setPadding(12,36,12,36);

        tableRow_heading.addView(textView_heading_action);
        tableRow_heading.addView(textView_heading_shopname);
        tableRow_heading.addView(textView_heading_landmark);

        tableLayout_shopDetails.addView(tableRow_heading,layoutParams);

        DatabaseReference databaseReference_shops = FirebaseDatabase.getInstance().getReference("Shops");

        databaseReference_shops.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemShopList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    ItemShop itemShop = dataSnapshot.getValue(ItemShop.class);
                    itemShopList.add(itemShop);
                }

                dialogShop.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        EditText editText_shopName = dialogShop.findViewById(R.id.dialog_shop_et_name);

        editText_shopName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ci = 0;
                if(!charSequence.toString().isEmpty())
                {
                    tableLayout_shopDetails.removeAllViews();
                    tableLayout_shopDetails.addView(addHeading(),layoutParams);
                    for(ItemShop shop:itemShopList)
                    {
                        if(shop.getShopName().toLowerCase().contains(charSequence.toString().toLowerCase()))
                        {
                            TableRow tableRow_data = new TableRow(context);
                            if(ci%2!=0)
                            {
                                tableRow_data.setBackgroundColor(Color.parseColor("#efefef"));
                            }
                            ci+=1;
                            tableRow_data.setLayoutParams(layoutParams);

                            TextView textView_data_shopname, textView_data_landmark;

                            MaterialButton button_data_action;

                            button_data_action = new MaterialButton(context);
                            layoutParams.setMargins(12,0,12,0);
                            TableRow.LayoutParams layoutParams_btn = new TableRow.LayoutParams(150,TableRow.LayoutParams.WRAP_CONTENT);
                            layoutParams_btn.setMargins(24,0,24,0);
                            button_data_action.setLayoutParams(layoutParams_btn);
                            button_data_action.setText("चुनें");
                            button_data_action.setGravity(Gravity.CENTER);
                            //button_data_action.setPadding(12,12,12,12);

                            textView_data_shopname = new TextView(context);
                            textView_data_shopname.setLayoutParams(layoutParamsCenter);
                            textView_data_shopname.setText(shop.getShopName());
                            textView_data_shopname.setGravity(Gravity.LEFT);
                            textView_data_shopname.setPadding(12,24,12,24);

                            textView_data_landmark = new TextView(context);
                            textView_data_landmark.setLayoutParams(layoutParams);
                            textView_data_landmark.setText(shop.getShopLandMark());
                            textView_data_landmark.setGravity(Gravity.LEFT);
                            textView_data_landmark.setPadding(12,24,12,24);

                            tableRow_data.addView(button_data_action);
                            tableRow_data.addView(textView_data_shopname);
                            tableRow_data.addView(textView_data_landmark);
                            tableLayout_shopDetails.addView(tableRow_data,layoutParams);
                        }
                    }
                }
                else
                {
                    tableLayout_shopDetails.removeAllViews();
                    tableLayout_shopDetails.addView(addHeading(),layoutParams);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private TableRow addHeading()
    {
        TableRow tableRow_heading = new TableRow(context);
        tableRow_heading.setBackgroundResource(R.color.colorPrimary);
        tableRow_heading.setLayoutParams(layoutParams);

        TextView textView_heading_action,textView_heading_shopname,textView_heading_landmark;

        textView_heading_action = new TextView(context);
        textView_heading_action.setLayoutParams(layoutParams);
        textView_heading_action.setText("Action");
        textView_heading_action.setGravity(Gravity.CENTER);
        textView_heading_action.setTextColor(Color.WHITE);
        //textView_heading_action.setBackgroundResource(R.color.colorPrimary);
        textView_heading_action.setPadding(12,36,12,36);

        textView_heading_shopname = new TextView(context);
        textView_heading_shopname.setLayoutParams(layoutParamsCenter);
        textView_heading_shopname.setText("Shop Name");
        textView_heading_shopname.setGravity(Gravity.CENTER);
        textView_heading_shopname.setTextColor(Color.WHITE);
        //textView_heading_shopname.setBackgroundResource(R.color.colorPrimary);
        textView_heading_shopname.setPadding(12,36,12,36);


        textView_heading_landmark = new TextView(context);
        textView_heading_landmark.setLayoutParams(layoutParams);
        textView_heading_landmark.setText("Landmark");
        textView_heading_landmark.setGravity(Gravity.LEFT);
        textView_heading_landmark.setTextColor(Color.WHITE);
        //textView_heading_landmark.setBackgroundResource(R.color.colorPrimary);
        textView_heading_landmark.setPadding(12,36,12,36);

        tableRow_heading.addView(textView_heading_action);
        tableRow_heading.addView(textView_heading_shopname);
        tableRow_heading.addView(textView_heading_landmark);

        return tableRow_heading;
    }

}