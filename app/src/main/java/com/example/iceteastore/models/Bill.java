package com.example.iceteastore.models;

import java.util.Date;

public class Bill {
    private int id;
    private Date date;
    private double total;
    private String username;
    private String status;

    public Bill(int id, Date date, double total, String username, String status) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.username = username;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
