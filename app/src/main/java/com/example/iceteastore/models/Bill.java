package com.example.iceteastore.models;

import java.util.Date;

public class Bill {
    private int id;
    private Date date;
    private double total;
    private String username;

    public Bill(int id, Date date, double total, String username) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.username = username;
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
}
