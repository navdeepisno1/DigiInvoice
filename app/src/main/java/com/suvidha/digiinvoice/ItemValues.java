package com.suvidha.digiinvoice;

public class ItemValues {
    //Class Variables
    String valueName,valueUnitPrice, valueUnitPriceUnit,totalprice,valueqty,valueItemUnit,discount;

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public ItemValues(String valueName, String valueUnitPrice, String valueUnitPriceUnit, String totalprice, String valueqty, String valueItemUnit, String discount) {
        this.valueName = valueName;
        this.valueUnitPrice = valueUnitPrice;
        this.valueUnitPriceUnit = valueUnitPriceUnit;
        this.totalprice = totalprice;
        this.valueqty = valueqty;
        this.valueItemUnit = valueItemUnit;
        this.discount = discount;
    }

    public String getValueItemUnit() {
        return valueItemUnit;
    }

    public void setValueItemUnit(String valueItemUnit) {
        this.valueItemUnit = valueItemUnit;
    }

    //Blank Constructor
    public ItemValues()
    {

    }

    public ItemValues(String valueName, String valueUnitPrice, String valueUnitPriceUnit, String totalprice, String valueqty) {
        this.valueName = valueName;
        this.valueUnitPrice = valueUnitPrice;
        this.valueUnitPriceUnit = valueUnitPriceUnit;
        this.totalprice = totalprice;
        this.valueqty = valueqty;
    }

    public ItemValues(String valueName, String valueUnitPrice, String valueUnitPriceUnit, String totalprice) {
        this.valueName = valueName;
        this.valueUnitPrice = valueUnitPrice;
        this.valueUnitPriceUnit = valueUnitPriceUnit;
        this.totalprice = totalprice;
    }

    public ItemValues(String valueName, String valueUnitPrice, String valueUnitPriceUnit) {
        this.valueName = valueName;
        this.valueUnitPrice = valueUnitPrice;
        this.valueUnitPriceUnit = valueUnitPriceUnit;
    }

    //Getters and Setters
    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getValueUnitPrice() {
        return valueUnitPrice;
    }

    public void setValueUnitPrice(String valueUnitPrice) {
        this.valueUnitPrice = valueUnitPrice;
    }

    public String getValueUnitPriceUnit() {
        return valueUnitPriceUnit;
    }

    public void setValueUnitPriceUnit(String valueUnitPriceUnit) {
        this.valueUnitPriceUnit = valueUnitPriceUnit;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    public String getValueqty() {
        return valueqty;
    }

    public void setValueqty(String valueqty) {
        this.valueqty = valueqty;
    }
}
