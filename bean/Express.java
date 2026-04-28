package com.example.campusexpress.bean;

public class Express {
    private int id;
    private String trackingNumber;
    private String company;
    private String pickupCode;
    private String expectedTime;
    private int status; // 0: 待取件, 1: 已取件
    private long createTime;
    private long pickupTime; // 取件时间

    public Express() {
    }

    public Express(String trackingNumber, String company, String pickupCode, String expectedTime) {
        this.trackingNumber = trackingNumber;
        this.company = company;
        this.pickupCode = pickupCode;
        this.expectedTime = expectedTime;
        this.status = 0;
        this.createTime = System.currentTimeMillis();
        this.pickupTime = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getPickupCode() { return pickupCode; }
    public void setPickupCode(String pickupCode) { this.pickupCode = pickupCode; }

    public String getExpectedTime() { return expectedTime; }
    public void setExpectedTime(String expectedTime) { this.expectedTime = expectedTime; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public long getPickupTime() { return pickupTime; }
    public void setPickupTime(long pickupTime) { this.pickupTime = pickupTime; }
}
