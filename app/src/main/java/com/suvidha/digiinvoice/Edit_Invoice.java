package com.suvidha.digiinvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Edit_Invoice extends AppCompatActivity implements View.OnClickListener{
    TextView textView_bNo,textView_date,textView_pdf_upload;
    AutoCompleteTextView autoCompleteTextView_shopName;
    EditText editText_landmark,editText_shopkeepername,editText_phone1,editText_phone2,editText_phone3;
    File filePath;
    Button button_addItem,button_addInvoice,button_selectDate,button_search_shop;
    Intent intent;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceInvoice;
    ItemInvoice itemInvoice;
    List<String> mobileNumbers,itemName,shopNames;
    HashMap<String, ItemShop> stringItemShopHashMap;
    RecyclerView recyclerViewItems;
    Context context = this;
    Dialog dialogItem;
    Spinner spinner_item;
    String url_rate,previousdate;
    StorageReference storageReference;
    HashMap<String,ItemValues> stringItemValuesHashMap;
    ProgressBar progressBar_pdf_upload;
    boolean isShopSelectedFromDatabase=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__invoice);

        textView_bNo = findViewById(R.id.edit_tv_bno);
        textView_date = findViewById(R.id.edit_tv_date);
        autoCompleteTextView_shopName = findViewById(R.id.edit_autocomplete);
        editText_landmark = findViewById(R.id.edit_et_landmark);
        editText_phone1 = findViewById(R.id.edit_et_mobile1);
        editText_phone2 = findViewById(R.id.edit_et_mobile2);
        editText_phone3 = findViewById(R.id.edit_et_mobile3);
        button_selectDate = findViewById(R.id.edit_btn_select_date);
        recyclerViewItems = findViewById(R.id.edit_rv_items);
        button_addItem = findViewById(R.id.edit_btn_add);
        editText_shopkeepername = findViewById(R.id.edit_et_name);
        button_addInvoice = findViewById(R.id.edit_btn_save);
        progressBar_pdf_upload = findViewById(R.id.edit_progress_pdf);
        textView_pdf_upload = findViewById(R.id.edit_tv_pdf);
        button_search_shop = findViewById(R.id.edit_btn_search_shop);

        dialogItem = new Dialog(context);
        dialogItem.setContentView(R.layout.dialog_add_item);
        spinner_item = dialogItem.findViewById(R.id.dialog_add_item_item_name);
        storageReference = FirebaseStorage.getInstance().getReference();

        mobileNumbers = new ArrayList<>();
        itemName = new ArrayList<>();
        shopNames = new ArrayList<>();
        stringItemValuesHashMap = new HashMap<String,ItemValues>();
        stringItemShopHashMap = new HashMap<String,ItemShop>();


        button_addItem.setOnClickListener(this);
        button_addInvoice.setOnClickListener(this);
        button_selectDate.setOnClickListener(this);

        intent = getIntent();

        button_search_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShopDialog();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceInvoice = firebaseDatabase.getReference("Invoices").child(intent.getStringExtra("invoiceDate")).child(intent.getStringExtra("invoiceId"));
        databaseReferenceInvoice.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemInvoice = snapshot.getValue(ItemInvoice.class);
                if(itemInvoice!=null)
                {
                    autoCompleteTextView_shopName.setText(itemInvoice.getInvoiceShop().getShopName());
                    editText_shopkeepername.setText(itemInvoice.getInvoiceShop().getShopShopkeepername());
                    switch (itemInvoice.getInvoiceShop().getShopPhones().size())
                    {
                        case 1:
                        {
                            Log.e("tag","1");
                            editText_phone1.setText(itemInvoice.getInvoiceShop().getShopPhones().get(0));
                            editText_phone2.setText("");
                            editText_phone3.setText("");
                            break;
                        }
                        case 2:
                        {
                            Log.e("tag","2");
                            editText_phone1.setText(itemInvoice.getInvoiceShop().getShopPhones().get(0));
                            editText_phone2.setText(itemInvoice.getInvoiceShop().getShopPhones().get(1));
                            editText_phone3.setText("");
                            break;
                        }
                        case 3: {
                            Log.e("tag","3");
                            editText_phone1.setText(itemInvoice.getInvoiceShop().getShopPhones().get(0));
                            editText_phone2.setText(itemInvoice.getInvoiceShop().getShopPhones().get(1));
                            editText_phone3.setText(itemInvoice.getInvoiceShop().getShopPhones().get(2));
                            break;
                        }
                    }
                    previousdate = itemInvoice.getInvoiceDate();
                    editText_landmark.setText(itemInvoice.getInvoiceShop().getShopLandMark());
                    textView_bNo.setText(itemInvoice.getInvoiceBillNumber());
                    textView_date.setText(itemInvoice.getInvoiceDate());
                    recyclerViewItems.setLayoutManager(new LinearLayoutManager(context));
                    recyclerViewItems.setAdapter(new RVAdaper_Items(context,itemInvoice.getInvoiceValues(),itemName,stringItemValuesHashMap));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        url_rate = "https://script.google.com/macros/s/AKfycbyYztNRsXSwrlDX46UADvUmqXRKKuRkcW96JnTrOPosyeV8n2Y/exec?action=getRateList";
        getRates(url_rate);
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
                                }
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,itemName);
                            spinner_item.setAdapter(arrayAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);

    }
    ItemValues itemValues;
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.edit_btn_select_date:
            {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        String[] months = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                        String iDate;
                        iDate = d + " " + months[m] + " " + y;
                        itemInvoice.setInvoiceDate(iDate);
                        textView_date.setText(iDate);
                    }
                },year,month,day);
                datePickerDialog.show();
                break;
            }
            case R.id.edit_btn_add:
            {
                itemValues = new ItemValues();
                final EditText d_quantity,d_unitprice,d_total,d_discount;
                final RadioButton radioButton_piece,radioButton_bags,radioButton_kg,radioButton_gms;
                radioButton_bags = dialogItem.findViewById(R.id.dialog_add_item_radio_bag);
                radioButton_piece = dialogItem.findViewById(R.id.dialog_add_item_radio_piece);
                radioButton_kg = dialogItem.findViewById(R.id.dialog_add_item_radio_kg);
                radioButton_gms = dialogItem.findViewById(R.id.dialog_add_item_radio_gm);
                d_discount = dialogItem.findViewById(R.id.dialog_add_item_discount);
                d_quantity = dialogItem.findViewById(R.id.dialog_add_item_quantity);
                d_unitprice = dialogItem.findViewById(R.id.dialog_add_item_unit_price);
                d_total = dialogItem.findViewById(R.id.dialog_add_item_total_price);
                d_quantity.setText("");
                d_total.setText("");
                d_unitprice.setText("");
                d_discount.setText("0");

                spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                Button button_addItem = dialogItem.findViewById(R.id.dialog_add_item_button_add);
                button_addItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(d_quantity.getText().toString().isEmpty() || d_unitprice.getText().toString().isEmpty() || d_total.getText().toString().isEmpty() || d_discount.getText().toString().trim().isEmpty())
                        {
                            Toast.makeText(Edit_Invoice.this,"Please fill required all fields",Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(radioButton_piece.isChecked()) itemValues.setValueItemUnit("0");
                        else if(radioButton_bags.isChecked()) itemValues.setValueItemUnit("1");
                        else if(radioButton_kg.isChecked()) itemValues.setValueItemUnit("2");
                        else if(radioButton_gms.isChecked()) itemValues.setValueItemUnit("3");

                        itemValues.setTotalprice(d_total.getText().toString().trim());
                        itemInvoice.getInvoiceValues().add(itemValues);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                        recyclerViewItems.setLayoutManager(linearLayoutManager);
                        RVAdaper_Items rvAdaper_items = new RVAdaper_Items(context,itemInvoice.getInvoiceValues(),itemName,stringItemValuesHashMap);
                        recyclerViewItems.setAdapter(rvAdaper_items);
                        dialogItem.dismiss();
                    }
                });
                dialogItem.show();
                break;
            }
            case R.id.edit_btn_save:
            {
                itemInvoice.getInvoiceShop().getShopPhones().clear();
                if(autoCompleteTextView_shopName.getText().toString().isEmpty())
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
                    itemInvoice.getInvoiceShop().getShopPhones().add(editText_phone1.getText().toString().trim());
                }
                if(!editText_phone2.getText().toString().isEmpty())
                {
                    if(editText_phone2.getText().toString().length()==10)
                    {
                        itemInvoice.getInvoiceShop().getShopPhones().add(editText_phone2.getText().toString().trim());
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
                        itemInvoice.getInvoiceShop().getShopPhones().add(editText_phone3.getText().toString().trim());
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
                if(itemInvoice.getInvoiceValues().size()==0)
                {
                    showToast("आइटम भरें");
                    return;
                }
                itemInvoice.getInvoiceShop().setShopName(autoCompleteTextView_shopName.getText().toString().trim());
                itemInvoice.getInvoiceShop().setShopShopkeepername(editText_shopkeepername.getText().toString().trim());
                itemInvoice.getInvoiceShop().setShopLandMark(editText_landmark.getText().toString().trim());
                float grandTotal = 0;
                for(int i=0;i<itemInvoice.getInvoiceValues().size();i++)
                {
                    grandTotal = grandTotal + Float.parseFloat(itemInvoice.getInvoiceValues().get(i).getTotalprice());
                }
                itemInvoice.setInvoiceTotal(Float.toString(grandTotal));
                createPDF(itemInvoice);
                break;
            }

        }
    }

    private void createPDF(ItemInvoice itemInvoice)
    {
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
        canvas.drawText(itemShop.getShopShopkeepername()+"," + itemShop.getShopName(),55,p_y,paint);
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
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Digi Invoice/" + itemInvoice.getInvoiceDate() + "/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }

        filePath = new File(directory_path + "Bill Number" + itemInvoice.getInvoiceBillNumber() + ".pdf");
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
        Toast.makeText(Edit_Invoice.this,msg,Toast.LENGTH_SHORT).show();
    }

    private void UploadPDF(final Uri uri, final ItemInvoice itemInvoice)
    {
        final LinearLayout linearLayout = findViewById(R.id.edit_linear_progress);
        linearLayout.setAlpha(1);
        if(previousdate.equals(itemInvoice.getInvoiceDate()))
        {
            final StorageReference sRef = storageReference.child("DigiInvoices/"+ itemInvoice.getInvoiceDate() + "/Bill_" + itemInvoice.getInvoiceBillNumber() + ".pdf");
            sRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    itemInvoice.setInvoicePDFLink(task.getResult().toString());
                                    databaseReferenceInvoice.setValue(itemInvoice);
                                    DatabaseReference databaseReferenceShops = FirebaseDatabase.getInstance().getReference("Shops");
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
        else
        {
            StorageReference puranaRef = FirebaseStorage.getInstance().getReferenceFromUrl(itemInvoice.getInvoicePDFLink());
            puranaRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    databaseReferenceInvoice.removeValue();
                    final StorageReference sRef = storageReference.child("DigiInvoices/"+ itemInvoice.getInvoiceDate() + "/Bill_" + itemInvoice.getInvoiceBillNumber() + ".pdf");
                    sRef.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            itemInvoice.setInvoicePDFLink(task.getResult().toString());
                                            final String fileId = databaseReferenceInvoice.push().getKey();
                                            itemInvoice.setInvoiceID(fileId);
                                            databaseReferenceInvoice = FirebaseDatabase.getInstance().getReference("Invoices");
                                            databaseReferenceInvoice.child(textView_date.getText().toString().trim()).child(fileId).setValue(itemInvoice);
                                            DatabaseReference databaseReferenceShops = FirebaseDatabase.getInstance().getReference("Shops");
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
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }



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

        final EditText editText_shopName = dialogShop.findViewById(R.id.dialog_shop_et_name);

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
                                    editText_shopName.setText(shop.getShopName());
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