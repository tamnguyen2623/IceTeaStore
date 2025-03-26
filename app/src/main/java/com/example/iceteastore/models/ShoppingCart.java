package com.example.iceteastore.models;

import java.io.Serializable;

public class ShoppingCart implements Serializable {
    private int productId;
    private String name;
    private String imageResource;
    private int quantity;
    private double price;

    public ShoppingCart(int productId, String name, String imageResource, int quantity, double price) {
        this.productId = productId;
        this.name = name;
        this.imageResource = imageResource;
        this.quantity = quantity;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
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
