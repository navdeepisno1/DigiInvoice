package com.suvidha.digiinvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Home extends AppCompatActivity implements View.OnClickListener{
    Button button_add_item,button_add_date,button_save,button_search_shop;
    Context context = this;
    String url_rate,billNumber;
    Spinner spinner_rates;
    Dialog dialog_item;
    EditText editText_phone1,editText_phone2,editText_phone3,editText_landmark,editText_shopkeepername;
    AutoCompleteTextView autoCompleteTextView_shop_name;
    RecyclerView recyclerView_items;
    List<ItemValues> itemValuesList;
    List<String> mobileNumbers,itemName,shopNames;
    HashMap<String,ItemValues> stringItemValuesHashMap;
    HashMap<String, ItemShop> stringItemShopHashMap;
    DatePickerDialog datePickerDialog;
    TextView editText_date,textView_bno,textView_pdf_upload;
    ItemInvoice itemInvoice;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceShops,databaseReferenceInvoices;
    StorageReference storageReference;
    File filePath;
    ProgressBar progressBar_pdf_upload;
    boolean isShopSelectedFromDatabase=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        itemInvoice = new ItemInvoice();

        button_search_shop = findViewById(R.id.home_btn_search_shop);
        button_add_item = findViewById(R.id.home_btn_add);
        button_add_date = findViewById(R.id.home_btn_select_date);
        editText_landmark = findViewById(R.id.home_et_landmark);
        editText_phone1 = findViewById(R.id.home_et_mobile1);
        editText_phone2 = findViewById(R.id.home_et_mobile2);
        editText_phone3 = findViewById(R.id.home_et_mobile3);
        editText_shopkeepername = findViewById(R.id.home_et_name);
        autoCompleteTextView_shop_name = findViewById(R.id.home_autocomplete);
        recyclerView_items = findViewById(R.id.home_rv_items);
        editText_date = findViewById(R.id.home_et_date);
        button_save = findViewById(R.id.home_btn_save);
        progressBar_pdf_upload = findViewById(R.id.home_progress_pdf);
        textView_pdf_upload = findViewById(R.id.home_tv_pdf);
        textView_bno = findViewById(R.id.home_tv_bno);

        shopNames = new ArrayList<>();
        stringItemShopHashMap = new HashMap<String,ItemShop>();
        itemValuesList = new ArrayList<ItemValues>();
        stringItemValuesHashMap = new HashMap<String, ItemValues>();
        itemName = new ArrayList<String>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceShops = firebaseDatabase.getReference("Shops");
        databaseReferenceInvoices = firebaseDatabase.getReference("Invoices");
        storageReference = FirebaseStorage.getInstance().getReference();

        mobileNumbers = new ArrayList<>();
        progressBar_pdf_upload.setVisibility(View.GONE);
        textView_pdf_upload.setAlpha(0);
        billNumber = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        textView_bno.setText(billNumber);

        button_add_item.setOnClickListener(this);
        button_add_date.setOnClickListener(this);
        button_save.setOnClickListener(this);

        dialog_item = new Dialog(context);
        dialog_item.setContentView(R.layout.dialog_add_item);
        spinner_rates = dialog_item.findViewById(R.id.dialog_add_item_item_name);
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        }, PackageManager.PERMISSION_GRANTED);

        button_search_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShopDialog();
            }
        });

        url_rate = "https://script.google.com/macros/s/AKfycbyYztNRsXSwrlDX46UADvUmqXRKKuRkcW96JnTrOPosyeV8n2Y/exec?action=getRateList";
        getRates(url_rate);
    }

    ItemValues itemValues;

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.home_btn_add:
            {
                itemValues = new ItemValues();
                final EditText d_quantity,d_unitprice,d_total,d_discount;
                final RadioButton radioButton_piece,radioButton_bags,radioButton_kg,radioButton_gms;
                radioButton_bags = dialog_item.findViewById(R.id.dialog_add_item_radio_bag);
                radioButton_piece = dialog_item.findViewById(R.id.dialog_add_item_radio_piece);
                radioButton_kg = dialog_item.findViewById(R.id.dialog_add_item_radio_kg);
                radioButton_gms = dialog_item.findViewById(R.id.dialog_add_item_radio_gm);
                d_quantity = dialog_item.findViewById(R.id.dialog_add_item_quantity);
                d_unitprice = dialog_item.findViewById(R.id.dialog_add_item_unit_price);
                d_total = dialog_item.findViewById(R.id.dialog_add_item_total_price);
                d_discount = dialog_item.findViewById(R.id.dialog_add_item_discount);
                d_quantity.setText("");
                d_total.setText("");
                d_unitprice.setText("");
                d_discount.setText("0");

                spinner_rates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        d_total.setText("");
                        d_quantity.setText("");
                        d_discount.setText("0");
                        itemValues.setValueName(adapterView.getItemAtPosition(i).toString());
                        itemValues.setValueUnitPriceUnit(stringItemValuesHashMap.get(adapterView.getItemAtPosition(i).toString()).getValueUnitPriceUnit());
                        itemValues.setValueUnitPrice(stringItemValuesHashMap.get(adapterView.getItemAtPosition(i).toString()).getValueUnitPrice());
                        itemValues.setDiscount("0");

                        if(itemValues.getValueUnitPriceUnit().equals("Kg"))
                        {
                            radioButton_bags.setChecked(false);
                            radioButton_piece.setChecked(false);
                            radioButton_kg.setChecked(true);
                            radioButton_kg.setEnabled(true);
                            radioButton_gms.setEnabled(true);
                            radioButton_bags.setEnabled(false);
                            radioButton_piece.setEnabled(false);
                        }
                        else if(itemValues.getValueUnitPriceUnit().equals("Bag"))
                        {
                            radioButton_bags.setChecked(true);
                            radioButton_bags.setEnabled(true);
                            radioButton_gms.setEnabled(false);
                            radioButton_kg.setEnabled(false);
                            radioButton_piece.setEnabled(false);
                        }
                        else if(itemValues.getValueUnitPriceUnit().equals("piece"))
                        {
                            radioButton_piece.setChecked(true);
                            radioButton_piece.setEnabled(true);
                            radioButton_gms.setEnabled(false);
                            radioButton_kg.setEnabled(false);
                            radioButton_bags.setEnabled(false);
                        }
                        d_unitprice.setText(itemValues.getValueUnitPrice());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                d_discount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(charSequence.toString().equals("."))
                        {
                            d_discount.setText("");
                            return;
                        }
                        if(!d_quantity.getText().toString().isEmpty() && !d_unitprice.getText().toString().isEmpty() && !d_discount.getText().toString().isEmpty()) {
                            if (radioButton_bags.isChecked() || radioButton_piece.isChecked() || radioButton_kg.isChecked()) {
                                float uunit = Float.parseFloat(d_quantity.getText().toString().trim());
                                float udiscount = Float.parseFloat(d_discount.getText().toString().trim());
                                float UnitPrice = Float.parseFloat(d_unitprice.getText().toString().trim());
                                itemValues.setDiscount(d_discount.getText().toString().trim());
                                d_total.setText("" + ((UnitPrice * uunit)-udiscount));
                            } else if(radioButton_gms.isChecked()) {
                                float uunit = Float.parseFloat(d_quantity.getText().toString().trim());
                                float udiscount = Float.parseFloat(d_discount.getText().toString().trim());
                                float UnitPrice = Float.parseFloat(d_unitprice.getText().toString().trim());
                                itemValues.setDiscount(d_discount.getText().toString().trim());
                                d_total.setText("" + ((UnitPrice * uunit / 1000)-udiscount));
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                d_quantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(charSequence.toString().equals("."))
                        {
                            d_quantity.setText("");
                            return;
                        }
                        if(!d_quantity.getText().toString().isEmpty() && !d_unitprice.getText().toString().isEmpty() && !d_discount.getText().toString().isEmpty()) {
                            if (radioButton_bags.isChecked() || radioButton_piece.isChecked() || radioButton_kg.isChecked()) {
                                float uunit = Float.parseFloat(d_quantity.getText().toString().trim());
                                float udiscount = Float.parseFloat(d_discount.getText().toString().trim());
                                float UnitPrice = Float.parseFloat(d_unitprice.getText().toString().trim());
                                itemValues.setValueqty(d_quantity.getText().toString().trim());
                                d_total.setText("" + ((UnitPrice * uunit)-udiscount));
                            } else if(radioButton_gms.isChecked()) {
                                float uunit = Float.parseFloat(d_quantity.getText().toString().trim());
                                float udiscount = Float.parseFloat(d_discount.getText().toString().trim());
                                float UnitPrice = Float.parseFloat(d_unitprice.getText().toString().trim());
                                itemValues.setValueqty(d_quantity.getText().toString().trim());
                                d_total.setText("" + ((UnitPrice * uunit / 1000)-udiscount));
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                d_unitprice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(charSequence.toString().equals("."))
                        {
                            d_unitprice.setText("");
                            return;
                        }
                        if(!d_quantity.getText().toString().isEmpty() && !d_unitprice.getText().toString().isEmpty() && !d_discount.getText().toString().isEmpty()) {
                            if (radioButton_bags.isChecked() || radioButton_piece.isChecked() || radioButton_kg.isChecked()) {
                                float uunit = Float.parseFloat(d_quantity.getText().toString().trim());
                                float udiscount = Float.parseFloat(d_discount.getText().toString().trim());
                                float UnitPrice = Float.parseFloat(d_unitprice.getText().toString().trim());
                                itemValues.setValueUnitPrice(d_unitprice.getText().toString().trim());
                                d_total.setText("" + ((UnitPrice * uunit)-udiscount));
                            } else if(radioButton_gms.isChecked()) {
                                float uunit = Float.parseFloat(d_quantity.getText().toString().trim());
                                float udiscount = Float.parseFloat(d_discount.getText().toString().trim());
                                float UnitPrice = Float.parseFloat(d_unitprice.getText().toString().trim());
                                itemValues.setValueUnitPrice(d_unitprice.getText().toString().trim());
                                d_total.setText("" + ((UnitPrice * uunit / 1000)-udiscount));
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                radioButton_gms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(d_quantity.getText().toString().isEmpty() || d_unitprice.getText().toString().isEmpty() || d_discount.getText().toString().trim().isEmpty())
                        {
                            return;
                        }
                        if(radioButton_bags.isChecked() || radioButton_piece.isChecked() || radioButton_kg.isChecked())
                        {
                            float uunit = Float.parseFloat(d_quantity.getText().toString().trim());
                            float udiscount = Float.parseFloat(d_discount.getText().toString().trim());
                            float UnitPrice = Float.parseFloat(d_unitprice.getText().toString().trim());
                            d_total.setText("" + ((UnitPrice * uunit)-udiscount));
                        }
                        else if(radioButton_gms.isChecked())
                        {
                            float uunit = Float.parseFloat(d_quantity.getText().toString().trim());
                            float udiscount = Float.parseFloat(d_discount.getText().toString().trim());
                            float UnitPrice = Float.parseFloat(d_unitprice.getText().toString().trim());
                            d_total.setText("" + ((UnitPrice * uunit / 1000)-udiscount));
                        }
                    }
                });
                radioButton_kg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(d_quantity.getText().toString().isEmpty() || d_unitprice.getText().toString().isEmpty() || d_discount.getText().toString().trim().isEmpty())
                        {
                            return;
                        }
                        if(radioButton_bags.isChecked() || radioButton_piece.isChecked() || radioButton_kg.isChecked())
                        {
                            float uunit = Float.parseFloat(d_quantity.getText().toString().trim());
                            float udiscount = Float.parseFloat(d_discount.getText().toString().trim());
                            float UnitPrice = Float.parseFloat(d_unitprice.getText().toString().trim());
                            d_total.setText("" + ((UnitPrice * uunit)-udiscount));
                        }
                        else if(radioButton_gms.isChecked())
                        {
                            float uunit = Float.parseFloat(d_quantity.getText().toString().trim());
                            float udiscount = Float.parseFloat(d_discount.getText().toString().trim());
                            float UnitPrice = Float.parseFloat(d_unitprice.getText().toString().trim());
                            d_total.setText("" + ((UnitPrice * uunit / 1000)-udiscount));
                        }
                    }
                });

                Button button_addItem = dialog_item.findViewById(R.id.dialog_add_item_button_add);
                button_addItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(d_quantity.getText().toString().isEmpty() || d_unitprice.getText().toString().isEmpty() || d_total.getText().toString().isEmpty() || d_discount.getText().toString().trim().isEmpty())
                        {
                            Toast.makeText(Home.this,"Please fill required all fields",Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(radioButton_piece.isChecked()) itemValues.setValueItemUnit("0");
                        else if(radioButton_bags.isChecked()) itemValues.setValueItemUnit("1");
                        else if(radioButton_kg.isChecked()) itemValues.setValueItemUnit("2");
                        else if(radioButton_gms.isChecked()) itemValues.setValueItemUnit("3");

                        itemValues.setTotalprice(d_total.getText().toString().trim());
                        itemValuesList.add(itemValues);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                        recyclerView_items.setLayoutManager(linearLayoutManager);
                        RVAdaper_Items rvAdaper_items = new RVAdaper_Items(context,itemValuesList,itemName,stringItemValuesHashMap);
                        recyclerView_items.setAdapter(rvAdaper_items);
                        dialog_item.dismiss();
                    }
                });
                dialog_item.show();
                break;
            }
            case R.id.home_btn_select_date:
            {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        String[] months = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                        String iDate;
                        iDate = d + " " + months[m] + " " + y;
                        itemInvoice.setInvoiceDate(iDate);
                        editText_date.setText(iDate);
                    }
                },year,month,day);
                datePickerDialog.show();
                break;
            }
            case R.id.home_btn_save:
            {
                mobileNumbers.clear();
                if(editText_date.getText().toString().isEmpty())
                {
                    showToast("Select Date");
                    return;
                }
                if(autoCompleteTextView_shop_name.getText().toString().isEmpty())
                {
                    showToast("शॉप का नाम भरें");
                    return;
                }
                if(editText_shopkeepername.getText().toString().trim().isEmpty())
                {
                    showToast("दुकानदार का नाम भरें");
                    return;
                }
                if(editText_phone1.getText().toString().isEmpty())
                {
                    showToast("मोबाइल नंबर भरें");
                    return;
                }
                if(editText_phone1.length()!=10)
                {
                    showToast("सही मोबाइल नंबर भरें");
                    return;
                }
                else
                {
                    mobileNumbers.add(editText_phone1.getText().toString().trim());
                }
                if(!editText_phone2.getText().toString().isEmpty())
                {
                    if(editText_phone2.getText().toString().length()==10)
                    {
                        mobileNumbers.add(editText_phone2.getText().toString().trim());
                    }
                    else
                    {
                        showToast("सही मोबाइल नंबर भरें");
                        return;
                    }
                }
                if(!editText_phone3.getText().toString().isEmpty())
                {
                    if(editText_phone3.getText().toString().length()==10)
                    {
                        mobileNumbers.add(editText_phone3.getText().toString().trim());
                    }
                    else
                    {
                        showToast("सही मोबाइल नंबर भरें");
                        return;
                    }
                }
                if(editText_landmark.getText().toString().isEmpty())
                {
                    showToast("दुकान का पता भरें");
                    return;
                }
                if(itemValuesList.size()==0)
                {
                    showToast("आइटम भरें");
                    return;
                }
                itemInvoice.setInvoiceDate(editText_date.getText().toString().trim());
                ItemShop itemShop = new ItemShop();
                itemShop.setShopName(autoCompleteTextView_shop_name.getText().toString().trim());
                itemShop.setShopLandMark(editText_landmark.getText().toString().trim());
                itemShop.setShopPhones(mobileNumbers);
                itemShop.setShopShopkeepername(editText_shopkeepername.getText().toString().trim());
                itemInvoice.setInvoiceShop(itemShop);
                itemInvoice.setInvoiceValues(itemValuesList);
                float grandTotal = 0;
                for(int i=0;i<itemValuesList.size();i++)
                {
                    grandTotal = grandTotal + Float.parseFloat(itemValuesList.get(i).getTotalprice());
                }
                itemInvoice.setInvoiceTotal(Float.toString(grandTotal));
                itemInvoice.setInvoiceBillNumber("BN"+billNumber);
                createPDF(itemInvoice);
                break;
            }

        }
    }

    private void getRates(String url)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(context,"Please Wait","Loading..",true,false);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            JSONArray jsonArray = response.getJSONArray("items");
                            //Log.e("Tag","" + jsonArray);
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if(!jsonObject.getString("item").isEmpty())
                                {
                                    String name,unit,unitprice;
                                    name = jsonObject.getString("item");
                                    unit = jsonObject.getString("unit");
                                    unitprice = jsonObject.getString("rate");
                                    itemName.add(name);
                                    ItemValues itemValues = new ItemValues(name,unitprice,unit);
                                    stringItemValuesHashMap.put(name,itemValues);
                                    //Log.e("Tag",name);
                                }
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,itemName);
                            spinner_rates.setAdapter(arrayAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Turn On Internet Fisrt",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }


    private void createPDF(ItemInvoice itemInvoice)
    {
        if(!checkConnection())
        {
            Toast.makeText(context,"Turn on Internet First",Toast.LENGTH_SHORT).show();
            return;
        }
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        paint.setTypeface(Typeface.create( Typeface.MONOSPACE, Typeface.NORMAL));

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(250,400,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        int p_y = 30;
        Canvas canvas = page.getCanvas();

        //For Date
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(7f);
        canvas.drawText("Bill Number",10,p_y,paint);
        p_y+=10;
        canvas.drawText(itemInvoice.getInvoiceBillNumber(),10,p_y,paint);
        p_y+=2;
        canvas.drawLine(10,p_y,60,p_y,paint);

        //For Bill Number
        p_y = 30;
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(7f);
        canvas.drawText("Date",pageInfo.getPageWidth()-10,p_y,paint);
        p_y+=10;
        canvas.drawText(itemInvoice.getInvoiceDate(),pageInfo.getPageWidth()-10,p_y,paint);
        p_y+=2;
        canvas.drawLine(pageInfo.getPageWidth()-60,p_y,pageInfo.getPageWidth()-10,p_y,paint);

        //For Invoice Title
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12f);
        p_y=55;
        canvas.drawText("REENA",pageInfo.getPageWidth()/2,p_y,paint);
        paint.setTextSize(6f);
        p_y+=10;
        canvas.drawText("Rough Estimate",pageInfo.getPageWidth()/2,p_y,paint);

        //For Shop Description
        p_y=85;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(6f);
        ItemShop itemShop = itemInvoice.getInvoiceShop();
        canvas.drawText("Shop Name:",10,p_y,paint);
        canvas.drawText(itemShop.getShopName() + "," + itemShop.getShopShopkeepername(),55,p_y,paint);
        canvas.drawLine(50,p_y+2,pageInfo.getPageWidth()-10,p_y+2,paint);
        p_y+=10;
        canvas.drawText("Address:",10,p_y,paint);

        int addressLength = itemShop.getShopLandMark().length();
        int addLinesCount = 0,s_index = 0;
        while(true)
        {
            if(addressLength>45)
            {
                canvas.drawText(itemShop.getShopLandMark().substring(s_index,s_index+45),55,p_y,paint);
                canvas.drawLine(50,p_y+2,pageInfo.getPageWidth()-10,p_y+2,paint);
                p_y+=10;
                s_index+=45;
                addressLength-=45;
                addLinesCount+=1;
            }
            else
            {
                canvas.drawText(itemShop.getShopLandMark().substring(s_index),55,p_y,paint);
                canvas.drawLine(50,p_y+2,pageInfo.getPageWidth()-10,p_y+2,paint);
                p_y+=10;
                addLinesCount+=1;
                break;
            }
        }

        canvas.drawText("Contact:",10,p_y,paint);
        if(mobileNumbers.size()==1) canvas.drawText(mobileNumbers.get(0),55,p_y,paint);
        else if(mobileNumbers.size()==2) canvas.drawText(mobileNumbers.get(0)+"," + mobileNumbers.get(1),55,p_y,paint);
        else if(mobileNumbers.size()==3) canvas.drawText(mobileNumbers.get(0)+"," + mobileNumbers.get(1)+","+mobileNumbers.get(2),55,p_y,paint);
        canvas.drawLine(50,p_y+2,pageInfo.getPageWidth()-10,p_y+2,paint);

        //For items
        p_y = 130 + (addLinesCount*10);
        paint.setTextAlign(Paint.Align.LEFT);
        int p_y_i = p_y-10;
        canvas.drawLine(10,p_y_i,pageInfo.getPageWidth()-10,p_y_i,paint);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(6f);
        canvas.drawText("Qty.",10,p_y,paint);
        canvas.drawText("Description of Goods",65,p_y,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Rate",pageInfo.getPageWidth()-44,p_y,paint);
        canvas.drawText("Amount",pageInfo.getPageWidth()-12,p_y,paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawLine(10,p_y+2,pageInfo.getPageWidth()-10,p_y+2,paint);

        p_y = 140 + (addLinesCount*10);

        for(int i=0;i<itemInvoice.getInvoiceValues().size();i++)
        {
            ItemValues itemValues = itemInvoice.getInvoiceValues().get(i);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(5f);
            canvas.drawText(itemValues.getValueqty() + " " + itemValues.getValueUnitPriceUnit(),12,p_y,paint);
            canvas.drawText(itemValues.getValueName(),67,p_y,paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(itemValues.getValueUnitPrice(),pageInfo.getPageWidth()-46,p_y,paint);
            canvas.drawText(itemValues.getTotalprice(),pageInfo.getPageWidth()-14,p_y,paint);
            p_y+=10;
        }


        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawLine(10,p_y_i,10,p_y-5,paint);
        canvas.drawLine(63,p_y_i,63,p_y-5,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawLine(pageInfo.getPageWidth()-70,p_y_i,pageInfo.getPageWidth()-70,p_y-5,paint);
        canvas.drawLine(pageInfo.getPageWidth()-42,p_y_i,pageInfo.getPageWidth()-42,p_y-5,paint);
        canvas.drawLine(pageInfo.getPageWidth()-10,p_y_i,pageInfo.getPageWidth()-10,p_y-5,paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawLine(10,p_y-5,pageInfo.getPageWidth()-10,p_y-5,paint);
        canvas.drawText("Grand Total",pageInfo.getPageWidth()-40,p_y,paint);
        canvas.drawText(itemInvoice.getInvoiceTotal(),pageInfo.getPageWidth()-10,p_y,paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Signature",20,p_y+40,paint);
        pdfDocument.finishPage(page);

        // write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Digi Invoice/" + editText_date.getText().toString().trim() + "/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }

        filePath = new File(directory_path + "Bill Number" + billNumber + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(filePath));
            UploadPDF(Uri.fromFile(filePath),itemInvoice);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();
    }

    private void showToast(String msg)
    {
        Toast.makeText(Home.this,msg,Toast.LENGTH_SHORT).show();
    }

    private void UploadPDF(Uri uri, final ItemInvoice itemInvoice)
    {
        progressBar_pdf_upload.setVisibility(View.VISIBLE);
        final StorageReference sRef = storageReference.child("DigiInvoices/"+ itemInvoice.getInvoiceDate() + "/Bill_" + itemInvoice.getInvoiceBillNumber() + ".pdf");
        sRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                itemInvoice.setInvoicePDFLink(task.getResult().toString());
                                final String fileId = databaseReferenceInvoices.push().getKey();
                                itemInvoice.setInvoiceID(fileId);
                                databaseReferenceInvoices.child(editText_date.getText().toString().trim()).child(fileId).setValue(itemInvoice);
                                if(!isShopSelectedFromDatabase)
                                {
                                    final String shopId = databaseReferenceShops.push().getKey();
                                    itemInvoice.getInvoiceShop().setShopId(shopId);
                                    databaseReferenceShops.child(shopId).setValue(itemInvoice.getInvoiceShop());
                                }
                                else
                                {
                                    databaseReferenceShops.child(itemInvoice.getInvoiceShop().getShopId()).setValue(itemInvoice.getInvoiceShop());
                                }
                                finish();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        textView_pdf_upload.setAlpha(1);
                        textView_pdf_upload.setText("Uploading..");

                    }
                });
    }

    private boolean checkConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    TableRow.LayoutParams layoutParams;
    TableRow.LayoutParams layoutParamsCenter;
    TableLayout tableLayout_shopDetails;
    List<ItemShop> itemShopList = new ArrayList<>();
    private void showShopDialog()
    {
        final Dialog dialogShop = new Dialog(context);
        dialogShop.setContentView(R.layout.dialog_shop_details);
        dialogShop.setCanceledOnTouchOutside(false);

        MaterialButton button_close_dialog = dialogShop.findViewById(R.id.dialog_shop_btn_close);
        button_close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogShop.dismiss();
            }
        });

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
                    for(final ItemShop shop:itemShopList)
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
                            button_data_action.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    autoCompleteTextView_shop_name.setText(shop.getShopName());
                                    editText_landmark.setText(shop.getShopLandMark());
                                    editText_shopkeepername.setText(shop.getShopShopkeepername());

                                    Log.e("Contacts",""+shop.getShopPhones().size());
                                    switch (shop.getShopPhones().size())
                                    {
                                        case 1:
                                        {
                                            editText_phone1.setText(shop.getShopPhones().get(0));
                                            editText_phone2.setText("");
                                            editText_phone3.setText("");
                                            break;
                                        }
                                        case 2:
                                        {
                                            editText_phone1.setText(shop.getShopPhones().get(0));
                                            editText_phone2.setText(shop.getShopPhones().get(1));
                                            editText_phone3.setText("");
                                            break;
                                        }
                                        case 3:
                                        {
                                            editText_phone1.setText(shop.getShopPhones().get(0));
                                            editText_phone2.setText(shop.getShopPhones().get(1));
                                            editText_phone3.setText(shop.getShopPhones().get(2));
                                            break;
                                        }
                                    }
                                    isShopSelectedFromDatabase = true;
                                    dialogShop.dismiss();
                                }
                            });

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
