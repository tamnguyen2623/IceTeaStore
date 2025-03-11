package com.example.iceteastore.models;

public class Favorite {
    private String username;
    private int productId;

    public Favorite(String username, int productId) {
        this.username = username;
        this.productId = productId;
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
}
