package com.suvidha.digiinvoice;


import java.util.List;

public class ItemShop {
    String shopName,shopLandMark,shopShopkeepername,shopId;
    List<String> shopPhones;

    public ItemShop()
    {

    }

    public ItemShop(String shopName, String shopLandMark, List<String> shopPhones) {
        this.shopName = shopName;
        this.shopLandMark = shopLandMark;
        this.shopPhones = shopPhones;
    }

    public ItemShop(String shopName, String shopLandMark, String shopShopkeepername, List<String> shopPhones) {
        this.shopName = shopName;
        this.shopLandMark = shopLandMark;
        this.shopShopkeepername = shopShopkeepername;
        this.shopPhones = shopPhones;
    }

    public ItemShop(String shopName, String shopLandMark, String shopShopkeepername, String shopId, List<String> shopPhones) {
        this.shopName = shopName;
        this.shopLandMark = shopLandMark;
        this.shopShopkeepername = shopShopkeepername;
        this.shopId = shopId;
        this.shopPhones = shopPhones;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopLandMark() {
        return shopLandMark;
    }

    public void setShopLandMark(String shopLandMark) {
        this.shopLandMark = shopLandMark;
    }

    public String getShopShopkeepername() {
        return shopShopkeepername;
    }

    public void setShopShopkeepername(String shopShopkeepername) {
        this.shopShopkeepername = shopShopkeepername;
    }

    public List<String> getShopPhones() {
        return shopPhones;
    }

    public void setShopPhones(List<String> shopPhones) {
        this.shopPhones = shopPhones;
    }
}
