package com.suvidha.digiinvoice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVAdapter_Invoice extends RecyclerView.Adapter<RVAdapter_Invoice.ViewHolderInvoice>{

    List<ItemInvoice> itemInvoices;
    Context context;

    public RVAdapter_Invoice(List<ItemInvoice> itemInvoices, Context context) {
        this.itemInvoices = itemInvoices;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderInvoice onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_invoice, parent, false);
        return new ViewHolderInvoice(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderInvoice holder, int position) {
        final ItemInvoice itemInvoice = itemInvoices.get(position);
        holder.textView_bno.setText(itemInvoice.getInvoiceBillNumber());
        holder.textView_date.setText(itemInvoice.getInvoiceDate());
        holder.textView_shop_name.setText(itemInvoice.getInvoiceShop().getShopName());

        holder.button_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemInvoice.getInvoicePDFLink()));
                context.startActivity(intent);
            }
        });

        holder.button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Edit_Invoice.class);
                intent.putExtra("invoiceDate",itemInvoice.getInvoiceDate());
                intent.putExtra("invoiceId",itemInvoice.getInvoiceID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemInvoices.size();
    }

    public class ViewHolderInvoice extends RecyclerView.ViewHolder {
        Button button_edit,button_delete,button_download;
        TextView textView_bno,textView_date,textView_shop_name;
        public ViewHolderInvoice(@NonNull View itemView) {
            super(itemView);
            button_delete = itemView.findViewById(R.id.rv_item_invoice_delete);
            button_download = itemView.findViewById(R.id.rv_item_invoice_download);
            button_edit = itemView.findViewById(R.id.rv_item_invoice_edit);
            textView_bno = itemView.findViewById(R.id.rv_item_invoice_bill_number);
            textView_date = itemView.findViewById(R.id.rv_item_invoice_date);
            textView_shop_name = itemView.findViewById(R.id.rv_item_invoice_shop_name);
        }
    }
}
