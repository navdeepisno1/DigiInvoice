package com.suvidha.digiinvoice;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class RVAdaper_Items extends RecyclerView.Adapter<RVAdaper_Items.RVView>{
    Context context;
    List<ItemValues> itemValuesList;
    List<String> itemName;
    HashMap<String,ItemValues> stringItemValuesHashMap;

    private Spinner spinner_itemName;
    private EditText editText_qty,editText_unitprice,editText_totalprice,editText_discount;
    private RadioButton radioButton_pcs,radioButton_bags,radioButton_kgs,radioButton_gms;
    private Button button_addItem;

    private int position;
    public RVAdaper_Items(Context context, List<ItemValues> itemValuesList, List<String> itemName, HashMap<String, ItemValues> stringItemValuesHashMap) {
        this.context = context;
        this.itemValuesList = itemValuesList;
        this.itemName = itemName;
        this.stringItemValuesHashMap = stringItemValuesHashMap;
    }

    @NonNull
    @Override
    public RVView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_items, parent, false);

        return new RVView(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RVView holder, int position) {
        final ItemValues itemValues = itemValuesList.get(position);
        holder.textView_name.setText(itemValues.getValueName());
        String itemUnit = "";
        switch (itemValues.getValueItemUnit())
        {
            case "0": {
                itemUnit = "piece";
                break;
            }
            case "1": {
                itemUnit = "bags";
                break;
            }
            case "2": {
                itemUnit = "kg";
                break;
            }
            case "3": {
                itemUnit = "grams";
                break;
            }

        }
        String price = itemValues.getValueqty() + " " + itemUnit;
        holder.textView_price.setText(price);

        holder.imageView_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("tag",""+holder.getAdapterPosition());
                final Dialog d_confirm = new Dialog(context);
                d_confirm.setContentView(R.layout.activity_dialog_confirm);
                Button button_Y,button_N;
                button_N = d_confirm.findViewById(R.id.dialog_confirm_NO);
                button_Y = d_confirm.findViewById(R.id.dialog_confirm_YES);
                button_N.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d_confirm.dismiss();
                    }
                });
                button_Y.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemValuesList.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                        //notifyItemChanged(holder.getAdapterPosition());
                        //notifyItemRangeChanged(holder.getAdapterPosition(),itemValuesList.size());
                        d_confirm.dismiss();
                    }
                });
                d_confirm.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemValuesList.size();
    }


    public class RVView extends RecyclerView.ViewHolder
    {
        TextView textView_name,textView_price;
        ImageView imageView_delete,imageView_edit;
        public RVView(@NonNull final View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.rv_item_name);
            textView_price = itemView.findViewById(R.id.rv_item_price);
            imageView_delete = itemView.findViewById(R.id.rv_item_iv_delete);
            imageView_edit = itemView.findViewById(R.id.rv_item_iv_edit);
            position = getAdapterPosition();

            imageView_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_add_item);
                    spinner_itemName = dialog.findViewById(R.id.dialog_add_item_item_name);
                    editText_qty = dialog.findViewById(R.id.dialog_add_item_quantity);
                    editText_discount = dialog.findViewById(R.id.dialog_add_item_discount);
                    editText_unitprice = dialog.findViewById(R.id.dialog_add_item_unit_price);
                    editText_totalprice = dialog.findViewById(R.id.dialog_add_item_total_price);
                    button_addItem = dialog.findViewById(R.id.dialog_add_item_button_add);
                    radioButton_pcs = dialog.findViewById(R.id.dialog_add_item_radio_piece);
                    radioButton_bags = dialog.findViewById(R.id.dialog_add_item_radio_bag);
                    radioButton_kgs = dialog.findViewById(R.id.dialog_add_item_radio_kg);
                    radioButton_gms = dialog.findViewById(R.id.dialog_add_item_radio_gm);

                    editText_qty.setText(itemValuesList.get(getAdapterPosition()).getValueqty());
                    editText_unitprice.setText(itemValuesList.get(getAdapterPosition()).getValueUnitPrice());
                    editText_totalprice.setText(itemValuesList.get(getAdapterPosition()).getTotalprice());
                    editText_discount.setText(itemValuesList.get(getAdapterPosition()).getDiscount());

                    if(itemValuesList.get(getAdapterPosition()).getValueUnitPriceUnit().equals("Kg"))
                    {
                        radioButton_kgs.setChecked(true);
                        radioButton_bags.setChecked(false);
                        radioButton_pcs.setChecked(false);
                        radioButton_kgs.setEnabled(true);
                        radioButton_gms.setEnabled(true);
                        radioButton_bags.setEnabled(false);
                        radioButton_pcs.setEnabled(false);
                    }
                    else if(itemValuesList.get(getAdapterPosition()).getValueUnitPriceUnit().equals("Kg"))
                    {
                        radioButton_gms.setChecked(true);
                        radioButton_bags.setChecked(false);
                        radioButton_pcs.setChecked(false);
                        radioButton_kgs.setEnabled(true);
                        radioButton_gms.setEnabled(true);
                        radioButton_bags.setEnabled(false);
                        radioButton_pcs.setEnabled(false);
                    }
                    else if(itemValuesList.get(getAdapterPosition()).getValueUnitPriceUnit().equals("Bag"))
                    {
                        radioButton_bags.setChecked(true);
                        radioButton_bags.setEnabled(true);
                        radioButton_gms.setEnabled(false);
                        radioButton_kgs.setEnabled(false);
                        radioButton_pcs.setEnabled(false);
                    }
                    else if(itemValuesList.get(getAdapterPosition()).getValueUnitPriceUnit().equals("piece"))
                    {
                        radioButton_pcs.setChecked(true);
                        radioButton_pcs.setEnabled(true);
                        radioButton_gms.setEnabled(false);
                        radioButton_kgs.setEnabled(false);
                        radioButton_bags.setEnabled(false);
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,itemName);
                    spinner_itemName.setAdapter(arrayAdapter);
                    spinner_itemName.setSelection(arrayAdapter.getPosition(itemValuesList.get(getAdapterPosition()).getValueName()));

                    spinner_itemName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            itemValuesList.get(getAdapterPosition()).setValueName(adapterView.getItemAtPosition(i).toString());
                            itemValuesList.get(getAdapterPosition()).setValueUnitPriceUnit(stringItemValuesHashMap.get(adapterView.getItemAtPosition(i).toString()).getValueUnitPriceUnit());
                            itemValuesList.get(getAdapterPosition()).setValueUnitPrice(stringItemValuesHashMap.get(adapterView.getItemAtPosition(i).toString()).getValueUnitPrice());
                            itemValuesList.get(getAdapterPosition()).setDiscount("0");

                            if(itemValuesList.get(getAdapterPosition()).getValueUnitPriceUnit().equals("Kg"))
                            {
                                radioButton_bags.setChecked(false);
                                radioButton_pcs.setChecked(false);
                                radioButton_kgs.setChecked(true);
                                radioButton_kgs.setEnabled(true);
                                radioButton_gms.setEnabled(true);
                                radioButton_bags.setEnabled(false);
                                radioButton_pcs.setEnabled(false);
                            }
                            else if(itemValuesList.get(getAdapterPosition()).getValueUnitPriceUnit().equals("Bag"))
                            {
                                radioButton_bags.setChecked(true);
                                radioButton_bags.setEnabled(true);
                                radioButton_gms.setEnabled(false);
                                radioButton_kgs.setEnabled(false);
                                radioButton_pcs.setEnabled(false);
                            }
                            else if(itemValuesList.get(getAdapterPosition()).getValueUnitPriceUnit().equals("piece"))
                            {
                                radioButton_pcs.setChecked(true);
                                radioButton_pcs.setEnabled(true);
                                radioButton_gms.setEnabled(false);
                                radioButton_kgs.setEnabled(false);
                                radioButton_bags.setEnabled(false);
                            }
                            editText_unitprice.setText(itemValuesList.get(getAdapterPosition()).getValueUnitPrice());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    editText_discount.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            if(charSequence.toString().equals("."))
                            {
                                editText_discount.setText("");
                                return;
                            }

                            if(!editText_qty.getText().toString().isEmpty() && !editText_unitprice.getText().toString().isEmpty() && !editText_discount.getText().toString().isEmpty()) {
                                if (radioButton_bags.isChecked() || radioButton_pcs.isChecked() || radioButton_kgs.isChecked()) {
                                    float uunit = Float.parseFloat(editText_qty.getText().toString().trim());
                                    float udiscount = Float.parseFloat(editText_discount.getText().toString().trim());
                                    float UnitPrice = Float.parseFloat(editText_unitprice.getText().toString().trim());
                                    itemValuesList.get(getAdapterPosition()).setDiscount(editText_discount.getText().toString().trim());
                                    editText_totalprice.setText("" + ((UnitPrice * uunit)-udiscount));
                                } else if(radioButton_gms.isChecked()) {
                                    float uunit = Float.parseFloat(editText_qty.getText().toString().trim());
                                    float udiscount = Float.parseFloat(editText_discount.getText().toString().trim());
                                    float UnitPrice = Float.parseFloat(editText_unitprice.getText().toString().trim());
                                    itemValuesList.get(getAdapterPosition()).setDiscount(editText_discount.getText().toString().trim());
                                    editText_totalprice.setText("" + ((UnitPrice * uunit / 1000)-udiscount));
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    editText_qty.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if(charSequence.toString().equals("."))
                            {
                                editText_qty.setText("");
                                return;
                            }
                            if(!editText_qty.getText().toString().isEmpty() && !editText_unitprice.getText().toString().isEmpty() && !editText_discount.getText().toString().isEmpty()) {
                                if (radioButton_bags.isChecked() || radioButton_pcs.isChecked() || radioButton_kgs.isChecked()) {
                                    float uunit = Float.parseFloat(editText_qty.getText().toString().trim());
                                    float udiscount = Float.parseFloat(editText_discount.getText().toString().trim());
                                    float UnitPrice = Float.parseFloat(editText_unitprice.getText().toString().trim());
                                    itemValuesList.get(getAdapterPosition()).setValueqty(editText_qty.getText().toString().trim());
                                    editText_totalprice.setText("" + ((UnitPrice * uunit)-udiscount));
                                } else if(radioButton_gms.isChecked()) {
                                    float uunit = Float.parseFloat(editText_qty.getText().toString().trim());
                                    float udiscount = Float.parseFloat(editText_discount.getText().toString().trim());
                                    float UnitPrice = Float.parseFloat(editText_unitprice.getText().toString().trim());
                                    itemValuesList.get(getAdapterPosition()).setValueqty(editText_qty.getText().toString().trim());
                                    editText_totalprice.setText("" + ((UnitPrice * uunit / 1000)-udiscount));
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    editText_unitprice.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if(charSequence.toString().equals("."))
                            {
                                editText_unitprice.setText("");
                                return;
                            }
                            if(!editText_qty.getText().toString().isEmpty() && !editText_unitprice.getText().toString().isEmpty() && !editText_discount.getText().toString().isEmpty()) {
                                if (radioButton_bags.isChecked() || radioButton_pcs.isChecked() || radioButton_kgs.isChecked()) {
                                    float uunit = Float.parseFloat(editText_qty.getText().toString().trim());
                                    float udiscount = Float.parseFloat(editText_discount.getText().toString().trim());
                                    float UnitPrice = Float.parseFloat(editText_unitprice.getText().toString().trim());
                                    itemValuesList.get(getAdapterPosition()).setValueUnitPrice(editText_unitprice.getText().toString().trim());
                                    editText_totalprice.setText("" + ((UnitPrice * uunit)-udiscount));
                                } else if(radioButton_gms.isChecked()) {
                                    float uunit = Float.parseFloat(editText_qty.getText().toString().trim());
                                    float udiscount = Float.parseFloat(editText_discount.getText().toString().trim());
                                    float UnitPrice = Float.parseFloat(editText_unitprice.getText().toString().trim());
                                    itemValuesList.get(getAdapterPosition()).setValueUnitPrice(editText_unitprice.getText().toString().trim());
                                    editText_totalprice.setText("" + ((UnitPrice * uunit / 1000)-udiscount));
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
                            if(editText_qty.getText().toString().isEmpty() || editText_unitprice.getText().toString().isEmpty() || editText_discount.getText().toString().trim().isEmpty())
                            {
                                return;
                            }
                            if(radioButton_bags.isChecked() || radioButton_pcs.isChecked() || radioButton_kgs.isChecked())
                            {
                                float uunit = Float.parseFloat(editText_qty.getText().toString().trim());
                                float udiscount = Float.parseFloat(editText_discount.getText().toString().trim());
                                float UnitPrice = Float.parseFloat(editText_unitprice.getText().toString().trim());
                                editText_totalprice.setText("" + ((UnitPrice * uunit)-udiscount));
                            }
                            else if(radioButton_gms.isChecked())
                            {
                                float uunit = Float.parseFloat(editText_qty.getText().toString().trim());
                                float udiscount = Float.parseFloat(editText_discount.getText().toString().trim());
                                float UnitPrice = Float.parseFloat(editText_unitprice.getText().toString().trim());
                                editText_totalprice.setText("" + ((UnitPrice * uunit / 1000)-udiscount));
                            }
                        }
                    });
                    radioButton_kgs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(editText_qty.getText().toString().isEmpty() || editText_unitprice.getText().toString().isEmpty() || editText_discount.getText().toString().trim().isEmpty())
                            {
                                return;
                            }
                            if(radioButton_bags.isChecked() || radioButton_pcs.isChecked() || radioButton_kgs.isChecked())
                            {
                                float uunit = Float.parseFloat(editText_qty.getText().toString().trim());
                                float udiscount = Float.parseFloat(editText_discount.getText().toString().trim());
                                float UnitPrice = Float.parseFloat(editText_unitprice.getText().toString().trim());
                                editText_totalprice.setText("" + ((UnitPrice * uunit)-udiscount));
                            }
                            else if(radioButton_gms.isChecked())
                            {
                                float uunit = Float.parseFloat(editText_qty.getText().toString().trim());
                                float udiscount = Float.parseFloat(editText_discount.getText().toString().trim());
                                float UnitPrice = Float.parseFloat(editText_unitprice.getText().toString().trim());
                                editText_totalprice.setText("" + ((UnitPrice * uunit / 1000)-udiscount));
                            }
                        }
                    });

                    button_addItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(editText_qty.getText().toString().isEmpty() || editText_unitprice.getText().toString().isEmpty() || editText_totalprice.getText().toString().isEmpty() || editText_discount.getText().toString().trim().isEmpty())
                            {
                                Toast.makeText(context,"Please fill required all fields",Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(radioButton_pcs.isChecked()) itemValuesList.get(getAdapterPosition()).setValueItemUnit("0");
                            else if(radioButton_bags.isChecked()) itemValuesList.get(getAdapterPosition()).setValueItemUnit("1");
                            else if(radioButton_kgs.isChecked()) itemValuesList.get(getAdapterPosition()).setValueItemUnit("2");
                            else if(radioButton_gms.isChecked()) itemValuesList.get(getAdapterPosition()).setValueItemUnit("3");

                            itemValuesList.get(getAdapterPosition()).setTotalprice(editText_totalprice.getText().toString().trim());
                            notifyItemChanged(getAdapterPosition());
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });


        }
    }


}
