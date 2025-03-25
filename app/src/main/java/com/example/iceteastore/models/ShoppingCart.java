package com.example.iceteastore.models;

public class ShoppingCart {
    private String name;
    private int imageResource;
    private int quantity;
    private double price;
    private float rating;

    public ShoppingCart(String name, int imageResource, int quantity, double price, float rating) {
        this.name = name;
        this.imageResource = imageResource;
        this.quantity = quantity;
        this.price = price;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
