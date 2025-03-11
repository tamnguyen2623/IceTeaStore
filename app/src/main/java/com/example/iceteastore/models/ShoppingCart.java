package com.example.iceteastore.models;

public class ShoppingCart {
    private String username;
    private int productId;
    private int quantity;

    public ShoppingCart(String username, int productId, int quantity) {
        this.username = username;
        this.productId = productId;
        this.quantity = quantity;
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
}
