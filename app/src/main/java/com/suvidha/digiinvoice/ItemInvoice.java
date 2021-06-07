package com.suvidha.digiinvoice;

import java.util.List;

public class ItemInvoice {
    String invoiceBillNumber,invoiceDate,invoicePDFLink,invoiceID,invoiceTotal;
    ItemShop invoiceShop;
    List<ItemValues> invoiceValues;

    public ItemInvoice()
    {

    }

    public ItemInvoice(String invoiceBillNumber, String invoiceDate, String invoicePDFLink, String invoiceID, String invoiceTotal, ItemShop invoiceShop, List<ItemValues> invoiceValues) {
        this.invoiceBillNumber = invoiceBillNumber;
        this.invoiceDate = invoiceDate;
        this.invoicePDFLink = invoicePDFLink;
        this.invoiceID = invoiceID;
        this.invoiceTotal = invoiceTotal;
        this.invoiceShop = invoiceShop;
        this.invoiceValues = invoiceValues;
    }

    public String getInvoiceBillNumber() {
        return invoiceBillNumber;
    }

    public void setInvoiceBillNumber(String invoiceBillNumber) {
        this.invoiceBillNumber = invoiceBillNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoicePDFLink() {
        return invoicePDFLink;
    }

    public void setInvoicePDFLink(String invoicePDFLink) {
        this.invoicePDFLink = invoicePDFLink;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(String invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public ItemShop getInvoiceShop() {
        return invoiceShop;
    }

    public void setInvoiceShop(ItemShop invoiceShop) {
        this.invoiceShop = invoiceShop;
    }

    public List<ItemValues> getInvoiceValues() {
        return invoiceValues;
    }

    public void setInvoiceValues(List<ItemValues> invoiceValues) {
        this.invoiceValues = invoiceValues;
    }
}
