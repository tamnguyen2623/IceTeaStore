    //ProductModel
    package com.example.iceteastore.models;

    import java.io.Serializable;

    public class Product implements Serializable {
        private int id;
        private String name;
        private String description;
        private String image;
        private int quantity;
        private double price;
        private float rating;  // Thêm thuộc tính rating
        private int reviews;   // Thêm thuộc tính reviews



        public Product(int id, String name, String description, String image, int quantity, double price, float rating, int reviews) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.image = image;
            this.quantity = quantity;
            this.price = price;
            this.rating = rating;
            this.reviews = reviews;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public int getReviews() {
            return reviews;
        }

        public void setReviews(int reviews) {
            this.reviews = reviews;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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
