package com.example.iceteastore.models;

public class ShoppingCart {
    private String username;
    private int productId;
    private int quantity;
    private double price;

    public ShoppingCart(String username, int productId, int quantity, double price) {
        this.username = username;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
